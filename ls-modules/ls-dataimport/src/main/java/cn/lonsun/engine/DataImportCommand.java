package cn.lonsun.engine;

import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.util.SpringContextHolder;
import cn.lonsun.core.util.ThreadUtil;
import cn.lonsun.datasourcemanage.internal.entity.DataimportDataSourceEO;
import cn.lonsun.datasourcemanage.internal.service.IDataimportDataSourceService;
import cn.lonsun.engine.vo.CommandParamVO;
import cn.lonsun.engine.vo.ExportQueryVO;
import cn.lonsun.engine.vo.ImportResultVO;
import cn.lonsun.internal.metadata.DataModule;
import cn.lonsun.internal.metadata.DbType;
import cn.lonsun.internal.metadata.ImportType;
import cn.lonsun.jdbc.DataSourceUtil;
import cn.lonsun.jdbc.JdbcAble;
import cn.lonsun.jdbc.MonGoAble;
import cn.lonsun.source.dataexport.IExportService;
import cn.lonsun.target.dataimport.service.IImportService;
import cn.lonsun.util.HibernateHandler;
import cn.lonsun.util.HibernateSessionUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.AsyncListenableTaskExecutor;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

/**
 * 最小粒度的导入功能
 * @author zhongjun
 */
@Component("dataImportCommand")
public class DataImportCommand {

    private final Logger log = LoggerFactory.getLogger("dataImport");

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

    @Resource
    private AsyncListenableTaskExecutor taskExecutor;

    @Autowired
    private IDataimportDataSourceService dataSourceService;
    /**
     * 线程命令
     */
    private final static Map<Long, Command> taskCommand = new Hashtable<Long, Command>();

    public void stop(Long taskId) {
        taskCommand.put(taskId, Command.stop);
    }

    /**
     * @return
     */
    private String execute(CommandParamVO param) {
        validateStopCommand(param.getTaskId());
        log.debug("开始执行导入: siteId({}), module({}), importType({})", param.getSiteId(), param.getModule(), param.getQueryVo().getImportType());
        //根据模块获取导入导出处理类
        List oldDbDataList = getData(param);
        if(oldDbDataList == null || oldDbDataList.isEmpty()){
            return "";
        }
        validateStopCommand(param.getTaskId());
        boolean isSuccess = true;
        try {
            //TODO 修改导入状态为 导入到新库
            getImportService(param.getModule()).importData(oldDbDataList, param.getQueryVo().getImportType());
        } catch (Exception e) {
            e.printStackTrace();
            isSuccess = false;
            //TODO 保存错误信息
            //TODO 修改导入状态 导入异常
        } finally {
            if(isSuccess){
                //TODO 修改导入状态 导入完成
            }
        }
        return "导入成功";
    }

    /**
     * 异步导入，可以通过Future.get()实现异步等待
     * @return 返回task，根据task能获取到线程的执行结果
     */
    Future<ImportResultVO> executeAsync(final CommandParamVO param){
        final DataImportCommand command = this;
        final Map<String, Object> sessionInfo = ThreadUtil.session.get();
        validateStopCommand(param.getTaskId());
        return taskExecutor.submit(new Callable<ImportResultVO>() {
            @Override
            public ImportResultVO call() {
                // 将session数据绑定到线程
                ThreadUtil.set(sessionInfo);
                //绑定hibernateSession到线程
                String result = HibernateSessionUtil.execute(new HibernateHandler<String>() {
                    @Override
                    public String execute() throws Throwable {
                        return command.execute(param);
                    }
                    @Override
                    public String complete(String result, Throwable exception) {
                        log.debug("异步导入完成，返回结果： {}", result);
                        return result;
                    }
                });
                return new ImportResultVO(param, result);
            }
        });
    }

    private void validateStopCommand(Long taskId) {
        if(taskCommand.get(taskId) == Command.stop){
            throw new BaseRunTimeException(Command.stop.name());
        }
    }

