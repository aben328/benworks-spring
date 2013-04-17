package benworks.spring.aop.annotation;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import benworks.spring.aop.UserManagerInf;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "AnnoAspectTest-context.xml" })
public class AnnoAspectTest {

	@Autowired
	private UserManagerInf userManager;

	@Test
	public void test() {
		userManager.create("benworks", "123456");
		userManager.changePassword("benworks", "123456", "password");
	}

}
