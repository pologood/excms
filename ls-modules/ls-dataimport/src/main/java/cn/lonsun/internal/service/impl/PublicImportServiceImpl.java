package cn.lonsun.internal.service.impl;

import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.util.ThreadUtil;
import cn.lonsun.engine.DataImportCommand;
import cn.lonsun.engine.TaskExecutor;
import cn.lonsun.engine.vo.CommandParamVO;
import cn.lonsun.engine.vo.ImportResultVO;
import cn.lonsun.internal.entity.PublicUnitRelationEO;
import cn.lonsun.internal.metadata.DataModule;
import cn.lonsun.internal.metadata.ImportType;
import cn.lonsun.internal.service.IPublicCatalogRelationService;
import cn.lonsun.internal.service.IPublicImportService;
import cn.lonsun.internal.service.IPublicUnitRelationService;
import cn.lonsun.source.dataexport.vo.PublicContentQueryVO;
import cn.lonsun.util.HibernateHandler;
import cn.lonsun.util.HibernateSessionUtil;
import org.directwebremoting.annotations.Auth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * 信息公开导入
 * @author zhongjun
 */
@Service
public class PublicImportServiceImpl implements IPublicImportService {

    private  static final Logger log = LoggerFactory.getLogger("dataImport");

    @Autowired
    private IPublicUnitRelationService publicUnitRelationService;

    @Autowired
    private IPublicCatalogRelationService publicCatalogRelationService;

    private final TaskExecutor.CallBack importPublicCallback = new TaskExecutor.CallBack(){
        @Override
        public void setItemImportSuccess(ImportResultVO result) {

        }
    };

    public void importSite(final ImportType importType, final Long siteId, String organId, final String... oldIds){
        //查询单位配置
        final List<PublicUnitRelationEO> unitRelation = publicUnitRelationService.getByOldId(siteId, organId);
        if(unitRelation == null || unitRelation.isEmpty()){
            throw new BaseRunTimeException("公开单位关系未配置！");
        }
        //查询目录配置
        final Map<Long, String> catalogMap = publicCatalogRelationService.getMap(siteId);
        if(catalogMap == null || catalogMap.isEmpty()){
            throw new BaseRunTimeException("公开目录关系未配置！");
        }
        final Map<String, Object> sessionInfo = ThreadUtil.session.get();
        TaskExecutor.execute(new Callable<Long>() {
            @Override
            public Long call() throws Exception {
                // 将session数据绑定到线程
                ThreadUtil.set(sessionInfo);
                Long taskId = HibernateSessionUtil.execute(new HibernateHandler<Long>() {
                    @Override
                    public Long execute() throws Throwable {
                        Long taskId = null;
                        List<CommandParamVO<PublicContentQueryVO>> commandParamVO = new ArrayList<CommandParamVO<PublicContentQueryVO>>();
                        //循环生成
                        for(PublicUnitRelationEO item : unitRelation){
                            //依申请公开
                            commandParamVO.add(new CommandParamVO<PublicContentQueryVO>(siteId, DataModule.PublicApply,
                                    new PublicContentQueryVO(importType, item.getOldUnitId(), null, null,
                                            item.getNewUnitId(),oldIds )));
                            //依申请公开目录
                            commandParamVO.add(new CommandParamVO<PublicContentQueryVO>(siteId, DataModule.PublicApplyCatalog,
                                    new PublicContentQueryVO(importType, item.getOldUnitId(), null, null,
                                            item.getNewUnitId(),oldIds )));
                            //公开指南
                            commandParamVO.add(new CommandParamVO<PublicContentQueryVO>(siteId, DataModule.PublicGuide,
                                    new PublicContentQueryVO(importType, item.getOldUnitId(), null, null,
                                            item.getNewUnitId(),oldIds )));
                            //公开制度
                            commandParamVO.add(new CommandParamVO<PublicContentQueryVO>(siteId, DataModule.PublicInstitution,
                                    new PublicContentQueryVO(importType, item.getOldUnitId(), null, null,
                                            item.getNewUnitId(),oldIds )));
                            //公开年报
                            commandParamVO.add(new CommandParamVO<PublicContentQueryVO>(siteId, DataModule.PublicAnnualReport,
                                    new PublicContentQueryVO(importType, item.getOldUnitId(), null, null,
                                            item.getNewUnitId(),oldIds )));
                            //主动公开
                            for(Map.Entry<Long, String> catIdRelation : catalogMap.entrySet()){
                                commandParamVO.add(new CommandParamVO<PublicContentQueryVO>(siteId, DataModule.PublicContent,
                                        new PublicContentQueryVO(importType, item.getOldUnitId(), catIdRelation.getValue(),
                                                catIdRelation.getKey(), item.getNewUnitId(),oldIds )));
                            }
                            //一个单位导入一次
                            try {
                                taskId = TaskExecutor.executeSync(importPublicCallback, taskId, commandParamVO.toArray(new CommandParamVO[]{}));
                            } catch (BaseRunTimeException e) {
                                //如果是终止命令抛出的异常
                                if(DataImportCommand.Command.stop.name().equals(e.getMessage())){
                                    return taskId;
                                }
                                throw e;
                            }
                        }
                        return taskId;
                    }
                    @Override
                    public Long complete(Long result, Throwable exception) {
                        log.debug("异步导入完成，返回结果： {}", result);
                        return result;
                    }
                });
                return taskId;
            }
        });
    }

    @Override
    public void importPublicCatalog(ImportType importType, Long siteId,  String organId, String... oldIds) {
        
    }

    @Override
    public void importCatalog(ImportType importType, Long siteId,  String organId, String... catId) {

    }

    @Override
    public List getCatalog(Long siteId, String organId) {
        return null;
    }

    @Override
    public List getPublicOrgan() {
        return null;
    }
}
