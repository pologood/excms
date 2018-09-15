package cn.lonsun.type.internal.service.impl;

import cn.lonsun.core.base.service.impl.MockService;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.exception.BusinessException;
import cn.lonsun.core.exception.HasChildrenException;
import cn.lonsun.core.exception.IllegalActException;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.type.internal.dao.IBusinessTypeDao;
import cn.lonsun.type.internal.entity.BusinessTypeEO;
import cn.lonsun.type.internal.service.IBusinessTypeService;
import cn.lonsun.type.internal.vo.BusinessTypeVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yy on 2014/8/16.
 */
@Service
public class BusinessTypeServiceImpl extends MockService<BusinessTypeEO> implements IBusinessTypeService {

    @Autowired
    private IBusinessTypeDao businessTypeDao;
    
    /**
	 * 根据主键和类型来获取对象
	 * @param businessTypeId
	 * @param type
	 * @return
	 */
    @Override
	public BusinessTypeEO getBusinessType(Long businessTypeId,String type){
    	if(businessTypeId==null||businessTypeId<=0||StringUtils.isEmpty(type)){
    		throw new IllegalArgumentException();
    	}
    	BusinessTypeEO bt = businessTypeDao.getEntity(BusinessTypeEO.class, businessTypeId);
    	//验证类型是否匹配，此处是为了放置其他业务模块调用时传递了错误的参数导致影响其他业务模块
    	if(!type.equals(bt.getType())){
    		throw new BaseRunTimeException();
    	}
		return bt;
	}
    
    /**
	 * 根据父键和类型来获取对象集合
	 * @param parentId
	 * @param type
	 * @return
	 */
    @Override
	public List<BusinessTypeEO> getBusinessTypes(Long parentId,String type){
		return null;
	}

	@Override
	public void deleteWithOutChildren(Long businessTypeId,String type) throws HasChildrenException {
		//验证是否存在子类型，如果存在子类型，那么禁止删除
		BusinessTypeEO bt = businessTypeDao.getEntity(BusinessTypeEO.class, businessTypeId);
		if(bt.getHasChildren()){
			throw new HasChildrenException();
		}
		if(!type.equals(bt.getType())){
			throw new BaseRunTimeException();
		}
		businessTypeDao.delete(bt);
	}

	@Override
	public void deleteWithChildren(Long businessTypeId,String type) {
		List<BusinessTypeEO> toDeletes = new ArrayList<BusinessTypeEO>();
		BusinessTypeEO bt = businessTypeDao.getEntity(BusinessTypeEO.class, businessTypeId);
		//TODO
		getChildren(businessTypeId, toDeletes,type);
		businessTypeDao.delete(toDeletes);
		if(bt.getParentId()!=null){
			Long count = businessTypeDao.getCountByParentId(bt.getParentId());
			//由于本次事务尚未提交，所以count==1表示本条尚未提交删除的记录
			boolean hasSuns = (count==null||count<=1)?false:true;
			//如果不同，那么进行修改
			if(bt.getHasChildren()!=hasSuns){
				BusinessTypeEO parent = businessTypeDao.getEntity(BusinessTypeEO.class, bt.getParentId());
				parent.setHasChildren(hasSuns);
				businessTypeDao.update(parent);
			}
		}
	}

	@Override
	public List<BusinessTypeEO> getChildren(Long businessTypeId,List<BusinessTypeEO> businessTypes,String type) {
		return null;
	}

	@Override
	public void save(BusinessTypeEO businessType) {
		//验证保存的业务对象类型是否合法
		if(StringUtils.isEmpty(businessType.getType())){
			throw new NullPointerException();
		}
		Long parentId = businessType.getParentId();
		BusinessTypeEO parent = null;
		if(parentId!=null){
			parent = businessTypeDao.getEntity(BusinessTypeEO.class, parentId);
			//如果保存的对象和父对象不是同一种类型，那么抛出异常
			if(!businessType.getType().equals(parent.getType())){
				throw new IllegalActException();
			}
		}
		businessTypeDao.save(businessType);
		//更新父节点是否含有子节点
		if(parent!=null){
			//如果不同，那么进行修改
			if(!parent.getHasChildren()){
				businessType.setHasChildren(true);
				businessTypeDao.update(parent);
			}
		}
	}
	
	/**
	 * 更新
	 * @param businessType
	 */
	public void update(BusinessTypeEO businessType){
		//type属性不能更新
		Long businessTypeId = businessType.getBusinessTypeId();
		BusinessTypeEO src = businessTypeDao.getEntity(BusinessTypeEO.class, businessTypeId);
		if(!src.getType().equals(businessType.getType())){
			throw new IllegalActException();
		}
		//如果更新parentId,那么要确保父节点的type与当前对象的type保持一致
		Long parentId = businessType.getParentId();
		if(parentId!=null&&parentId!=src.getParentId()){
			BusinessTypeEO newParent = businessTypeDao.getEntity(BusinessTypeEO.class, parentId);
			//如果保存的对象和父对象不是同一种类型，那么抛出异常
			if(!businessType.getType().equals(newParent.getType())){
				throw new IllegalActException();
			}
		}
		BeanUtils.copyProperties(businessType, src);
		//更新
		businessTypeDao.update(src);
	}

	@Override
	public List<BusinessTypeEO> getBusinessTypeByCase(Long caseId,
			String caseType) {
		if(caseId==null||caseId<=0||StringUtils.isEmpty(caseType)){
			throw new IllegalActException();
		}
		return businessTypeDao.getBusinessTypeByCase(caseId, caseType);
	}

    @Override
    public List<BusinessTypeVO> getTreeByTypeWithCaseCode(String type, String caseCode) {
        List<BusinessTypeVO> bvs = new ArrayList<BusinessTypeVO>();
        List<BusinessTypeEO> bts = businessTypeDao.getByTypeWithCaseCode(type, caseCode);
        if(bts!=null&&bts.size()>0){
            for(BusinessTypeEO b : bts){
                BusinessTypeVO bv = new BusinessTypeVO();
                BeanUtils.copyProperties(b, bv);
                bv.setPid(b.getParentId());
                bv.setIsParent(true);
                bvs.add(bv);
            }
        }
        return bvs;
    }

    @Override
    public void delete(Long businessTypeId) throws BusinessException {
        if(null!=businessTypeId&&businessTypeId>0) {
            BusinessTypeEO b = businessTypeDao.getEntity(BusinessTypeEO.class, businessTypeId);
            businessTypeDao.delete(b);
        }
        
    }

	@Override
	public Pagination getPage(Long index, int size, String type) {
		// TODO Auto-generated method stub
		return null;
	}

}
