package org.sprngframework.beans.facotry;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class NewFeedTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
		NewsFeedManager manager = (NewsFeedManager) ctx.getBean("newsFeedManager");
		manager.printNews();
		manager.printNews();
	}

}
