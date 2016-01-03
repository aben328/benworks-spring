package spring.data.redis.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 锁逻辑回调模板
 * @author Ben
 * @date 2016年1月1日下午1:55:20
 */
public class LockCallBack<T> implements ILockCallBack<T> {

	private static Logger logger = LoggerFactory.getLogger(LockCallBack.class);

	@Override
	public T exec(String key) {
		return null;
	}

	@Override
	public void onError(String key, Exception ex) {
		logger.error("redis lock error [{}], {}", key, ex);
	}
}
