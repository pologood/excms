package cn.lonsun.system.filecenter.internal.dao;

import cn.lonsun.core.base.dao.IMockDao;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.system.filecenter.internal.entity.FileCenterEO;
import cn.lonsun.system.filecenter.internal.vo.FileCenterVO;

public interface IFileCenterDao extends IMockDao<FileCenterEO> {

	/**
	 * 
	 * @Title: updateStutas
	 * @Description: 引用状态
	 * @param ids
	 * @param status    设定文件
	 * @return void    返回类型
	 * @throws
	 */
	public void updateStatus(Long[] ids,Integer status);
	
	/**
	 * 
	 * @Title: updateStutas
	 * @Description: 引用状态
	 * @param ids
	 * @param status    设定文件
	 * @return void    返回类型
	 * @throws
	 */
	public void updateStatus(Long[] ids,Integer status,String desc);
	
	/**
	 * 
	 * @Title: updateStutas
	 * @Description: TODO
	 * @param mongoIds
	 * @param status
	 * @param desc   设定文件
	 * @return  void   返回类型
	 * @throws
	 */
	public void updateStatus(String[] mongoIds,Integer status);
	
	/**
	 * 
	 * @Title: updateFileEO
	 * @Description: TODO
	 * @param mongoIds
	 * @param status
	 * @param ids   设定文件
	 * @return  void   返回类型
	 * @throws
	 */
	public void updateFileEO(String[] mongoIds,Integer status,Long...ids);
	/**
	 * 
	 * @Title: getFilePage
	 * @Description: 文件分页列表
	 * @param fileVO
	 * @param    设定文件
	 * @return Pagination    返回类型
	 * @throws
	 */
	public Pagination getFilePage(FileCenterVO fileVO);
	
	/**
	 * 
	 * @Title: deleteByMongoId
	 * @Description: 根据mongodb ID删除
	 * @param mongoIds   设定文件
	 * @return  void   返回类型
	 * @throws
	 */
	public void deleteByMongoId(String[] mongoIds);
	
	/**
	 * 
	 * @Title: updateByContentId
	 * @Description: 根据contentIds更改引用状态
	 * @param ids
	 * @param status   设定文件
	 * @return  void   返回类型
	 * @throws
	 */
	public void updateByContentId(Long[] ids,Integer status);

	public void doSave(FileCenterEO eo);

	/**
	 * 根据名称获取文件对象
	 * @param mongoname
	 * @return
     */
	FileCenterEO getByMongoName(String mongoname);
}
