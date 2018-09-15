package cn.lonsun.datacollect;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.lonsun.activemq.MessageSender;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.core.util.SpringContextHolder;
import cn.lonsun.datacollect.entity.DBCollectColumnEO;
import cn.lonsun.datacollect.entity.DBCollectDataEO;
import cn.lonsun.datacollect.entity.DBCollectTaskEO;
import cn.lonsun.datacollect.service.IDBCollectColumnService;
import cn.lonsun.datacollect.service.IDBCollectDataService;
import cn.lonsun.datacollect.service.IDBCollectTaskService;
import cn.lonsun.datacollect.util.JdbcUtil;
import cn.lonsun.datacollect.vo.CollectPageVO;
import cn.lonsun.message.internal.entity.MessageSystemEO;
import cn.lonsun.site.template.util.ResponseData;
import cn.lonsun.util.ColumnUtil;
import cn.lonsun.util.ConcurrentUtil;
import cn.lonsun.util.LoginPersonUtil;

/**
 * @author gu.fei
 * @version 2016-2-17 8:32
 */
@Controller
@RequestMapping("/db/collect")
public class DBCollectController extends BaseController {

    private static final String FILE_BASE = "/datacollect/db/";

    @Autowired
    private IDBCollectTaskService dbCollectTaskService;

    @Autowired
    private IDBCollectColumnService dbCollectColumnService;

    @Autowired
    private IDBCollectDataService dbCollectDataService;

    @Autowired
    private TaskExecutor taskExecutor;

    @RequestMapping("/index")
    public String index() {
        return FILE_BASE + "index";
    }

    @RequestMapping("/addOrEditTask")
    public String addOrEditTask() {
        return FILE_BASE + "edit_task";
    }

    @RequestMapping("/contentRule")
    public String contentRule() {
        return FILE_BASE + "content_rule";
    }

    @RequestMapping("/addOrEditContentRule")
    public String addOrEditContentRule() {
        return FILE_BASE + "content_rule_edit";
    }

    @RequestMapping("/dataDetail")
    public String dataDetail(Long taskId,ModelMap map) {
        map.put("taskId",taskId);
        return FILE_BASE + "content_data";
    }

    @RequestMapping("/showDetail")
    public String showDetail() {
        return FILE_BASE + "show_detail";
    }

    @RequestMapping("/columnSelect")
    public String columnSelect() {
        return FILE_BASE + "column_select";
    }

    @RequestMapping("/quoteColumn")
    public String quoteColumn(Long columnId,Long cSiteId,ModelMap map) {
        map.put("columnId",columnId);
        map.put("cSiteId",cSiteId);
        map.put("columnName", ColumnUtil.getColumnName(columnId,cSiteId));
        return FILE_BASE + "quote_column";
    }

    /**
     * 数据预览
     * @return
     */
    @RequestMapping("/scanDetail")
    public String scanDetail(Long id,ModelMap map) {
        DBCollectDataEO eo = dbCollectDataService.getEntity(DBCollectDataEO.class,id);
        if(null != eo) {
            map.put("eo",eo);
        }
        return FILE_BASE + "scan_detail";
    }


    @ResponseBody
    @RequestMapping("/testConnect")
    public Object testConnect(DBCollectTaskEO eo) {
        String rst = null;
        String dbUrl = null;
        String ip = eo.getIp();
        Integer port = eo.getPort();
        String dataBase = eo.getDataBase();
        String dbType = eo.getDbType();

        if("oracle".equals(dbType)) {
            dbUrl = "jdbc:oracle:thin:@" + ip + ":" + port + ":" + dataBase;
        } else if("mysql".equals(dbType)) {
            dbUrl = "jdbc:mysql://" + ip + ":" + port + "/" + dataBase;
        } else if("mssql".equals(dbType)) {
            dbUrl = "jdbc:sqlserver://" + ip + ":" + port + ";DatabaseName=" + dataBase;
        }

        try {
            JdbcUtil jdbcUtil = new JdbcUtil(dbUrl,eo.getUsername(),eo.getPassword());
            rst = jdbcUtil.isConnected();
            jdbcUtil.close();
        } catch (Exception e) {
            rst = e.getMessage();
        }

        if(null == rst) {
            return ResponseData.success("连接成功!");
        }

        return ResponseData.fail("连接失败:" + rst);
    }

