package benworks.spring.context.event;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ListenerEventDemo {
	/** * @param args */
	public static void main(String[] args) {
		ApplicationContext context = new ClassPathXmlApplicationContext(
				"SpringEvent.xml");
		EmailListEvent emailListEvent = new EmailListEvent("hello",
				"helloSpring@sina.com", "this is a test eamil content");
		// 在ApplicationContext中发布一个 ApplicationEvent
		context.publishEvent(emailListEvent);
	}
}