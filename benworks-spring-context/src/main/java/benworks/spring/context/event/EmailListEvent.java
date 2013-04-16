/**
 * 
 */
package benworks.spring.context.event;

import org.springframework.context.ApplicationEvent;

/**
 * 首先创建一个Event事件类：
 * 
 * @author Ben
 * 
 */
public class EmailListEvent extends ApplicationEvent {

	private static final long serialVersionUID = -7023691106373856124L;
	public String address;
	public String text;

    public EmailListEvent(Object source, String address, String text) {
        super(source);
        this.address = address;
        this.text = text;
    }

    public void print() {
        System.out.println("Hello,Spring Event!!!");
    }
    
	public EmailListEvent(Object source) {
		super(source);
	}
}
