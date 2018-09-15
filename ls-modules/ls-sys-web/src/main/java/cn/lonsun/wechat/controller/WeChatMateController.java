package cn.lonsun.wechat.controller;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.util.LoginPersonUtil;
import cn.lonsun.wechatmgr.internal.entity.WeChatArticleEO;
import cn.lonsun.wechatmgr.internal.service.IWeChatArticleService;
import cn.lonsun.wechatmgr.internal.wechatapiutil.ApiUtil;
import cn.lonsun.wechatmgr.internal.wechatapiutil.UploadFile;
import cn.lonsun.wechatmgr.vo.WeChatArticleVO;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Hewbing
 * @ClassName: WeChatMateController
 * @Description: 微信素材控制器
 * @date 2016年4月7日 下午5:14:17
 */
@Controller
@RequestMapping("/weChat/mateMgr")
public class WeChatMateController extends BaseController {

    /**
     * 135编辑器配置
     */
    @Value("${weixin.appkey}")
    protected String wxappkey;

    @Autowired
    private IWeChatArticleService weChatArticleService;

    @RequestMapping("index")
    public String index() {
        return "/wechat/mate_index";
    }

    /**
     * @param vo
     * @return Object   return type
     * @throws
     * @Title: getArticelPage
     * @Description: 素材列表页
     */
    @RequestMapping("getArticelPage")
    @ResponseBody
    public Object getArticelPage(WeChatArticleVO vo) {
        return getObject(weChatArticleService.getPage(vo));
    }

    /**
     * @param id
     * @param map
     * @return String   return type
     * @throws
     * @Title: editArticle
     * @Description: 编辑素材
     */
    @RequestMapping("editArticle")
    public String editArticle(Long id, ModelMap map) {
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String str1 = sdf1.format(new Date());
        map.put("nowDate", str1);
        map.put("ID", id);
        map.put("author", LoginPersonUtil.getPersonName());
        return "/wechat/article_edit";
    }

    /**
     * @param id
     * @param map
     * @return String   return type
     * @throws
     * @Title: wxeditor
     * @Description: 微信素材编辑器
     */
    @RequestMapping("wxeditor")
    public String wxEditor(Long id, ModelMap map) {
        map.put("wxappkey", wxappkey);
        return "/wechat/wxeditor_135";
    }

    /**
     * @param id
     * @return Object   return type
     * @throws
     * @Title: getArticle
     * @Description: 获取素材内容
     */
    @RequestMapping("getArticle")
    @ResponseBody
    public Object getArticle(Long id) {
        return getObject(weChatArticleService.getEntity(WeChatArticleEO.class, id));
    }

    /**
     * @param article
     * @return Object   return type
     * @throws
     * @Title: saveArticle
     * @Description: 保存素材
     */
    @RequestMapping("saveArticle")
    @ResponseBody
    public Object saveArticle(WeChatArticleEO article) {
        Long siteId = LoginPersonUtil.getSiteId();
        if (null == siteId) {
            return ajaxErr("权限不足");
        } else {
            article.setSiteId(siteId);
//            String content =article.getContent();
//            if(!AppUtil.isEmpty(content)){
//                Pattern p = Pattern.compile("<img[^>]+src\\s*=\\s*['\"]([^'\"]+)['\"][^>]*>");
//                Matcher m = p.matcher(content);
//                String image="";
//                String url ="";
//                while(m.find()){
////                    System.out.println(m.group()+"-------------↓↓↓↓↓↓");
////                    System.out.println(m.group(1));
//                    image = m.group(1);
//                    url = UploadFile.uploadImage(image.substring(image.indexOf("/mongo")).replace("/mongo/",""));
//                    content = content.replace(image,url);
//                }
//
//
//            }
            weChatArticleService.saveArticle(article);
            return getObject();
        }
    }

    /**
     * @param id
     * @param mateId
     * @return Object   return type
     * @throws
     * @Title: deleteArt
     * @Description: 删除素材
     */
    @RequestMapping("deleteArt")
    @ResponseBody
    public Object deleteArt(Long id, String mateId) {
        weChatArticleService.delete(WeChatArticleEO.class, id);
        return getObject();
    }

    /**
     * @return Object   return type
     * @throws
     * @Title: getMateList
     * @Description: 从微信服务器取得素材（未实现）
     */
    @RequestMapping("getMateList")
    @ResponseBody
    public Object getMateList() {
        JSONObject json = ApiUtil.getNewsList(null);
        return json;
    }

    @RequestMapping("mateList")
    public String mateList(WeChatArticleVO vo) {
        return "/wechat/mate_list";
    }
}
