package spring.data.redis.model;

/**
 * 事务逻辑回调
 * @author Ben
 * @date 2016年1月1日下午1:54:53
 */
public interface ITxCallBack<T> {

	public T exec(T entity);

}
