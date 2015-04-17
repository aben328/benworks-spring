package benworks.spring.bean.lifecycle;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@Component
public class BeanLifeCycleTest implements ApplicationContextAware {

	@Test
	public void LifeCycleInBeanFactory() {
		// ①下面两句装载配置文件并启动容器
		Resource res = new ClassPathResource("benworks/spring/bean/lifecycle/BeanLifeCycleTest-context.xml");
		BeanFactory bf = new XmlBeanFactory(res);

		// ②向容器中注册MyBeanPostProcessor后处理器
		((ConfigurableBeanFactory) bf).addBeanPostProcessor(new MyBeanPostProcessor());

		// ③向容器中注册MyInstantiationAwareBeanPostProcessor后处理器
		((ConfigurableBeanFactory) bf).addBeanPostProcessor(new MyInstantiationAwareBeanPostProcessor());
		// ④第一次从容器中获取car，将触发容器实例化该Bean，这将引发Bean生命周期方法的调用。
		Car car1 = (Car) bf.getBean("car");
		car1.introduce();
		car1.setColor("红色");

		// ⑤第二次从容器中获取car，直接从缓存池中获取
		Car car2 = (Car) bf.getBean("car");

		// ⑥查看car1和car2是否指向同一引用
		System.out.println("car1==car2:" + (car1 == car2));
		// ⑦关闭容器
		((XmlBeanFactory) bf).destroySingletons();
	}

	// 实现接口的方法
	private ApplicationContext applicationContext;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
}
