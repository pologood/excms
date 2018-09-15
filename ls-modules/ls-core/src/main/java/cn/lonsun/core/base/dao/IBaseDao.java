package cn.lonsun.core.base.dao;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import cn.lonsun.core.base.entity.IMockEntity;
import cn.lonsun.core.util.Pagination;

import org.hibernate.SessionFactory;

/**
 * 数据库访问层接口
 *
 * @param <T>
 * @author 徐建华
 */
public interface IBaseDao<T> {

    public SessionFactory getSessionFactory();

    /**
     * 保存Entity对象t
     *
     * @param t
     */
    public Long save(final T t);

    /**
     * 保存Entity对象集合ts
     *
     * @param ts
     */
    public void save(final Collection<T> ts);

    /**
     * 保存Entity对象数组ts
     *
     * @param ts
     */
    public void save(final T[] ts);

    /**
     * 执行hql
     *
     * @param hql
     * @param values
     * @return
     */
    public int executeUpdateByHql(String hql, Object[] values);

    /**
     * 执行sql
     *
     * @param sql
     * @param values
     * @return
     */
    public int executeUpdateBySql(String sql, Object[] values);


    /**
     * 更新Entity对象t
     *
     * @param t
     */
    public void update(final T t);

    /**
     * 更新对象的属性
     *
     * @param clazz
     * @param items      条件参数,key:属性名，value:属性值
     * @param properties 待修改的参数，key:属性名，value:属性值
     */
    public void updateProperties(Class<IMockEntity> clazz,
                                 Map<String, Object> items, Map<String, Object> properties);

    /**
     * 更新Entity对象集合ts
     *
     * @param ts
     */
    public void update(final Collection<T> ts);

    /**
     * 更新Entity对象数组ts
     *
     * @param ts
     */
    public void update(final T[] ts);

    /**
     * 保存或更新对象t
     *
     * @param t
     */
    public void saveOrUpdate(final T t);

    /**
     * 保存或更新Entity对象集合ts
     *
     * @param ts
     */
    public void saveOrUpdate(final Collection<T> ts);

    /**
     * 保存或更新Entity对象数组ts
     *
     * @param ts
     */
    public void saveOrUpdate(final T[] ts);

    /**
     * 使用hql更新
     *
     * @param ts
     */
    public int saveOrUpdate(final String hql, final Map<String, Object> params);


    /**
     * 删除对象t
     *
     * @param t
     */
    public void delete(final T t);

    /**
     * 删除对象集合ts
     *
     * @param t
     */
    public void delete(final Collection<T> ts);

    /**
     * 删除对象数组ts
     *
     * @param t
     */
    public void delete(final T[] ts);

    /**
     * 删除主键为id的对象
     *
     * @param clazz 被删除对象的具体类型
     * @param id
     */
    public void delete(final Class<T> clazz, final Long id);

    /**
     * 删除主键在ids中的对象
     *
     * @param clazz 被删除对象的具体类型
     * @param ids
     */
    public void delete(final Class<T> clazz, final Collection<Long> ids);

    /**
     * 删除主键在ids中的对象
     *
     * @param clazz 被删除对象的具体类型
     * @param ids
     */
    public void delete(final Class<T> clazz, final Long[] ids);


    /**
     * 根据主键获取Entity对象
     *
     * @param clazz
     * @param id
     */
    public T getEntity(final Class<?> clazz, final Long id);

    /**
     * 根据主键获取Entity对象集合
     *
     * @param clazz
     * @param ids
     */
    public List<T> getEntities(final Class<?> clazz, final List<Long> ids);

    /**
     * 根据主键获取Entity对象集合
     *
     * @param clazz
     * @param ids
     */
    public List<T> getEntities(final Class<?> clazz, final Long[] ids);

    /**
     * 根据params获取对象列表
     *
     * @param clazz
     * @param params 集合类型，不支持长度为0的对象
     * @return
     */
    public T getEntity(final Class<T> clazz, final Map<String, Object> params);

    /**
     * 根据hql获取Object对象
     *
     * @param hql
     * @param values
     * @return
     */
    public Object getObject(final String hql, final Object[] values);


    /**
     * 根据hql获取指定的Bean对象
     *
     * @param hql
     * @param values
     * @param convertBean
     * @return
     */
    public Object getBean(final String hql, final Object[] values, Class<?> convertBean);


    /**
     * 根据sql获取指定的Bean对象
     *
     * @param sql
     * @param values
     * @param convertBean
     * @return
     */
    public Object getBeanBySql(final String sql, final Object[] values, Class<?> convertBean);


    /**
     * 根据sql获取指定的Bean对象,并指定查询的字段
     *
     * @param sql
     * @param values
     * @param convertBean
     * @param queryFields
     * @return
     */
    public Object getBeanBySql(final String sql, final Object[] values, Class<?> convertBean, String[] queryFields);

    /**
     * 根据params获取对象列表
     *
     * @param clazz
     * @param params
     * @return
     */
    public List<T> getEntities(final Class<T> clazz, final Map<String, Object> params);

    /**
     * 根据hql获取Entity对象集合
     *
     * @param hql
     * @return
     */
    public List<T> getEntitiesByHql(final String hql, final Object[] values);


    /**
     * 根据hql获取Bean对象集合
     *
     * @param hql
     * @return
     */
    public List<?> getBeansByHql(final String hql, final Object[] values, Class<?> convertBean);


    public List<?> getBeansByHql(final String hql, final Object[] values, Class<?> convertBean, Integer limit);


