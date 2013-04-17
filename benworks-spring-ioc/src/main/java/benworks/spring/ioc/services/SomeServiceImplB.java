package benworks.spring.ioc.services;

import benworks.spring.ioc.dao.ContentDao;

public class SomeServiceImplB implements SomeService {

	private ContentDao contentDao;

	@Override
	public void doSomeThing() {
		System.out.println("SomeServiceImplB:");
		System.out.println("ContentDao is " + contentDao);
		contentDao.showService();
	}

	public void setContentDao(ContentDao contentDao) {
		this.contentDao = contentDao;
	}

}
