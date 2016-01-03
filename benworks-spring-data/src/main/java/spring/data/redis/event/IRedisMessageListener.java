package spring.data.redis.event;

import org.springframework.data.redis.connection.MessageListener;
/**
 * 
 * @author Ben
 *
 * @date 2016年1月1日下午1:10:56
 */
public interface IRedisMessageListener extends MessageListener {

	public String getChannel();

	public void onEvent(IRedisEvent event);
}
