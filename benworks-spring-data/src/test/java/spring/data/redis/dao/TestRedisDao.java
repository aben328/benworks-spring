package spring.data.redis.dao;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.stereotype.Component;

import spring.data.redis.anno.StoreStrategy;
import spring.data.redis.model.Item;
import spring.data.redis.server.RedisDao;

@Component
@StoreStrategy(dataSource = "play1")
public class TestRedisDao extends RedisDao<Item> {

	@PreDestroy
	void init() {
		System.out.println(" == PreDestroy =");
	}

	@PostConstruct
	void PostConstruct() {
		System.out.println(" == PostConstruct =");
	}
}
