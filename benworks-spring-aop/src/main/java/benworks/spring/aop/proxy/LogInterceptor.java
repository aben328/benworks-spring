package benworks.spring.aop.proxy;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

public class LogInterceptor implements MethodInterceptor {

	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		String name = invocation.getMethod().getName();
		System.out.println("[Before] : " + name);

		Object ret = invocation.proceed();

		System.out.println("[After] : " + name);
		return ret;
	}

}
