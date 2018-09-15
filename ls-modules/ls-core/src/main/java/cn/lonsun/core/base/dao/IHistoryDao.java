package cn.lonsun.core.base.dao;

import java.util.List;
import java.util.Map;

import cn.lonsun.core.util.Pagination;

/**
 * 底层ORM接口
 *
 * @author xujh
 * @version 1.0
 *          2015年1月28日
 */
public interface IHistoryDao<T> {


    /**
     * 保存Entity对象t
     *
     * @param t
     */
    public Long save(final T t);


    /**
     * 更新Entity对象t
     *
     * @param t
     */
    public void update(final T t);


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
    @Deprecated
    public List<T> getEntities(final Class<?> clazz, final Long[] ids);

    /**
     * 根据hql获取Object对象
     *
     * @param hql
     * @param values
     * @return
     */
    public Object getObject(final String hql, final Object[] values);

    /**
     * 根据sql获取指定的Bean对象
     *
     * @param sql
     * @param values
     * @param clazz
     * @return
     */
    public Object getBeanBySql(final String sql, final Object[] params, Class<?> clazz);

    /**
     * 通过sql查询，并将查询结果转换为clazz类型的对象列表
     *
     * @param sql
     * @param params
     * @param clazz
     * @param fieldNames
     * @return
     */
    public Object getBeansBySql(String sql, Map<String, Object> params, Class<?> clazz, String[] fieldNames);

    /**
     * 通过hql查询实体对象列表
     *
     * @param hql
     * @return
     */
    public List<T> getEntitiesByHql(final String hql);

    /**
     * 根据params获取对象列表
     *
     * @param clazz
     * @param params 集合类型，不支持长度为0的对象
     * @return
     */
    public List<T> getEntities(final Class<T> clazz, final Map<String, Object> params);

    /**
     * 根据params获取对象列表
     *
     * @param clazz
     * @param params 集合类型，不支持长度为0的对象
     * @return
     */
    public T getEntity(final Class<T> clazz, final Map<String, Object> params);

    /**
     * 根据hql获取Entity对象集合
     *
     * @param hql
     * @return
     */
    public List<T> getEntitiesByHql(final String hql, final Object[] values);

    /**
     * 根据hql获取Entity对象
     *
     * @param hql
     * @return
     */
    public T getEntityByHql(final String hql, final Object[] values);

    /**
     * 根据hql获取Entity对象集合
     *
     * @param hql
     * @return
     */
    public List<T> getEntitiesByHql(final String hql,
                                    final Map<String, Object> values);

    /**
     * 根据hql获取Entity对象集合
     *
     * @param hql
     * @return
     */
    public List<?> getBeansByHql(final String hql, final Object[] values, Class<?> convertBean);


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
     * 根据hql获取Object对象集合
     *
     * @param hql
     * @param values
     * @return
     */
    public List<?> getObjects(final String hql, final Object[] values);

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
     * @param fields
     * @return
     */
    public List<Object> getObjectsBySql(final String sql, final Object[] values, final List<String> fields);

    /**
     * 根据sql获取Object对象集合
     *
     * @param sql
     * @param values
     * @return
     */
    public List<?> getObjectsBySql(final String sql,
                                   final Map<String, Object> values);

    /**
     * 返回Entity对象分页列表,values不支持集合，数组
     *
     * @param pageIndex
     * @param pageSize
     * @param hql
     * @param values
     * @return
     */
    public Pagination getPagination(final Long pageIndex,
                                    final Integer pageSize, final String hql, final Object[] values);

    /**
     * 返回Entity对象分页列表,values支持集合，数组
     *
     * @param pageIndex
     * @param pageSize
     * @param hql
     * @param values
     * @return
     */
    public Pagination getPaginationBySql(final Long pageIndex,
                                         final Integer pageSize, final String hql, final Object[] values,
                                         List<String> fields);

    /**
     * 返回Entity对象分页列表,values支持集合，数组
     *
     * @param pageIndex
     * @param pageSize
     * @param hql
     * @param values
     * @return
     */
    public Pagination getPagination(final Long pageIndex,
                                    final Integer pageSize, final String hql, final Object[] values,
                                    final Class<?> convertBean);