    /**
     * 根据参数查询数据
     * importType = ImportType.appoint 时调用 getDataByIds
     * @param param
     * @param <E>
     * @return
     */
    public <E> List<E> getData(CommandParamVO param){
        DataimportDataSourceEO dataSourceConfig = getDataSourceConfig(param.getSiteId(), param.getModule());
        IExportService exportService = initExportService(dataSourceConfig, param.getModule(), param.getQueryVo());
        if(param.getQueryVo().getImportType() == ImportType.appoint){
            return exportService.getDataByIds(param.getQueryVo(), param.getQueryVo().getOldContentIds());
        }
        return exportService.getDataList(param.getQueryVo());
    }

    private IExportService initExportService(DataimportDataSourceEO dataSourceConfig, DataModule module, ExportQueryVO queryVo){
        DbType dbType = DbType.mongodb;
        IExportService exportService = getExportService("ex7",module);
        if(DbType.mongodb == dbType) { //如果数据库类型时mongoDB
            //如果不是继承 mongoService
            if(!(exportService instanceof MonGoAble)){
                throw new RuntimeException("数据库类型配置为mongodb,获取到处理类未继承抽象类 MonGoAble!");
            }
            MonGoAble monGoAble = (MonGoAble)exportService;
            monGoAble.setMongoDb(DataSourceUtil.getMongoDb(dataSourceConfig));
//        }else if(DbType.access == dbType){ //如果数据库类型时mongoDB
//            //如果不是继承 mongoService
//            if(!(exportService instanceof JdbcAble)){
//                throw new RuntimeException("数据库类型配置为mongodb,获取到处理类未继承抽象类 MonGoAble!");
//            }
        }else{
            //如果不是继承 mongoService
            if(!(exportService instanceof JdbcAble)){
                throw new RuntimeException("数据库类型配置为mongodb,获取到处理类未继承抽象类 MonGoAble!");
            }
            JdbcAble jdbcAble = (JdbcAble)exportService;
            jdbcAble.setDataSource(DataSourceUtil.getDataSource(dataSourceConfig));
        }
        return exportService;
    }

    /**
     * 查询常用数据库的数据
     * @param dbConfig
     * @param siteId
     * @param module
     * @param queryVo
     * @param <E>
     * @return
     */
    private <E> List<E> queryData(DataimportDataSourceEO dbConfig, Long siteId, DataModule module, ExportQueryVO queryVo){
        DataSource dataSource = DataSourceUtil.getDataSource(dbConfig);
        List<E> result = queryDataBySql(dataSource, siteId,module, queryVo);
        //如果根据sql查询到结果集了，则返回结果集，否则获取对应模块代码查询数据
        if(result != null){
            return result;
        }
        Object service = getExportService("ex7", module);
        //如果不是继承mongoService
        if(service instanceof IExportService){
            throw new RuntimeException("数据库类型不匹配,希望 IExportService!");
        }
        JdbcAble exportService = (JdbcAble)service;
        exportService.setDataSource(dataSource);
        return ((IExportService)service).getDataList(queryVo);
    }

    /**
     * 查询常用数据库的数据, 这是一个通用的方法，根据queryVo.sqlConfig 字段来查询数据,
     * 如果该字段为空，则不会执行查询，返回null
     * @param siteId
     * @param module
     * @param queryVo
     * @param <E>
     * @return
     */
    private <E> List<E> queryDataBySql(DataSource dataSource, Long siteId, DataModule module, ExportQueryVO queryVo){
        if(StringUtils.isEmpty(queryVo.getSqlConfig())){
            return null;
        }
        Object service = SpringContextHolder.getBean("sqlExportService");
        JdbcAble exportService = (JdbcAble)service;
        exportService.setDataSource(dataSource);
        return ((IExportService)service).getDataList(queryVo);
    }

    private DataimportDataSourceEO getDataSourceConfig(Long siteId, DataModule module){
        return dataSourceService.getDataSourceBySite(siteId,module);
    }

    /**
     * 根据模块编码，获取代码实现类，mongodb的查询类
     * @param manufacturer 厂商
     * @param module 模块
     * @return
     */
    private IExportService getExportService(String manufacturer, DataModule module){
        String beanName = manufacturer + module + "ExportService";
        Object exportService = SpringContextHolder.getBean(beanName);
        return (IExportService)exportService;
    }

    /**
     * 根据模块编码，获取代码实现类
     * @param module 模块
     * @return
     */
    private IImportService getImportService(DataModule module){
        return (IImportService)SpringContextHolder.getBean(module + "ImportService");
    }

}
