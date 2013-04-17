package benworks.spring.ioc.di;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import benworks.spring.ioc.services.SomeService;

/**
 * 以 ApplicationContext 方式使用 Spring 的范例
 * @author Ben
 */
public class ApplicationOfContext {

	public static void main(String[] args) {
		// 按文件系统路径加载
		// ApplicationContext context = new
		// FileSystemXmlApplicationContext("src/frank/spring/ioc/di/beans.xml");
		// 按类路径加载
		ApplicationContext context = new ClassPathXmlApplicationContext("frank/spring/ioc/di/beans.xml");
		SomeService service = (SomeService) context.getBean("someService");
		service.doSomeThing();
	}
}
