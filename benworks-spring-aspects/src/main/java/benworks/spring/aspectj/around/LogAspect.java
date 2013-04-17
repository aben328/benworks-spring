package benworks.spring.aspectj.around;

import java.util.logging.Logger;

public class LogAspect {
	private Logger logger = Logger.getLogger("trace");

	// public pointcut write(): execution(* *.*(..)) && !within(LogAspect) &&
	// !execution(public static * *.*(..));
	//
	// void around():write(){
	//
	// Signature sig = thisJoinPointStaticPart.getSignature();
	//
	// logger.logp(Level.INFO, sig.getDeclaringType().getName(),
	//
	// sig.getName(), "processing");
	//
	// }
	//
}
