package cn.lonsun.core.base.service.impl;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import cn.lonsun.core.base.dao.IMockDao;
import cn.lonsun.core.base.entity.IMockEntity;
import cn.lonsun.core.base.service.IMockService;

/**
 * 业务逻辑层底层实现
 * @author 徐建华
 *
 * @param <T>
 */
@Service("mockService")
public class MockService<T extends IMockEntity> implements IMockService<T> {
	@Resource
	private IMockDao<T> mockDao;
	
	@Override
	@Deprecated
	public List<T> getEntities(final Class<?> clazz,final Long[] ids){
		return mockDao.getEntities(clazz, ids);
	}

	/**
	 * 根据主键获取Entity对象
	 * @param clazz
	 * @param id
	 */
	public T getEntity(Class<?> clazz,Long id){
		return mockDao.getEntity(clazz, id);
	}

	@Override
	public Long saveEntity(T t) {
		return mockDao.save(t);
	}
	
	@Override
	@Deprecated
	public List<T> getEntities(final Class<?> clazz,final List<Long> ids){
		return mockDao.getEntities(clazz, ids);
	}

	@Override
	public void saveEntities(Collection<T> ts) {
		mockDao.save(ts);		
	}

	@Override
	public void saveEntities(T[] ts) {
		mockDao.save(ts);		
	}

	@Override
	public void updateEntity(T t) {
		mockDao.update(t);		
	}

	@Override
	public void updateEntities(Collection<T> ts) {
		mockDao.update(ts);		
	}

	@Override
	public void updateEntities(T[] ts) {
		mockDao.update(ts);	
	}

	@Override
	public void saveOrUpdateEntity(T t) {
		mockDao.saveOrUpdate(t);		
	}

	@Override
	public void saveOrUpdateEntities(Collection<T> ts) {
		mockDao.saveOrUpdate(ts);		
	}

	@Override
	public void saveOrUpdateEntities(T[] ts) {
		mockDao.saveOrUpdate(ts);		
	}

	@Override
	public void delete(T t) {
		mockDao.delete(t);		
	}

	@Override
	public void delete(Collection<T> ts) {
		mockDao.delete(ts);		
	}

	@Override
	public void delete(T[] ts) {
		mockDao.delete(ts);		
	}

	@Override
	public void delete(Class<T> clazz, Long id) {
		mockDao.delete(clazz, id);		
	}

	@Override
	public void delete(Class<T> clazz, Collection<Long> ids) {
		mockDao.delete(clazz, ids);		
	}

	@Override
	public void delete(Class<T> clazz, Long[] ids) {
		mockDao.delete(clazz, ids);		
	}
	@Override
	public List<T> getEntities(Class<T> clazz, Map<String, Object> params) {
		return mockDao.getEntities(clazz, params);
	}
	
	@Override
	public T getEntity(Class<T> clazz, Map<String, Object> params) {
		return mockDao.getEntity(clazz, params);
	}
	

	public IMockDao<T> getMockDao() {
		return mockDao;
	}

	public void setMockDao(IMockDao<T> mockDao) {
		this.mockDao = mockDao;
	}

	
	
}
