package spring.data.redis.server.manager;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.data.redis.connection.RedisConnectionFactory;

/**
 * 数据来源管理 XML 注入
 * @author Ben
 * @date 2016年1月1日下午2:00:34
 */
public class RedisDataSourceManager implements BeanPostProcessor {

	private Map<String, RedisConnectionFactory> redisConnectionFactorys = new HashMap<String, RedisConnectionFactory>();

	public RedisConnectionFactory getRedisConnection(String dataSource) {
		return redisConnectionFactorys.get(dataSource);
	}

	public synchronized void registerRedisConnection(String dataSource, RedisConnectionFactory connectionFactory) {
		redisConnectionFactorys.put(dataSource, connectionFactory);
	}

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}

	// getter and setter

	public Map<String, RedisConnectionFactory> getRedisConnectionFactorys() {
		return redisConnectionFactorys;
	}

	public void setRedisConnectionFactorys(Map<String, RedisConnectionFactory> redisConnectionFactorys) {
		this.redisConnectionFactorys = redisConnectionFactorys;
	}

}
