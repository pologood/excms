package cn.lonsun.wechat.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jxl.ErrorCell;
import net.sf.json.JSONObject;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.util.ArrayFormat;
import cn.lonsun.util.LoginPersonUtil;
import cn.lonsun.wechatmgr.internal.entity.WeChatArticleEO;
import cn.lonsun.wechatmgr.internal.entity.WeChatPushMsgEO;
import cn.lonsun.wechatmgr.internal.service.IUserGroupService;
import cn.lonsun.wechatmgr.internal.service.IWeChatArticleService;
import cn.lonsun.wechatmgr.internal.service.IWeChatPushMsgService;
import cn.lonsun.wechatmgr.internal.wechatapiutil.ApiUtil;
import cn.lonsun.wechatmgr.internal.wechatapiutil.Article;
import cn.lonsun.wechatmgr.internal.wechatapiutil.UploadFile;
import cn.lonsun.wechatmgr.vo.PushMsgVO;
import cn.lonsun.wechatmgr.vo.WeChatArticleVO;

/**
 *
 * @ClassName: WeChatPushMsgController
 * @Description: 微信推消息
 * @author Hewbing
 * @date 2016年4月7日 下午5:19:12
 *
 */
@Controller
@RequestMapping("/weChat/pushMsg")
public class WeChatPushMsgController extends BaseController {

	private static final Logger logger = LoggerFactory.getLogger(WeChatPushMsgController.class);
	@Autowired
	private IWeChatArticleService weChatArticleService;

	@Autowired
	private IWeChatPushMsgService weChatPushMsgService;

	@Autowired
	private IUserGroupService userGroupService;

	@RequestMapping("index")
	public String index(){
		return "/wechat/push_index";
	}

	/**
	 *
	 * @Title: editMsg
	 * @Description: 编辑消息
	 * @param id
	 * @param map
	 * @return   Parameter
	 * @return  String   return type
	 * @throws
	 */
	@RequestMapping("editMsg")
	public String editMsg(Long id,ModelMap map){
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String str1 = sdf1.format(new Date());
		map.put("nowDate", str1);
		map.put("ID", id);
		Long siteId=LoginPersonUtil.getSiteId();
		map.put("UGROUP",userGroupService.getListBySite(siteId));
		return "/wechat/edit_push_msg";
	}

	/**
	 *
	 * @Title: saveMsg
	 * @Description: 保存推送消息
	 * @param msgEO
	 * @param artList
	 * @return   Parameter
	 * @return  Object   return type
	 * @throws
	 */
	@RequestMapping("saveMsg")
	@ResponseBody
	public Object saveMsg(WeChatPushMsgEO msgEO,@RequestParam(value="artList[]",required=false)Long[] artList){
		msgEO.setSiteId(LoginPersonUtil.getSiteId());
		if(msgEO.getType()==1||msgEO.getType()==2){
			if(artList!=null&&artList.length>0){
				String art=ArrayFormat.ArrayToString(artList);
				msgEO.setArticles(art);
			}
			if(msgEO.getId()!=null){
				weChatPushMsgService.updateEntity(msgEO);
			}else{
				weChatPushMsgService.saveEntity(msgEO);
			}
			return getObject();
		}else{
			return ajaxErr("消息类型不匹配");
		}
	}

	/**
	 *
	 * @Title: getMsgPage
	 * @Description: 消息分页列表
	 * @param msgVO
	 * @return   Parameter
	 * @return  Object   return type
	 * @throws
	 */
	@RequestMapping("getMsgPage")
	@ResponseBody
	public Object getMsgPage(PushMsgVO msgVO){
		return getObject(weChatPushMsgService.getPage(msgVO));
	}

	/**
	 *
	 * @Title: getMsg
	 * @Description: 消息详情
	 * @param id
	 * @return   Parameter
	 * @return  Object   return type
	 * @throws
	 */
	@RequestMapping("getMsg")
	@ResponseBody
	public Object getMsg(Long id){
		WeChatPushMsgEO msg=weChatPushMsgService.getPushMsg(id);
		return getObject(msg);
	}

