package spring.data.redis.model;

public interface ICursorCallBack<T> {

	public void exec(T entity);

}
