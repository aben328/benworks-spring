package com.baobaotao;

/**
 * NaiveWaiter是一个Bean，它拥有一个greetTo()的方法，这个方法连接点匹配于上面 我们通过@AspectJ所定义的切点
 * 
 * @author jy
 * 
 */
public class NaiveWaiter implements Waiter {

	public void greetTo(String clientName) {
		System.out.println("NaiveWaiter:greet to " + clientName + "...");
	}

	public void serveTo(String clientName) {
		System.out.println("NaiveWaiter:serving " + clientName + "...");
	}

}
