package benworks.spring.ioc.services;

import benworks.spring.ioc.dao.UserDao;

public class SomeServiceImplA implements SomeService {

	private UserDao userDao;
	
	@Override
	public void doSomeThing() {
		System.out.println("SomeServiceImplA:");
		System.out.println("UserDao is " + userDao);
	}

	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}
}
