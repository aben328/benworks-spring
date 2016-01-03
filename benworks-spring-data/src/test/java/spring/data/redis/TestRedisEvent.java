package spring.data.redis;

import java.util.concurrent.TimeUnit;

import org.junit.Test;

import spring.data.redis.event.RedisEventCode;
import spring.data.redis.model.TestMyRedisEvent;
import spring.data.redis.server.ser.Jackson3JsonRedisSerializer;
import spring.data.redis.utils.QuickLZUtils;

/**
 * 
 * @author Ben
 *
 * @date 2016年1月1日下午2:35:32
 */
public class TestRedisEvent {

	@Test
	public void test() {
		TestMyRedisEvent msg = new TestMyRedisEvent();
		byte[] body = Jackson3JsonRedisSerializer.object2Byte(msg);
		boolean compress = true;
		System.out.println(" compress before :  " + body.length);
		if (compress) {
			body = QuickLZUtils.zip(body);
			System.out.println(" compress after :  " + body.length);
		}
		byte[] bytes = Jackson3JsonRedisSerializer.object2Byte(RedisEventCode.of(msg.getClass(), body, compress));

		RedisEventCode event = Jackson3JsonRedisSerializer.json2Object(bytes, RedisEventCode.class);
		body = event.getBody();
		if (event.isCompress()) {
			body = QuickLZUtils.unzip(body, 60, TimeUnit.SECONDS);
		}
		msg = (TestMyRedisEvent) Jackson3JsonRedisSerializer.json2Object(body, event.getType());
		msg.println();
	}
}