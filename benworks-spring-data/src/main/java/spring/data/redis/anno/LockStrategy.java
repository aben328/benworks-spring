package spring.data.redis.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/***
 * 锁策略
 * @author Ben
 * @date 2016年1月1日下午1:16:44
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface LockStrategy {

	/** 有效时间 */
	int expires();

	/** 下次请求时间 */
	int sleepTime();

	/** 重试处理次数 */
	int times() default 25;
}
