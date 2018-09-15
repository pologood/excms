package cn.lonsun.wechatmgr.internal.service.impl;

import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.internal.service.IBaseContentService;
import cn.lonsun.core.base.util.StringUtils;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.indicator.internal.entity.IndicatorEO;
import cn.lonsun.site.site.internal.entity.ColumnMgrEO;
import cn.lonsun.util.LoginPersonUtil;
import cn.lonsun.wechatmgr.internal.entity.*;
import cn.lonsun.wechatmgr.internal.respmsg.Article;
import cn.lonsun.wechatmgr.internal.respmsg.NewsMessage;
import cn.lonsun.wechatmgr.internal.respmsg.TextMessage;
import cn.lonsun.wechatmgr.internal.service.*;
import cn.lonsun.wechatmgr.internal.wechatapiutil.*;
import cn.lonsun.wechatmgr.vo.InputMessage;
import cn.lonsun.wechatmgr.vo.MessageVO;
import com.thoughtworks.xstream.XStream;
import org.apache.log4j.Logger;
import org.apache.poi.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.util.*;

@Service("coreService")
public class CoreServiceImpl implements ICoreService{

	public static Logger log = Logger.getLogger(CoreServiceImpl.class);

	@Autowired
	private IWeChatUserService weChatUserService;

	@Autowired
	private IKeyWordsMsgService keyWordsMsgService;

	@Autowired
	private IAutoMsgArticleService autoMsgArticleService;

	@Autowired
	private IWeChatArticleService weChatArticleService;

	@Autowired
	private IWeChatAccountsInfoService weChatAccountsInfoService;

	@Autowired
	private ISubscribeMsgService subscribeMsgService;

	@Autowired
	private TaskExecutor taskExecutor;

	@Autowired
	private IBaseContentService baseContentService;
	@Autowired
	private IWeChatTurnService weChatTurnService;

//	/**
//	 * 微信有动作时，每5秒会重新发送请求，先简单处理下（后期优化）
//	 * zc
//	 */
//	private static Map<String,String> mapCall = new HashMap<String, String>();

	@Autowired
	private IWeChatMsgService weChatMsgService;

	@Value("${fileServer}")
	private String fileServer;

