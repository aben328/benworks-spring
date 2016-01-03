package spring.data.redis.exception;

/**
 * Redis连接异常
 * @author Ben
 * @date 2016年1月1日下午12:48:18
 */
public class RedisConnectException extends RedisException {

	private static final long serialVersionUID = 3565545073210233714L;

	public RedisConnectException() {
		super();
	}

	public RedisConnectException(String message, Throwable cause) {
		super(message, cause);
	}

	public RedisConnectException(String message) {
		super(message);
	}

	public RedisConnectException(Throwable cause) {
		super(cause);
	}
}