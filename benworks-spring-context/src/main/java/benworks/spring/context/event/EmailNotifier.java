/**
 * 
 */
package benworks.spring.context.event;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

/**
 * @author Ben
 * 
 */
public class EmailNotifier implements ApplicationListener<ApplicationEvent> {

	public void onApplicationEvent(ApplicationEvent evt) {
		if (evt instanceof EmailListEvent) {
			EmailListEvent emailEvent = (EmailListEvent) evt;
			emailEvent.print();
			System.out.println("the source is:" + emailEvent.getSource());
			System.out.println("the address is:" + emailEvent.address);
			System.out.println("the mail's context is :" + emailEvent.text);
		}
	}

}
