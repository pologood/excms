package cn.lonsun.engine.vo;


import cn.lonsun.engine.vo.ExportQueryVO;
import cn.lonsun.internal.metadata.DataModule;

public class CommandParamVO<P extends ExportQueryVO> {

    public CommandParamVO(Long siteId, DataModule module, P queryVo) {
        this.siteId = siteId;
        this.module = module;
        this.queryVo = queryVo;
        if(queryVo != null){
            queryVo.setSiteId(siteId);
        }
    }

    private Long siteId;

    private Long taskId;

    private DataModule module;

    private P queryVo;

    public Long getSiteId() {
        return siteId;
    }

    public void setSiteId(Long siteId) {
        this.siteId = siteId;
    }

    public DataModule getModule() {
        return module;
    }

    public void setModule(DataModule module) {
        this.module = module;
    }

    public P getQueryVo() {
        return queryVo;
    }

    public void setQueryVo(P queryVo) {
        this.queryVo = queryVo;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

}
