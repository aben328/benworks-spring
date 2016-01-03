package spring.data.redis;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import spring.data.redis.dao.TestRedisDao;
import spring.data.redis.model.Item;

/**
 * @author Ben
 * @date 2016年1月1日下午2:34:55
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@Configuration
public class RedisTest {

	@Autowired
	private TestRedisDao testRedisDao;

	@Autowired
	private GenericApplicationContext applicationContext;

	@Test
	public void testStart() throws InterruptedException {
		String key = "test1";
		System.out.println(testRedisDao.findOne(key) == null);
		testRedisDao.saveOrUpdate(Item.of(key, 1));
		applicationContext.close();
	}
}