/* Copyright 2011-2015 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License. */
package spring.data.redis.server;

import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.ExceptionTranslationStrategy;
import org.springframework.data.redis.PassThroughExceptionTranslationStrategy;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnection;
import org.springframework.data.redis.connection.jedis.JedisConverters;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisSentinelPool;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.Protocol;
import redis.clients.util.Pool;

/**
 * Connection factory creating <a href="http://github.com/xetorthio/jedis">Jedis</a> based connections.
 * @author Costin Leau
 * @author Thomas Darimont
 * @author Christoph Strobl
 * @author solq
 */
public class JedisConnectionFactory implements InitializingBean, DisposableBean, RedisConnectionFactory {

	private final static Logger log = LoggerFactory.getLogger(JedisConnectionFactory.class);
	private static final ExceptionTranslationStrategy EXCEPTION_TRANSLATION = new PassThroughExceptionTranslationStrategy(
			JedisConverters.exceptionConverter());

	private static final Method SET_TIMEOUT_METHOD;
	private static final Method GET_TIMEOUT_METHOD;

	static {

		// We need to configure Jedis socket timeout via reflection since the
		// method-name was changed between releases.
		Method setTimeoutMethodCandidate = ReflectionUtils.findMethod(JedisShardInfo.class, "setTimeout", int.class);
		if (setTimeoutMethodCandidate == null) {
			// Jedis V 2.7.x changed the setTimeout method to setSoTimeout
			setTimeoutMethodCandidate = ReflectionUtils.findMethod(JedisShardInfo.class, "setSoTimeout", int.class);
		}
		SET_TIMEOUT_METHOD = setTimeoutMethodCandidate;

		Method getTimeoutMethodCandidate = ReflectionUtils.findMethod(JedisShardInfo.class, "getTimeout");
		if (getTimeoutMethodCandidate == null) {
			getTimeoutMethodCandidate = ReflectionUtils.findMethod(JedisShardInfo.class, "getSoTimeout");
		}

		GET_TIMEOUT_METHOD = getTimeoutMethodCandidate;
	}

	private JedisShardInfo shardInfo;
	private String hostName = "localhost";
	private int port = Protocol.DEFAULT_PORT;
	private int timeout = Protocol.DEFAULT_TIMEOUT;
	private int connectionTimeout = Protocol.DEFAULT_TIMEOUT;
	private String password;
	private boolean usePool = true;
	private Pool<Jedis> pool;
	private JedisPoolConfig poolConfig = new JedisPoolConfig();
	private int dbIndex = 0;
	private boolean convertPipelineAndTxResults = true;

	/**
	 * Constructs a new <code>JedisConnectionFactory</code> instance with default settings (default connection pooling,
	 * no shard information).
	 */
	public JedisConnectionFactory() {
	}

	/**
	 * Constructs a new <code>JedisConnectionFactory</code> instance. Will override the other connection parameters
	 * passed to the factory.
	 * @param shardInfo shard information
	 */
	public JedisConnectionFactory(JedisShardInfo shardInfo) {
		this.shardInfo = shardInfo;
	}

	/**
	 * Constructs a new {@link JedisConnectionFactory} instance using the given {@link JedisPoolConfig} applied to
	 * {@link JedisSentinelPool}.
	 * @param sentinelConfig
	 * @param poolConfig pool configuration. Defaulted to new instance if {@literal null}.
	 * @since 1.4
	 */
	public JedisConnectionFactory(JedisPoolConfig poolConfig) {
		this.poolConfig = poolConfig != null ? poolConfig : new JedisPoolConfig();
	}

	/**
	 * Returns a Jedis instance to be used as a Redis connection. The instance can be newly created or retrieved from a
	 * pool.
	 * @return Jedis instance ready for wrapping into a {@link RedisConnection}.
	 */
	protected Jedis fetchJedisConnector() {
		try {
			if (usePool && pool != null) {
				return pool.getResource();
			}
			Jedis jedis = new Jedis(getShardInfo());
			// force initialization (see Jedis issue #82)
			jedis.connect();
			return jedis;
		} catch (Exception ex) {
			throw new RedisConnectionFailureException("Cannot get Jedis connection", ex);
		}
	}

	/**
	 * Post process a newly retrieved connection. Useful for decorating or executing initialization commands on a new
	 * connection. This implementation simply returns the connection.
	 * @param connection
	 * @return processed connection
	 */
	protected JedisConnection postProcessConnection(JedisConnection connection) {
		return connection;
	}

	/* (non-Javadoc)
	 * 
	 * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet() */
	public void afterPropertiesSet() {
		if (shardInfo == null) {
			shardInfo = new JedisShardInfo(hostName, port);

			if (StringUtils.hasLength(password)) {
				shardInfo.setPassword(password);
			}

			if (timeout > 0) {
				setTimeoutOn(shardInfo, timeout);
			}
			if (connectionTimeout > 0) {
				shardInfo.setConnectionTimeout(connectionTimeout);
			}
		}

		if (usePool) {
			this.pool = createPool();
		}
	}

	private Pool<Jedis> createPool() {
		return createRedisPool();
	}

	/**
	 * Creates {@link JedisPool}.
	 * @return
	 * @since 1.4
	 */
	protected Pool<Jedis> createRedisPool() {

		return new JedisPool(getPoolConfig(), getShardInfo().getHost(), getShardInfo().getPort(),
				getShardInfo().getConnectionTimeout(), getShardInfo().getSoTimeout(), getShardInfo().getPassword(), 0,
				null);

	}

