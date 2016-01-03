package spring.data.redis.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 仓库策略
 * @author Ben
 * @date 2016年1月1日下午1:20:04
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface StoreStrategy {

	/** 数据来源 */
	String dataSource();

	/** crud 操作delay >0 开启 */
	int delay() default -1;

	/** crud 重试delay >0 开启 */
	int retryDelay() default -1;

	/** crud 操作 开启批处理 */
	boolean fix() default false;

	/** 是否开启压缩value */
	boolean compressValue() default false;
}
