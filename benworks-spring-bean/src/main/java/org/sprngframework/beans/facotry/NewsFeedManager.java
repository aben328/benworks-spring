package org.sprngframework.beans.facotry;

import org.springframework.beans.factory.ObjectFactory;

/**
 * @author Ben
 */
public class NewsFeedManager {

	private ObjectFactory factory;

	public void printNews() {
		// 这样Bean可以以编程的方式操控创建他们的beanFactory,当然我们可以将引用的BeanFacotry
		// 造型（cast)为已知的了类型来获得更多的功能，它的主要功能用于通过编程来取得BeanFacotry
		// 所管理的其它bean，虽然这个功能会很有用，但是一般来说应该尽量避免使用，因为这样代码与Spring
		// 进行了耦合，违反Ioc原则 。
		NewsFeed news = (NewsFeed) factory.getObject();
		System.out.println(news.getNews());
	}



	public ObjectFactory getFactory() {
		return factory;
	}

	public void setFactory(ObjectFactory factory) {
		this.factory = factory;
	}

}
