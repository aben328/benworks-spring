/**
 * 
 */
package benworks.spring.ioc.dao;

import benworks.spring.ioc.entities.Content;

/**
 * @author Ben
 *
 */
public interface ContentDao extends BasicDao<Content, String> {

	void showService();
}
