package spring.data.redis.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import spring.data.redis.event.RedisEventCommon;

public class TestMyRedisEvent extends RedisEventCommon {
	private Map<String, String> map = new HashMap<String, String>();
	private List<Integer> list = new ArrayList<Integer>();
	private Date date = new Date();
	private Double d = 0.6d;

	private Long l = 26666L;

	public TestMyRedisEvent() {
		for (int i = 0; i < 50; i++) {
			map.put(i + "", i + "");
			list.add(i);
		}
	}

	public void println() {
		for (int i : list) {
			System.out.println("list : " + i);
		}
		for (Entry<String, String> entry : map.entrySet()) {
			System.out.println("map : " + entry.getValue());
		}
		System.out.println("date : " + this.date);
		System.out.println("double : " + this.d);
	}

	// set get

	public Map<String, String> getMap() {
		return map;
	}

	public void setMap(Map<String, String> map) {
		this.map = map;
	}

	public List<Integer> getList() {
		return list;
	}

	public void setList(List<Integer> list) {
		this.list = list;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Double getD() {
		return d;
	}

	public void setD(Double d) {
		this.d = d;
	}

	public Long getL() {
		return l;
	}

	public void setL(Long l) {
		this.l = l;
	}

}