/**
 *TODO          <br/>
 *@date 2014-12-3 9:33  <br/> 
 *@author zhusy  <br/>
 *@version v1.0  <br/>
 */
package cn.lonsun.common.fileupload.internal.dao;

import cn.lonsun.common.fileupload.internal.entity.FileEO;
import cn.lonsun.core.base.dao.IBaseDao;

import java.util.List;

/**
 * Created by Administrator on 2014-12-3.
 */
public interface IFileDao extends IBaseDao<FileEO> {

    List<FileEO> getFilesByIds(Long[] fileIds);

    /**
     * 获取实体所拥有的文件
     * @param caseType
     * @param caseId
     * @return
     */
    List<FileEO> getFilesByCase(String caseType, Long caseId,Integer ...status);
    
    /**
     * 获取实体所拥有的文件
     * @param caseId
     * @return
     */
    List<FileEO> getFilesByCaseId(Long caseId);

    /**
     * 获取实体下没有序号的文件
     * @param caseType
     * @param caseId
     * @return
     */
    List<FileEO> getFilesByCaseNoIndex(String caseType, Long caseId);

    /**
     * 获取实体所拥有附件最大序号
     * @param caseType
     * @param caseId
     * @return
     */
    Integer getMaxIndex(String caseType, Long caseId);


}
