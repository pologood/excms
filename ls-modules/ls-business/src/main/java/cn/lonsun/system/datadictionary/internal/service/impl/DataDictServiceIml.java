package cn.lonsun.system.datadictionary.internal.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.lonsun.core.base.service.impl.BaseService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.system.datadictionary.internal.dao.IDataDictDao;
import cn.lonsun.system.datadictionary.internal.entity.DataDictEO;
import cn.lonsun.system.datadictionary.internal.service.IDataDictItemService;
import cn.lonsun.system.datadictionary.internal.service.IDataDictService;
import cn.lonsun.system.datadictionary.vo.DataDictPageVO;
import cn.lonsun.system.role.internal.cache.RightDictCache;
import cn.lonsun.system.systemlog.internal.entity.CmsLogEO;
import cn.lonsun.util.SysLog;

/**
 * 
 * @ClassName: DataDictServiceIml
 * @Description: 数据字典业务逻辑层
 * @author Hewbing
 * @date 2015年10月15日 上午11:32:22
 *
 */
@Service("dataDictService")
public class DataDictServiceIml extends BaseService<DataDictEO> implements
		IDataDictService {

	@Autowired
	private IDataDictDao dataDictDao;
	
	@Autowired
	private IDataDictItemService dataDictItemService;
	@Override
	public Pagination getPage(DataDictPageVO pageVO) {
		return dataDictDao.getPage(pageVO);
	}

	@Override
	public DataDictEO getDataDictByCode(String code) {
		return dataDictDao.getDataDictByCode(code);
	}

	@Override
	public void deleteDict(Long dictId) {
		dataDictDao.delete(DataDictEO.class, dictId);
		dataDictItemService.deleteItemByDictId(dictId);
		SysLog.log("删除数据字典 >> ID："+dictId, "DataDictEO", CmsLogEO.Operation.Delete.toString());
		RightDictCache.refresh();
	}

	@Override
	public void markUsed(String code, Integer isUsed) {
		dataDictDao.markUsed(code, isUsed);
	}

	@Override
	public void changeUsed(Long id) {
		DataDictEO _eo=getEntity(DataDictEO.class, id);
		if(_eo.getIsUsed()==1){
			dataDictDao.changeUsed(id, 0);
		}else{
			dataDictDao.changeUsed(id, 1);
		}
	}

}