    /**
     * 返回Entity对象分页列表,values支持集合，数组
     *
     * @param pageIndex
     * @param pageSize
     * @param hql
     * @param values
     * @return
     */
    public Pagination getPaginationBySql(final Long pageIndex, final Integer pageSize, final String hql, final Object[] values);


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
     * 返回指定的Bean对象分页列表，使用sql语句查询
     *
     * @param pageIndex
     * @param pageSize
     * @param sql
     * @param values
     * @param convertBean
     * @param queryFields 指定查询的字段名
     * @return
     */
    public Pagination getPaginationBySql(final Long pageIndex, final Integer pageSize, String sql, final Object[] values, final Class<?> convertBean, final String[] queryFields);

    /**
     * 返回指定的Bean对象分页列表，使用sql语句查询
     *
     * @param pageIndex
     * @param pageSize
     * @param sql
     * @param values
     * @param convertBean
     * @param queryFields 指定查询的字段名
     * @return
     */
    public Pagination getPaginationBySql(final Long pageIndex, final Integer pageSize, String sql, final Map<String, Object> values, final Class<?> convertBean, final String[] queryFields);

    /**
     * 返回Entity对象分页列表
     *
     * @param pageIndex
     * @param pageSize
     * @param hql
     * @param values
     * @return
     */
    public Pagination getPagination(final Long pageIndex,
                                    final Integer pageSize, final String hql,
                                    final Map<String, Object> values);

    /**
     * 获取Entity总的记录条数
     *
     * @param hql
     * @param values
     * @return
     */
    public Long getCount(final String hql, final Object[] values);

    /**
     * 获取Entity总的记录条数
     *
     * @param sql
     * @param values
     * @return
     */
    public Long getCountBySql(final String sql, final Object[] values);

    /**
     * 获取Entity总的记录条数
     *
     * @param sql
     * @param values
     * @return
     */
    public Long getCountBySql(final String sql, final Map<String, Object> params);

    /**
     * 获取Entity总的记录条数
     *
     * @param hql
     * @param values
     * @return
     */
    public Long getCount(final String hql, final Map<String, Object> values);

    /**
     * 获取分页对象集合
     *
     * @param hql
     * @param first
     * @param max
     * @param values
     * @return
     */
    public List<Object> getPaginationRecords(final String hql, final int first,
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
    public List<Object> getPaginationRecores(final String hql, final int first,
                                             final int max, final Object[] values);

    /**
     * 获取分页对象集合
     *
     * @param hql
     * @param first
     * @param max
     * @param values
     * @return
     */
    public List<Object> getPaginationRecores(final String hql, final int first,
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
    public List<Object> getPaginationRecoresBySql(final String sql,
                                                  final int first, final int max, final Object[] values);

    /**
     * @param sql
     * @param first
     * @param max
     * @param values
     * @param fields
     * @return
     */
    public List<Object> getPaginationRecoresBySql(final String sql,
                                                  final int first, final int max, final Object[] values,
                                                  final List<String> fields);

    /**
     * 获取分页对象集合,sql语句查询并指定返回集合中对象的实体Bean
     *
     * @param sql
     * @param first
     * @param max
     * @param values
     * @param convertBean
     * @return
     */
    public List<?> getPaginationRecoresBySql(final String sql, final int first, final int max, final Object[] values, final Class<?> convertBean, final String[] queryFields);

    /**
     * 获取分页对象集合,sql语句查询并指定返回集合中对象的实体Bean
     *
     * @param sql
     * @param first
     * @param max
     * @param values
     * @param convertBean
     * @return
     */
    public List<?> getPaginationRecoresBySql(final String sql, final int first, final int max, final Map<String, Object> params, final Class<?> convertBean, final String[] queryFields);

    /**
     * 通过hql获取对象列表，最多返回maxCount条记录
     *
     * @param hql
     * @param values
     * @param maxCount
     * @return
     */
    public List<T> getEntities(final String hql, final Object[] values,
                               Integer maxCount);

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
     * 根据hql获取Object对象
     *
     * @param hql
     * @param values
     * @return
     */
    public Object getBean(final String hql, final Object[] values, Class<?> convertBean);

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
}
