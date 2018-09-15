package cn.lonsun.system.datadictionary.internal.dao;

import cn.lonsun.core.base.dao.IBaseDao;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.system.datadictionary.internal.entity.DataDictEO;
import cn.lonsun.system.datadictionary.vo.DataDictPageVO;

/**
 * 
 * @ClassName: IDataDictDao
 * @Description: 数据字典Dao
 * @author Hewbing
 * @date 2015年8月25日 上午10:02:43
 *
 */
public interface IDataDictDao extends IBaseDao<DataDictEO> {
	
	/**
	 * 
	 * @Description 按条件获取数据字典分页
	 * @param pageVO
	 * @return
	 */
	 public Pagination getPage(DataDictPageVO pageVO);
	 
	 /**
	  * 
	  * @Description 根据code编码获取数据字典
	  * @param code
	  * @return
	  */
	 public DataDictEO getDataDictByCode(String code);
	 
	 /**
	  * 
	  * @Description 根据code标记字典是否被使用
	  * @author Hewbing
	  * @date 2015年8月31日 下午3:46:44
	  * @param code
	  */
	 public void markUsed(String code,Integer isUsed);
	 
	 public void changeUsed(Long id,Integer isUsed);
}
