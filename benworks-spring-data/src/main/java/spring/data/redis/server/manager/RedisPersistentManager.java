package spring.data.redis.server.manager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;

import spring.data.redis.model.DelayedElement;
import spring.data.redis.model.IRedisElementAction;
import spring.data.redis.server.RedisDao;

/**
 * 持久化管理
 * @author Ben
 * @date 2016年1月1日下午2:11:39
 */
@Component
public class RedisPersistentManager implements ApplicationListener<ApplicationEvent> {

	private static Logger logger = LoggerFactory.getLogger(RedisPersistentManager.class);

	private static volatile boolean PUT_FLAG = true;

	private final static BlockingQueue<DelayedElement<? extends IRedisElementAction>> delayQueue = new DelayQueue<DelayedElement<? extends IRedisElementAction>>();
	private final static BlockingQueue<IRedisElementAction> blockQueue = new LinkedBlockingQueue<IRedisElementAction>();
	private final static Map<String, DelayedElement<? extends IRedisElementAction>> records = new ConcurrentHashMap<String, DelayedElement<? extends IRedisElementAction>>();
	private final static ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();
	private static Map<String, RedisDao<?>> daos = new ConcurrentHashMap<String, RedisDao<?>>();

	private static volatile boolean CAN_RUN_BAT = true;

	static {
		Thread delayThread = new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					try {
						DelayedElement<? extends IRedisElementAction> action = delayQueue.take();
						action.getContent().exec();
						if (logger.isWarnEnabled()) {
							logger.warn("RedisPersistenting... {}", action.getContent().getId());
						}
					} catch (InterruptedException e) {
						logger.error("RedisPersistent Interrupted error : {}", e);
					} catch (Exception e) {
						logger.error("RedisPersistent  error : {}", e);
					}
				}
			}
		}, "RedisPersistent delayQueue ");

		delayThread.setDaemon(true);
		delayThread.start();

		Thread blockThread = new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					try {
						IRedisElementAction action = blockQueue.take();
						action.exec();

						if (logger.isWarnEnabled()) {
							logger.warn("RedisPersistenting... {}", action.getId());
						}
					} catch (InterruptedException e) {
						logger.error("RedisPersistent Interrupted error : {}", e);
					} catch (Exception e) {
						logger.error("RedisPersistent  error : {}", e);
					}
				}
			}
		}, "RedisPersistent blockQueue ");

		blockThread.setDaemon(true);
		blockThread.start();

	}

	public static void register(String owner, RedisDao<?> dao) {
		daos.put(owner, dao);
	}

	public static void putAction(IRedisElementAction element, int delay) {
		if (!PUT_FLAG) {
			if (logger.isWarnEnabled()) {
				logger.warn("RedisPersistent thread is closeing ");
			}
			return;
		}
		if (delay > 0) {
			delayQueue.add(DelayedElement.of(element, delay));
		} else {
			blockQueue.add(element);
		}
	}

	// //////////////////////////批处理////////////////////////////////////
	public static void putDelayActionForBat(DelayedElement<? extends IRedisElementAction> action) {
		if (!PUT_FLAG) {
			if (logger.isWarnEnabled()) {
				logger.warn("RedisPersistent thread is closeing ");
			}
			return;
		}
		rwLock.writeLock().lock();
		try {
			final String key = action.getContent().getId();
			// 统一重试处理逻辑 重试数据一律当为坑数据处理，忽略
			if (action.getContent().isRetry() && records.containsKey(key)) {
				return;
			}
			records.put(key, action);

			if (CAN_RUN_BAT) {
				CAN_RUN_BAT = false;
				buildBatAction();
			}
		} finally {
			rwLock.writeLock().unlock();
		}
	}

	static void runBatAction() {
		rwLock.writeLock().lock();
		try {
			if (records.isEmpty()) {
				return;
			}
			Collection<DelayedElement<? extends IRedisElementAction>> actions = new ArrayList<DelayedElement<? extends IRedisElementAction>>(
					records.values());
			records.clear();
			delayQueue.addAll(actions);
		} finally {
			CAN_RUN_BAT = true;
			rwLock.writeLock().unlock();
		}
	}

	static void buildBatAction() {
		delayQueue.add(DelayedElement.of(new IRedisElementAction() {

			@Override
			public String getId() {
				return "BatAction";
			}

			@Override
			public void exec() {
				runBatAction();
			}

			@Override
			public boolean isRetry() {
				return false;
			}
		}, 300));
	}

	void preClosedPersistent() {
		logger.error("RedisPersistent");
		PUT_FLAG = false;
		while (true) {
			if (!delayQueue.isEmpty()) {
				Thread.yield();
			}
			if (!blockQueue.isEmpty()) {
				Thread.yield();
			}
			break;
		}
		// 等侍socket 回写
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@PreDestroy
	void preDestroy() {
		for (RedisDao<?> dao : daos.values()) {
			dao.destroy();
		}
	}

	@Override
	public void onApplicationEvent(ApplicationEvent event) {
		if (event instanceof ContextClosedEvent) {
			preClosedPersistent();
			return;
		}
	}
}