	/* (non-Javadoc)
	 * 
	 * @see org.springframework.beans.factory.DisposableBean#destroy() */
	public void destroy() {
		if (usePool && pool != null) {
			try {
				pool.destroy();
			} catch (Exception ex) {
				log.warn("Cannot properly close Jedis pool", ex);
			}
			pool = null;
		}
	}

	/* (non-Javadoc)
	 * 
	 * @see org.springframework.data.redis.connection.RedisConnectionFactory# getConnection() */
	public JedisConnection getConnection() {
		Jedis jedis = fetchJedisConnector();
		JedisConnection connection = (usePool ? new JedisConnection(jedis, pool, dbIndex)
				: new JedisConnection(jedis, null, dbIndex));
		connection.setConvertPipelineAndTxResults(convertPipelineAndTxResults);
		return postProcessConnection(connection);
	}

	/* (non-Javadoc)
	 * 
	 * @see org.springframework.dao.support.PersistenceExceptionTranslator#
	 * translateExceptionIfPossible(java.lang.RuntimeException) */
	public DataAccessException translateExceptionIfPossible(RuntimeException ex) {
		return EXCEPTION_TRANSLATION.translate(ex);
	}

	/**
	 * Returns the Redis hostName.
	 * @return Returns the hostName
	 */
	public String getHostName() {
		return hostName;
	}

	/**
	 * Sets the Redis hostName.
	 * @param hostName The hostName to set.
	 */
	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public int getConnectionTimeout() {
		return connectionTimeout;
	}

	public void setConnectionTimeout(int connectionTimeout) {
		this.connectionTimeout = connectionTimeout;
	}

	/**
	 * Returns the password used for authenticating with the Redis server.
	 * @return password for authentication
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * Sets the password used for authenticating with the Redis server.
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * Returns the port used to connect to the Redis instance.
	 * @return Redis port.
	 */
	public int getPort() {
		return port;

	}

	/**
	 * Sets the port used to connect to the Redis instance.
	 * @param port Redis port
	 */
	public void setPort(int port) {
		this.port = port;
	}

	/**
	 * Returns the shardInfo.
	 * @return Returns the shardInfo
	 */
	public JedisShardInfo getShardInfo() {
		return shardInfo;
	}

	/**
	 * Sets the shard info for this factory.
	 * @param shardInfo The shardInfo to set.
	 */
	public void setShardInfo(JedisShardInfo shardInfo) {
		this.shardInfo = shardInfo;
	}

	/**
	 * Returns the timeout.
	 * @return Returns the timeout
	 */
	public int getTimeout() {
		return timeout;
	}

	/**
	 * @param timeout The timeout to set.
	 */
	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	/**
	 * Indicates the use of a connection pool.
	 * @return Returns the use of connection pooling.
	 */
	public boolean getUsePool() {
		return usePool;
	}

	/**
	 * Turns on or off the use of connection pooling.
	 * @param usePool The usePool to set.
	 */
	public void setUsePool(boolean usePool) {
		this.usePool = usePool;
	}

	/**
	 * Returns the poolConfig.
	 * @return Returns the poolConfig
	 */
	public JedisPoolConfig getPoolConfig() {
		return poolConfig;
	}

	/**
	 * Sets the pool configuration for this factory.
	 * @param poolConfig The poolConfig to set.
	 */
	public void setPoolConfig(JedisPoolConfig poolConfig) {
		this.poolConfig = poolConfig;
	}

	/**
	 * Returns the index of the database.
	 * @return Returns the database index
	 */
	public int getDatabase() {
		return dbIndex;
	}

	/**
	 * Sets the index of the database used by this connection factory. Default is 0.
	 * @param index database index
	 */
	public void setDatabase(int index) {
		Assert.isTrue(index >= 0, "invalid DB index (a positive index required)");
		this.dbIndex = index;
	}

	/**
	 * Specifies if pipelined results should be converted to the expected data type. If false, results of
	 * {@link JedisConnection#closePipeline()} and {@link JedisConnection#exec()} will be of the type returned by the
	 * Jedis driver
	 * @return Whether or not to convert pipeline and tx results
	 */
	public boolean getConvertPipelineAndTxResults() {
		return convertPipelineAndTxResults;
	}

	/**
	 * Specifies if pipelined results should be converted to the expected data type. If false, results of
	 * {@link JedisConnection#closePipeline()} and {@link JedisConnection#exec()} will be of the type returned by the
	 * Jedis driver
	 * @param convertPipelineAndTxResults Whether or not to convert pipeline and tx results
	 */
	public void setConvertPipelineAndTxResults(boolean convertPipelineAndTxResults) {
		this.convertPipelineAndTxResults = convertPipelineAndTxResults;
	}

	private void setTimeoutOn(JedisShardInfo shardInfo, int timeout) {
		ReflectionUtils.invokeMethod(SET_TIMEOUT_METHOD, shardInfo, timeout);
	}

	@SuppressWarnings("unused")
	private int getTimeoutFrom(JedisShardInfo shardInfo) {
		return (Integer) ReflectionUtils.invokeMethod(GET_TIMEOUT_METHOD, shardInfo);
	}

}
