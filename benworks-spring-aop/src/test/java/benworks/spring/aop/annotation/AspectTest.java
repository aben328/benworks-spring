package benworks.spring.aop.annotation;

import javax.annotation.Resource;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import benworks.spring.aop.UserManagerInf;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "AspectTest-context.xml" })
public class AspectTest {

	@Resource
	private UserManagerInf manager;

	@Test
	public void test() {
		Assert.assertNotNull(manager);
		manager.create("username", "password");
		manager.delete("username");
		manager.changePassword("username", "old", "new");
	}
}
