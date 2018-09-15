package cn.lonsun.rbac.internal.dao.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import cn.lonsun.common.vo.PageQueryVO;
import cn.lonsun.core.base.dao.impl.MockDao;
import cn.lonsun.core.base.entity.AMockEntity.RecordStatus;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.rbac.internal.dao.IRoleRelationDao;
import cn.lonsun.rbac.internal.entity.RoleRelationEO;
import cn.lonsun.rbac.vo.RoleRelationVO;

@Repository
public class RoleRelationDaoImpl extends MockDao<RoleRelationEO> implements IRoleRelationDao{

	@Override
	public Pagination getPage(Long roleId,PageQueryVO query) {
		Pagination page = new Pagination();
		String hql = "select rr.id,r.code,rr.createDate,r.name from RoleRelationEO rr,RoleEO r where rr.roleId=? and rr.recordStatus=? and r.recordStatus=? and rr.targetRoleId=r.roleId";
		String srotField = query.getSortField();
		String sortOrder = query.getSortOrder();
		if(!StringUtils.isEmpty(srotField)){
			hql = hql +" order by rr."+srotField;
			if(!StringUtils.isEmpty(sortOrder)){
				hql = hql +" "+sortOrder;
			}
		}
		Long index = query.getPageIndex();
		Integer size = query.getPageSize();
		int first = Pagination.getStartNumber(index, size).intValue();
		int max = Pagination.getPageSize(size);
		Object[] values = new Object[]{roleId,RecordStatus.Normal.toString(),RecordStatus.Normal.toString()};
		//记录总数量
		Long totalCount = getCount(hql, values);
		page.setTotal(totalCount);
		if(totalCount!=null&&totalCount>0){
			//获取记录
			List<?> objects = getPaginationRecores(hql, first,max, values);
			if(objects!=null&&objects.size()>0){
				List<Object> relations = new ArrayList<Object>(objects.size());
				for(Object object:objects){
					Object[] array = (Object[])object;
					if(array!=null&&array.length>0){
						RoleRelationVO vo = new RoleRelationVO();
						Long id = array[0]==null?null:Long.valueOf(array[0].toString());
						vo.setId(id);
						String code = array[1]==null?null:array[1].toString();
						vo.setTargetRoleCode(code);
						String createDate = array[2] == null ? null : array[2].toString();
						if (createDate != null) {
							SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							try {
								vo.setCreateDate(sdf.parse(createDate));
							} catch (ParseException e) {
								e.printStackTrace();
							}
						}
						String targetRoleName = array[3]==null?null:array[3].toString();
						vo.setTargetRoleName(targetRoleName);
						relations.add(vo);
					}
				}
				page.setData(relations);
			}
		}
		return page;
	}

	@Override
	public void deleteByRoleIdAndTargetRoleId(Long roleId) {
		String hql = "update RoleRelationEO rr set rr.recordStatus=? where rr.roleId=? or rr.targetRoleId=? and rr.recordStatus=?";
		executeUpdateByHql(hql, new Object[]{RecordStatus.Removed.toString(),roleId,roleId,RecordStatus.Normal.toString()});
	}

}
