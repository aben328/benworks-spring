package benworks.spring.ioc;

import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * 如果你正在一个非Web应用的环境下使用Spring的Ioc容器，例如在桌面富客户端环境下，你想让容器优雅的关闭，
 * 并调用singleton bean上的相应析构回调方法，你需要在JVM里注册 一个“关闭钩子”（shutdown hook)
 * 这一点非常容易做到，并且将会确保你的Spring Ioc容器被恰当的关闭，以及所有由单例持有的资源都会被释放。
 * 
 * @author Ben
 *
 */
public final class Boot {

	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		AbstractApplicationContext ctx = new ClassPathXmlApplicationContext(new String[] { "bean.xml" });

		// add shutdown hook for above context;
		ctx.registerShutdownHook();

		//app runs here ...
		
		
	}

}
