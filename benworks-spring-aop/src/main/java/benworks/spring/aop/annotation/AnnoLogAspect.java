package benworks.spring.aop.annotation;

import java.lang.reflect.Method;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import benworks.spring.aop.LogIt;

@Aspect
public class AnnoLogAspect {

	@Pointcut("@annotation(package benworks.spring.aop.LogIt)")
	protected void logMethods() {
	};

	@After("logMethods()")
	protected void log(JoinPoint jp) throws NoSuchMethodException,
			SecurityException {
		Signature sign = jp.getSignature();
		Object target = jp.getTarget();
		Class<?> clz = target.getClass();
		Method method = clz.getMethod(sign.getName(), toType(jp.getArgs()));
		LogIt anno = method.getAnnotation(LogIt.class);
		System.out.println("拦截命中了：" + anno.value());
	}

	private Class<?>[] toType(Object[] args) {
		Class<?>[] result = new Class<?>[args.length];
		for (int i = 0; i < args.length; i++) {
			result[i] = args[i].getClass();
		}
		return result;
	}

}
