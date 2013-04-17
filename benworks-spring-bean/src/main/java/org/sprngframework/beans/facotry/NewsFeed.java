/**
 * 
 */
package org.sprngframework.beans.facotry;

/**
 * @author Ben
 */
public class NewsFeed {

	private String news;

	public String getNews() {
		return toString();
	}

	public void setNews(String news) {
		this.news = news;
	}

	@Override
	public String toString() {
		return "NewsFeed [news=" + news + "]";
	}

}
