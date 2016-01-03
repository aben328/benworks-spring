package spring.data.redis.server;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;

import com.google.common.cache.CacheBuilder;

import redis.clients.jedis.Protocol;
import redis.clients.jedis.exceptions.JedisConnectionException;
import redis.clients.util.SafeEncoder;
import spring.data.redis.anno.CacheStrategy;
import spring.data.redis.anno.LockStrategy;
import spring.data.redis.anno.StoreStrategy;
import spring.data.redis.event.IRedisEvent;
import spring.data.redis.event.RedisEventCode;
import spring.data.redis.exception.RedisException;
import spring.data.redis.exception.RedisLockTimeOutException;
import spring.data.redis.model.ICursorCallBack;
import spring.data.redis.model.ILockCallBack;
import spring.data.redis.model.IRedisDao;
import spring.data.redis.model.IRedisEntity;
import spring.data.redis.model.IRedisMBean;
import spring.data.redis.server.manager.RedisDataSourceManager;
import spring.data.redis.server.manager.RedisPersistentManager;
import spring.data.redis.server.ser.Jackson3JsonRedisSerializer;
import spring.data.redis.server.ser.StringSerializer;
import spring.data.redis.utils.QuickLZUtils;

/**
 * 基础 http://shift-alt-ctrl.iteye.com/category/277626 <br>
 * 备份问题 http://my.oschina.net/freegeek/blog/324410
 * @author Ben
 * @date 2016年1月1日下午2:10:40
 */
@SuppressWarnings("unchecked")
public class RedisDao<T extends IRedisEntity> implements IRedisDao<String, T>, IRedisMBean, InitializingBean {
	private static Logger logger = LoggerFactory.getLogger(RedisDao.class);

	@Autowired
	protected RedisDataSourceManager redisDataSourceManager;

	protected RedisConnectionFactory cf;
	protected RedisTemplate<String, ?> redis;

	protected Jackson3JsonRedisSerializer<T> valueRedisSerializer;
	protected Class<T> entityClass;

	protected LockStrategy lockStrategy;

	protected CacheStrategy cacheStrategy;

	protected StoreStrategy storeStrategy;

	private ConcurrentMap<String, T> cache = new ConcurrentHashMap<String, T>();

	private String owner;

	private final static LockStrategy defaultgLockStrategy = new LockStrategy() {

		@Override
		public Class<? extends Annotation> annotationType() {
			return LockStrategy.class;
		}

		@Override
		public int times() {
			return 40;
		}

		@Override
		public int sleepTime() {
			return 250;
		}

		@Override
		public int expires() {
			return 1000 * 10;
		}
	};

	public void afterPropertiesSet() {
		if (entityClass == null) {
			// 执行期获取java 泛型类型,class 必须要有硬文件存在
			entityClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass())
					.getActualTypeArguments()[0];
		}

		lockStrategy = getClass().getAnnotation(LockStrategy.class);
		cacheStrategy = getClass().getAnnotation(CacheStrategy.class);
		storeStrategy = getClass().getAnnotation(StoreStrategy.class);

		// dataSource 指定来源
		if (cf == null) {
			if (redisDataSourceManager == null) {
				throw new RuntimeException("redis redisDataSourceManager is null");
			}
			if (storeStrategy == null) {
				throw new RuntimeException("redis storeStrategy is null");
			}
			cf = redisDataSourceManager.getRedisConnection(storeStrategy.dataSource());
		}
		if (cf == null) {
			throw new RuntimeException("RedisConnectionFactory is null");
		}

		// 默认锁策略设置
		if (lockStrategy == null) {
			lockStrategy = defaultgLockStrategy;
		}

		// 缓存设置
		if (cacheStrategy != null) {
			int length = cacheStrategy.lenth();
			long expires = cacheStrategy.expires();

			CacheBuilder<Object, Object> builder = CacheBuilder.newBuilder();
			builder.initialCapacity(16);
			builder.concurrencyLevel(16);
			// LRU
			if (length > 0) {
				builder.maximumSize(length);
			}

			// TIME OUT
			if (expires > 0) {
				builder.expireAfterAccess(expires, TimeUnit.MILLISECONDS);
				builder.expireAfterWrite(expires, TimeUnit.MILLISECONDS);
			}

			cache = (ConcurrentMap<String, T>) builder.build();
		}

		valueRedisSerializer = new Jackson3JsonRedisSerializer<T>((Class<T>) entityClass);

