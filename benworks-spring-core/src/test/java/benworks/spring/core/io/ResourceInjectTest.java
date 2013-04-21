package benworks.spring.core.io;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.ClassPathResource;

public class ResourceInjectTest {
	@Test
	public void test() {
		ApplicationContext ctx = new ClassPathXmlApplicationContext("/resourceInject.xml");
		ResourceBean2 resourceBean1 = ctx.getBean("resourceBean1", ResourceBean2.class);
		ResourceBean2 resourceBean2 = ctx.getBean("resourceBean2", ResourceBean2.class);
		Assert.assertTrue(resourceBean1.getResource() instanceof ClassPathResource);
		Assert.assertTrue(resourceBean2.getResource() instanceof ClassPathResource);
	}

}
