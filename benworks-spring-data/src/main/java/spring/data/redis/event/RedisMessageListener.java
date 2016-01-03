package spring.data.redis.event;

/**
 * @author Ben
 * @date 2016年1月1日下午1:46:43
 */
public class RedisMessageListener extends RedisMessageListenerCommon {

	public final static String NAME = "test:channel";

	@Override
	public String getChannel() {
		return NAME;
	}

	@Override
	public void onEvent(IRedisEvent event) {
		System.out.println(event.getFrom());
	}
}
