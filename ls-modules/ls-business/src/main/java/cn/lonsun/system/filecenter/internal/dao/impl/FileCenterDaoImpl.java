package cn.lonsun.system.filecenter.internal.dao.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.base.dao.impl.MockDao;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.system.filecenter.internal.dao.IFileCenterDao;
import cn.lonsun.system.filecenter.internal.entity.FileCenterEO;
import cn.lonsun.system.filecenter.internal.vo.FileCenterVO;

@Repository("fileCenterDao")
public class FileCenterDaoImpl extends MockDao<FileCenterEO> implements IFileCenterDao {

	
	@Override
	public void updateStatus(Long[] ids, Integer status) {
		Map<String,Object> map=new HashMap<String,Object>();
		map.put("status", status);
		map.put("updateDate", new Date());
		map.put("ids", ids);
		String hql="update FileCenterEO set status=:status,updateDate=:updateDate where id in (:ids) ";
		executeUpdateByJpql(hql, map);
	}

	@Override
	public Pagination getFilePage(FileCenterVO fileVO) {
		StringBuffer hql=new StringBuffer("FROM FileCenterEO WHERE recordStatus='Normal'");
		Map<String,Object> map=new HashMap<String,Object>();
		if(!AppUtil.isEmpty(fileVO.getFileName())){
			hql.append(" and fileName like :fileName");
			map.put("fileName", "%".concat(fileVO.getFileName()).concat("%"));
		}
		if(!AppUtil.isEmpty(fileVO.getSuffix())){
			hql.append(" and suffix like :suffix");
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
		if(null!=fileVO.getColumnId()){
			hql.append(" and columnId=:columnId");
			map.put("columnId", fileVO.getColumnId());
		}
		if(null!=fileVO.getColumnId()){
			hql.append(" and contentId=:contentId");
			map.put("contentId", fileVO.getContentId());			
		}
		if(null!=fileVO.getCreateUserId()){
			hql.append(" and createUserId=:createUserId");
			map.put("createUserId", fileVO.getCreateUserId());			
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
	public void updateStatus(Long[] ids, Integer status, String desc) {
		Map<String,Object> map=new HashMap<String,Object>();
		map.put("status", status);
		map.put("updateDate", new Date());
		map.put("ids", ids);
		map.put("des",desc);
		String hql="update FileCenterEO set status=:status,updateDate=:updateDate,description =:desc where id in (:ids) ";
		executeUpdateByJpql(hql, map);		
	}

	@Override
	public void updateStatus(String[] mongoIds, Integer status) {
		Map<String,Object> map=new HashMap<String,Object>();
		map.put("status", status);
		map.put("updateDate", new Date());
		map.put("mongoIds", mongoIds);
		String hql="update FileCenterEO set status=:status,updateDate=:updateDate where mongoId in (:mongoIds) ";
		executeUpdateByJpql(hql, map);
	}

	@Override
	public void deleteByMongoId(String[] mongoIds) {
		Map<String,Object> map=new HashMap<String,Object>();
		map.put("mongoIds", mongoIds);
		String hql="update FileCenterEO set status=0 where mongoId in (:mongoIds) ";
		executeUpdateByJpql(hql, map);
	}

	@Override
	public void updateFileEO(String[] mongoIds, Integer status, Long... ids) {
		StringBuffer hql=new StringBuffer("update FileCenterEO set status=:status,updateDate=:updateDate");
		Map<String,Object> map=new HashMap<String,Object>();
		map.put("status", status);
		map.put("updateDate", new Date());
		if(ids.length>0){
			for(int i=0;i<ids.length;i++){
				if(i==0){
					hql.append(",contentId=:contentId");
					map.put("contentId", ids[i]);
				}
				if(i==1){
					hql.append(",columnId=:columnId");
					map.put("columnId", ids[i]);					
				}
				if(i==2){
					hql.append(",siteId=:siteId");
					map.put("siteId", ids[i]);						
				}
			}
		}
		hql.append(" where mongoId in (:mongoIds)");
		map.put("mongoIds", mongoIds);
		executeUpdateByJpql(hql.toString(), map);
	}

	@Override
	public void updateByContentId(Long[] ids, Integer status) {
		Map<String,Object> map=new HashMap<String,Object>();
		map.put("status", status);
		map.put("updateDate", new Date());
		map.put("ids", ids);
		String hql="update FileCenterEO set status=:status,updateDate=:updateDate where contentId in (:ids) ";
		executeUpdateByJpql(hql, map);
	}
	public void doSave(FileCenterEO eo){
		save(eo);
	}

	@Override
	public FileCenterEO getByMongoName(String mongoname) {
		String hql = "from FileCenterEO t where t.mongoName like ? ";
		List<FileCenterEO> list =  getEntitiesByHql(hql,new Object[]{mongoname+".%"});
		if(null != list && list.size() > 0){
			return list.get(0);
		}
		return null;
	}

}
