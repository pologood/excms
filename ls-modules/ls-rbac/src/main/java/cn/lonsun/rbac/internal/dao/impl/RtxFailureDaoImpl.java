package cn.lonsun.rbac.internal.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.base.dao.impl.BaseDao;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.core.util.SqlUtil;
import cn.lonsun.rbac.internal.dao.IRtxFailureDao;
import cn.lonsun.rbac.internal.entity.RtxFailureEO;
import cn.lonsun.rbac.internal.vo.RtxFailureVO;
import cn.lonsun.rbac.internal.vo.RtxQueryVO;



/**
 * RTX同步失败记录ORM接口实现
 *
 * @author xujh
 * @version 1.0
 * 2015年4月22日
 *
 */
@Repository("rtxFailureDao")
public class RtxFailureDaoImpl extends BaseDao<RtxFailureEO> implements
		IRtxFailureDao {

	//条件查询
	public Pagination getPage(RtxQueryVO rfvo) {
		 StringBuffer sql= new StringBuffer("select t.id,t.type,t.operation,t.description,t.password,t.personName,o.name_ as organName from (select s.*,p.name_ as personName from rbac_rtx_sync s left join rbac_person p on (s.person_id=p.person_id and s.organ_id is null) order by s.id desc) t left join rbac_organ o on t.organ_id=o.organ_id where 1=1");
		 List<Object> list=new ArrayList<Object>();
		 if(!AppUtil.isEmpty(rfvo.getType())){
			if(rfvo.getType().equals("Person")){
				sql.append(" and t.type='Person'");
				if(!AppUtil.isEmpty(rfvo.getKey())){
					sql.append(" and t.personName like ? ");
					list.add("%".concat(SqlUtil.prepareParam4Query(rfvo.getKey())).concat("%"));
				}
			}else if(rfvo.getType().equals("Organ")){
				sql.append(" and t.type='Organ'");
				if(!AppUtil.isEmpty(rfvo.getKey())){
					sql.append(" and o.name_ like ? ");
					list.add("%".concat(SqlUtil.prepareParam4Query(rfvo.getKey())).concat("%"));
				}
			}
		}
		sql.append(" order by t.update_date");
		return getPaginationBySql(rfvo.getPageIndex(), rfvo.getPageSize(), sql.toString(),list.toArray(), RtxFailureVO.class);
	}


}
