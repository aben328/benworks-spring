package benworks.spring.core.io;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.ResourceLoader;

public class ResoureLoaderAwareTest {
	@Test
	public void test() {
		ApplicationContext ctx = new ClassPathXmlApplicationContext("resourceLoaderAware.xml");
		ResourceBean resourceBean = ctx.getBean(ResourceBean.class);
		ResourceLoader loader = resourceBean.getResourceLoader();
		Assert.assertTrue(loader instanceof ApplicationContext);
	}

}
