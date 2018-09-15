package cn.lonsun.system.datadictionary.internal.dao;

import java.util.List;

import cn.lonsun.core.base.dao.IBaseDao;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.system.datadictionary.internal.entity.DataDictItemEO;
import cn.lonsun.system.datadictionary.vo.DataSortVO;

/**
 * 
* @ClassName: IDataDictItemDao
* @Description: 数据字典项Dao层
* @author Hewbing
* @date 2015年8月25日 上午10:03:21
*
 */
public interface IDataDictItemDao extends IBaseDao<DataDictItemEO> {
	
	/**
	 * 
	 * @Description 根据id获取字典项集合
	 * @param DictId
	 * @return
	 */
	public List<DataDictItemEO> getListByDictId(Long DictId);
	
	/**
	 * 
	 * @Description 根据编码code获取字典项集合
	 * @param code
	 * @return
	 */
	public DataDictItemEO getDataDictItemByCode(String code,Long dictId);
	
	/**
	 * 
	 * @Description 根据字典ID获取字典项分页
	 * @param pageIndex
	 * @param pageSize
	 * @param DictId
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
	public DataSortVO getMaxItem(Long dictId);
	
	/**
	 * 
	 * @Description 修改默认值
	 * @author Hewbing
	 * @date 2015年10月9日 下午3:56:06
	 * @param dictId
	 * @param itemId
	 * @param flag
	 */
	public void updateDefault(Long dictId,Long itemId,Integer flag,Long siteId);
	
	/**
	 * 
	 * @Description 根据itemId修改默认值
	 * @author Hewbing
	 * @date 2015年10月9日 下午3:57:16
	 * @param itemId
	 * @param flag
	 */
	public void updateDefault(Long itemId,Integer flag);

	/**
	 * 修改隐藏状态
	 * @param itemId
	 *
     */
	public void updateHide(Long itemId,Integer flag);

	public List<DataDictItemEO> getItemList(Long dictId);
}
