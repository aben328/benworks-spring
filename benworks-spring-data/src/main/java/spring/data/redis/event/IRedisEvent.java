package spring.data.redis.event;

/**
 * Redis事件
 * @author Ben
 * @date 2016年1月1日下午1:05:28
 */
public interface IRedisEvent {

	public String getName();

	public String getFrom();
}
