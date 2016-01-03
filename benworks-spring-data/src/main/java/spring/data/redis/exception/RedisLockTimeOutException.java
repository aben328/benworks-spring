package spring.data.redis.exception;

/**
 * Redis锁超过时异常
 * @author Ben
 * @date 2016年1月1日下午12:48:49
 */
public class RedisLockTimeOutException extends RedisException {

	private static final long serialVersionUID = 7679427291186673286L;

	public RedisLockTimeOutException() {
		super();
	}

	public RedisLockTimeOutException(String message, Throwable cause) {
		super(message, cause);
	}

	public RedisLockTimeOutException(String message) {
		super(message);
	}

	public RedisLockTimeOutException(Throwable cause) {
		super(cause);
	}
}
