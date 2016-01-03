package spring.data.redis.server;

import spring.data.redis.model.IRedisElementAction;
import spring.data.redis.model.IRedisEntity;

/**
 * 删除操作
 * @author Ben
 * @date 2016年1月1日下午2:14:23
 */
@SuppressWarnings({ "rawtypes" })
public class RedisRemoveAction<T extends IRedisEntity> implements IRedisElementAction {

	private RedisDao<T> dao;

	private String key;

	private boolean retry;

	public static <T extends IRedisEntity> RedisRemoveAction<T> of(RedisDao<T> dao, String key, boolean retry) {
		RedisRemoveAction<T> result = new RedisRemoveAction<T>();
		result.dao = dao;
		result.key = key;
		result.retry = retry;
		return result;
	}

	@Override
	public String getId() {
		return key;
	}

	@Override
	public boolean isRetry() {
		return retry;
	}

	@Override
	public void exec() {
		dao.removeSync(key);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((key == null) ? 0 : key.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RedisRemoveAction other = (RedisRemoveAction) obj;
		if (key == null) {
			if (other.key != null)
				return false;
		} else if (!key.equals(other.key))
			return false;
		return true;
	}

}
