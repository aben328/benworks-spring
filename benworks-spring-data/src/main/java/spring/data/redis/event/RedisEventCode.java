package spring.data.redis.event;

/**
 * Redis事件编码
 * @author Ben
 * @date 2016年1月1日下午1:06:04
 */
public class RedisEventCode {

	private Class<? extends IRedisEvent> type;

	private byte[] body;

	private boolean compress;

	public static RedisEventCode of(Class<? extends IRedisEvent> type, byte[] body, boolean compress) {
		RedisEventCode result = new RedisEventCode();
		result.type = type;
		result.body = body;
		result.compress = compress;
		return result;
	}

	// getter

	public Class<? extends IRedisEvent> getType() {
		return type;
	}

	public byte[] getBody() {
		return body;
	}

	public boolean isCompress() {
		return compress;
	}

	void setCompress(boolean compress) {
		this.compress = compress;
	}

	void setType(Class<? extends IRedisEvent> type) {
		this.type = type;
	}

	void setBody(byte[] body) {
		this.body = body;
	}

}
