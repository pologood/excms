package cn.lonsun.content.contentcorrection.internal.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.content.contentcorrection.internal.dao.IContentCorrectionDao;
import cn.lonsun.content.contentcorrection.internal.entity.ContentCorrectionEO;
import cn.lonsun.content.contentcorrection.internal.service.IContentCorrectionService;
import cn.lonsun.content.contentcorrection.vo.CorrectionPageVO;
import cn.lonsun.core.base.service.impl.MockService;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.core.util.TipsMode;
import cn.lonsun.system.datadictionary.vo.DataDictVO;
import cn.lonsun.util.DataDictionaryUtil;

@Service("contentCorrectionService")
public class ContentCorrectionServiceImpl extends MockService<ContentCorrectionEO> implements
		IContentCorrectionService {
	
	@Autowired
	private IContentCorrectionDao contentCorrectionDao;

	@Override
	public Pagination getPage(CorrectionPageVO pageVO) {
		Pagination page=contentCorrectionDao.getPage(pageVO);
		List<ContentCorrectionEO> list=(List<ContentCorrectionEO>) page.getData();
		//数据字典获取错误类型
		List<DataDictVO> ddList = DataDictionaryUtil.getDDList("error_correction");
		Map<String,String> map=new HashMap<String,String>();
		for(DataDictVO dd:ddList){
			map.put(dd.getValue(), dd.getKey());
		}
		for(ContentCorrectionEO li:list){
			li.setTypeName(map.get(li.getType()));
		}
		page.setData(list);
		return page;
	}

	@Override
	public void updatePublish(Long[] ids, Integer status) {
		contentCorrectionDao.updatePublish(ids, status);
	}

	@Override
	public void changePublish(Long id) {
		ContentCorrectionEO eo=getEntity(ContentCorrectionEO.class, id);
		if(AppUtil.isEmpty(eo)){
			throw new BaseRunTimeException(TipsMode.Message.toString(), "参数出错(Parameter error)");
		}else{
			Integer st=eo.getIsPublish();
			if(st==1){
				contentCorrectionDao.changePublish(id, 0);
			}else{
				contentCorrectionDao.changePublish(id, 1);
			}
		}
	}
	
}
