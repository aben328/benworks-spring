package spring.data.redis.event;

/**
 * Redis事件通用
 * @author Ben
 * @date 2016年1月1日下午1:07:27
 */
public abstract class RedisEventCommon implements IRedisEvent {

	private String name;
	private String from;

	public RedisEventCommon() {
	}

	public RedisEventCommon(String name, String from) {
		this.name = name;
		this.from = from;
	}

	public String getName() {
		return name;
	}

	public String getFrom() {
		return from;
	}

	void setName(String name) {
		this.name = name;
	}

	void setFrom(String from) {
		this.from = from;
	}
}
