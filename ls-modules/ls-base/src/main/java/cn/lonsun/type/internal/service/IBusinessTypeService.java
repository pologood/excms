package cn.lonsun.type.internal.service;

import cn.lonsun.core.base.service.IMockService;
import cn.lonsun.core.exception.BusinessException;
import cn.lonsun.core.exception.HasChildrenException;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.type.internal.entity.BusinessTypeEO;
import cn.lonsun.type.internal.vo.BusinessTypeVO;

import java.util.List;

/**
 * 业务对象类型服务类
 * @author xujh
 *
 */
public interface IBusinessTypeService extends IMockService<BusinessTypeEO> {
	
	/**
	 * 根据type获取分页
	 * @param index
	 * @param size
	 * @param type
	 * @return
	 */
	public Pagination getPage(Long index, int size, String type);
	
	/**
	 * 根据主键和类型来获取对象
	 * @param businessTypeId
	 * @param type
	 * @return
	 */
	public BusinessTypeEO getBusinessType(Long businessTypeId, String type);
	
	/**
	 * 根据caseId和caseType来获取对象集合
	 * @param caseId
	 * @param caseType
	 * @return
	 */
	public List<BusinessTypeEO> getBusinessTypeByCase(Long caseId, String caseType);
	
	/**
	 * 根据父键和类型来获取对象集合
	 * @param parentId
	 * @param type
	 * @return
	 */
	public List<BusinessTypeEO> getBusinessTypes(Long parentId, String type);
	
	/**
	 * 保存业务对象类型，保存后需要更新父类型的是否存在子类型的hasChildren字段
	 * @param businessType
	 */
	public void save(BusinessTypeEO businessType);
	
	/**
	 * 更新
	 * @param businessType
	 */
	public void update(BusinessTypeEO businessType);
    
    /**
     * 删除类型，当存在子类型时，删除失败
     * @param businessTypeId
     * @param type
     * @throws cn.lonsun.core.exception.HasChildrenException
     */
    public void deleteWithOutChildren(Long businessTypeId, String type) throws HasChildrenException;
    
    /**
     * 删除类型，当存在子类型时，连子类型一起删除
     * @param businessTypeId
     * @param type
     */
    public void deleteWithChildren(Long businessTypeId, String type);
    
    /**
     * 获取所有的子孙对象
     * @param businessTypeId
     * @param businessType
     * @param type
     * @return
     */
    public List<BusinessTypeEO> getChildren(Long businessTypeId, List<BusinessTypeEO> businessType, String type);

    /**
     * 根据业务类型，模块子类型查询
     *
     * @author yy
     * @param type
     * @param caseCode
     * @return
     */
    public List<BusinessTypeVO> getTreeByTypeWithCaseCode(String type, String caseCode);

    /**
     * 删除
     *
     * @author yy
     * @param businessTypeId
     */
    public void delete(Long businessTypeId) throws BusinessException;
    
}