	@Override
	public String processRequest(HttpServletRequest request) {
		String respMessage = null;
		try {
			// xml请求解析
//			Map<String, String> requestMap = MessageUtil.parseXml(request);
//			log.info("参数集 >>>> "+requestMap.toString());
//			// 消息类型
//			String msgType = requestMap.get(Constant.RESP_MESSAGE_MSGTYPE);
//			//事件
//			String Event=requestMap.get("Event");
//			//点击事件
//			String eventKey=requestMap.get(Constant.REQ_EVENT_EVENTKEY);
//			// 发送方帐号（open_id）
//			String fromUserName = requestMap.get(Constant.REQ_EVENT_FROMUSERNAME);
//			//发送时间
//			String times = requestMap.get(Constant.RESP_MESSAGE_CREATETIME);
//			// 公众帐号
//			String toUserName = requestMap.get(Constant.RESP_MESSAGE_TOUSERNAME);
//			//文本
//			String content = requestMap.get(Constant.RESP_MESSAGE_CONTENT);
			MessageVO  msg = MessageUtil.parseXmlToObj(request);
			log.info("封装对象 >>>> "+msg.toString());
			//获取公众号站点id
			Long siteId=weChatAccountsInfoService.getSiteId(msg.getToUserName());
			log.info("SITEID >>>> "+siteId);
			if (msg.getMsgType().equals(Constant.REQ_MESSAGE_TYPE_TEXT)) {//文本消息
				return keyWordsMsg(msg,siteId);
			}else if(msg.getMsgType().equals(Constant.REQ_MESSAGE_TYPE_EVENT)){//事件消息
				return eventMsg(msg,siteId);
			}else if(msg.getMsgType().equals(Constant.RESP_MESSAGE_TYPE_IMAGE)) {//图片、暂且没有处理
//				acceptMsgNew(msg,siteId);
				return "";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return respMessage;
	}
	//事件消息
	private String eventMsg(MessageVO  msg,Long siteId ){
		String respMessage="";
		String Event = msg.getEvent();
		String fromUserName = msg.getFromUserName();
		String toUserName = msg.getToUserName();
		String times = msg.getCreateTime();
		String eventKey = msg.getEventKey();
		log.info("事件类型 >>>> "+msg.getEvent());
		TextMessage textMessage = new TextMessage();
		textMessage.setToUserName(fromUserName);
		textMessage.setFromUserName(toUserName);
		textMessage.setCreateTime(new Date().getTime());
		textMessage.setMsgType(Constant.RESP_MESSAGE_TYPE_TEXT);
		textMessage.setFuncFlag(0);
		//未匹配关键词默认提示
		textMessage.setContent(subscribeMsgService.getMsgCache(siteId));
		if(Constant.REQ_EVENT_TYPE_SUBSCRIBE.equals(Event)){//关注
			asyncOperation(siteId,fromUserName,Constant.REQ_EVENT_TYPE_SUBSCRIBE,times,null);
			// 将文本消息对象转换成xml字符串
			respMessage = MessageUtil.textMessageToXml(textMessage);
			return respMessage;
		}else if(Constant.REQ_EVENT_TYPE_UNSUBSCRIBE.equals(Event)){//取消关注
			//异步操作
			asyncOperation(siteId,fromUserName,Constant.REQ_EVENT_TYPE_UNSUBSCRIBE,times,null);
			return respMessage;
		}else if(Constant.REQ_EVENT_TYPE_VIEW.equals(Event)){//浏览事件
			//异步操作
			asyncOperation(siteId,fromUserName,Constant.REQ_EVENT_TYPE_VIEW,times,eventKey);
			return respMessage;
		}else if(Constant.REQ_EVENT_TYPE_CLICK.equals(Event)){
			// 接收用户发送的文本消息内容
			KeyWordsMsgEO kwm=keyWordsMsgService.getKeyWordsMsg(siteId,eventKey);
			//异步操作
			asyncOperation(siteId,fromUserName,Constant.REQ_EVENT_TYPE_CLICK,times,eventKey);
			if(kwm != null){
				if(kwm.getMsgType().equals(Constant.REQ_MESSAGE_TYPE_TEXT)){//文字信息直接返回
					textMessage.setContent(kwm.getContent());
					return MessageUtil.textMessageToXml(textMessage);
				}else if(kwm.getMsgType().equals(Constant.REQ_MESSAGE_TYPE_NEWS)){//图文信息则查询素材
					//查询关键词绑定的所有素材
					List<Article> articles = null;
					List<WeChatArticleEO> wcArticles = weChatArticleService.getByKeyWordsMsgId(kwm.getId());
					if(wcArticles != null && wcArticles.size() > 0){
						articles = new ArrayList<Article>();
						for(WeChatArticleEO art:wcArticles){
							Article article=new Article();
							article.setTitle(art.getTitle());
							article.setDescription(art.getDescription());
							if(art.getThumbImg()!=null){
								article.setPicUrl(fileServer+art.getThumbImg());
							}
							if(!AppUtil.isEmpty(art.getType())&& art.getType()==2){
								article.setUrl(art.getUrl());
							}
							articles.add(article);
						}
					}
					//设置返回对象
					NewsMessage newsMsg=new NewsMessage();
					newsMsg.setToUserName(fromUserName);
					newsMsg.setFromUserName(toUserName);
					newsMsg.setArticleCount(articles.size());
					newsMsg.setMsgType(Constant.REQ_MESSAGE_TYPE_NEWS);
					newsMsg.setArticles(articles);
					// 将文本消息对象转换成xml字符串
					respMessage = MessageUtil.newsMessageToXml(newsMsg);
					return respMessage;
				}
			}
			// 将文本消息对象转换成xml字符串
			respMessage = MessageUtil.textMessageToXml(textMessage);
			return respMessage;
		}
		return respMessage;
	}
	//文本消息
	private String keyWordsMsg(final MessageVO  msg,final Long siteId) {

		String respMessage="";
//		String fromUserName = requestMap.get(Constant.REQ_EVENT_FROMUSERNAME);
//		String toUserName = requestMap.get(Constant.RESP_MESSAGE_TOUSERNAME);
//		String content = requestMap.get(Constant.RESP_MESSAGE_CONTENT);
		String fromUserName = msg.getFromUserName();
		String toUserName = msg.getToUserName();
		String times = msg.getCreateTime();
		String content = msg.getContent();
		TextMessage textMessage = new TextMessage();
		textMessage.setToUserName(fromUserName);
		textMessage.setFromUserName(toUserName);
		textMessage.setCreateTime(new Date().getTime());
		textMessage.setMsgType(Constant.RESP_MESSAGE_TYPE_TEXT);
		textMessage.setFuncFlag(0);
		KeyWordsMsgEO kwm=keyWordsMsgService.getKeyWordsMsg(siteId,content);
		//异步操作 保存日志
		asyncOperation(siteId,fromUserName,Constant.REQ_MESSAGE_TYPE_TEXT,times,content);
		//评价处理
		if(msg.getContent().equals("1")||msg.getContent().equals("2")||msg.getContent().equals("3")||msg.getContent().equals("4")){
			List<WechatMsgEO> wechatMsgEOs =	weChatMsgService.getTodoJudge(msg, siteId);
			if(null!=wechatMsgEOs&&wechatMsgEOs.size()>0){
				WechatMsgEO wechatMsgEO  =  wechatMsgEOs.get(0);
				wechatMsgEO.setIsJudge(1);
				wechatMsgEO.setJudge(msg.getContent());
				weChatMsgService.updateEntity(wechatMsgEO);
			}

			return "";
		}



		if(kwm != null){
			if(kwm != null){
				if(kwm.getMsgType().equals(Constant.REQ_MESSAGE_TYPE_TEXT)){//文字信息直接返回
					textMessage.setContent(kwm.getContent());
					log.info("返回文本 >>>> "+MessageUtil.textMessageToXml(textMessage));
					return MessageUtil.textMessageToXml(textMessage);
				}else if(kwm.getMsgType().equals(Constant.REQ_MESSAGE_TYPE_NEWS)){//图文信息则查询素材
					//查询关键词绑定的所有素材
					List<Article> articles = null;
					List<WeChatArticleEO> wcArticles = weChatArticleService.getByKeyWordsMsgId(kwm.getId());
					if(wcArticles != null && wcArticles.size() > 0){
						articles = new ArrayList<Article>();
						for(WeChatArticleEO art:wcArticles){
							Article article=new Article();
							article.setTitle(art.getTitle());
							article.setDescription(art.getDescription());
							if(art.getThumbImg()!=null){
								article.setPicUrl(fileServer+art.getThumbImg());
							}
							if(!AppUtil.isEmpty(art.getType())&&art.getType()==2){
								article.setUrl(art.getUrl());
							}
							articles.add(article);
						}
					}
					//设置返回对象
					NewsMessage newsMsg=new NewsMessage();
					newsMsg.setToUserName(fromUserName);
					newsMsg.setFromUserName(toUserName);
					newsMsg.setArticleCount(articles.size());
					newsMsg.setMsgType(Constant.REQ_MESSAGE_TYPE_NEWS);
					newsMsg.setArticles(articles);
					// 将文本消息对象转换成xml字符串
					respMessage = MessageUtil.newsMessageToXml(newsMsg);
					log.info("返回新闻 >>>> "+respMessage);
					return respMessage;
				}
			}
		}
		//未匹配关键词默认提示
//			SubscribeMsgEO sbm = subscribeMsgService.getMsgBySite(siteId);
//			textMessage.setContent(sbm.getContent());
//			// 将文本消息对象转换成xml字符串
//			respMessage = MessageUtil.textMessageToXml(textMessage);
		Pagination page=baseContentService.getContentForWeChat(content, Constant.REQ_MESSAGE_TYPE_NEWS, siteId,0L,9);
		@SuppressWarnings("unchecked")
		List<BaseContentEO> eos=(List<BaseContentEO>) page.getData();
		if(null!=eos&&eos.size()>0){
			NewsMessage newsMsg=new NewsMessage();
			newsMsg.setToUserName(fromUserName);
			newsMsg.setFromUserName(toUserName);
			newsMsg.setArticleCount(eos.size());
			newsMsg.setMsgType(Constant.REQ_MESSAGE_TYPE_NEWS);
			List<Article> li=new ArrayList<Article>();
			IndicatorEO site = CacheHandler.getEntity(IndicatorEO.class, siteId);
			for(BaseContentEO eo:eos){
				Article article=new Article();
				article.setTitle(eo.getTitle());
				article.setDescription(eo.getRemarks());
				if(null!=eo.getImageLink()){
					article.setPicUrl(fileServer+eo.getImageLink());
				}
				if(BaseContentEO.TypeCode.public_content.toString().equals(eo.getTypeCode())){
					article.setUrl(site.getUri()+"/html/public/" + eo.getColumnId() + eo.getId() + ".html");
				}else{
					//根据站点ID匹配url
					ColumnMgrEO column = CacheHandler.getEntity(ColumnMgrEO.class, eo.getColumnId());
					if(column != null){
						article.setUrl(site.getUri()+"/html/"+ column.getUrlPath() +"/"+eo.getId()+".html");
					}else{
						article.setUrl(site.getUri()+"/html/"+ eo.getColumnId() +"/"+eo.getId()+".html");
					}
				}
				li.add(article);
			}
			newsMsg.setArticles(li);
			respMessage = MessageUtil.newsMessageToXml(newsMsg);
		} else {
			taskExecutor.execute(new Runnable() {
				@Override
				public void run() {
					acceptMsgNew(msg,siteId);
				}
			});
			respMessage ="";
		}
		// 将文本消息对象转换成xml字符串

		log.info("最后返回 >>>> "+respMessage);
		return respMessage;
	}

	private void acceptMsg(HttpServletRequest request, HttpServletResponse response) throws Exception {

//		// 处理接收消息
//		ServletInputStream in = request.getInputStream();
//		// 将POST流转换为XStream对象
//		XStream xs = SerializeXmlUtil.createXstream();
//		xs.processAnnotations(InputMessage.class);
//		// 将指定节点下的xml节点数据映射为对象
//		xs.alias("xml", InputMessage.class);
//		// 将流转换为字符串
//		StringBuilder xmlMsg = new StringBuilder();
//		byte[] b = new byte[4096];
//		for (int n; (n = in.read(b)) != -1; ) {
//			xmlMsg.append(new String(b, 0, n, "UTF-8"));
//		}
//		// 将xml内容转换为InputMessage对象
//		InputMessage inputMsg = (InputMessage) xs.fromXML(xmlMsg.toString());
//		if (null != inputMsg) {
//			WechatMsgEO eo = new WechatMsgEO();
//			eo.setToUserName(inputMsg.getToUserName());
//			eo.setOriginUserName(inputMsg.getFromUserName());
//			eo.setCreateTime(inputMsg.getCreateTime());
//			eo.setMsgType(inputMsg.getMsgType());
//			eo.setMsgId(inputMsg.getMsgId());
//			eo.setContent(inputMsg.getContent());
//			eo.setPicUrl(inputMsg.getPicUrl());
//			eo.setLocationX(inputMsg.getLocationX());
//			eo.setLocationY(inputMsg.getLocationY());
//			eo.setScale(inputMsg.getScale());
//			eo.setLabel(inputMsg.getLabel());
//			eo.setTitle(inputMsg.getTitle());
//			eo.setDescription(inputMsg.getDescription());
//			eo.setUrl(inputMsg.getURL());
//			eo.setMediaId(inputMsg.getMediaId());
//			eo.setFormat(inputMsg.getFormat());
//			eo.setRecognition(inputMsg.getRecognition());
//			eo.setEvent(inputMsg.getEvent());
//			eo.setEventKey(inputMsg.getEventKey());
//			eo.setTicket(inputMsg.getTicket());
//			eo.setSiteId(LoginPersonUtil.getSiteId());
//			weChatMsgService.saveEntity(eo);
//		}
	}


	protected void acceptMsgNew(MessageVO  inputMsg,Long siteId) {
		if (null != inputMsg) {
			WechatMsgEO eo = new WechatMsgEO();
			eo.setToUserName(inputMsg.getToUserName());
			eo.setOriginUserName(inputMsg.getFromUserName());
			eo.setCreateTime(Long.valueOf(inputMsg.getCreateTime()));
			eo.setMsgType(inputMsg.getMsgType());
			if(!AppUtil.isEmpty(inputMsg.getMsgId())){
				eo.setMsgId(Long.valueOf(inputMsg.getMsgId()));
			}
			eo.setContent(inputMsg.getContent());
			eo.setPicUrl(inputMsg.getPicUrl());
			eo.setLocationX(inputMsg.getLocationX());
			eo.setLocationY(inputMsg.getLocationY());
			if(!AppUtil.isEmpty(inputMsg.getScale())){
				eo.setScale(Long.valueOf(inputMsg.getScale()) );
			}
			eo.setLabel(inputMsg.getLabel());
			eo.setTitle(inputMsg.getTitle());
			eo.setDescription(inputMsg.getDescription());
			eo.setUrl(inputMsg.getuRL());
			eo.setMediaId(inputMsg.getMediaId());
			eo.setFormat(inputMsg.getFormat());
			eo.setRecognition(inputMsg.getRecognition());
			eo.setEvent(inputMsg.getEvent());
			eo.setEventKey(inputMsg.getEventKey());
			eo.setTicket(inputMsg.getTicket());
			eo.setSiteId(siteId);
			weChatMsgService.saveEntity(eo);

			WeChatTurnEO weChatTurnEO =new WeChatTurnEO();
			weChatTurnEO.setOriginUserName(eo.getOriginUserName());
			weChatTurnEO.setMsgId(eo.getId());
			weChatTurnEO.setSiteId(siteId);
			weChatTurnEO.setOperateUnitId(LoginPersonUtil.getUnitId());
			weChatTurnEO.setOperateUnitName(LoginPersonUtil.getUnitName());
			weChatTurnEO.setType(WeChatTurnEO.TYPE.sub.toString());
			if(LoginPersonUtil.isSiteAdmin()){
				weChatTurnEO.setIsSiteAdmin(1);
			}
			if(LoginPersonUtil.isSuperAdmin()){
				weChatTurnEO.setIsSuperAdmin(1);
			}
			weChatTurnService.saveEntity(weChatTurnEO);


		}

	}


	//保存日志信息（关注或取消管理时，添加和新增关注用户，异步执行）
	private void asyncOperation(Long siteId,String fromUserName,  String msgType, String times, String content) {
		WeChatSyn wc = new WeChatSyn();
		wc.setWeChatLog(new WeChatLogEO(fromUserName,siteId,msgType,
				StringUtils.isEmpty(times)?new Date():new Date(Long.parseLong(times) * 1000),content));
		taskExecutor.execute(wc);
//		Thread t = new Thread(wc);
//		t.start();
	}
}