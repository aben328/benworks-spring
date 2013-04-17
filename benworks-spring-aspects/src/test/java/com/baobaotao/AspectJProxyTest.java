package com.baobaotao;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.baobaotao.aspectj.example.PreGreetingAspect;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@Component
public class AspectJProxyTest {

	@Test
	public void test() {
		Waiter target = new NaiveWaiter();
		AspectJProxyFactory factory = new AspectJProxyFactory();
		factory.setTarget(target); // ① 设置目标对象
		factory.addAspect(PreGreetingAspect.class); // ②添加切面类
		Waiter proxy = factory.getProxy(); // ③ 生成织入切面的代理对象
		proxy.greetTo("John");
		proxy.serveTo("John");
	}

}
