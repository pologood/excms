package cn.lonsun.core.base.service.impl;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import cn.lonsun.core.base.dao.IBaseDao;
import cn.lonsun.core.base.entity.IBaseEntity;
import cn.lonsun.core.base.service.IBaseService;

/**
 * 业务逻辑层底层实现
 * @author 徐建华
 *
 * @param <T>
 */
@Service("baseService")
public class BaseService<T extends IBaseEntity> implements IBaseService<T> {
	
	@Resource
	private IBaseDao<T> baseDao;
	
	public void setBaseDao(IBaseDao<T> baseDao) {
		this.baseDao = baseDao;
	}
	
	public IBaseDao<T> getBaseDao() {
		return baseDao;
	}



	/**
	 * 根据主键获取Entity对象
	 * @param clazz
	 * @param id
	 */
	public T getEntity(Class<?> clazz,Long id){
		return baseDao.getEntity(clazz, id);
	}
	
	@Override
	public List<T> getEntities(final Class<?> clazz,final List<Long> ids){
		return baseDao.getEntities(clazz, ids);
	}
	
	@Override
	public List<T> getEntities(final Class<?> clazz,final Long[] ids){
		return baseDao.getEntities(clazz, ids);
	}
	@Override
	public List<T> getEntities(Class<T> clazz, Map<String, Object> params) {
		return baseDao.getEntities(clazz, params);
	}
	
	@Override
	public T getEntity(Class<T> clazz, Map<String, Object> params) {
		return baseDao.getEntity(clazz, params);
	}

	@Override
	public Long saveEntity(T t) {
		return baseDao.save(t);
	}

	@Override
	public void saveEntities(Collection<T> ts) {
		baseDao.save(ts);		
	}

	@Override
	public void saveEntities(T[] ts) {
		baseDao.save(ts);		
	}

	@Override
	public void updateEntity(T t) {
		baseDao.update(t);		
	}

	@Override
	public void updateEntities(Collection<T> ts) {
		baseDao.update(ts);		
	}

	@Override
	public void updateEntities(T[] ts) {
		baseDao.update(ts);	
	}

	@Override
	public void saveOrUpdateEntity(T t) {
		baseDao.saveOrUpdate(t);		
	}

	@Override
	public void saveOrUpdateEntities(Collection<T> ts) {
		baseDao.saveOrUpdate(ts);		
	}

	@Override
	public void saveOrUpdateEntities(T[] ts) {
		baseDao.saveOrUpdate(ts);		
	}

	@Override
	public void delete(T t) {
		baseDao.delete(t);		
	}

	@Override
	public void delete(Collection<T> ts) {
		baseDao.delete(ts);		
	}

	@Override
	public void delete(T[] ts) {
		baseDao.delete(ts);		
	}

	@Override
	public void delete(Class<T> clazz, Long id) {
		baseDao.delete(clazz, id);		
	}

	@Override
	public void delete(Class<T> clazz, Collection<Long> ids) {
		baseDao.delete(clazz, ids);		
	}

	@Override
	public void delete(Class<T> clazz, Long[] ids) {
		baseDao.delete(clazz, ids);		
	}

}
