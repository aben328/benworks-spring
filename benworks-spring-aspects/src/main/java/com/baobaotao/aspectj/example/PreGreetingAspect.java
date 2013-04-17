package com.baobaotao.aspectj.example;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

/**
 * // ①通过该注解将PreGreetingAspect标识为一个切面
 * 
 * @author jy
 * 
 */
@Aspect
public class PreGreetingAspect {

	@Before("execution(* greetTo(..))")
	// ②定义切点和增强类型
	public void execute() {// ③增强的横切逻辑
		System.out.println("How are you");
	}

}
