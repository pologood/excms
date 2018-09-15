package cn.lonsun.core.base.service;

import java.util.List;
import java.util.Map;

public interface IHistoryService<T> {


	/**
	 * 保存Entity对象t
	 * @param t
	 */
	public Long saveEntity(T t);



	/**
	 * 更新Entity对象t
	 * @param t
	 */
	public void updateEntity(T t);
	
	
	/**
	 * 根据主键获取Entity对象
	 * @param clazz
	 * @param id
	 */
	public T getEntity(Class<?> clazz,Long id);
	
	/**
	 * 根据params获取对象列表
	 * 
	 * @param clazz
	 * @param params
	 * @return
	 */
	public List<T> getEntities(final Class<T> clazz,final Map<String, Object> params);
	/**
	 * 根据params获取对象
	 * 
	 * @param clazz
	 * @param params
	 * @return
	 */
	public T getEntity(Class<T> clazz, Map<String, Object> params);

	/**
	 * 根据主键获取Entity对象集合
	 * @param clazz
	 * @param ids
	 */
	public List<T> getEntities(final Class<?> clazz,final Long[] ids);

}
