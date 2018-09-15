package cn.lonsun.system.datadictionary.internal.service;

import cn.lonsun.core.base.service.IBaseService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.system.datadictionary.internal.entity.DataDictEO;
import cn.lonsun.system.datadictionary.vo.DataDictPageVO;

/**
 * 
 * @ClassName: IDataDictService
 * @Description: 数据字典service层
 * @author Hewbing
 * @date 2015年8月25日 上午10:19:55
 *
 */
public interface IDataDictService extends IBaseService<DataDictEO> {
	
	/**
	 * 
	 * @Description 根据条件获取数据字典分页
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
	  * @Description 删除字典及其字典项
	  * @param ids
	  */
	 public void deleteDict(Long dictId);
	 
	 /**
	  * 
	  * @Description 
	  * @author Hewbing
	  * @date 2015年8月31日 下午3:49:45
	  * @param code
	  * @param isUsed
	  */
	 public void markUsed(String code,Integer isUsed);
	 
	 
	 public void changeUsed(Long id);

/*	 public void saveDataDict(DataDictEO dataDictEO);
	 
	 public void updataDataDict(DataDictEO dataDictEO);*/
}
