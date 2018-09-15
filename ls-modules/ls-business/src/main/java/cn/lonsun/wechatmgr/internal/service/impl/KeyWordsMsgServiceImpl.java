package cn.lonsun.wechatmgr.internal.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.lonsun.core.base.service.impl.MockService;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.core.util.TipsMode;
import cn.lonsun.system.systemlog.internal.entity.CmsLogEO;
import cn.lonsun.util.LoginPersonUtil;
import cn.lonsun.util.SysLog;
import cn.lonsun.wechatmgr.internal.dao.IAutoMsgArticleDao;
import cn.lonsun.wechatmgr.internal.dao.IKeyWordsMsgDao;
import cn.lonsun.wechatmgr.internal.dao.IWeChatArticleDao;
import cn.lonsun.wechatmgr.internal.entity.AutoMsgArticleEO;
import cn.lonsun.wechatmgr.internal.entity.KeyWordsMsgEO;
import cn.lonsun.wechatmgr.internal.entity.WeChatArticleEO;
import cn.lonsun.wechatmgr.internal.service.IKeyWordsMsgService;
import cn.lonsun.wechatmgr.vo.KeyWordsVO;

@Service("keyWordsMsgService")
public class KeyWordsMsgServiceImpl extends MockService<KeyWordsMsgEO> implements
		IKeyWordsMsgService {
	
	@Autowired
	private IKeyWordsMsgDao keyWordsMsgDao;
	@Autowired
	private IAutoMsgArticleDao autoMsgArticleDao;

	@Autowired
	private IWeChatArticleDao weChatArticleDao;
	@Override
	public List<KeyWordsMsgEO> getListBySite(Long siteId) {
		return keyWordsMsgDao.getListBySite(siteId);
	}

	@Override
	public Pagination getPage(KeyWordsVO vo) {
		Long siteId=LoginPersonUtil.getSiteId();
		vo.setSiteId(siteId);
		Pagination page=keyWordsMsgDao.getPage(vo);
		List<KeyWordsMsgEO> kwms=(List<KeyWordsMsgEO>) page.getData();
		for(KeyWordsMsgEO kwm:kwms){
			if("news".equals(kwm.getMsgType())){
				List<AutoMsgArticleEO> autoList = autoMsgArticleDao.getListByKey(kwm.getId());
				List<WeChatArticleEO> newsList=new ArrayList<WeChatArticleEO>();
				for(AutoMsgArticleEO li:autoList){
					WeChatArticleEO eo=weChatArticleDao.getEntity(WeChatArticleEO.class, li.getArticleId());
					newsList.add(eo);
				}
				kwm.setNewsList(newsList);
			}
		}
		page.setData(kwms);
		return page;
	}

	@Override
	public KeyWordsMsgEO getKeywords(Long id) {
		KeyWordsMsgEO msg = getEntity(KeyWordsMsgEO.class, id);
		if(msg==null){
			return new KeyWordsMsgEO();
		}else if("news".equals(msg.getMsgType())){
			List<AutoMsgArticleEO> automsg = autoMsgArticleDao.getListByKey(msg.getId());
			if(automsg!=null){
				List<WeChatArticleEO> articles=new ArrayList<WeChatArticleEO>();
				for(AutoMsgArticleEO li:automsg){
					WeChatArticleEO article = weChatArticleDao.getEntity(WeChatArticleEO.class, li.getArticleId());
					if(article!=null){
						articles.add(article);
					}
				}
				msg.setNewsList(articles);
			}
			return msg;
		}else{
			return msg;
		}
	}

	@Override
	public void saveKeywords(KeyWordsMsgEO eo, Long[] articles) {
		Long siteId=LoginPersonUtil.getSiteId();
		eo.setSiteId(siteId);
		if("text".equals(eo.getMsgType())){
			if(eo.getId()!=null){
				updateEntity(eo);
				SysLog.log("修改微信关键词回复(文本) >> KeyWords："+eo.getKeyWords(), "KeyWordsMsgEO", CmsLogEO.Operation.Update.toString());
			}else{
				saveEntity(eo);
				SysLog.log("新增微信关键词回复(文本) >> KeyWords："+eo.getKeyWords(), "KeyWordsMsgEO", CmsLogEO.Operation.Add.toString());
			}
		}else if("news".equals(eo.getMsgType())){
			Long keyId=null;
			if(eo.getId()!=null){
				updateEntity(eo);
				keyId=eo.getId();
				SysLog.log("修改微信关键词回复(图文) >> KeyWords："+eo.getKeyWords(), "KeyWordsMsgEO", CmsLogEO.Operation.Update.toString());
			}else{
				keyId=saveEntity(eo);
				SysLog.log("新增微信关键词回复(文本) >> KeyWords："+eo.getKeyWords(), "KeyWordsMsgEO", CmsLogEO.Operation.Add.toString());
			}	
			autoMsgArticleDao.deleteByKey(eo.getId());
			List<AutoMsgArticleEO> autoList=new ArrayList<AutoMsgArticleEO>();
			if(articles.length<=0){
				throw new BaseRunTimeException(TipsMode.Message.toString(), "素材选择不能为空");
			}else{
			for(int i=0;i<articles.length;i++){
				AutoMsgArticleEO aeo=new AutoMsgArticleEO();
				aeo.setKeyWordsId(keyId);
				aeo.setArticleId(articles[i]);
				aeo.setRecordStatus("Normal");
				autoList.add(aeo);
			}
			autoMsgArticleDao.save(autoList);
			}
		}else{
			throw new BaseRunTimeException(TipsMode.Message.toString(), "消息类型不匹配");
		}
	}

	@Override
	public KeyWordsMsgEO getKeyWordsMsg(Long siteId, String eventKey) {
		List<KeyWordsMsgEO> msgs = keyWordsMsgDao.getgetKeyWordsMsg(siteId,eventKey);
		if(msgs != null && msgs.size() > 0){
			return msgs.get(0);
		}
		return null;
	}
}
