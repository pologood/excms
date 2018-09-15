package cn.lonsun.mongodb.service;

import java.util.List;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import cn.lonsun.mongodb.dao.MongoDbTestDao;
import cn.lonsun.mongodb.entity.MongoDbTest;


@Service
public class MongoDbTestService {



	@Autowired
	private MongoDbTestDao mongoDbTestDao;

	//	private MongoOperations mongoTemplate; 

	//	public void setMongoTemplate(MongoTemplate mongoTemplate) {
	//		this.mongoTemplate = mongoTemplate;
	//	}
	/** 
	 *  
	 * @param user 
	 */  
	public void saveMongoDbTest(MongoDbTest user){  
		mongoDbTestDao.save(user);
	}  
	/** 
	 *  
	 * @param name 
	 * @return  
	 */  
	public MongoDbTest findMongoDbTest(String name){  

//		1、完全匹配
//		Pattern pattern = Pattern.compile("^王$", Pattern.CASE_INSENSITIVE);
//		2、右匹配
//		Pattern pattern = Pattern.compile("^.*王$", Pattern.CASE_INSENSITIVE);
//		3、左匹配
//		Pattern pattern = Pattern.compile("^王.*$", Pattern.CASE_INSENSITIVE);
//		4、模糊匹配
//		Pattern pattern = Pattern.compile("^.*王.*$", Pattern.CASE_INSENSITIVE);
		Pattern pattern = Pattern.compile("^.*"+name+".*$", Pattern.CASE_INSENSITIVE);
		List<MongoDbTest> list= mongoDbTestDao.queryList(new Query(Criteria.where("name").is(pattern)));

		return null;
	}  
	public void deleteMongoDbTest(String name){  

		this.mongoDbTestDao.delete(new Query(Criteria.where("name").is(name)));;
	}

}
