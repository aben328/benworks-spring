package spring.data.redis.model;

/**
 * 元素操作
 * @author Ben
 * @date 2016年1月1日下午2:13:49
 */
public interface IRedisElementAction {

	public void exec();

	public String getId();

	public boolean isRetry();
}
