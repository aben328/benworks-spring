package spring.data.redis.model;

/**
 * 锁逻辑回调
 * 
 * @author Ben
 *
 * @date 2016年1月1日下午1:52:56
 */
public interface ILockCallBack<T> {
	/**
	 * @param key redis id
	 * @return 业务回调可返回相应实体数据
	 */
	public T exec(String key);

	public void onError(String key, Exception ex);
}
