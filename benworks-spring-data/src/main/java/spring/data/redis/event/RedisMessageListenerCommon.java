package spring.data.redis.event;

import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.connection.Message;

import spring.data.redis.server.ser.Jackson3JsonRedisSerializer;
import spring.data.redis.utils.QuickLZUtils;

public abstract class RedisMessageListenerCommon implements IRedisMessageListener {

	@Override
	public void onMessage(Message message, byte[] pattern) {
		RedisEventCode event = Jackson3JsonRedisSerializer.json2Object(message.getBody(), RedisEventCode.class);
		byte[] body = event.getBody();
		if (event.isCompress()) {
			body = QuickLZUtils.unzip(body, 60, TimeUnit.SECONDS);
		}
		onEvent(Jackson3JsonRedisSerializer.json2Object(body, event.getType()));
	}
}
