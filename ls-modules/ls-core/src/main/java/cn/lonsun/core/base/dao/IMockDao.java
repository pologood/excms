package cn.lonsun.core.base.dao;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;

import cn.lonsun.core.base.entity.IMockEntity;
import cn.lonsun.core.util.Pagination;

/**
 * 底层ORM接口
 *
 * @author xujh
 * @version 1.0
 *          2015年1月28日
 */
public interface IMockDao<T extends IMockEntity> {

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
    public void updateProperties(Class<IMockEntity> clazz, Map<String, Object> items, Map<String, Object> properties);

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
     * 保存或更新Entity对象数组ts
     *
     * @param hql
     * @param values
     */
    public int saveOrUpdate(final String hql, final Object[] values);

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
     * @param t Parameter
     * @return void   return type
     * @throws
     * @Title: remove
     * @Description: 物理删除
     */
    public void remove(final T t);

    /**
     * @param ts Parameter
     * @return void   return type
     * @throws
     * @Title: remove
     * @Description: TODO
     */
    public void remove(final Collection<T> ts);

    /**
     * @param ts Parameter
     * @return void   return type
     * @throws
     * @Title: remove
     * @Description: 物理删除
     */
    public void remove(final T[] ts);

    /**
     * @param clazz
     * @param id    Parameter
     * @return void   return type
     * @throws
     * @Title: remove
     * @Description: 物理删除
     */
    public void remove(final Class<T> clazz, final Long id);

    /**
     * @param clazz
     * @param ids   Parameter
     * @return void   return type
     * @throws
     * @Title: remove
     * @Description: 物理删除
     */
    public void remove(final Class<T> clazz, final Long[] ids);

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
    @Deprecated
    public List<T> getEntities(final Class<?> clazz, final List<Long> ids);

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
     * 通过sql查询，并将查询结果转换为clazz类型的对象列表
     *
     * @param sql
     * @param params
     * @param clazz
     * @param fieldNames
     * @return
     */
    public Object getBeansBySql(String sql, Map<String, Object> params, Class<?> clazz, String[] fieldNames, Integer limit);

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
     * 设置参数
     *
     * @param values
     * @param q
     */
    public void setParameters(final Object[] values, final Query q);


    // add by dzl 2014.09.26

    /**
     * 查询返回实体对象
     *
     * @param hql
     * @param params
     * @return
     */
    public T getEntityByJpql(final String hql, final Map<String, Object> params);

    public Query createQuery(final String hql);

    public Query createQuery(final String hql, final Map<String, Object> params);

    /**
     * 查询返回实体对象列表
     *
     * @param hql
     * @param params
     * @return
     */
    public List<T> getListByJqpl(final String hql,
                                 final Map<String, Object> params);

    /**
     * 返回Entity对象分页列表
     *
     * @param pageIndex
     * @param pageSize
     * @param hql
     * @param params
     * @return
     */
    public Pagination getPaginationByJpql(final Long pageIndex,
                                          final Integer pageSize, final String hql,
                                          final Map<String, Object> params);

    /**
     * 处理基于Hibernate的游离状态对象<br/>
     * 应用场景为: 当表单修改传递是JSON格式符串对象,如:<br/>
     * id:1,title:"1111" -->
     * 服务端接收以后直接反序列化为实体对象(因为主键ID存在,反序列化为对象时,此对象就自动成为就当作游离态处理)<br/>
     * --> 执行修改操作 (正常使用update会出错,a different object with the same identifier
     * value was already associated with the session)
     *
     * @param t
     */
    public void merge(final T t);

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
     * 执行sql(命名查询)
     *
     * @param hql
     * @param params 参数使用命名查询方式
     */
    public int executeUpdateByJpql(final String hql, final Map<String, Object> params);

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

    public List<T> getEntities(String hql, Map<String, Object> values, Integer maxCount);

    List<?> getEntities(String hql, Object[] values, Class<?> convertBean,
                        Integer maxCount);
}
