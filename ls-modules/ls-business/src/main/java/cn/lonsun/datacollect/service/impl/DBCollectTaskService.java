package cn.lonsun.datacollect.service.impl;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.base.service.impl.MockService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.datacollect.dao.IDBCollectTaskDao;
import cn.lonsun.datacollect.entity.DBCollectColumnEO;
import cn.lonsun.datacollect.entity.DBCollectDataEO;
import cn.lonsun.datacollect.entity.DBCollectTaskEO;
import cn.lonsun.datacollect.service.IDBCollectColumnService;
import cn.lonsun.datacollect.service.IDBCollectDataService;
import cn.lonsun.datacollect.service.IDBCollectTaskService;
import cn.lonsun.datacollect.util.JdbcUtil;
import cn.lonsun.datacollect.vo.CollectPageVO;
import cn.lonsun.util.LoginPersonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author gu.fei
 * @version 2016-1-21 14:36
 */
@Service("dbCollectTaskService")
public class DBCollectTaskService extends MockService<DBCollectTaskEO> implements IDBCollectTaskService {

    @Autowired
    private IDBCollectTaskDao dbCollectTaskDao;

    @Autowired
    private IDBCollectColumnService dbCollectColumnService;

    @Autowired
    private IDBCollectDataService dbCollectDataService;

    @Override
    public Pagination getPageEOs(CollectPageVO vo) {
        return dbCollectTaskDao.getPageEOs(vo);
    }

    @Override
    public void saveEO(DBCollectTaskEO eo) {
        Long siteId = LoginPersonUtil.getSiteId();
        eo.setSiteId(siteId);
        this.saveEntity(eo);
    }

    @Override
    public void updateEO(DBCollectTaskEO eo) {
        DBCollectTaskEO taskEO = this.getEntity(DBCollectTaskEO.class, eo.getId());
        taskEO.setTaskName(eo.getTaskName());
        taskEO.setColumnId(eo.getColumnId());
        taskEO.setcSiteId(eo.getcSiteId());
        taskEO.setDbType(eo.getDbType());
        taskEO.setIp(eo.getIp());
        taskEO.setPort(eo.getPort());
        taskEO.setDataBase(eo.getDataBase());
        taskEO.setUsername(eo.getUsername());
        taskEO.setPassword(eo.getPassword());
        taskEO.setCollectTable(eo.getCollectTable());
        taskEO.setCollectCondition(eo.getCollectCondition());
        taskEO.setCollectCount(eo.getCollectCount());
        this.updateEntity(taskEO);
    }

    @Override
    public void deleteEOs(Long[] ids) {
        dbCollectTaskDao.deleteEOs(ids);
    }

    @Override
    public void execTask(Long taskId) throws Exception {
        DBCollectTaskEO task = this.getEntity(DBCollectTaskEO.class, taskId);
        if(null == task) {
            throw new Exception("任务不存在!");
        }

        List<DBCollectColumnEO> columns = dbCollectColumnService.getEOsByTaskId(taskId);
        if(null == columns || columns.isEmpty()) {
            throw new Exception("请配置采集字段!");
        }

        String tableName = task.getCollectTable();
        String dbUrl = null;
        String ip = task.getIp();
        Integer port = task.getPort();
        String dataBase = task.getDataBase();
        String username = task.getUsername();
        String password = task.getPassword();
        String dbType = task.getDbType();
        int count = task.getCollectCount();

        if("oracle".equals(dbType)) {
            dbUrl = "jdbc:oracle:thin:@" + ip + ":" + port + ":" + dataBase;
        } else if("mysql".equals(dbType)) {
            dbUrl = "jdbc:mysql://" + ip + ":" + port + "/" + dataBase;
        } else if("mssql".equals(dbType)) {
            dbUrl = "jdbc:sqlserver://" + ip + ":" + port + ";DatabaseName=" + dataBase;
        }

        String sql = " SELECT ";
        Map<String,Object> defaultMap = new HashMap<String, Object>();
        boolean flag = true;
        for(DBCollectColumnEO eo : columns) {
            defaultMap.put(eo.getToColumn(),eo.getDefaultValue());
            if(flag) {
                sql += eo.getFromColumn() + " AS " + eo.getToColumn() + " ";
                flag  = false;
            } else {
                sql += " , " + eo.getFromColumn() + " AS " + eo.getToColumn() + " ";
            }
        }
        String condition = " WHERE 1=1 ";

        if(null != task.getCollectCondition()) {
            condition += " AND " + task.getCollectCondition();
        }

        sql  += " FROM " + tableName + condition;

        if(count >= 0) {
            if("oracle".equals(dbType)) {
                sql += " " + " AND ROWNUM <= " + count;
            } else if("mysql".equals(dbType)) {
                sql += " " + " LIMIT 0," + count;
            } else if("mssql".equals(dbType)) {
                sql = "select top " + count + " a.* from (" + sql + ") a";
            }
        }

        JdbcUtil jdbcUtil = new JdbcUtil(dbUrl,username,password);
        List<Map<String,Object>> list = jdbcUtil.getJdbcTemplate().queryForList(sql);
        List<DBCollectDataEO> datas = new ArrayList<DBCollectDataEO>();
        if(null != list && !list.isEmpty()) {
            for(Map<String,Object> map : list) {
                DBCollectDataEO eo = new DBCollectDataEO();
                eo.setTaskId(taskId);
                eo.setTitle(AppUtil.isEmpty(String.valueOf(map.get("TITLE"))) ? String.valueOf(defaultMap.get("TITLE")) : String.valueOf(map.get("TITLE")));
                eo.setContent(AppUtil.isEmpty(String.valueOf(map.get("TITLE"))) ? String.valueOf(defaultMap.get("CONTENT")) : String.valueOf(map.get("CONTENT")));
                eo.setAuthor(AppUtil.isEmpty(String.valueOf(map.get("AUTHOR"))) ? String.valueOf(defaultMap.get("AUTHOR")) : String.valueOf(map.get("AUTHOR")));
                eo.setEditor(AppUtil.isEmpty(String.valueOf(map.get("EDITOR"))) ? String.valueOf(defaultMap.get("EDITOR")) : String.valueOf(map.get("EDITOR")));
                eo.setTypeCode(DBCollectDataEO.TypeCode.articleNews.toString());
                eo.setSubTitle(AppUtil.isEmpty(String.valueOf(map.get("SUB_TITLE"))) ? String.valueOf(defaultMap.get("SUB_TITLE")) : String.valueOf(map.get("SUB_TITLE")));
                eo.setResources(AppUtil.isEmpty(String.valueOf(map.get("RESOURCES"))) ? String.valueOf(defaultMap.get("RESOURCES")) : String.valueOf(map.get("RESOURCES")));
                eo.setPublishDate(AppUtil.isEmpty(String.valueOf(map.get("PUBLISH_DATE"))) ? string2Date(String.valueOf(defaultMap.get("PUBLISH_DATE"))) : string2Date(String.valueOf(map.get("PUBLISH_DATE"))));
                datas.add(eo);
            }
        }
        dbCollectDataService.deleteByTaskId(taskId);
        dbCollectDataService.saveEntities(datas);
        task.setCollectDate(new Date());
        this.updateEntity(task);
    }

    private Date string2Date(String str) {
        if(AppUtil.isEmpty(str)) {
            return new Date();
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh-mm-ss");
        try {
            Date date = sdf.parse(str);
            return date;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Date();
    }
}
