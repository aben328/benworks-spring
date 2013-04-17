package benworks.spring.aop.config;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;

public class LogAspect {
	protected void log(JoinPoint jp) {
		Signature sign = jp.getSignature();
		System.out.println("[LogAspect] : " + sign.getName());
	}
}
