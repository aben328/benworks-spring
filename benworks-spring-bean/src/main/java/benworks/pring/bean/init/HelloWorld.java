/**
 * 
 */
package benworks.pring.bean.init;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanPostProcessor;

/**
 * @author Ben
 * 
 */
public class HelloWorld implements BeanNameAware, BeanFactoryAware,
		BeanPostProcessor, InitializingBean, DisposableBean {

	private String hello;

	public void setBeanName(String arg0) {
		System.out.println("调用BeanNameAware的setBeanName()...");
	}

	public String getHello() {
		return hello;
	}

	public void setHello(String hello) {
		this.hello = hello;
		System.out.println("调用setHello()...");
	}

	public void customInit() {
		System.out.println("调用customInit()...");
	}

	public void customDestroy() {
		System.out.println("调用customDestroy()...");
	}

	public Object postProcessAfterInitialization(Object arg0, String arg1)
			throws BeansException {
		System.out
				.println("调用BeanPostProcessor的postProcessAfterInitialization()...");
		return null;
	}

	public Object postProcessBeforeInitialization(Object arg0, String arg1)
			throws BeansException {
		System.out
				.println("调用BeanPostProcessor的postProcessBeforeInitialization()...");
		return null;
	}

	public void destroy() throws Exception {
		System.out.println("调用DisposableBean的destory()...");
	}

	public void afterPropertiesSet() throws Exception {
		System.out.println("调用InitializingBean的afterPropertiesSet()...");
	}

	public void setBeanFactory(BeanFactory arg0) throws BeansException {
		System.out.println("调用BeanFactoryAware的setBeanFactory()...");
	}

}
