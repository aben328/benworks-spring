package spring.data.redis.server.manager;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import spring.data.redis.model.IRedisClearHandle;

/***
 * 清理数据管理
 * @author Ben
 * @date 2016年1月1日下午2:25:00
 */
@Component
public class RedisClearManager implements ApplicationListener<ContextRefreshedEvent> {

	private static Map<Class<? extends IRedisClearHandle>, IRedisClearHandle> handles = new ConcurrentHashMap<Class<? extends IRedisClearHandle>, IRedisClearHandle>();

	@Autowired
	private ApplicationContext applicationContext;

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		Map<String, IRedisClearHandle> beans = applicationContext.getBeansOfType(IRedisClearHandle.class);
		for (IRedisClearHandle b : beans.values()) {
			register(b);
		}
	}

	public static void register(IRedisClearHandle handle) {
		handles.put(handle.getClass(), handle);
	}

}