	/**
	 *
	 * @Title: deleteMsg
	 * @Description: 删除消息
	 * @param id
	 * @return   Parameter
	 * @return  Object   return type
	 * @throws
	 */
	@RequestMapping("deleteMsg")
	@ResponseBody
	public Object deleteMsg(Long id){
		weChatPushMsgService.delete(WeChatPushMsgEO.class, id);
		return getObject();
	}

	/**
	 *
	 * @Title: sendMsg
	 * @Description: 发送消息
	 * @param id
	 * @return   Parameter
	 * @return  Object   return type
	 * @throws
	 */
	@RequestMapping("sendMsg")
	@ResponseBody
	public Object sendMsg(Long id){
		WeChatPushMsgEO pushEO = weChatPushMsgService.getEntity(WeChatPushMsgEO.class, id);
		String is_to_all="true";
		String group_id="";
		String groupID="";
		if(pushEO.getPushGroup()!=-1L){
			is_to_all="false";
			group_id=String.valueOf(pushEO.getPushGroup());
			groupID=",\"group_id\":\""+group_id+"\"";
		}
		if(pushEO.getType()==1){
			String jsonData="{\"filter\":{\"is_to_all\":"+is_to_all+""+groupID+"},\"text\":{\"content\":\""+pushEO.getMsgContent()+"\"},\"msgtype\":\"text\"}";;
			logger.info("SEND TEXT MSG >>>>>"+jsonData);
			JSONObject json = ApiUtil.sendMsg(jsonData);
			if(json.getInt("errcode")==0){
				weChatPushMsgService.updatePublish(pushEO.getId(),json.getLong("msg_id"), 1);
				return ajaxOk();
			}else{
				return ajaxErr("发送失败,errcode:"+json.getInt("errcode"));
			}
		}else{
			String[] list=null;
			if(pushEO.getArticles()!=null){
				list=pushEO.getArticles().split(",");
			}
			Long[] artList=new Long[list.length];
			if(list.length<=0){
				return ajaxErr("图文素材数不能为空");
			}else{
				for(int i=0;i<list.length;i++){
					artList[i]=Long.parseLong(list[i]);
				}
			}
			Map<String,Object> map=new HashMap<String, Object>();
			List<WeChatArticleEO> arts = weChatArticleService.getEntities(WeChatArticleEO.class, artList);
			List<Article> articles=new ArrayList<Article>();
			for(WeChatArticleEO art:arts){
				Article article=new Article();
				String media_id = UploadFile.uploadPermanentMedia2(art.getThumbImg(), art.getTitle(), art.getDescription());
				article.setThumb_media_id(media_id);
				article.setAuthor(art.getAuthor());
				article.setTitle(art.getTitle());
				if(AppUtil.isEmpty(art.getContent())){
					article.setContent(art.getDescription());
				}else{
					article.setContent(art.getContent());
				}
				String content =article.getContent();
				if(!AppUtil.isEmpty(content)){
					Pattern p = Pattern.compile("<img[^>]+src\\s*=\\s*['\"]([^'\"]+)['\"][^>]*>");
					Matcher m = p.matcher(content);
					String image="";
					String url ="";
					while(m.find()){
//                    System.out.println(m.group()+"-------------↓↓↓↓↓↓");
//                    System.out.println(m.group(1));
						image = m.group(1);
						url = UploadFile.uploadImage(image.substring(image.indexOf("/mongo")).replace("/mongo/",""));
						content = content.replace(image,url);
					}
					article.setContent(content);

				}
				article.setDigest(art.getDescription());
				article.setShow_cover_pic("1");
				article.setContent_source_url(art.getUrl());
				articles.add(article);
			}
			map.put("articles", articles);
			String jsonData=JSONObject.fromObject(map).toString();
			logger.info(jsonData);
			JSONObject retl = ApiUtil.addNews(jsonData);
			if(retl.containsKey("errcode")){
				int errcode = retl.getInt("errcode");
				if(errcode != 0){//请求错误
					String errorMsg = ApiUtil.errorCode.get(errcode);
					if(StringUtils.isEmpty(errorMsg)){
						return ajaxErr("发送失败,错误编码："+ errcode);
					}
					return ajaxErr(errorMsg);
				}
			}
			String mateId=retl.getString("media_id");
			System.out.println("媒体ID>>>>>>>>>"+mateId);
			String jsonData2="{\"filter\":{\"is_to_all\":"+is_to_all+""+groupID+"},\"mpnews\":{\"media_id\":\""+mateId+"\"},\"msgtype\":\"mpnews\"}";
			logger.info("SEND TEXT MSG >>>>>"+jsonData2);
			JSONObject json = ApiUtil.sendMsg(jsonData2);
			if(json.getInt("errcode")==0){
				weChatPushMsgService.updatePublish(pushEO.getId(),json.getLong("msg_id"), 1);
				return ajaxOk();
			}else{
				ApiUtil.delMaterial(mateId);
				return ajaxErr("发送失败,errcode:"+json.getInt("errcode"));
			}
		}

	}

