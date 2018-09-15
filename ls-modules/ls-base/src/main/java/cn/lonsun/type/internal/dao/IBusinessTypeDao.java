package cn.lonsun.type.internal.dao;

import cn.lonsun.core.base.dao.IMockDao;
import cn.lonsun.type.internal.entity.BusinessTypeEO;

import java.util.List;

/**
 * Created by yy on 2014/8/13.
 */
public interface IBusinessTypeDao extends IMockDao<BusinessTypeEO> {
	
	/**
	 * 通过parentId获取子类型的数量
	 * @param parentId
	 * @return
	 */
	public Long getCountByParentId(Long parentId);
	
	
	/**
	 * 根据父键和类型来获取对象集合
	 * @param parentId
	 * @param type
	 * @return
	 */
	public List<BusinessTypeEO> getBusinessTypes(Long parentId, String type);
	
	/**
	 * 根据caseId和caseType来获取对象集合
	 * @param caseId
	 * @param caseType
	 * @return
	 */
	public List<BusinessTypeEO> getBusinessTypeByCase(Long caseId, String caseType);

	/**
	 * 根据业务类型，模块子类型查询
	 *
	 * @author yy
	 * @param type
	 * @param caseCode
	 * @return
	 */
    public List<BusinessTypeEO> getByTypeWithCaseCode(String type, String caseCode);
}