    /**
     * 根据hql获取Entity对象集合
     *
     * @param hql
     * @return
     */
    public List<?> getBeansByHql(final String hql, final Map<String, Object> params, Class<?> convertBean, final Integer limit);

    /**
     * 根据sql获取Bean对象集合
     *
     * @param sql
     * @param values
     * @param convertBean
     * @return
     */
    public List<?> getBeansBySql(final String sql, final Object[] values, Class<?> convertBean);


    /**
     * 根据sql获取Bean对象集合,并指定查询的字段
     *
     * @param sql
     * @param values
     * @param convertBean
     * @param queryFields
     * @return
     */
    public List<?> getBeansBySql(final String sql, final Object[] values, Class<?> convertBean, String[] queryFields);

    /**
     * 根据hql获取Entity对象集合
     *
     * @param hql
     * @return
     */
    public List<T> getEntitiesByHql(final String hql, final Map<String, Object> values);

    /**
     * 根据hql获取Object对象集合
     *
     * @param hql
     * @param values
     * @return
     */
    public List<?> getObjects(final String hql, final Object[] values);

    /**
     * 根据hql获取Object对象集合
     *
     * @param hql
     * @param values
     * @param limit
     * @return
     */
    public List<?> getObjects(final String hql, final Object[] values, final Integer limit);


    /**
     * 根据sql获取Object对象集合
     *
     * @param sql
     * @param values
     * @return
     */
    public List<?> getObjectsBySql(final String sql, final Object[] values);

    /**
     * 根据sql获取Object对象集合
     *
     * @param sql
     * @param values
     * @return
     */
    public List<?> getObjectsBySql(final String sql, final Object[] values, final Integer limit);

    /**
     * 根据sql获取Object对象集合
     *
     * @param sql
     * @param values
     * @return
     */
    public List<?> getObjectsBySql(final String sql, final Map<String, Object> values);

    /**
     * 返回Entity对象分页列表,values不支持集合，数组
     *
     * @param pageIndex
     * @param pageSize
     * @param hql
     * @param values
     * @return
     */
    public Pagination getPagination(final Long pageIndex, final Integer pageSize, final String hql, final Object[] values);

    /**
     * 返回Entity对象分页列表,values支持集合，数组
     *
     * @param pageIndex
     * @param pageSize
     * @param hql
     * @param values
     * @return
     */
    public Pagination getPagination(final Long pageIndex, final Integer pageSize, final String hql, final Map<String, Object> values);


    /**
     * 返回Entity对象分页列表,values支持集合，数组
     *
     * @param pageIndex
     * @param pageSize
     * @param hql
     * @param values
     * @param convertBean
     * @return
     */
    public Pagination getPagination(final Long pageIndex, final Integer pageSize, final String hql, final Object[] values, final Class<?> convertBean);

    /**
     * 返回指定的Bean对象分页列表，使用sql语句查询
     *
     * @param pageIndex
     * @param pageSize
     * @param sql
     * @param values
     * @param convertBean
     * @return
     */
    public Pagination getPaginationBySql(final Long pageIndex, final Integer pageSize, String sql, final Object[] values, final Class<?> convertBean);


    /**
     * 返回指定的Bean对象分页列表，使用sql语句查询,并指定查询的字段
     *
     * @param pageIndex
     * @param pageSize
     * @param sql
     * @param values
     * @param convertBean
     * @param queryFields
     * @return
     */
    public Pagination getPaginationBySql(final Long pageIndex, final Integer pageSize, String sql, final Object[] values, final Class<?> convertBean, final String[] queryFields);

    /**
     * 获取Entity总的记录条数
     *
     * @param hql
     * @param values
     * @return
     */
    public Long getCount(final String hql, final Object[] values);

    /**
     * 通过sql获取记录条数
     *
     * @param sql
     * @param values
     * @return
     */
    public Long getCountBySql(String sql, final Object[] values);

    /**
     * 获取Entity总的记录条数
     *
     * @param hql
     * @param values
     * @return
     */
    public Long getCount(final String hql, final Map<String, Object> values);

    /**
     * 根据hql获取Entity对象
     *
     * @param hql
     * @return
     */
    public T getEntityByHql(final String hql, final Object[] values);


    /**
     * 获取分页对象集合
     *
     * @param hql
     * @param first
     * @param max
     * @param values
     * @return
     */
    public List<?> getPaginationRecores(final String hql, final int first,
                                        final int max, final Map<String, Object> values);


    /**
     * 获取分页对象集合
     *
     * @param hql
     * @param first
     * @param max
     * @param values
     * @return
     */
    public List<?> getPaginationRecores(final String hql, final int first,
                                        final int max, final Object[] values, final Class<?> conventBean);

    /**
     * 获取分页对象集合
     *
     * @param hql
     * @param first
     * @param max
     * @param values
     * @return
     */
    public List<?> getPaginationRecores(final String hql, final int first,
                                        final int max, final Object[] values);

    /**
     * 获取分页对象集合
     *
     * @param sql
     * @param first
     * @param max
     * @param values
     * @return
     */
    public List<?> getPaginationRecoresBySql(final String sql, final int first,
                                             final int max, final Object[] values);

    /**
     * 获取分页对象集合,sql语句查询并指定返回集合中对象的实体Bean
     *
     * @param sql
     * @param first
     * @param max
     * @param values
     * @param convertBean
     * @param queryFields
     * @return
     */
    public List<?> getPaginationRecoresBySql(final String sql, final int first, final int max, final Object[] values, final Class<?> convertBean, final String[] queryFields);

    public List<T> getEntities(String hql, Map<String, Object> values, Integer maxCount);
}
