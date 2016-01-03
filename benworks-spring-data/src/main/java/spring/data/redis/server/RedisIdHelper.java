package spring.data.redis.server;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Redis ID 生成帮助器
 * @author Ben
 * @date 2016年1月1日下午2:17:55
 */
public abstract class RedisIdHelper {

	/***
	 * 每天ID
	 */
	public static String toId(Class<?> clz, String id, Date date) {
		return clz.getName() + "_" + id + "_" + new SimpleDateFormat("yyyy-MM-dd").format(date);
	}

	/***
	 * 搜索每天ID
	 */
	public static String search(Class<?> clz, String id, Date date) {
		return clz.getName() + "_" + id + "_" + new SimpleDateFormat("yyyy-MM-dd").format(date);
	}

	/***
	 * 唯一ID
	 */
	public static String toId(Class<?> clz, String id) {
		return clz.getName() + "_" + id;
	}

	/***
	 * 搜索唯一ID
	 */
	public static String search(Class<?> clz, String id) {
		return clz.getName() + "_" + id;
	}

}
