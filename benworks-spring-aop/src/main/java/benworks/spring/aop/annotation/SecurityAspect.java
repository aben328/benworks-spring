package benworks.spring.aop.annotation;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

import benworks.spring.aop.proxy.Session;


@Aspect
public class SecurityAspect {

	@Pointcut("execution(* benworks.spring.aop.UserManagerInf.*(..))")
	public void securityMethods() {
	}

	@Before("securityMethods()")
	public void check(JoinPoint jp) {
		if (!Session.isAdmin)
			throw new SecurityException("安全异常！");
	}

}
