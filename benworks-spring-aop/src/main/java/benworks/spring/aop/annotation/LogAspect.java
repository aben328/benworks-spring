package benworks.spring.aop.annotation;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

/**
 * 定义 Log 切面(Aspect)类<br/>
 * 切面（Aspect）：一个关注点的模块化，这个关注点可能会横切多个对象。
 * 
 * @author Frank Yang
 */
@Aspect
public class LogAspect {

	/**
	 * 定义 切入点(Pointcut)<br/>
	 * 该切入点的名称为 "logMethods"，该方法不需要参数、返回值与实现，它仅作为切入点的签名存在。 具体的切入点指示符声明语法，可参见
	 * Spring 参考手册的 6.2.3
	 * 
	 * @Ben 这种定义@Point的方式是旧的，新版本不需要定义，直接写在通知处
	 */
	@Pointcut("execution(*package benworks.spring.aop.UserManagerInf.*(..))")
	protected void logMethods() {
	}

	/**
	 * 定义 通知(Advice)<br/>
	 * 通知（Advice）：在切面的某个特定的连接点上执行的动作。其中包括了“around”、“before”和“after”等不同类型的通知。<br/>
	 * 切入点表达式可能是指向已命名的切入点的简单引用或者是一个已经声明过的切入点表达式。
	 * 
	 * @Before("execution(* frank.spring.UserManagerInf.*(..))")
	 * @param jp
	 * @throws Throwable
	 */
	@Before("logMethods()")
	protected void log(JoinPoint jp) throws Throwable {
		/*
		 * 任何通知方法可以将第一个参数定义为org.aspectj.lang.JoinPoint类型
		 * （环绕通知需要定义第一个参数为ProceedingJoinPoint类型， 它是JoinPoint 的一个子类）。 JoinPoint
		 * 接口提供了一系列有用的方法，比如: getArgs()（返回方法参数）、 getThis()（返回代理对象）、
		 * getTarget()（返回目标）、 getSignature()（返回正在被通知的方法相关信息）
		 * toString()（打印出正在被通知的方法的有用信息）。
		 */
		Signature sign = jp.getSignature();
		System.out.println("[LogAspect] : " + sign.getName());
	}
}
