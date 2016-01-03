package spring.data.redis.model;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.connection.DataType;

import spring.data.redis.event.IRedisEvent;

/**
 * Redis数据访问对象
 * @author Ben
 * @date 2016年1月1日下午1:56:03
 */
@SuppressWarnings("unchecked")
public interface IRedisDao<Key, T extends IRedisEntity> {

	///////////////////////// 事务/////////////////
	// public T tx(Key key, TxCallBack<T> callBack);

	public <R> R lock(Key key, ILockCallBack<R> callBack);

	// /////////////////////CRUD/////////////////////
	public T findOne(Key key);

	public T findOneForCache(Key key);

	public T findOneAndThrow(Key key);

	
	public void saveOrUpdateSync(T... entitys);

	public void saveOrUpdate(T... entitys);

	public void remove(Key... keys);

	public void removeSync(Key... keys);

	public void clearCache(String... keys);

	public void clearAllCache();

	/////////////////////// key生命周期管理////////////////////////

	public boolean rename(Key oldKey, Key newKey);

	public boolean exists(Key key);

	public void expire(Key key, long timeOut, TimeUnit unit);

	public DataType type(Key key);

	// //////////////////////搜索////////////////////////////////

	public Set<String> keys(String pattern);

	public List<T> query(String pattern);

	public void cursor(String pattern, ICursorCallBack<T> cb);

	public void destroy();

	public void send(IRedisEvent msg, String... channels);

}
