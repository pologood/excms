package cn.lonsun.mongodb.base.impl;

import cn.lonsun.common.util.AppUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class MongoDbBaseDao<T> {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    protected MongoTemplate mongoTemplate;

    /**
     * 钩子方法,由子类实现返回反射对象的类型
     *
     * @return
     */
    protected abstract Class<T> getEntityClass();

    /**
     * 保存一个对象
     *
     * 
     * @param t
     * @return
     */
    public void save(T t) {
        logger.info("[Mongo dao ]save:" + t);
        this.mongoTemplate.save(t);
    }

    /**
     * 根据Id从Collection中查询对象
     *
     * 
     * @param id
     *            实体对象的Id,对应Collection中记录的_id字段.
     *            <p>
     *            需要说明的是,Mongdo自身没有主键自增机制.解决方法
     *            <ol>
     *            <li>实体入库的时候,程序中为实体赋主键值.
     *            <li>实体入库的时候,在mongodb中自定义函数实现主键自增机制.定义方法同js代码类似
     *            </ol>
     *            </p>
     * @return
     */
    public T queryById(Object id) {
        if(null == id) return null;
        if(null == AppUtil.getLong(id)) return null;
        Query query = new Query();
        Criteria criteria = Criteria.where("_id").is(id);
        query.addCriteria(criteria);
        logger.info("[Mongo dao ]queryById:" + query);
        return this.mongoTemplate.findOne(query, getEntityClass());
    }

    /**
     * 根据ids 数组查询
     * @return
     */
    public List<T> queryListByIds(Object[] ids) {
        int n = 5000;
        int count = ids.length/n;
        List<T> list = new ArrayList<T>();
        for(int i = 0;i < count ;i++) {
            Query query = new Query();
            Object[] newarr = Arrays.copyOfRange(ids,i*n,(i+1)*n);
            Criteria criteria = Criteria.where("_id").in(newarr);
            query.addCriteria(criteria);
            logger.info("[Mongo dao ]queryById:" + query);
            list.addAll(this.mongoTemplate.find(query,getEntityClass()));
        }

        if(ids.length > 0) {
            Query query = new Query();
            Object[] lastarr = Arrays.copyOfRange(ids,count*n,ids.length);
            Criteria criteria = Criteria.where("_id").in(lastarr);
            query.addCriteria(criteria);
            logger.info("[Mongo dao ]queryById:" + query);
            list.addAll(this.mongoTemplate.find(query,getEntityClass()));
        }
//        return this.mongoTemplate.find(query,getEntityClass());
        return list;
    }

    /**
     * 根据条件查询集合
     *
     * @param query
     *            查询条件
     * @return 满足条件的集合
     */
    public List<T> queryList(Query query) {
        logger.info("[Mongo dao ]queryList:" + query);
        return this.mongoTemplate.find(query, getEntityClass());
    }

    /**
     * 通过条件查询单个实体
     *
     * @param query
     * @return
     */
    public T queryOne(Query query) {
        logger.info("[Mongo dao ]queryOne:" + query);
        return this.mongoTemplate.findOne(query, getEntityClass());
    }

    /**
     * 通过条件进行分页查询
     *
     * @param query
     *            查询条件
     * @param start
     *            查询起始值 <strong> 类似mysql查询中的 limit start, size 中的 start</strong>
     * @param size
     *            查询大小 <strong> 类似mysql查询中的 limit start, size 中的 size</strong>
     * @return 满足条件的集合
     */
    public List<T> getPage(Query query, int start, int size) {
        query.skip(start);
        query.limit(size);
        logger.info("[Mongo dao ]queryPage:" + query + "(" + start + "," + size + ")");
        List<T> lists = this.mongoTemplate.find(query, getEntityClass());
        return lists;
    }

    /**
     * 根据条件查询库中符合记录的总数,为分页查询服务
     *
     * @param query
     *            查询条件
     * @return 满足条件的记录总数
     */
    public Long getPageCount(Query query) {
        logger.info("[Mongo dao ]queryPageCount:" + query);
        return this.mongoTemplate.count(query, getEntityClass());
    }

    /**
     * 根据Id删除用户
     *
     * @param id
     */
    public void deleteById(Object id) {
        Criteria criteria = Criteria.where("_id").is(id);
        if (null != criteria) {
            Query query = new Query(criteria);
            logger.info("[Mongo dao ]deleteById:" + query);
            if (null != query && this.queryOne(query) != null) {
                this.delete(query);
            }
        }
    }

    /**
     * 根据Id删除用户
     *
     * @param id
     */
    public void removeById(Object id) {
        Criteria criteria = Criteria.where("_id").is(id);
        if (null != criteria) {
            Query query = new Query(criteria);
            logger.info("[Mongo dao ]deleteById:" + query);
            this.delete(query);
        }
    }

    /**
     * 根据Ids删除用户
     *
     * @param ids
     */
    public void removeByIds(Object... ids) {
        Criteria criteria = Criteria.where("_id").in(ids);
        if (null != criteria) {
            Query query = new Query(criteria);
            logger.info("[Mongo dao ]deleteById:" + query);
            this.delete(query);
        }
    }

    public void delete(Query query) {
        mongoTemplate.remove(query, getEntityClass());
    }

    public void removeOne(Query query) {
        if (query != null && mongoTemplate.findOne(query, getEntityClass()) != null)
            mongoTemplate.remove(mongoTemplate.findOne(query, getEntityClass()));
    }

    /**
     * 删除对象
     *
     * @param t
     */
    public void delete(T t) {
        logger.info("[Mongo dao ]delete:" + t);
        this.mongoTemplate.remove(t);
    }

    /**
     * 更新满足条件的第一个记录
     *
     * @param query
     * @param update
     */
    public void updateFirst(Query query, Update update) {
        logger.info("[Mongo dao ]updateFirst:query(" + query + "),update(" + update + ")");
        this.mongoTemplate.updateFirst(query, update, getEntityClass());
    }

    /**
     * 更新满足条件的所有记录
     *
     * @param query
     * @param update
     */
    public void updateMulti(Query query, Update update) {
        logger.info("[Mongo dao ]updateMulti:query(" + query + "),update(" + update + ")");
        this.mongoTemplate.updateMulti(query, update, getEntityClass());
    }

    /**
     * 查找更新,如果没有找到符合的记录,则将更新的记录插入库中
     *
     * @param query
     * @param update
     */
    public void updateInser(Query query, Update update) {
        logger.info("[Mongo dao ]updateInser:query(" + query + "),update(" + update + ")");
        this.mongoTemplate.upsert(query, update, getEntityClass());
    }
}