package cn.lonsun.wechatmgr.internal.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.base.service.impl.MockService;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.core.util.TipsMode;
import cn.lonsun.system.systemlog.internal.entity.CmsLogEO;
import cn.lonsun.util.FileUploadUtil;
import cn.lonsun.util.LoginPersonUtil;
import cn.lonsun.util.SysLog;
import cn.lonsun.wechatmgr.internal.dao.IWeChatArticleDao;
import cn.lonsun.wechatmgr.internal.entity.WeChatArticleEO;
import cn.lonsun.wechatmgr.internal.service.IWeChatArticleService;
import cn.lonsun.wechatmgr.vo.WeChatArticleVO;

import java.util.List;

@Service("weChatArticleService")
public class WeChatArticleServiceImpl extends MockService<WeChatArticleEO> implements
		IWeChatArticleService {

	@Autowired
	private IWeChatArticleDao weChatArticleDao;
	
	@Override
	public Pagination getPage(WeChatArticleVO vo) {
		Long siteId=LoginPersonUtil.getSiteId();
		vo.setSiteId(siteId);
		return weChatArticleDao.getPage(vo);
	}

	@Override
	public void saveArticle(WeChatArticleEO artEO) {
		/*if(AppUtil.isEmpty(artEO.getThumbImg())){
			throw new BaseRunTimeException(TipsMode.Message.toString(), "图文素材图片不能为空");
		}else{
			if(artEO.getId()==null){
				Long id=weChatArticleDao.save(artEO);
				SysLog.log("新增微信素材 >> ID："+id+"，标题："+artEO.getTitle(), "WeChatArticleEO", CmsLogEO.Operation.Add.toString());
			}else{
				WeChatArticleEO oldArt = getEntity(WeChatArticleEO.class, artEO.getId());
				FileUploadUtil.setStatus(oldArt.getThumbImg(), 0);
				weChatArticleDao.merge(artEO);
				SysLog.log("修改微信素材 >> ID："+artEO.getId()+"，标题："+artEO.getTitle(), "WeChatArticleEO", CmsLogEO.Operation.Update.toString());
			}
			FileUploadUtil.setStatus(artEO.getThumbImg(), 1);
		}*/
		if(artEO.getId()==null){
			Long id=weChatArticleDao.save(artEO);
			SysLog.log("新增微信素材，标题："+artEO.getTitle(), "WeChatArticleEO", CmsLogEO.Operation.Add.toString());
		}else{
			WeChatArticleEO oldArt = getEntity(WeChatArticleEO.class, artEO.getId());
			FileUploadUtil.setStatus(oldArt.getThumbImg(), 0);
			weChatArticleDao.merge(artEO);
			SysLog.log("修改微信素材，标题："+artEO.getTitle(), "WeChatArticleEO", CmsLogEO.Operation.Update.toString());
		}
		FileUploadUtil.setStatus(artEO.getThumbImg(), 1);
	}

	@Override
	public List<WeChatArticleEO> getByKeyWordsMsgId(Long id) {
		return weChatArticleDao.getByKeyWordsMsgId(id);
	}
}
