package cn.lonsun.core.base.dao.impl;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.stereotype.Repository;

import cn.lonsun.core.base.dao.IHistoryDao;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.util.Pagination;

/**
 * 数据库访问层实现
 * 
 * @author 徐建华
 * 
 * @param <T>
 */
@Repository("historyDao")
public class HistoryDao<T> implements IHistoryDao<T> {

	@Resource
	private SessionFactory sessionFactory;

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public Session getCurrentSession() {
		return sessionFactory.getCurrentSession();
	}


	@Override
	public Long save(final T t) {
		Serializable seralizable = getCurrentSession().save(t);
		return Long.valueOf(seralizable.toString());
	}

	@Override
	public void update(final T t) {
		if (t != null) {
			getCurrentSession().update(t);
		}
	}

	@Override
	@Deprecated
	public List<T> getEntities(final Class<?> clazz, final Long[] ids) {
		if (clazz == null || ids == null || ids.length <= 0) {
			throw new NullPointerException("One of the arguments is null.");
		}
		List<T> entities = new ArrayList<T>(ids.length);
		for (Long id : ids) {
			T entity = getEntity(clazz, id);
			entities.add(entity);
		}
		return entities;
	}

	/**
	 * 根据hql获取Object对象
	 * 
	 * @param hql
	 * @param values
	 * @return
	 */
	@Override
	public Object getObject(final String hql, final Object[] values) {
		List<?> entities = getEntitiesByHql(hql, values);
		Object object = null;
		if (entities != null && !entities.isEmpty()) {
//          if (entities.size() == 1) {
//              object = entities.get(0);
//          } else {
//              throw new RuntimeException();
//          }
            object = entities.get(0);
        }
		return object;
	}

	/**
	 * 根据hql获取Object对象
	 * 
	 * @param hql
	 * @param values
	 * @return
	 */
	@Override
	public Object getBean(final String hql, final Object[] values,Class<?> convertBean) {
		List<?> entities = getBeansByHql(hql, values, convertBean);
		Object object = null;
		if (entities != null && !entities.isEmpty()) {
//          if (entities.size() == 1) {
//              object = entities.get(0);
//          } else {
//              throw new RuntimeException();
//          }
            object = entities.get(0);
        }
		return object;
	}

    @Override
    public Object getBeanBySql(final String sql,final Object[] values,Class<?> convertBean) {
        return getBeanBySql(sql,values,convertBean,null);
    }

    @Override
    public Object getBeanBySql(final String sql,final Object[] values,Class<?> convertBean,String[] queryFields) {
        List<?> entities = getBeansBySql(sql, values, convertBean,queryFields);
        Object object = null;
        if (entities != null && !entities.isEmpty()) {
//          if (entities.size() == 1) {
//              object = entities.get(0);
//          } else {
//              throw new RuntimeException();
//          }
            object = entities.get(0);
        }
        return object;
    }