    /**
     * 分页获取采集任务
     * @param vo
     * @return
     */
    @ResponseBody
    @RequestMapping("/getPageTaskEOs")
    public Object getPageTaskEOs(CollectPageVO vo) {
        Pagination page = dbCollectTaskService.getPageEOs(vo);
        List<DBCollectTaskEO> list = (List<DBCollectTaskEO>) page.getData();

        for(DBCollectTaskEO eo : list) {
            if(null != eo.getColumnId()) {
                eo.setColumnName(ColumnUtil.getColumnName(eo.getColumnId(),eo.getcSiteId()));
            }
        }

        return page;
    }

    /**
     * 分页获取内容采集
     * @param vo
     * @return
     */
    @ResponseBody
    @RequestMapping("/getPageContentEOs")
    public Object getPageContentEOs(CollectPageVO vo) {
        return dbCollectColumnService.getPageEOs(vo);
    }

    /**
     * 分页获取采集数据
     * @param vo
     * @return
     */
    @ResponseBody
    @RequestMapping("/getPageDataEOs")
    public Object getPageDataEOs(CollectPageVO vo) {
        return dbCollectDataService.getPageEOs(vo);
    }

    /**
     * 保存采集任务
     * @param eo
     * @return
     */
    @ResponseBody
    @RequestMapping("/saveTask")
    public Object saveTask(DBCollectTaskEO eo) {
        dbCollectTaskService.saveEO(eo);
        return ResponseData.success("保存成功!");
    }

    /**
     * 更新采集任务
     * @param eo
     * @return
     */
    @ResponseBody
    @RequestMapping("/updateTask")
    public Object updateTask(DBCollectTaskEO eo) {
        dbCollectTaskService.updateEO(eo);
        return ResponseData.success("更新成功!");
    }

    /**
     * 删除任务
     * @param ids
     * @return
     */
    @ResponseBody
    @RequestMapping("/deleteTasks")
    public Object deleteTasks(@RequestParam(value="ids[]",required=false) Long[] ids) {
        dbCollectTaskService.deleteEOs(ids);
        return ResponseData.success("删除成功!");
    }

    /**
     * 保存内容规则
     * @param eo
     * @return
     */
    @ResponseBody
    @RequestMapping("/saveContentRule")
    public Object saveContentRule(DBCollectColumnEO eo) {
        dbCollectColumnService.saveEO(eo);
        return ResponseData.success("保存成功!");
    }

    /**
     * 更新内容规则
     * @param eo
     * @return
     */
    @ResponseBody
    @RequestMapping("/updateContentRule")
    public Object updateContentRule(DBCollectColumnEO eo) {
        dbCollectColumnService.updateEO(eo);
        return ResponseData.success("更新成功!");
    }

    /**
     * 删除内容规则
     * @param ids
     * @return
     */
    @ResponseBody
    @RequestMapping("/deleteContentRules")
    public Object deleteContentRules(@RequestParam(value="ids[]",required=false) Long[] ids) {
        dbCollectColumnService.deleteEOs(ids);
        return ResponseData.success("删除成功!");
    }

