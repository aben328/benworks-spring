package benworks.spring.aop.proxy;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

public class SecurityInterceptor implements MethodInterceptor {

	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		if (Session.isAdmin) {
			return invocation.proceed();
		}
		throw new SecurityException("不给你调用！");
	}

}
