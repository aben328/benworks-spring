package benworks.pring.bean.init;

import junit.framework.TestCase;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TestLifeCycle extends TestCase {
	private BeanFactory bf;

	protected void setUp() throws Exception {
		bf = new ClassPathXmlApplicationContext("applicationContext.xml");
	}

	public void testLifeCycle() throws Exception {
		HelloWorld hello = (HelloWorld) bf.getBean("helloWorld");
		assertEquals("hello world!", hello.getHello());
		hello.destroy();
	}
}
