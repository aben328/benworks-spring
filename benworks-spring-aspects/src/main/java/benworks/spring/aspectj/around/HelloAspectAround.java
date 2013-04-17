package benworks.spring.aspectj.around;

import java.util.logging.Level;
import java.util.logging.Logger;

public class HelloAspectAround {
	private Logger logger = Logger.getLogger("trace");

	public void sayHello() {
		logger.logp(Level.INFO, "HelloAspectAround", "sayHello", "Hello AspectJ!");

	}

	public static void main(String[] args) {
		HelloAspectAround ht = new HelloAspectAround();
		ht.sayHello();
	}

}