	/**
	 *
	 * @Title: getArticelPage
	 * @Description:
	 * @param vo
	 * @return   Parameter
	 * @return  Object   return type
	 * @throws
	 */
	@RequestMapping("getArticelPage")
	@ResponseBody
	public Object getArticelPage(WeChatArticleVO vo){
		return getObject(weChatArticleService.getPage(vo));
	}

	@RequestMapping("editArticle")
	public String editArticle(Long id,ModelMap map){
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String str1 = sdf1.format(new Date());
		map.put("nowDate", str1);
		map.put("ID", id);
		map.put("author", LoginPersonUtil.getPersonName());
		return "/wechat/article_edit";
	}

	@RequestMapping("getArticle")
	@ResponseBody
	public Object getArticle(Long id){
		return getObject(weChatArticleService.getEntity(WeChatArticleEO.class, id));
	}

	//保存图文消息
	@RequestMapping("saveArticle")
	@ResponseBody
	public Object saveArticle(WeChatArticleEO article){
		Long siteId=LoginPersonUtil.getSiteId();
		if(null==siteId){
			return ajaxErr("权限不足");
		}else{
			String media_id = UploadFile.uploadPermanentMedia2(article.getThumbImg(), article.getTitle(), article.getDescription());
			if(AppUtil.isEmpty(article.getUrl())){
				article.setUrl("www.baidu.com");
			}
			if(article.getMediaId()!=null){
				String jsonData="{"+
						" \"media_id\":\""+article.getMediaId()+"\","+
						" \"index\":0,"+
						" \"articles\": {"+
						"      \"title\": \""+article.getTitle()+"\","+
						"       \"thumb_media_id\": \""+media_id+"\","+
						"       \"author\": \""+article.getAuthor()+"\","+
						"       \"digest\": \""+article.getDescription()+"\","+
						"       \"show_cover_pic\": 1,"+
						"       \"content\": \""+article.getContent()+"\","+
						"      \"content_source_url\": \""+article.getUrl()+"\""+
						"    }"+
						" }";
				JSONObject rel=ApiUtil.updateNews(jsonData);
				if(rel.getInt("errcode")!=0){
					return ajaxErr("修改失败");
				}
			}else{
				String jsonData="{"+
						" \"articles\": [{"+
						" \"title\": \""+article.getTitle()+"\","+
						" \"thumb_media_id\": \""+media_id+"\","+
						" \"author\": \""+article.getAuthor()+"\","+
						" \"digest\": \""+article.getDescription()+"\","+
						" \"show_cover_pic\": 1,"+
						" \"content\": \""+article.getContent()+"\","+
						" \"content_source_url\": \""+article.getUrl()+"\""+
						" },"+
						//若新增的是多图文素材，则此处应有几段articles结构，最多8段
						"]"+
						"}";
				System.out.println(jsonData);
				JSONObject retl = ApiUtil.addNews(jsonData);
				article.setMediaId(retl.getString("media_id"));
			}
			article.setSiteId(siteId);
			article.setThumbMedtaId(media_id);
			weChatArticleService.saveOrUpdateEntity(article);
			return getObject();
		}
	}

	@RequestMapping("revokeMsg")
	@ResponseBody
	public Object revokeMsg(Long msgId,Long id){
		JSONObject json = ApiUtil.deletePush(msgId);
		if(json.getInt("errcode")!=0){
			return ajaxErr("撤消失败");
		}else{
			weChatPushMsgService.updatePublish(id, msgId, 0);
			return ajaxOk();
		}
	}

}
