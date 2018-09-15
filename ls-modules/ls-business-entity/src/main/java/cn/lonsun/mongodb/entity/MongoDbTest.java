package cn.lonsun.mongodb.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Id;

public class MongoDbTest implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id 
	private String id;
	
	private String name;
	
	private Date time;


	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}
	

}
