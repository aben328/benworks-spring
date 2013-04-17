package benworks.spring.ioc.dao;

import benworks.spring.ioc.entities.Content;
import benworks.spring.ioc.services.SomeService;

public class ContentDaoImpl implements ContentDao {

	private SomeService service;

	@Override
	public void create(Content entity) {
	}

	@Override
	public void delete(String id) {
	}

	@Override
	public Content load(String id) {
		return null;
	}

	@Override
	public void update(Content entity) {
	}

	public void setService(SomeService service) {
		this.service = service;
	}

	private boolean flag = false;

	@Override
	public void showService() {
		if (flag)
			System.out.println(service);
		else
			System.out.println("Hello World !");
		flag = !flag;
	}

}
