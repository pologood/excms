package cn.lonsun.mongodb.dao;

import org.springframework.stereotype.Repository;

import cn.lonsun.mongodb.base.impl.MongoDbBaseDao;
import cn.lonsun.mongodb.entity.MongoDbTest;

@Repository
public class MongoDbTestDao extends MongoDbBaseDao<MongoDbTest>{

	@Override
	protected Class<MongoDbTest> getEntityClass() {
		return MongoDbTest.class;
	}

}
