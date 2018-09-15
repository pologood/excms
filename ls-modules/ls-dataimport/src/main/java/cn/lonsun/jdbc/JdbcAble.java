package cn.lonsun.jdbc;

import cn.lonsun.core.exception.BaseRunTimeException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import javax.sql.DataSource;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * jdbc查询模块
 * @author zhongjun
 */
public abstract class JdbcAble<E> extends SqlConfigAble {

    private final static String defaultConfigFilePath = "dataexport/dataexport.sql";

    protected static Logger log = LoggerFactory.getLogger("dataImport");

    protected final Class<E> persistentClass;

    public JdbcAble() {
        this(defaultConfigFilePath);
    }

    /**
     *
     * @param configFilePath 会自动拼上后缀.xml,无需手动添加后缀
     */
    public JdbcAble(String configFilePath) {
        super(configFilePath);
        this.persistentClass = (Class<E>)getSuperClassGenricType(getClass(), 0);
    }

    /**
     * 通过反射, 获得定义Class时声明的父类的泛型参数的类型. 如无法找到, 返回Object.class.
     *@param clazz clazz The class to introspect
     * @param index  the Index of the generic ddeclaration,start from 0.
     * @return the index generic declaration, or Object.class if cannot be  determined
     */
    @SuppressWarnings("unchecked")
    public static Class<Object> getSuperClassGenricType(final Class clazz, final int index) {

        //返回表示此 Class 所表示的实体（类、接口、基本类型或 void）的直接超类的 Type。
        Type genType = clazz.getGenericSuperclass();

        if (!(genType instanceof ParameterizedType)) {
            return Object.class;
        }
        //返回表示此类型实际类型参数的 Type 对象的数组。
        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();

        if (index >= params.length || index < 0) {
            return Object.class;
        }
        if (!(params[index] instanceof Class)) {
            return Object.class;
        }

        return (Class) params[index];
    }

    private DataSource dataSource;

    private JdbcTemplate jdbcTemplate;

    /**
     * @param manufacturer 如果为空，则直接根据key从根节点下查询相应的节点
     * @param key sql配置的key
     * @param convertBean 返回实体类
     * @param param sql参数
     * @return
     */
    protected <T> List<T> queryListBySqlKey(String manufacturer, String key, Class<T> convertBean, Object... param){
        String sql = getSql(manufacturer, key);
        if(StringUtils.isEmpty(sql)){
            throw new BaseRunTimeException("sql 不存在");
        }
        return queryBeanList(sql, convertBean, param);
    }

    /**
     * @param manufacturer 如果为空，则直接根据key从根节点下查询相应的节点
     * @param key
     * @param param
     * @return
     */
    protected List<E> queryListBySqlKey(String manufacturer, String key, Object... param){
        String sql = getSql(manufacturer, key);
        if(StringUtils.isEmpty(sql)){
            throw new BaseRunTimeException("sql 不存在");
        }
        return queryBeanList(sql, persistentClass, param);
    }

    /**
     * @param manufacturer 如果为空，则直接根据key从根节点下查询相应的节点
     * @param key
     * @param param
     * @return
     */
    protected SqlRowSet queryResultSetBySqlKey(String manufacturer, String key, Object... param){
        String sql = getSql(manufacturer, key);
        if(StringUtils.isEmpty(sql)){
            throw new BaseRunTimeException("sql 不存在");
        }
        return queryResultSet(sql, param);
    }

    /**
     * @param manufacturer 如果为空，则直接根据key从根节点下查询相应的节点
     * @param key
     * @param param
     * @return
     */
    protected List<Map<String, Object>> queryMapBySqlKey(String manufacturer, String key, Object... param){
        String sql = getSql(manufacturer, key);
        if(StringUtils.isEmpty(sql)){
            throw new BaseRunTimeException("sql 不存在");
        }
        return queryMap(sql, param);
    }

    /**
     * @param sql sql
     * @param param 参数
     * @return
     */
    protected List<E> queryList(String sql, Object... param){
        return getJdbcTemplate().query(sql, new BeanPropertyRowMapper<E>(persistentClass,false), param);
    }

    /**
     * @param sql sql
     * @param param 参数
     * @return
     */
    protected <F> List<F> queryBeanList(String sql, Class<F> convertBean, Object... param){
        return getJdbcTemplate().query(sql, new BeanPropertyRowMapper<F>(convertBean,false), param);
    }

    /**
     * @param sql
     * @param param
     * @return
     */
    protected SqlRowSet queryResultSet(String sql, Object... param){
        return jdbcTemplate.queryForRowSet(sql, param);
    }

    /**
     * @param param
     * @return
     */
    protected  List<Map<String, Object>> queryMap(String sql, Object... param){
        return getJdbcTemplate().queryForList(sql, param);
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
        this.setJdbcTemplate(new JdbcTemplate(dataSource));
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    protected JdbcTemplate getJdbcTemplate(){
        return this.jdbcTemplate;
    }
}
