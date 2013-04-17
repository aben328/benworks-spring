package benworks.spring.ioc.dao;

import java.io.Serializable;

public interface BasicDao<T, PK extends Serializable> {

	void create(T entity);

	void delete(PK id);

	void update(T entity);

	T load(PK id);

}
