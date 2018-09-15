/**
 *TODO          <br/>
 *@date 2014-12-3 9:33  <br/> 
 *@author zhusy  <br/>
 *@version v1.0  <br/>
 */
package cn.lonsun.common.fileupload.internal.dao.impl;

import cn.lonsun.common.fileupload.internal.dao.IFileDao;
import cn.lonsun.common.fileupload.internal.entity.FileEO;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.base.dao.impl.BaseDao;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2014-12-3.
 */
@Repository("fileDao")
public class FileDaoImpl extends BaseDao<FileEO> implements IFileDao {


    @Override
    public List<FileEO> getFilesByIds(Long[] fileIds) {
        String hql = "from cn.lonsun.common.fileupload.internal.entity.FileEO t where t.fileId in (:ids) order by t.createDate asc ";
        Map<String,Object> params = new HashMap<String,Object>();
        params.put("ids",fileIds);
        return this.getEntitiesByHql(hql,params);
    }

    /**
     * 获取实体所拥有的文件
     *
     * @param caseType
     * @param caseId
     * @param status
     * @return
     */
    @Override
    public List<FileEO> getFilesByCase(String caseType, Long caseId, Integer... status){

        StringBuilder hql = new StringBuilder("from cn.lonsun.common.fileupload.internal.entity.FileEO t where t.caseType=? and t.caseId=? ");
        List<Object> list = new ArrayList<Object>();
        list.add(caseType);
        list.add(caseId);
        if(null != status && status.length > 0){
            hql.append(" and (");
            for(Integer obj : status){
                hql.append(" t.status = ? or");
                list.add(obj);
            }
            hql.delete(hql.length()-2,hql.length());
            hql.append(")");
        }
        hql.append(" order by t.fileIndex asc,t.createDate asc");
        return this.getEntitiesByHql(hql.toString(),list.toArray());
    }

    
    /**
     * 获取实体所拥有的文件
     *
     * @param caseId
     * @return
     */
    @Override
    public List<FileEO> getFilesByCaseId(Long caseId) {
        StringBuilder hql = new StringBuilder("from cn.lonsun.common.fileupload.internal.entity.FileEO t where  t.caseId=? ");
        List<Object> list = new ArrayList<Object>();
        list.add(caseId);
        hql.append(" order by t.version asc,t.createDate desc");
        return this.getEntitiesByHql(hql.toString(),list.toArray());
    }
    /**
     * 获取实体下没有序号的文件
     *
     * @param caseType
     * @param caseId
     * @return
     */
    @Override
    public List<FileEO> getFilesByCaseNoIndex(String caseType, Long caseId) {
        String hql = "from cn.lonsun.common.fileupload.internal.entity.FileEO t where t.caseType=? and t.caseId = ? and t.fileIndex = 0 and t.status = ? order by t.createDate asc ";
        return this.getEntitiesByHql(hql,new Object[]{caseType,caseId,FileEO.Status.Used.getValue()});
    }

    /**
     * 获取实体所拥有附件最大序号
     *
     * @param caseType
     * @param caseId
     * @return
     */
    @Override
    public Integer getMaxIndex(String caseType, Long caseId) {
        String hql = "select max(t.fileIndex) from cn.lonsun.common.fileupload.internal.entity.FileEO t where t.caseType=? and t.caseId = ? and (t.status=? or t.status=?)";
        Object result = this.getObject(hql,new Object[]{caseType,caseId,FileEO.Status.Used.getValue(),FileEO.Status.Delete.getValue()});
        return AppUtil.getInteger(result);
    }

}
