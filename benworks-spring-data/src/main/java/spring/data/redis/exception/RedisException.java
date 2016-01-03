package spring.data.redis.exception;

/**
 * redis异常
 * @author Ben
 * @date 2016年1月1日下午12:47:29
 */
public class RedisException extends RuntimeException {

	private static final long serialVersionUID = 2268048043336868299L;

	public RedisException() {
		super();
	}

	public RedisException(String message, Throwable cause) {
		super(message, cause);
	}

	public RedisException(String message) {
		super(message);
	}

	public RedisException(Throwable cause) {
		super(cause);
	}
}
