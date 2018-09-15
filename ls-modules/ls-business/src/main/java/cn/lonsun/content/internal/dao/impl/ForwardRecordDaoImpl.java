package cn.lonsun.content.internal.dao.impl;

import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.content.internal.dao.IForwardRecordDao;
import cn.lonsun.content.internal.entity.GuestBookForwardRecordEO;
import cn.lonsun.core.base.dao.impl.MockDao;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.rbac.internal.entity.OrganEO;
import cn.lonsun.system.datadictionary.vo.DataDictVO;
import cn.lonsun.util.DataDictionaryUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("forwardRecordDao")
public class ForwardRecordDaoImpl extends MockDao<GuestBookForwardRecordEO> implements IForwardRecordDao{

	@Override
	public void saveRecord(GuestBookForwardRecordEO eo) {
		save(eo);
	}

	@Override
	public Pagination getRecord(Long pageIndex, Integer pageSize,Long id) {
		String hql = "from GuestBookForwardRecordEO where recordStatus='Normal' and guestBookId="+id;
		hql+=" order by createDate desc";
		Pagination page = getPagination(pageIndex,pageSize,hql,new Object[]{});
		if(page!=null&&page.getData()!=null&&page.getData().size()>0){
			List<GuestBookForwardRecordEO> list=(List<GuestBookForwardRecordEO>)page.getData();
			for(GuestBookForwardRecordEO eo:list){
				if(eo.getReceiveOrganId()!=null){
					OrganEO organEO=CacheHandler.getEntity(OrganEO.class,eo.getReceiveOrganId());
					if(organEO!=null){
						eo.setReceiveName(organEO.getName());
					}
				}else{
					if(!StringUtils.isEmpty(eo.getReceiveUserCode())){
						DataDictVO dictVO = DataDictionaryUtil.getItem("guest_book_rec_users", eo.getReceiveUserCode());
                        if(dictVO!=null){
							eo.setReceiveName(dictVO.getKey());
						}
					}
				}
			}
		}
		return page;
	}
	
	
	//查询转办记录分页
	

	

}
