package benworks.spring.ioc.di;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import benworks.spring.ioc.services.SomeService;

/**
 * 以 BeanFactory 方式使用 Spring 的范例
 * @author Ben
 */
public class ApplicationOfBeanFactory {

	public static void main(String[] args) {
		// 按文件系统路径加载
		// Resource res = new
		// FileSystemResource("src/frank/spring/ioc/di/beans.xml");
		// 按类路径加载
		Resource res = new ClassPathResource("frank/spring/ioc/di/beans.xml");
		BeanFactory factory = new XmlBeanFactory(res);
		SomeService service = (SomeService) factory.getBean("someService");
		System.out.println(service);
		System.out.println("----------华丽的分割线---------");
		service.doSomeThing();
	}

}
