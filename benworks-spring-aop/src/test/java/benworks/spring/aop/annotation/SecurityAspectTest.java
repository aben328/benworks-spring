package benworks.spring.aop.annotation;

import javax.annotation.Resource;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import benworks.spring.aop.UserManagerInf;
import benworks.spring.aop.proxy.Session;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "SecurityAspectTest-context.xml" })
public class SecurityAspectTest {

	@Resource
	private UserManagerInf manager;

	@Test
	public void test() {
		Assert.assertNotNull(manager);
		Session.isAdmin = true;
		manager.create("username", "password");
		manager.delete("username");
		manager.changePassword("username", "old", "new");
	}

	@Test(expected = SecurityException.class)
	public void testEx() {
		Assert.assertNotNull(manager);
		Session.isAdmin = false;
		manager.create("username", "password");
		// manager.delete("username");
		// manager.changePassword("username", "old", "new");
	}
}
