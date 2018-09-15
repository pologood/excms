package cn.lonsun.system.filecenter.internal.service;

import cn.lonsun.core.base.service.IMockService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.system.filecenter.internal.entity.FileCenterEO;
import cn.lonsun.system.filecenter.internal.vo.FileCenterVO;

public interface IFileCenterService extends IMockService<FileCenterEO> {
	/**
	 * 
	 * @Title: setStutas
	 * @Description: 引用状态
	 * @param ids
	 * @param status    设定文件
	 * @return void    返回类型
	 * @throws
	 */
	public void setStatus(Long[] ids,Integer status);
	
	/**
	 * 
	 * @Title: setStutas
	 * @Description: TODO
	 * @param mongoIds
	 * @param status   设定文件
	 * @return  void   返回类型
	 * @throws
	 */
	public void setStatus(String[] mongoIds,Integer status);
	
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
	public void setFileEO(String[] mongoIds,Integer status,Long...ids);
	/**
	 * 
	 * @Title: getFilePage
	 * @Description: 文件分页列表
	 * @param fileVO
	 * @param   设定文件
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
	 * @Title: removeFromDir
	 * @Description: 从mongodb彻底删除
	 * @param ids
	 * @return   设定文件
	 * @return  boolean   返回类型
	 * @throws
	 */
	public boolean removeFromDir(Long[] ids);
	/**
	 * 
	 * @Title: markStatusByContentId
	 * @Description: TODO
	 * @param ids
	 * @param status   Parameter
	 * @return  void   return type
	 * @throws
	 */
	public void markStatusByContentId(Long[] ids,Integer status);
	
	/**
	 * 
	 * @Title: cleanUp
	 * @Description: TODO
	 * @param ids   Parameter
	 * @return  void   return type
	 * @throws
	 */
	public boolean cleanUp(Long[] ids);

	public void doSave(FileCenterEO eo);


	public FileCenterEO getByMongoName(String mongoname);
}