	@SuppressWarnings("unchecked")
	@Override
	public List<T> getEntitiesByHql(final String hql) {
		Query query = getCurrentSession().createQuery(hql);
		return query.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<T> getEntitiesByHql(final String hql, final Object[] values) {
		Query query = getCurrentSession().createQuery(hql);
		setParameters(values, query);
		return query.list();
	}

	@Override
	public List<?> getBeansByHql(final String hql, final Object[] values,
			Class<?> convertBean) {
		Query query = getCurrentSession().createQuery(hql);
		setParameters(values, query);
		query.setResultTransformer(Transformers.aliasToBean(convertBean));
		return query.list();
	}

	/**
	 * 根据hql获取Entity对象集合
	 *
	 * @param hql
	 * @param params
	 * @param convertBean
	 * @param limit       @return
	 */
	@Override
	public List<?> getBeansByHql(String hql, Map<String, Object> params, Class<?> convertBean, Integer limit) {
		Query query = getCurrentSession().createQuery(hql);
		setParameters(params, query);
		if(null != limit){
			query.setMaxResults(limit);
		}
		query.setResultTransformer(Transformers.aliasToBean(convertBean));
		return query.list();
	}

    @Override
    public List<?> getBeansBySql(final String sql, final Object[] values,Class<?> convertBean) {
        return getBeansBySql(sql,values,convertBean,null);
    }

    @Override
    public List<?> getBeansBySql(final String sql, final Object[] values,Class<?> convertBean,String[] queryFields) {
        if (sql == null || "".equals(sql)) {
            return null;
        }
        SQLQuery q = getCurrentSession().createSQLQuery(sql);
        addSclar(q, convertBean,queryFields);
        setParameters(values, q);
        q.setResultTransformer(Transformers.aliasToBean(convertBean));
        return q.list();
    }

    @Override
	public T getEntityByHql(final String hql, final Object[] values) {
		List<T> entities = getEntitiesByHql(hql, values);
		T entity = null;
		if (entities != null && !entities.isEmpty()) {
//          if (entities.size() == 1) {
//              object = entities.get(0);
//          } else {
//              throw new RuntimeException();
//          }
		    entity = entities.get(0);
        }
		return entity;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<T> getEntities(final Class<T> clazz,
			final Map<String, Object> params) {
		StringBuffer hql = new StringBuffer("from ");
		hql.append(clazz.getSimpleName()).append(" t where 1=1");
		Map<String, Object> values1 = null;
		List<Object> values2 = null;
		StringBuffer subHql = null;
		if (params != null && params.size() > 0) {
			Set<String> keys = params.keySet();
			for (String key : keys) {
				Object value = params.get(key);
				// 参数为null
				if (value == null) {
					hql.append(" and t.").append(key).append(" is null");
				} else {
					// 如果参数是集合或者数组
					if (value instanceof Collection
							|| value instanceof Object[]) {
						// 集合或数组参数容器初始化
						if (values1 == null) {
							values1 = new HashMap<String, Object>();
						}
						if (values1 == null || subHql == null) {
							subHql = new StringBuffer();
						}
						String newKey = key.concat("s");
						subHql.append(" and t.").append(key).append(" in (:").append(newKey).append(") ");
						values1.put(newKey, value);
					} else {
						if (values2 == null) {
							values2 = new ArrayList<Object>();
						}
						hql.append(" and t.").append(key).append(" = ?");
						values2.add(value);
					}
				}
			}
		}
		if (subHql != null) {
			hql = hql.append(subHql);
		}
		Query query = getCurrentSession().createQuery(hql.toString());
		if (values2 != null) {
			setParameters(values2.toArray(), query);
		}
		if (values1 != null) {
			setParameters(values1, query);
		}
		return (List<T>) query.list();
	}
	
	@Override
	public T getEntity(final Class<T> clazz,final Map<String, Object> params){
		List<T> ts = getEntities(clazz, params);
		T t = null;
		if(ts!=null&&ts.size()>0){
			t = ts.get(0);
		}
		return t;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<T> getEntitiesByHql(final String hql,
			final Map<String, Object> values) {
		Query query = getCurrentSession().createQuery(hql);
		setParameters(values, query);
		return query.list();
	}

	@Override
	public List<?> getObjectsBySql(final String sql, final Object[] values) {
		SQLQuery q = getCurrentSession().createSQLQuery(sql);
		setParameters(values, q);
		return q.list();
	}

	@Override
	public List<?> getObjectsBySql(final String sql,
			final Map<String, Object> values) {
		SQLQuery q = getCurrentSession().createSQLQuery(sql);
		setParameters(values, q);
		return q.list();
	}

	/**
	 * 为Query设置查询参数
	 * 
	 * @param values
	 * @param q
	 */
	public void setParameters(final Object[] values, final Query q) {
		if (null != values && values.length > 0) {
			for (Integer i = 0; i < values.length; ++i) {
				q.setParameter(i, values[i]);
			}
		}
	}

	/**
	 * 为Query设置查询参数
	 * 
	 * @param params
	 * @param q
	 */
	private void setParameters(final Map<String, Object> params, final Query q) {
		if (null != params && params.size() > 0) {
			Set<String> keys = params.keySet();
			for (String key : keys) {
				Object value = params.get(key);
				if (value instanceof Object[]) {
					q.setParameterList(key, (Object[]) value);
				} else if (value instanceof Collection<?>) {
					q.setParameterList(key, (Collection<?>) value);
				} else if (value instanceof List<?>) {
					q.setParameterList(key, (List<?>) value);
				}  else if(value instanceof Date){
                    q.setTimestamp(key, (Date) value);
                } else {
					q.setParameter(key, value);
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public T getEntity(final Class<?> clazz, final Long id) {
		if (clazz == null || id == null) {
			return null;
		}
		T entity = (T) getCurrentSession().get(clazz, id);
		return entity;
	}

	@Override
	public List<?> getObjects(final String hql, final Object[] values) {
		if (StringUtils.isEmpty(hql) || "".equals(hql)) {
			return null;
		}
		Query query = getCurrentSession().createQuery(hql);
		setParameters(values, query);
		return query.list();
	}

	@Override
	public Pagination getPagination(final Long pageIndex,
			final Integer pageSize, String hql, final Object[] values) {
		// 分页数量边界控制
		if (pageSize <= 0 || pageSize > Pagination.MAX_SIZE) {
			throw new BaseRunTimeException();
		}
		List<Object> results = getPaginationRecores(hql, Pagination
				.getStartNumber(pageIndex, pageSize).intValue(),
				Pagination.getPageSize(pageSize), values);
		Long count = getCount(hql, values);
		Pagination page = new Pagination(results, count, pageSize, pageIndex);
		return page;
	}

	@Override
	public Pagination getPagination(final Long pageIndex,
			final Integer pageSize, String hql, final Object[] values,
			final Class<?> convertBean) {
		// 分页数量边界控制
		if (pageSize <= 0 || pageSize > Pagination.MAX_SIZE) {
			throw new BaseRunTimeException();
		}
		List<Object> results = getPaginationRecores(hql, Pagination
				.getStartNumber(pageIndex, pageSize).intValue(),
				Pagination.getPageSize(pageSize), values, convertBean);
		Long count = getCount(hql, values);
		Pagination page = new Pagination(results, count, pageSize, pageIndex);
		return page;
	}


    @Override
    public Pagination getPaginationBySql(final Long pageIndex,
             final Integer pageSize, String sql, final Object[] values,
             final Class<?> convertBean) {

        return getPaginationBySql(pageIndex,pageSize,sql,values,convertBean,null);
    }

    @Override
    public Pagination getPaginationBySql(final Long pageIndex,
             final Integer pageSize, String sql, final Object[] values,
             final Class<?> convertBean,final String[] queryFields) {
        // 分页数量边界控制
        if (pageSize <= 0 || pageSize > Pagination.MAX_SIZE) {
            throw new BaseRunTimeException();
        }
       List<?> results = getPaginationRecoresBySql(sql, Pagination
                        .getStartNumber(pageIndex, pageSize).intValue(),
                Pagination.getPageSize(pageSize), values, convertBean,queryFields);
        Long count = getCountBySql(sql, values);
        Pagination page = new Pagination(results, count, pageSize, pageIndex);
        return page;
    }

    @Override
    public Pagination getPaginationBySql(final Long pageIndex,
             final Integer pageSize, String sql, final Map<String,Object> params,
             final Class<?> convertBean,final String[] queryFields) {
        // 分页数量边界控制
        if (pageSize <= 0 || pageSize > Pagination.MAX_SIZE) {
            throw new BaseRunTimeException();
        }
        List<?> results = getPaginationRecoresBySql(sql, Pagination
                        .getStartNumber(pageIndex, pageSize).intValue(),
                Pagination.getPageSize(pageSize), params, convertBean,queryFields);
        Long count = getCountBySql(sql, params);
        Pagination page = new Pagination(results, count, pageSize, pageIndex);
        return page;
    }

	@Override
	public Pagination getPaginationBySql(final Long pageIndex,
			final Integer pageSize, String sql, final Object[] values) {
		// 分页数量边界控制
		if (pageSize <= 0 || pageSize > Pagination.MAX_SIZE) {
			throw new BaseRunTimeException();
		}
		List<Object> results = getPaginationRecoresBySql(sql, Pagination
				.getStartNumber(pageIndex, pageSize).intValue(),
				Pagination.getPageSize(pageSize), values);
		Long count = getCountBySql(sql, values);
		Pagination page = new Pagination(results, count, pageSize, pageIndex);
		return page;
	}

	@Override
	public Pagination getPaginationBySql(final Long pageIndex,
			final Integer pageSize, String sql, final Object[] values,
			final List<String> fields) {
		// 分页数量边界控制
		if (pageSize <= 0 || pageSize > Pagination.MAX_SIZE) {
			throw new BaseRunTimeException();
		}
		List<Object> results = getPaginationRecoresBySql(sql, Pagination
				.getStartNumber(pageIndex, pageSize).intValue(),
				Pagination.getPageSize(pageSize), values, fields);
		Long count = getCountBySql(sql, values);
		Pagination page = new Pagination(results, count, pageSize, pageIndex);
		return page;
	}

	@Override
	public Pagination getPagination(final Long pageIndex,
			final Integer pageSize, String hql, final Map<String, Object> values) {
		// 分页数量边界控制
		if (pageSize <= 0 || pageSize > Pagination.MAX_SIZE) {
			throw new BaseRunTimeException();
		}
		List<Object> results = getPaginationRecords(hql, Pagination
						.getStartNumber(pageIndex, pageSize).intValue(),
				Pagination.getPageSize(pageSize), values);
		Long count = getCount(hql, values);
		Pagination page = new Pagination(results, count, pageSize, pageIndex);
		return page;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Object> getPaginationRecords(final String hql, final int first,
			final int max, final Map<String, Object> values) {
		if (StringUtils.isEmpty(hql) || first < 0 || max <= 0) {
			return null;
		}
		Query q = getCurrentSession().createQuery(hql.toString());
		setParameters(values, q);
		q.setFirstResult(first);
		q.setMaxResults(max);
		return q.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Object> getPaginationRecores(final String hql, final int first,
			final int max, final Object[] values) {
		if (StringUtils.isEmpty(hql) || first < 0 || max <= 0) {
			return null;
		}
		Query q = getCurrentSession().createQuery(hql.toString());
		setParameters((Object[]) values, q);
		q.setFirstResult(first);
		q.setMaxResults(max);
		return q.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Object> getPaginationRecores(final String hql, final int first,
			final int max, final Object[] values, final Class<?> convertBean) {
		if (StringUtils.isEmpty(hql) || first < 0 || max <= 0) {
			return null;
		}
		Query q = getCurrentSession().createQuery(hql.toString());
		setParameters((Object[]) values, q);
		q.setFirstResult(first);
		q.setMaxResults(max);
		q.setResultTransformer(Transformers.aliasToBean(convertBean));
		return q.list();
	}


    @Override
    public List<?> getPaginationRecoresBySql(final String sql, final int first,
           final int max, final Object[] values,final Class<?> convertBean,final String[] queryFields) {
        if (sql == null || first < 0 || max <= 0) {
            return null;
        }
        String fullSql = null;
        if(!sql.startsWith("select")){
            fullSql = "select * ".concat(sql);
        }else{
            fullSql = sql;
        }
        SQLQuery q = getCurrentSession().createSQLQuery(fullSql);
        addSclar(q, convertBean, queryFields);
        setParameters((Object[]) values, q);
        q.setFirstResult(first);
        q.setMaxResults(max);
        q.setResultTransformer(Transformers.aliasToBean(convertBean));
        return q.list();
    }

    @Override
    public List<?> getPaginationRecoresBySql(final String sql, final int first,
           final int max, final Map<String,Object> params,final Class<?> convertBean,final String[] queryFields) {
        if (sql == null || first < 0 || max <= 0) {
            return null;
        }
        String fullSql = null;
        if(!sql.startsWith("select")){
            fullSql = "select * ".concat(sql);
        }else{
            fullSql = sql;
        }
        SQLQuery q = getCurrentSession().createSQLQuery(fullSql);
        addSclar(q, convertBean, queryFields);
        setParameters(params, q);
        q.setFirstResult(first);
        q.setMaxResults(max);
        q.setResultTransformer(Transformers.aliasToBean(convertBean));
        return q.list();
    }

	@Override
	@SuppressWarnings("unchecked")
	public List<T> getEntities(final String hql, final Object[] values,
			Integer maxCount) {
		if (StringUtils.isEmpty(hql) || maxCount <= 0) {
			return null;
		}
		Query q = getCurrentSession().createQuery(hql.toString());
		setParameters((Object[]) values, q);
		q.setFirstResult(0);
		q.setMaxResults(maxCount);
		return (List<T>) q.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Object> getPaginationRecoresBySql(final String sql,
			final int first, final int max, final Object[] values) {
		if (StringUtils.isEmpty(sql) || first < 0 || max <= 0) {
			return null;
		}
		Query q = getCurrentSession().createSQLQuery(sql.toString());
		setParameters((Object[]) values, q);
		q.setFirstResult(first);
		q.setMaxResults(max);
		return q.list();
	}

	@SuppressWarnings("unchecked")
	public List<Object> getPaginationRecoresBySql(final String sql,
			final int first, final int max, final Object[] values,
			final List<String> fields) {
		if (StringUtils.isEmpty(sql) || first < 0 || max <= 0) {
			return null;
		}
		SQLQuery q = getCurrentSession().createSQLQuery(sql.toString());
		if (fields != null && fields.size() > 0) {
			for (String filed : fields) {
				if (!StringUtils.isEmpty(filed)) {
					q.addScalar(filed);
				}
			}
		}
		setParameters((Object[]) values, q);
		q.setFirstResult(first);
		q.setMaxResults(max);
		return q.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Object> getObjectsBySql(final String sql,
			final Object[] values, final List<String> fields) {
		SQLQuery q = getCurrentSession().createSQLQuery(sql.toString());
		if (fields != null && fields.size() > 0) {
			for (String filed : fields) {
				if (!StringUtils.isEmpty(filed)) {
					q.addScalar(filed);
				}
			}
		}
		setParameters((Object[]) values, q);
		return q.list();
	}

	/**
	 * 通用的查询语句返回结果数的实现
	 */
	@Override
	public Long getCount(String hql, final Object[] values) {
		if (StringUtils.isEmpty(hql)) {
			return Long.valueOf(0);
		}
		Long count = Long.valueOf(0);
		if (hql.trim().startsWith("select")) {
			Query query = getCurrentSession().createQuery(hql);
			setParameters(values, query);
			count = Long.valueOf(query.list().size());
		} else {
			hql = "select count(*) ".concat(hql);
			Query query = getCurrentSession().createQuery(hql);
			setParameters(values, query);
			List<?> list = query.list();

			if (list != null && list.size() == 1) {
				count = Long.valueOf(list.get(0).toString());
			}
		}

		return count;
	}

	/**
	 * 通用的查询语句返回结果数的实现
	 */
	@Override
	public Long getCountBySql(String sql, final Object[] values) {

		if (StringUtils.isEmpty(sql)) {
			return Long.valueOf(0);
		}
		Long count = Long.valueOf(0);
		if (sql.startsWith("select")) {
			sql = "select count(*) from (".concat(sql).concat(")");
		} else {
			sql = "select count(*) ".concat(sql);
		}
		Query query = getCurrentSession().createSQLQuery(sql);
		setParameters(values, query);
		List<?> list = query.list();
		if (list != null && list.size() == 1) {
			count = Long.valueOf(list.get(0).toString());
		}

		return count;
	}

	/**
	 * 通用的查询语句返回结果数的实现
	 */
	@Override
	public Long getCountBySql(String sql, final Map<String,Object> params) {

		if (StringUtils.isEmpty(sql)) {
			return Long.valueOf(0);
		}
		Long count = Long.valueOf(0);
		if (sql.startsWith("select")) {
			Query query = getCurrentSession().createSQLQuery(sql);
			setParameters(params, query);
			count = Long.valueOf(query.list().size());
		} else {
			sql = "select count(*) ".concat(sql);
			Query query = getCurrentSession().createSQLQuery(sql);
			setParameters(params, query);
			List<?> list = query.list();

			if (list != null && list.size() == 1) {
				count = Long.valueOf(list.get(0).toString());
			}
		}
		return count;
	}

	/**
	 * 通用的查询语句返回结果数的实现
	 */
	@Override
	public Long getCount(String hql, final Map<String, Object> values) {
		if (StringUtils.isEmpty(hql)) {
			return Long.valueOf(0);
		}
		Long count = Long.valueOf(0);
		if (hql.startsWith("select")) {
			Query query = getCurrentSession().createQuery(hql);
			setParameters(values, query);
			count = Long.valueOf(query.list().size());
		} else {
			hql = "select count(*) ".concat(hql);
			Query query = getCurrentSession().createQuery(hql);
			setParameters(values, query);
			List<?> list = query.list();
			if (list != null && list.size() == 1) {
				count = Long.valueOf(list.get(0).toString());
			}
		}

		return count;
	}

	/**
	 * 字符串转换成日期
	 * 
	 * @param str  要转换的日期字符串
	 * @param sdf  格式: 如:yyyy-MM-dd
	 * @return
	 */
	protected Date strToDate(String str, String sdf) {
		SimpleDateFormat format = new SimpleDateFormat(sdf);
		Date date = null;
		try {
			date = format.parse(str);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}

	@Override
	public int executeUpdateByHql(String hql, Object[] values) {
		Query query = getCurrentSession().createQuery(hql);
		setParameters((Object[]) values, query);
		return query.executeUpdate();
	}
	
	@Override
	public int executeUpdateBySql(String sql, Object[] values) {
		SQLQuery query = getCurrentSession().createSQLQuery(sql);
		setParameters(values, query);
		return query.executeUpdate();
	}

    private void addSclar(SQLQuery sqlQuery, Class<?> clazz,String[] queryFields){
        if (clazz == null) {
            throw new NullPointerException("[clazz] could not be null!");
        }
        Field[] fields = clazz.getDeclaredFields();
        if(null ==  queryFields){
            for (Field field : fields) {
                if ((field.getType() == long.class) || (field.getType() == Long.class)) {
                    sqlQuery.addScalar(field.getName(), StandardBasicTypes.LONG);
                } else if ((field.getType() == int.class) || (field.getType() == Integer.class)) {
                    sqlQuery.addScalar(field.getName(), StandardBasicTypes.INTEGER);
                } else if ((field.getType() == char.class) || (field.getType() == Character.class)) {
                    sqlQuery.addScalar(field.getName(), StandardBasicTypes.CHARACTER);
                } else if ((field.getType() == short.class) || (field.getType() == Short.class)) {
                    sqlQuery.addScalar(field.getName(), StandardBasicTypes.SHORT);
                } else if ((field.getType() == double.class) || (field.getType() == Double.class)) {
                    sqlQuery.addScalar(field.getName(), StandardBasicTypes.DOUBLE);
                } else if ((field.getType() == float.class) || (field.getType() == Float.class)) {
                    sqlQuery.addScalar(field.getName(), StandardBasicTypes.FLOAT);
                } else if ((field.getType() == boolean.class) || (field.getType() == Boolean.class)) {
                    sqlQuery.addScalar(field.getName(), StandardBasicTypes.BOOLEAN);
                } else if (field.getType() == String.class) {
                    sqlQuery.addScalar(field.getName(), StandardBasicTypes.STRING);
                } else if (field.getType() == Date.class) {
                    sqlQuery.addScalar(field.getName(), StandardBasicTypes.TIMESTAMP);
                }
            }
        }else{
            for (String fieldName : queryFields) {
                for (Field field : fields) {
                    if (fieldName.equals(field.getName())) {
                        if ((field.getType() == long.class) || (field.getType() == Long.class)) {
                            sqlQuery.addScalar(field.getName(), StandardBasicTypes.LONG);
                        } else if ((field.getType() == int.class) || (field.getType() == Integer.class)) {
                            sqlQuery.addScalar(field.getName(), StandardBasicTypes.INTEGER);
                        } else if ((field.getType() == char.class) || (field.getType() == Character.class)) {
                            sqlQuery.addScalar(field.getName(), StandardBasicTypes.CHARACTER);
                        } else if ((field.getType() == short.class) || (field.getType() == Short.class)) {
                            sqlQuery.addScalar(field.getName(), StandardBasicTypes.SHORT);
                        } else if ((field.getType() == double.class) || (field.getType() == Double.class)) {
                            sqlQuery.addScalar(field.getName(), StandardBasicTypes.DOUBLE);
                        } else if ((field.getType() == float.class) || (field.getType() == Float.class)) {
                            sqlQuery.addScalar(field.getName(), StandardBasicTypes.FLOAT);
                        } else if ((field.getType() == boolean.class) || (field.getType() == Boolean.class)) {
                            sqlQuery.addScalar(field.getName(), StandardBasicTypes.BOOLEAN);
                        } else if (field.getType() == String.class) {
                            sqlQuery.addScalar(field.getName(), StandardBasicTypes.STRING);
                        } else if (field.getType() == Date.class) {
                            sqlQuery.addScalar(field.getName(), StandardBasicTypes.TIMESTAMP);
                        }
                    }
                }
            }
        }
    }

	@Override
	public Object getBeansBySql(String sql, Map<String, Object> params,Class<?> clazz,String[] fieldNames) {
        SQLQuery query = getCurrentSession().createSQLQuery(sql);
        addSclar(query, clazz,fieldNames);
        setParameters(params, query);
        query.setResultTransformer(Transformers.aliasToBean(clazz));
        return query.list();
	}
}