    /**
     * 获取表
     * @param eo
     * @return
     */
    @ResponseBody
    @RequestMapping("/getTables")
    public Object getTables(DBCollectTaskEO eo) {

        String dbUrl = null;
        String ip = eo.getIp();
        Integer port = eo.getPort();
        String dataBase = eo.getDataBase();
        String dbType = eo.getDbType();
        String username = eo.getUsername();
        String password = eo.getPassword();
        JdbcUtil jdbcUtil = null;

        String sql = null;
        if("oracle".equals(dbType)) {
            dbUrl = "jdbc:oracle:thin:@" + ip + ":" + port + ":" + dataBase;
            sql = "SELECT TABLE_NAME AS \"tableName\" FROM USER_TABLES ORDER BY TABLE_NAME";
        } else if("mysql".equals(dbType)) {
            dbUrl = "jdbc:mysql://" + ip + ":" + port + "/" + dataBase;
            sql = "SELECT TABLE_NAME AS  \"tableName\" FROM information_schema.tables WHERE table_schema='" + dataBase + "' AND table_type='base table' ORDER BY TABLE_NAME";
        } else if("mssql".equals(dbType)) {
            dbUrl = "jdbc:sqlserver://" + ip + ":" + port + ";DatabaseName=" + dataBase;
            sql = "SELECT Name AS  \"tableName\" FROM SysObjects Where XType='U' ORDER BY Name";
        }

        try {
            jdbcUtil = new JdbcUtil(dbUrl,username,password);
            //解决初始化错误问题
            if("mssql".equals(dbType)) {
                jdbcUtil.setDbType("sqlserver");
            }
            Object list = jdbcUtil.getJdbcTemplate().queryForList(sql);
            return getObject(list);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(null != jdbcUtil) {
                jdbcUtil.close();
            }
        }

        return null;
    }

    /**
     * 获取字段
     * @param taskId
     * @return
     */
    @ResponseBody
    @RequestMapping("/getFromColumns")
    public Object getColumns(Long taskId,String fromColumn) {
        DBCollectTaskEO eo = dbCollectTaskService.getEntity(DBCollectTaskEO.class, taskId);
        String tableName = eo.getCollectTable();
        String dbUrl = null;
        String ip = eo.getIp();
        Integer port = eo.getPort();
        String dataBase = eo.getDataBase();
        String dbType = eo.getDbType();
        String username = eo.getUsername();
        String password = eo.getPassword();
        JdbcUtil jdbcUtil = null;

        List<DBCollectColumnEO> eos = dbCollectColumnService.getEOsByTaskId(taskId);
        String exsit = null;
        for(DBCollectColumnEO _eo : eos) {
            if(_eo.getFromColumn().equals(fromColumn)) {
                continue;
            }
            if(null == exsit) {
                exsit = "'" + _eo.getFromColumn() + "'";
            } else {
                exsit += ",'" + _eo.getFromColumn() + "'";
            }
        }

        if("oracle".equals(dbType)) {
            dbUrl = "jdbc:oracle:thin:@" + ip + ":" + port + ":" + dataBase;
            String sql = "SELECT t1.COLUMN_NAME AS \"columnName\",t1.DATA_TYPE AS \"dataType\",t1.DATA_LENGTH AS \"dataLength\",t2.COMMENTS AS \"comments\"" +
                    " FROM user_tab_columns t1,user_col_comments t2 WHERE t1.TABLE_NAME='" + tableName + "' AND t1.TABLE_NAME  =t2.TABLE_NAME AND t1.COLUMN_NAME =t2.COLUMN_NAME ";
            if(null != exsit) {
                sql += " AND t1.COLUMN_NAME NOT IN(" + exsit + ")";
            }
            try {
                jdbcUtil = new JdbcUtil(dbUrl,username,password);
                Object list = jdbcUtil.getJdbcTemplate().queryForList(sql);
                return getObject(list);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if(null != jdbcUtil) {
                    jdbcUtil.close();
                }
            }
        } else if("mysql".equals(dbType)) {
            dbUrl = "jdbc:mysql://" + ip + ":" + port + "/" + dataBase;
            String sql = "SELECT COLUMN_NAME AS \"columnName\",COLUMN_TYPE AS \"columnType\",COLUMN_COMMENT AS \"comments\"  FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME='" + tableName + "' ";
            if(null != exsit) {
                sql += " AND COLUMN_NAME NOT IN(" + exsit + ")";
            }
            try {
                jdbcUtil = new JdbcUtil(dbUrl,username,password);
                Object list = jdbcUtil.getJdbcTemplate().queryForList(sql);
                return getObject(list);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if(null != jdbcUtil) {
                    jdbcUtil.close();
                }
            }
        } else if("mssql".equals(dbType)) {
            dbUrl = "jdbc:sqlserver://" + ip + ":" + port + ";DatabaseName=" + dataBase;
            String sql = "select b.name columnName,c.name dataType,c.length dataLength from sysobjects a,syscolumns b,systypes c where a.id=b.id " +
                    " and a.name='" + tableName + "' and a.xtype='U' " +
                    " and b.xtype=c.xtype ";
            if(null != exsit) {
                sql += " AND b.name NOT IN(" + exsit + ")";
            }
            try {
                jdbcUtil = new JdbcUtil(dbUrl,username,password);
                Object list = jdbcUtil.getJdbcTemplate().queryForList(sql);
                return getObject(list);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if(null != jdbcUtil) {
                    jdbcUtil.close();
                }
            }
        }
        return null;
    }

