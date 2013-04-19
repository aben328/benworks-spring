/**
 * 
 */
package benworks.spring.context.support;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.MessageSource;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author Ben
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class MessageSourceTest {

	@Test
	public void test_object() {
		MessageSource resouces = new ClassPathXmlApplicationContext("MessageSourceTest-context.xml");
		String message = resouces.getMessage("message", null, "Default", null);
		System.out.println(message);
	}

}
