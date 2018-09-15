package cn.lonsun.core.base.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import cn.lonsun.core.base.dao.IHistoryDao;
import cn.lonsun.core.base.service.IHistoryService;

/**
 * 业务逻辑层底层实现
 * @author 徐建华
 *
 * @param <T>
 */
@Service("historyService")
public class HistoryService<T> implements IHistoryService<T> {
	@Resource
	private IHistoryDao<T> historyDao;


	@Override
	public Long saveEntity(T t) {
		return historyDao.save(t);
	}


	@Override
	public void updateEntity(T t) {
		historyDao.update(t);
	}

	/**
	 * 根据主键获取Entity对象
	 * @param clazz
	 * @param id
	 */
	public T getEntity(Class<?> clazz,Long id){
		return historyDao.getEntity(clazz, id);
	}


	@Override
	@Deprecated
	public List<T> getEntities(final Class<?> clazz,final Long[] ids){
		return historyDao.getEntities(clazz, ids);
	}

	@Override
	public List<T> getEntities(Class<T> clazz, Map<String, Object> params) {
		return historyDao.getEntities(clazz, params);
	}
	
	@Override
	public T getEntity(Class<T> clazz, Map<String, Object> params) {
		return historyDao.getEntity(clazz, params);
	}
}
