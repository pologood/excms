package cn.lonsun.core.base.service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import cn.lonsun.core.base.entity.IMockEntity;

public interface IMockService<T extends IMockEntity> {


    public List<T> getEntities(final Class<?> clazz, final Long[] ids);

    /**
     * 根据主键获取Entity对象
     *
     * @param clazz
     * @param id
     */
    public T getEntity(Class<?> clazz, Long id);

    /**
     * 根据主键获取Entity对象集合
     *
     * @param clazz
     * @param ids
     */
    @Deprecated
    public List<T> getEntities(final Class<?> clazz, final List<Long> ids);

    /**
     * 根据params获取对象列表
     *
     * @param clazz
     * @param params
     * @return
     */
    public List<T> getEntities(final Class<T> clazz, final Map<String, Object> params);

    /**
     * 根据params获取对象
     *
     * @param clazz
     * @param params
     * @return
     */
    public T getEntity(Class<T> clazz, Map<String, Object> params);

    /**
     * 保存Entity对象t
     *
     * @param t
     */
    public Long saveEntity(T t);

    /**
     * 保存Entity对象集合ts
     *
     * @param ts
     */
    public void saveEntities(Collection<T> ts);

    /**
     * 保存Entity对象数组ts
     *
     * @param ts
     */
    public void saveEntities(T[] ts);

    /**
     * 更新Entity对象t
     *
     * @param t
     */
    public void updateEntity(T t);

    /**
     * 更新Entity对象集合ts
     *
     * @param ts
     */
    public void updateEntities(Collection<T> ts);

    /**
     * 更新Entity对象数组ts
     *
     * @param ts
     */
    public void updateEntities(T[] ts);

    /**
     * 保存或更新对象t
     *
     * @param t
     */
    public void saveOrUpdateEntity(T t);

    /**
     * 保存或更新Entity对象集合ts
     *
     * @param ts
     */
    public void saveOrUpdateEntities(Collection<T> ts);

    /**
     * 保存或更新Entity对象数组ts
     *
     * @param ts
     */
    public void saveOrUpdateEntities(T[] ts);

    /**
     * 删除对象t
     *
     * @param t
     */
    public void delete(T t);

    /**
     * 删除对象集合ts
     *
     * @param t
     */
    public void delete(Collection<T> ts);

    /**
     * 删除对象数组ts
     *
     * @param t
     */
    public void delete(T[] ts);

    /**
     * 删除主键为id的对象
     *
     * @param clazz 被删除对象的具体类型
     * @param id
     */
    public void delete(Class<T> clazz, Long id);

    /**
     * 删除主键在ids中的对象
     *
     * @param clazz 被删除对象的具体类型
     * @param ids
     */
    public void delete(Class<T> clazz, Collection<Long> ids);

    /**
     * 删除主键在ids中的对象
     *
     * @param clazz 被删除对象的具体类型
     * @param ids
     */
    public void delete(Class<T> clazz, Long[] ids);

}