		// 设置redis tpl
		if (redis == null) {
			redis = new RedisTemplate<String, Object>();
			redis.setEnableDefaultSerializer(false);
			redis.setConnectionFactory(cf);
			redis.setKeySerializer(StringSerializer.INSTANCE);
			// redis.setValueSerializer(valueRedisSerializer);
			redis.afterPropertiesSet();
		}
		// 注册监听
		owner = getClass() + "_" + entityClass + "_" + UUID.randomUUID().toString();
		RedisPersistentManager.register(owner, this);
	}

	public static <T extends IRedisEntity> RedisDao<T> of(Class<T> entityClass, RedisConnectionFactory cf) {
		RedisDao<T> result = new RedisDao<T>();
		result.entityClass = entityClass;
		result.cf = cf;
		result.afterPropertiesSet();
		return result;
	}

	public static <T extends IRedisEntity> RedisDao<T> of(Class<T> entityClass, RedisConnectionFactory cf,
			RedisTemplate<String, ?> redis) {
		RedisDao<T> result = new RedisDao<T>();
		result.entityClass = entityClass;
		result.cf = cf;
		result.redis = redis;
		result.afterPropertiesSet();
		return result;
	}

	@Override
	public T findOneForCache(String key) {
		if (cacheStrategy == null) {
			return findOne(key);
		}

		T result = (T) cache.get(key);
		if (result == null) {
			result = findOne(key);
			if (result != null) {
				cache.put(key, result);
			}
		}
		return result;
	}

	@Override
	public T findOne(final String key) {
		T result = null;
		try {
			result = findOneAndThrow(key);
		} catch (Exception e) {
			if (!checkConnectException(e)) {
				throw new RedisException(e);
			}
		}
		return result;
	}

	@Override
	public T findOneAndThrow(final String key) {
		return redis.execute(new RedisCallback<T>() {
			@Override
			public T doInRedis(RedisConnection connection) throws DataAccessException {
				byte[] rawKey = keySerializer(key);
				byte[] result = connection.get(rawKey);
				return valueDeserialize(result);
			}
		}, true);
	}

	@Override
	public void saveOrUpdateSync(T... entitys) {
		for (final T entity : entitys) {
			boolean ok = false;
			try {
				redis.execute(new RedisCallback<T>() {
					@Override
					public T doInRedis(RedisConnection connection) throws DataAccessException {
						byte[] rawKey = keySerializer(entity.toId());
						connection.set(rawKey, valueSerializer(entity));
						if (cacheStrategy != null) {
							cache.put(entity.toId(), entity);
						}
						return null;
					}
				}, true);
				ok = true;
			} catch (Exception e) {
				if (checkConnectException(e)) {
					ok = false;
				} else {
					throw new RedisException(e);
				}
			} finally {
				// 连接失败处理
				if (!ok && this.storeStrategy.retryDelay() > 0) {
					RedisPersistentManager.putAction(RedisSaveOrUpdateAction.of(this, entity, true),
							this.storeStrategy.retryDelay());
				}
			}
		}
	}

	@Override
	public void saveOrUpdate(T... entitys) {
		for (T entity : entitys) {
			if (cacheStrategy != null) {
				cache.put(entity.toId(), entity);
			}
			RedisPersistentManager.putAction(RedisSaveOrUpdateAction.of(this, entity, false),
					this.storeStrategy.delay());
		}
	}

	@Override
	public Set<String> keys(String pattern) {
		try {
			return redis.keys(pattern);
		} catch (Exception e) {
			if (checkConnectException(e)) {
				return Collections.emptySet();
			} else {
				throw new RedisException(e);
			}
		}
	}

	@Override
	public List<T> query(String pattern) {
		try {
			Set<String> ids = redis.keys(pattern);
			List<T> result = new ArrayList<T>(ids.size());
			for (String id : ids) {
				result.add(findOne(id));
			}
			return result;
		} catch (Exception e) {
			if (checkConnectException(e)) {
				return Collections.EMPTY_LIST;
			} else {
				throw new RedisException(e);
			}
		}
	}

	@Override
	public void cursor(String pattern, ICursorCallBack<T> cb) {
		try {
			Set<String> ids = redis.keys(pattern);
			for (String id : ids) {
				cb.exec(findOne(id));
			}
		} catch (Exception e) {
			if (!checkConnectException(e)) {
				throw new RedisException(e);
			}
		}
	}

	@Override
	public void remove(String... keys) {
		for (String key : keys) {
			cache.remove(key);
			RedisPersistentManager.putAction(RedisRemoveAction.of(this, key, false), this.storeStrategy.delay());
		}
	}

	@Override
	public void removeSync(String... keys) {
		for (String key : keys) {
			try {
				cache.remove(key);
				redis.delete(key);
			} catch (Exception e) {
				if (checkConnectException(e) && this.storeStrategy.retryDelay() > 0) {
					RedisPersistentManager.putAction(RedisRemoveAction.of(this, key, true),
							this.storeStrategy.retryDelay());
				} else {
					throw new RedisException(e);
				}
			}
		}
	}

	private boolean checkConnectException(Exception e) {
		boolean flag = (e instanceof java.net.ConnectException || e instanceof RedisConnectionFailureException
				|| e instanceof JedisConnectionException);
		if (flag) {
			if (logger.isDebugEnabled()) {
				logger.debug("{}", e);
			}
		}
		return flag;
	}

	@Override
	public void clearCache(String... keys) {
		for (String key : keys) {
			cache.remove(key);
		}
	}

	@Override
	public void clearAllCache() {
		cache.clear();
	}

	@Override
	public <R> R lock(String key, ILockCallBack<R> callBack) {
		try {
			return _lock(key, callBack);
		} catch (Exception e) {
			callBack.onError(key, e);
			return null;
		}
	}

	<R> R _lock(String key, ILockCallBack<R> callBack) {
		String lockKey = "_clock_" + key;
		if (lock(lockKey)) {
			try {
				return callBack.exec(key);
			} finally {
				unLock(lockKey);
			}
		}
		throw new RedisLockTimeOutException("lock time out : " + key);
	}

	@Override
	public void send(IRedisEvent msg, String... channels) {
		byte[] body = object2Byte(msg);
		if (this.storeStrategy.compressValue()) {
			body = QuickLZUtils.zip(body);
		}
		byte[] bytes = this.valueRedisSerializer
				.serialize(RedisEventCode.of(msg.getClass(), body, this.storeStrategy.compressValue()));
		for (String channel : channels) {
			this.redis.convertAndSend(channel, bytes);
		}
	}

	@Override
	public void destroy() {
		if (cf instanceof DisposableBean) {
			try {
				((DisposableBean) cf).destroy();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public boolean rename(String oldKey, String newKey) {
		try {
			redis.rename(oldKey, newKey);
			return true;
		} catch (JedisConnectionException e) {
			return false;
		}
	}

	@Override
	public boolean exists(String key) {
		return redis.hasKey(key);
	}

	@Override
	public void expire(String key, long timeout, TimeUnit unit) {
		redis.expire(key, timeout, unit);
	}

	@Override
	public DataType type(String key) {
		return redis.type(key);
	}

	@Override
	public long getDbUseSize() {
		return redis.execute(new RedisCallback<Long>() {
			@Override
			public Long doInRedis(RedisConnection connection) throws DataAccessException {
				return connection.dbSize();
			}
		});
	}

	// //////////////////// 内部方法//////////////////////////

	private boolean lock(String lockKey) {
		_await(random(1, 50));

		// 处理次数
		int times = lockStrategy.times();
		// 下次请求等侍时间
		int sleepTime = lockStrategy.sleepTime();
		// 有效时间
		int expires = lockStrategy.expires();
		// 最大请求等侍时间
		final int maxWait = 2 * 1000;

		expires = expires / 1000;
		final RedisConnection redisConnection = redis.getConnectionFactory().getConnection();
		final byte[] key = keySerializer(lockKey);
		int i = 0;
		while (times-- >= 0) {

			// 首次设置成功
			// 使用系统自带管理
			Object ok = redisConnection.execute(Protocol.Command.SET.name(), key, new byte[] { 0 },
					SafeEncoder.encode("EX"), Protocol.toByteArray(expires), SafeEncoder.encode("NX"));
			if (ok != null) {
				return true;
			}
			int t = Math.min(maxWait, sleepTime * i);
			t = random(t - 50, t + 50);
			_await(t);
			i++;
		}

		return false;
	}

	private void unLock(String lockKey) {
		redis.delete(lockKey);
	}

	private static int random(int min, int max) {
		Random random = new Random();
		int time = random.nextInt(max) % (max - min + 1) + min;
		return time;
	}

	private void _await(long sleepTime) {
		if (sleepTime <= 0) {
			sleepTime = 50;
		}
		try {
			Thread.sleep(sleepTime);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	protected byte[] keySerializer(String key) {
		return StringSerializer.INSTANCE.serialize(key);
	}

	protected byte[] valueSerializer(Object entity) {
		byte[] bytes = this.valueRedisSerializer.serialize(entity);
		if (this.storeStrategy.compressValue()) {
			return QuickLZUtils.zip(bytes);
		}
		return bytes;
	}

	protected T valueDeserialize(byte[] bytes) {
		if (this.storeStrategy.compressValue()) {
			return this.valueRedisSerializer.deserialize(QuickLZUtils.unzip(bytes, 60, TimeUnit.SECONDS));
		}
		return this.valueRedisSerializer.deserialize(bytes);
	}

	protected byte[] object2Byte(Object entity) {
		return Jackson3JsonRedisSerializer.object2Byte(entity);
	}

	protected String object2Json(Object entity) {
		return Jackson3JsonRedisSerializer.object2Json(entity);
	}

	// get set

	public void setRedisDataSourceManager(RedisDataSourceManager redisDataSourceManager) {
		this.redisDataSourceManager = redisDataSourceManager;
	}

	public Class<T> getEntityClass() {
		return entityClass;
	}

	public String getOwner() {
		return owner;
	}

}
