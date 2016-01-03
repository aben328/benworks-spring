package spring.data.redis.model;

/**
 * 
 * @author Ben
 *
 * @date 2016年1月1日下午1:53:21
 */
public interface IRedisClearHandle {

	public void exec();

	public int getValidDay();
}
