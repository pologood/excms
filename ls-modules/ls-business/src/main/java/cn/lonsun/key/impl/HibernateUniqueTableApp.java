package cn.lonsun.key.impl;

import cn.lonsun.core.util.SpringContextHolder;
import cn.lonsun.key.CacheValue;
import cn.lonsun.key.UniqueException;
import cn.lonsun.key.UniqueTableApp;
import cn.lonsun.key.entity.PrimarykeyEO;
import cn.lonsun.key.service.IPrimarykeyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.Assert;

/**
 * @author gu.fei
 * @version 2016-07-21 11:27
 */
public class HibernateUniqueTableApp implements UniqueTableApp {
	private static final Logger logger = LoggerFactory.getLogger(HibernateUniqueTableApp.class);

	private long initCode = 0;

	IPrimarykeyService primarykeyService = SpringContextHolder.getBean("primarykeyService");

	public CacheValue getCacheValue(int cacheNum, String name,String idName) {
		Assert.isTrue(TransactionSynchronizationManager.isSynchronizationActive(), 
				"Transaction must be running");

		CacheValue cache = null;
		try {
			cache = getCurrCode(name);
			
			if(cache == null) {
				try{
					long maxVal = this.primarykeyService.getMaxKeyValue(name,idName);
					this.initCode = maxVal++;
				} catch (Exception e) {
					logger.error(name + " 没有查到主键【" + idName + "】的最大值");
					e.printStackTrace();
				}
				insert(name);
		        cache = getCurrCode(name);
			}
			
			update(cacheNum, name);
		    cache.setMaxVal(cache.getMinVal() + cacheNum);
		} catch(Exception e) {
			logger.error("获取主键失败", e);
		}
		return cache;
	}
	
	private CacheValue getCurrCode(String name) {
		CacheValue value = null;
		try {
			PrimarykeyEO eo = this.primarykeyService.getEntityByName(name);
			if(eo != null) {
				value = new CacheValue();
				value.setMinVal(eo.getCode() + 1);
			}
		} catch (EmptyResultDataAccessException e) {
			logger.debug(name + "没有找到记录");
		} catch (Exception e) {
			e.printStackTrace();
			throw new UniqueException(name + "获取主键失败");
		}
		return value;
	}
	
	private void insert(String name) {
		PrimarykeyEO eo = new PrimarykeyEO();
		eo.setName(name);
		eo.setCode(this.initCode);
		primarykeyService.saveAEntity(eo);
	}
	
	private void update(int cacheNum, String name) {
		PrimarykeyEO eo = this.primarykeyService.getEntityByName(name);
		eo.setCode(eo.getCode() + cacheNum);
		this.primarykeyService.updateEntity(eo);
	}
}