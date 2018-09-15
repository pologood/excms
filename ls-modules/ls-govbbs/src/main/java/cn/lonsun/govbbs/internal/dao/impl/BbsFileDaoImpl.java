package cn.lonsun.govbbs.internal.dao.impl;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.base.dao.impl.BaseDao;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.govbbs.internal.dao.IBbsFileDao;
import cn.lonsun.govbbs.internal.entity.BbsFileEO;
import cn.lonsun.system.filecenter.internal.vo.FileCenterVO;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 论坛附件Dao实现类<br/>
 */

@Repository
public class BbsFileDaoImpl extends BaseDao<BbsFileEO> implements IBbsFileDao {
    /**
     * 获取论坛附件分页
     *
     */
    @Override
    public Pagination getPage(FileCenterVO fileVO) {
        StringBuffer hql=new StringBuffer("FROM BbsFileEO WHERE recordStatus = :recordStatus");

        Map<String,Object> map=new HashMap<String,Object>();
        map.put("recordStatus",fileVO.getRecordStatus());
        if(!AppUtil.isEmpty(fileVO.getFileName())){
            hql.append(" and fileName like :fileName escape'\\'");
            map.put("fileName", "%".concat(fileVO.getFileName()).concat("%"));
        }
        if(!AppUtil.isEmpty(fileVO.getSuffix())){
            hql.append(" and suffix like :suffix escape'\\'");
            map.put("suffix", "%".concat(fileVO.getSuffix()).concat("%"));
        }
        if(!AppUtil.isEmpty(fileVO.getType())){
            if("image".equals(fileVO.getType())){
                hql.append(" and (lower(suffix)='gif' or lower(suffix)='png' or lower(suffix)='jpg' or lower(suffix)='jpeg')");
            }else if("video".equals(fileVO.getType())){
                hql.append(" and (lower(suffix)='flv' or lower(suffix)='mp4')");
            }else if("audio".equals(fileVO.getType())){
                hql.append(" and (lower(suffix)='mp3' or lower(suffix)='wma')");
            }else if("text".equals(fileVO.getType())){
                hql.append(" and (lower(suffix)='txt' or lower(suffix)='pdf' or lower(suffix)='xml' or lower(suffix)='html' or lower(suffix)='js' or lower(suffix)='css' or lower(suffix)='doc' or lower(suffix)='docx' or lower(suffix)='xls' or lower(suffix)='xlsx')");
            }else if("noRef".equals(fileVO.getType())){
                hql.append(" and status=0");
            }else if("other".equals(fileVO.getType())){
                hql.append(" and (lower(suffix)!='gif' and lower(suffix)!='png' and lower(suffix)!='jpg' and lower(suffix)!='flv' and lower(suffix)!='mp4' and"
                        + " lower(suffix)!='mp3' and lower(suffix)!='wma' and lower(suffix)!='txt' and lower(suffix)!='xml' and lower(suffix)!='html' and lower(suffix)!='js' and lower(suffix)!='css' and lower(suffix)!='doc' and "
                        + "lower(suffix)!='docx' and lower(suffix)!='xls' and lower(suffix)!='jpeg' and lower(suffix)!='xlsx')");
            }
        }
        if(null!=fileVO.getSiteId()){
            hql.append(" and siteId=:siteId");
            map.put("siteId", fileVO.getSiteId());
        }
        if(fileVO.getStartDate()!=null){
            hql.append(" and createDate>:startDate");
            map.put("startDate", fileVO.getStartDate());
        }
        if(fileVO.getEndDate()!=null){
            hql.append(" and createDate<:endDate");
            map.put("endDate", fileVO.getEndDate());
        }
        hql.append(" order by createDate desc");
        return getPagination(fileVO.getPageIndex(), fileVO.getPageSize(), hql.toString(), map);
    }

    @Override
    public void deleteIds(Long[] ids) {
        String hql ="delete from BbsFileEO where id in (:ids)";
        getCurrentSession().createQuery(hql).setParameterList("ids", ids).executeUpdate();
    }

    @Override
    public List<BbsFileEO> getBbsFiles(Long caseId) {
        return getEntitiesByHql("from BbsFileEO where recordStatus='Normal' and caseId = ? order by id asc",new Object[]{caseId});
    }
}
