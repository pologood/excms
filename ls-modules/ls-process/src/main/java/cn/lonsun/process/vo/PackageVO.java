
package cn.lonsun.process.vo;

import cn.lonsun.webservice.processEngine.vo.ProcessVO;

import java.util.List;

/**
 * 工作流过程包VO
 *@date 2014-12-15 15:13  <br/>
 *@author zhusy  <br/>
 *@version v1.0  <br/>
 */
public class PackageVO {

    private Long moduleId;
    private Long packageId;
    private String packageName;
    private List<ProcessVO> processList;

    public List<ProcessVO> getProcessList() {
        return processList;
    }

    public void setProcessList(List<ProcessVO> processList) {
        this.processList = processList;
    }

    public Long getModuleId() {
        return moduleId;
    }

    public void setModuleId(Long moduleId) {
        this.moduleId = moduleId;
    }

    public Long getPackageId() {
        return packageId;
    }

    public void setPackageId(Long packageId) {
        this.packageId = packageId;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }
}
