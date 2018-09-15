package cn.lonsun.dbimport.service.base;

import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.util.ThreadUtil;
import cn.lonsun.datacollect.util.JdbcUtil;
import cn.lonsun.dbimport.service.IImportService;
import cn.lonsun.dbimport.service.impl.Ex7PublicContentImportService;
import cn.lonsun.util.HibernateHandler;
import cn.lonsun.util.HibernateSessionUtil;
import cn.lonsun.util.LoginPersonUtil;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Attribute;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.eclipse.jetty.util.BlockingArrayQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public abstract class BaseImportService implements IImportService {

    /**
     * 命令
     */
    public enum Command{

        stop("终止命令");

        Command(String text) {
            this.text = text;
        }

        private String text;

        public String getText() {
            return text;
        }
    }

    /**
     * 进度信息队列的大小
     */
    protected Long queueSize = 300l;

    protected Logger log = LoggerFactory.getLogger(getClass());

    private Element sqlConfig;

    private JdbcUtil jdbcutil;

    protected final String group;

    protected static ThreadLocal<Long> siteId = new ThreadLocal<Long>();

    /**
     * 根据配置文件初始化，如果group传空， 则需要重写initjdbc方法
     * @param group
     * @author zhongjun
     */
    public BaseImportService(String group) {
        this.group = group;
        reloadConfig();
    }

    /**
     * 线程命令
     *
     */
    public final static Map<String, Command> taskCommand = new Hashtable<String, Command>();

    /**
     * 终止导入
     * @param taskId
     * @return
     */
    public final boolean stopImport(String taskId){
        try {
            addProcess(taskId, "开始终止导入");
            taskCommand.put(taskId, Command.stop);
        } catch (Exception e) {
            log.error("任务终止失败：" + e.getMessage());
            return false;
        }
        return true;
    }

    @Override
    public final void reloadConfig() {
        Element old = sqlConfig;
        try {
            sqlConfig = null;
            sqlConfig = getSqlConfig();
        } catch (Exception e) {
            sqlConfig = old;
            e.printStackTrace();
            log.error("sql配置读取失败：{}", e.getCause());
            throw new RuntimeException(e);
        }
        JdbcUtil oldJdbc = jdbcutil;
        try {
            jdbcutil = initJdbc();
        } catch (Exception e) {
            //出错后还原
            sqlConfig = old;
            jdbcutil = oldJdbc;

            e.printStackTrace();
            log.error("数据库连接池初始化失败：{}", e.getCause());
            throw new RuntimeException(e);
        }
    }

    private JdbcUtil initJdbc() throws Exception {
        Element groupElement = sqlConfig.element(group);
        Attribute url = groupElement.attribute("url");
        Attribute user = groupElement.attribute("user");
        Attribute password = groupElement.attribute("password");
        return new JdbcUtil(url.getStringValue(), user.getStringValue(), password.getStringValue());
    }

    public String doBefore(){
        return UUID.randomUUID().toString();
    }

    public Collection<String> getProcess(String taskId){
        Queue<String> q = Ex7PublicContentImportService.importMsg.get(taskId);
        if(q == null || q.isEmpty()){
            List result = new ArrayList();
            result.add("任务不存在");
        }
        return q;
    }

    public void addProcess(String taskId, String msg){
        Queue<String> q = Ex7PublicContentImportService.importMsg.get(taskId);
        if(q== null){
            q = new BlockingArrayQueue<String>();
            Ex7PublicContentImportService.importMsg.put(taskId, q);
        }else{
            removeImportMsg(q);
        }
        q.add(new SimpleDateFormat("MM-dd HH:mm:ss S  ").format(new Date()) + msg);
    }

    /**
     * 删除队列中多余的数据
     * @param q
     */
    private void removeImportMsg(Queue<String> q){
        if(q.size()>= queueSize - 10){
            q.poll();
            removeImportMsg(q);
        }
    }

    /**
     * 获取一级节点
     * @return
     * @throws Exception
     */
    protected Element getSqlConfig() throws RuntimeException {
        if(sqlConfig == null){
            try {
                //创建SAXReader对象
                SAXReader reader = new SAXReader();
                InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("dbimport.sql.xml");
                //读取文件 转换成Document
                sqlConfig = reader.read(is).getRootElement();
            } catch (Exception e) {
                log.error("sql配置读取失败：{}", e.getCause());
                throw new BaseRunTimeException("获取sql失败：" + e.getMessage());
            }
        }
        return sqlConfig;
    }

    /**
     * 获取root->group 下的sql
     * @param group
     * @param key
     * @return
     */
    protected String getSql(String group, String key){
        Element sqlElement = null;
        if(group != null){
            sqlElement = getSqlConfig().element(group);
        }
        sqlElement = sqlElement.element(key);
        return sqlElement.getTextTrim();
    }

    /**
     * @param group 如果为空，则直接根据key从根节点下查询相应的节点
     * @param key
     * @param param
     * @return
     */
    protected <T> List<T> queryListBySqlKey(String group, String key, Class<T> convertBean, Object... param){
        String sql = getSql(group, key);
        return jdbcutil.getJdbcTemplate().query(sql, new BeanPropertyRowMapper<T>(convertBean,false), param);
    }

    /**
     * @param group 如果为空，则直接根据key从根节点下查询相应的节点
     * @param key
     * @param param
     * @return
     */
    protected SqlRowSet queryResultSetBySqlKey(String group, String key, Object... param){
        String sql = getSql(group, key);
        if(StringUtils.isEmpty(sql)){
            throw new BaseRunTimeException("sql 不存在");
        }
        return jdbcutil.getJdbcTemplate().queryForRowSet(sql, param);
    }

    /**
     * @param group 如果为空，则直接根据key从根节点下查询相应的节点
     * @param key
     * @param param
     * @return
     */
    protected List<Map<String, Object>> queryMapBySqlKey(String group, String key, Object... param){
        String sql = getSql(group, key);
        return jdbcutil.getJdbcTemplate().queryForList(sql, param);
    }

    @Override
    public final Object doImport() {
        final String taskId = this.doBefore();
        final Long site = LoginPersonUtil.getSiteId();
        final Map<String, Object> map = ThreadUtil.session.get();
        final BaseImportService current = this;
        current.addProcess(taskId, ">异步线程线程数据处理完成");
        new Thread(new Runnable() {
            @Override
            public void run() {
                ThreadUtil.set(map);
                HibernateSessionUtil.execute(new HibernateHandler<Object>() {
                    @Override
                    public Object execute() throws Throwable {
                        //设置为当前选择的站点id
                        siteId.set(site);
                        return current.excuteImport(taskId);
                    }
                    @Override
                    public Object complete(Object result, Throwable exception) {
                        if(exception != null){
                            current.addProcess(taskId, ">" + exception.getMessage());
                        }
                        current.afterImport(taskId);
                        return result;
                    }
                });
            }
        }).start();
        return taskId;
    }

    public abstract Object excuteImport(final String taskId);

    /**
     * 多线程任务等待
     * @param taskList
     */
    protected boolean waitTaskFinish(List<Future> taskList){
        try {
            //等待所有线程完成
            for(Future task : taskList){
                Object message = task.get();
                if(message != null){
                    throw new BaseRunTimeException(message.toString());
                }
            }
            taskList.clear();
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw new BaseRunTimeException(e.getMessage());
        } catch (ExecutionException e) {
            e.printStackTrace();
            throw new BaseRunTimeException(e.getMessage());
        }
        return true;
    }

    /**
     * 返回任务id
     * @param taskId
     * @return
     */
    public Object afterImport(String taskId){
        if(Ex7PublicContentImportService.importMsg.containsKey(taskId)){
            Ex7PublicContentImportService.importMsg.remove(taskId);
        }
        return taskId;
    }

    @Override
    public List doBackUp() {
        return null;
    }

    @Override
    public void revert(Long task_id) throws BaseRunTimeException {

    }


}
