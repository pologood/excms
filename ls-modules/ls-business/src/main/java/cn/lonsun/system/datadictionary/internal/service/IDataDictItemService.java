package cn.lonsun.system.datadictionary.internal.service;

import java.util.List;

import cn.lonsun.core.base.service.IBaseService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.system.datadictionary.internal.entity.DataDictItemEO;

/**
 * 
 * @ClassName: DataDictItemServiceImpl
 * @Description: 数据字典项service层
 * @author Hewbing
 * @date 2015年8月25日 上午10:16:05
 *
 */
public interface IDataDictItemService extends IBaseService<DataDictItemEO> {
	
	/**
	 * 
	 * @Description 根据字典ID获取字典项集合
	 * @param dictId
	 * @return
	 */
	public List<DataDictItemEO> getListByDictId(Long dictId);
	
	/**
	 * 
	 * @Description 根据code编码获取字典项集合
	 * @param code
	 * @return
	 */
	public DataDictItemEO getDataDictItemByCode(String code,Long dictId);
	
	/**
	 * 
	 * @Title: getDataDictItemByCode
	 * @Description: TODO
	 * @param @param code
	 * @param @param dataCode
	 * @param @return    设定文件
	 * @return DataDictItemEO    返回类型
	 * @throws
	 */
	public DataDictItemEO getDataDictItemByCode(String code,String dataCode);
	
	/**
	 * 
	 * @Description 根据字典ID获取字典项分页
	 * @param pageIndex
	 * @param pageSize
	 * @param dictId
	 * @return
	 */
	public Pagination getPageByDictId(Long pageIndex,Integer pageSize,Long dictId,String name);
	
	/**
	 * 
	 * @Description 根据dictId删除字典项
	 * @param dictId
	 */
	public void deleteItemByDictId(Long dictId);
	
	/**
	 * 
	 * @Description 根据dictid获取该项最大排序
	 * @author Hewbing
	 * @date 2015年9月29日 下午2:14:31
	 * @param dictId
	 * @return
	 */
	public Integer getMaxItem(Long dictId);

	/**
	 * 
	 * @Description 修改默认值
	 * @author Hewbing
	 * @date 2015年9月29日 下午3:51:43
	 * @param itemId
	 * @param dictId
	 * @param flag
	 */
	public void setDefault(Long itemId,Long dictId,Integer flag);

	/**
	 * 修改隐藏状态
 	*/
	public void updateHide(Long itemId,Integer flag);

	/**
	 * 
	 * @Description 保存修改处理
	 * @author Hewbing
	 * @date 2015年9月29日 下午3:57:47
	 * @param itemEO
	 */
	public void updateItem(DataDictItemEO itemEO);
	
	/**
	 * 
	 * @Description 保存增加处理
	 * @author Hewbing
	 * @date 2015年9月29日 下午3:58:42
	 * @param itemEO
	 */
	public Long saveItem(DataDictItemEO itemEO);
	
	public List<DataDictItemEO> getItemList(Long dictId);
}