    /**
     * 执行采集任务
     * @param taskId
     * @return
     */
    @ResponseBody
    @RequestMapping("/execTask")
    public Object execTask(final Long taskId) {
        final Long userId = LoginPersonUtil.getUserId();
        final DBCollectTaskEO dbEO = dbCollectTaskService.getEntity(DBCollectTaskEO.class,taskId);
        taskExecutor.execute(new Runnable() {
            @Override
            public void run() {
                MessageSystemEO eo = new MessageSystemEO();
                eo.setLink("/db/collect/dataDetail?taskId=" + taskId);
                eo.setModeCode("dbCollect");
                eo.setRecUserIds(userId + "");
                eo.setTitle("数据库采集!");
                try {
                    SessionFactory sessionFactory = SpringContextHolder.getBean(SessionFactory.class);
                    boolean participate = ConcurrentUtil.bindHibernateSessionToThread(sessionFactory);
                    dbCollectTaskService.execTask(taskId);
                    eo.setMessageStatus(MessageSystemEO.MessageStatus.success.toString());
                    eo.setContent(dbEO.getTaskName() + "采集成功!");
                    ConcurrentUtil.closeHibernateSessionFromThread(participate, sessionFactory);
                } catch (Exception e) {
                    e.printStackTrace();
                    eo.setMessageStatus(MessageSystemEO.MessageStatus.error.toString());
                    eo.setContent(dbEO.getTaskName() + "采集失败,原因：" + e.getMessage());
                }
                MessageSender.sendMessage(eo);
            }
        });

        return ResponseData.success("任务正在采集!");
    }

    /**
     * 引用全部
     * @param columnId
     * @return
     */
    @ResponseBody
    @RequestMapping("/quoteAll")
    public Object quoteAll(Long columnId,Long cSiteId,Long taskId) {
        Map<String,Object> param = new HashMap<String, Object>();
        param.put("taskId",taskId);

        List<DBCollectDataEO> list = dbCollectDataService.getEntities(DBCollectDataEO.class, param);
        Long[] ids = new Long[list.size()];

        int i = 0;
        for(DBCollectDataEO eo : list) {
            ids[i++] = eo.getId();
        }

        dbCollectDataService.quoteData(columnId,cSiteId,ids);
        return ResponseData.success("引用成功!");
    }

    /**
     * 引用选中
     * @param columnId
     * @param ids
     * @return
     */
    @ResponseBody
    @RequestMapping("/quoteCheck")
    public Object quoteCheck(Long columnId,Long cSiteId,@RequestParam(value="ids[]",required=false) Long[] ids) {
        dbCollectDataService.quoteData(columnId,cSiteId,ids);
        return ResponseData.success("引用成功!");
    }
}
