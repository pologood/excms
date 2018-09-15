package cn.lonsun.wechatmgr.internal.wechatapiutil;

import cn.lonsun.core.util.SpringContextHolder;
import cn.lonsun.wechatmgr.internal.entity.WeChatMenuEO;
import cn.lonsun.wechatmgr.internal.entity.WeChatUserEO;
import cn.lonsun.wechatmgr.internal.service.IWeChatMenuService;
import cn.lonsun.wechatmgr.vo.Menu1VO;
import cn.lonsun.wechatmgr.vo.Menu2VO;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//import net.sf.json.JSONArray;
//import net.sf.json.JSONObject;

/**
 * @author Hewbing
 * @ClassName: ApiUtil
 * @Description: 微信开发者工具类 详情请阅：http://mp.weixin.qq.com/wiki/0/4c93e31f953f24a42b921d2ae8d4e5e0.html
 * @date 2015年12月23日 下午2:30:04
 */
public class ApiUtil {

    private static final Logger logger = LoggerFactory.getLogger(ApiUtil.class);

    private static IWeChatMenuService weChatMenuService = SpringContextHolder.getBean("weChatMenuService");

    public static final Map<Integer, String> errorCode = new HashMap<Integer, String>();
    static{
        errorCode.put(-1, "系统繁忙，此时请开发者稍候再试");
        errorCode.put(0, "请求成功");
        errorCode.put(40001, "获取 access_token 时 AppSecret 错误，或者 access_token 无效。请开发者认真比对 AppSecret 的正确性，或查看是否正在为恰当的公众号调用接口");
        errorCode.put(40002, "不合法的凭证类型");
        errorCode.put(40003, "不合法的 OpenID ，请开发者确认 OpenID （该用户）是否已关注公众号，或是否是其他公众号的 OpenID");
        errorCode.put(40004, "不合法的媒体文件类型");
        errorCode.put(40005, "不合法的文件类型");
        errorCode.put(40006, "不合法的文件大小");
        errorCode.put(40007, "不合法的媒体文件 id");
        errorCode.put(40008, "不合法的消息类型");
        errorCode.put(40009, "不合法的图片文件大小");
        errorCode.put(40010, "不合法的语音文件大小");
        errorCode.put(40011, "不合法的视频文件大小");
        errorCode.put(45001, "文本消息内容为空");
        errorCode.put(45002, "消息内容超过限制");
        errorCode.put(45003, "标题字段超过限制");
        errorCode.put(45004, "描述字段超过限制");
        errorCode.put(45005, "链接字段超过限制");
        errorCode.put(45006, "图片链接字段超过限制");
        errorCode.put(45009, "接口调用超过限制");
        errorCode.put(47001, "解析 JSON/XML 内容错误");
        errorCode.put(50001, "用户未授权该 api");
        errorCode.put(40164, "ip地址不在白名单中");
        errorCode.put(61451, "参数错误 (invalid parameter)");
    }


    //获取用户列表
    private static final String GET_USER_LIST = "https://api.weixin.qq.com/cgi-bin/user/get?access_token=TOKEN&next_openid=NEXT_OPENID";
    //获取关注用户信息
    private static final String USER_INFO_URL = "https://api.weixin.qq.com/cgi-bin/user/info?access_token=TOKEN&openid=OPENID&lang=zh_CN";
    //粉丝备注
    private static final String REMARK_USER = "https://api.weixin.qq.com/cgi-bin/user/info/updateremark?access_token=TOKEN";
    //分组发送
    private static final String SEND_MSG = "https://api.weixin.qq.com/cgi-bin/message/mass/sendall?access_token=TOKEN";
    //新增素材
    private static final String ADD_NEWS = "https://api.weixin.qq.com/cgi-bin/material/add_news?access_token=TOKEN";
    //修改素材
    private static final String UPDATE_NEWS = "https://api.weixin.qq.com/cgi-bin/material/update_news?access_token=TOKEN";
    //删除素材
    private static final String DEL_MATE = "https://api.weixin.qq.com/cgi-bin/material/del_material?access_token=TOKEN";
    //获取素材
    private static final String GET_NEWS = "https://api.weixin.qq.com/cgi-bin/material/get_material?access_token=TOKEN";
    //获取素材列表
    private static final String GET_NEWS_LIST = "https://api.weixin.qq.com/cgi-bin/material/batchget_material?access_token=TOKEN";
    //修改素材
    private static final String UPLOAD_NEWS = "https://api.weixin.qq.com/cgi-bin/media/uploadnews?access_token=TOKEN";
    //创建菜单
    private static final String CREATE_MENU = "https://api.weixin.qq.com/cgi-bin/menu/create?access_token=TOKEN";
    //删除菜单
    private static final String DELETE_MENU = "https://api.weixin.qq.com/cgi-bin/menu/delete?access_token=TOKEN";
    //单点发送
    private static final String CUSTOM_SEND = "https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token=TOKEN";
    //获取素材列表
    private static final String GET_MATERIAL = "https://api.weixin.qq.com/cgi-bin/material/batchget_material?access_token=TOKEN";
    //创建分组
    private static final String CREATE_GROUP = "https://api.weixin.qq.com/cgi-bin/groups/create?access_token=TOKEN";
    //分组列表
    private static final String GET_GROUP_LIST = "https://api.weixin.qq.com/cgi-bin/groups/get?access_token=TOKEN";
    //批量移动至组
    private static final String BATCH_UPDATA_GROUP = "https://api.weixin.qq.com/cgi-bin/groups/members/batchupdate?access_token=TOKEN";
    //删除组
    private static final String DELETE_GROUP = "https://api.weixin.qq.com/cgi-bin/groups/delete?access_token=TOKEN";
    //删除群发消息
    private static final String DELETE_PUSH = "https://api.weixin.qq.com/cgi-bin/message/mass/delete?access_token=TOKEN";
    //更新组
    private static final String UPDATE_GROUP = "https://api.weixin.qq.com/cgi-bin/groups/update?access_token=TOKEN";
    //网页授权用户信息
    private static final String SNS_USERINFO = "https://api.weixin.qq.com/sns/userinfo?access_token=TOKEN&openid=OPENID&lang=zh_CN";


    /**
     * @param openid
     * @param siteId
     * @return WeChatUserEO   return type
     * @throws
     * @Title: getUserInfo
     * @Description: 获取关注用户详细信息
     */
    public static WeChatUserEO getUserInfo(String openid, Long siteId) {
        JSONObject json = HttpAccessUtil.HttpGet(USER_INFO_URL.replace("TOKEN", TokenHander.getAccessToken(siteId)).replace("OPENID", openid));
        logger.info("wechat user >>>> " + json.toString());
        WeChatUserEO weChatUser = (WeChatUserEO) JSONObject.toBean(json, WeChatUserEO.class);
        return weChatUser;
    }

    /**
     * @param jsonData
     * @return JSONObject   return type
     * @throws
     * @Title: addNews
     * @Description: 上传永久图文素材
     */
    public static JSONObject addNews(String jsonData) {
        JSONObject json = HttpAccessUtil.HttpPost(ADD_NEWS.replace("TOKEN", TokenHander.getAccessToken()), jsonData);
        System.err.println(json.toString());
        return json;
    }

    /**
     * @param jsonData
     * @return JSONObject   return type
     * @throws
     * @Title: updateNews
     * @Description: 修改素材
     */
    public static JSONObject updateNews(String jsonData) {
        JSONObject json = HttpAccessUtil.HttpPost(UPDATE_NEWS.replace("TOKEN", TokenHander.getAccessToken()), jsonData);
        System.err.println(json.toString());
        return json;
    }

    /**
     * @param jsonData Parameter
     * @return void   return type
     * @throws
     * @Title: uploadNews
     * @Description: 上传素材
     */
    public static void uploadNews(String jsonData) {
        JSONObject json = HttpAccessUtil.HttpPost(UPLOAD_NEWS.replace("TOKEN", TokenHander.getAccessToken()), jsonData);
        System.err.println(json.toString());
    }

    /**
     * @param jsonData
     * @return JSONObject   return type
     * @throws
     * @Title: sendMsg
     * @Description: 发送消息
     */
    public static JSONObject sendMsg(String jsonData) {
        JSONObject json = HttpAccessUtil.HttpPost(SEND_MSG.replace("TOKEN", TokenHander.getAccessToken()), jsonData);
        return json;
    }

    public static void getNews(String jsonData) {
        //jsonData="{\"media_id\":\"7OPmBm66hKuDlYX0alPwDP2LXmQ-y7eg3O9Ex3Srsw4\"}";
        JSONObject json = HttpAccessUtil.HttpPost(GET_NEWS.replace("TOKEN", TokenHander.getAccessToken()), jsonData);
        System.err.println(json.toString());
    }

    public static JSONObject getNewsList(String jsonData) {
        //jsonData="{\"type\":\"news\",\"offset\":0,\"count\":10}";
        JSONObject json = HttpAccessUtil.HttpPost(GET_NEWS_LIST.replace("TOKEN", TokenHander.getAccessToken()), jsonData);
        System.err.println(json.toString());
        return json;
    }

    /**
     * @param content
     * @param openid
     * @return JSONObject   return type
     * @throws
     * @Title: customSend
     * @Description:一对一消息
     */
    public static JSONObject customSend(String content, String openid) {
        String jsonData = "{" +
                "\"touser\":\"" + openid + "\"," +
                "\"msgtype\":\"text\"," +
                "\"text\":" +
                "{" +
                "\"content\":\"" + content + "\"" +
                "}" +
                "}";
        JSONObject json = HttpAccessUtil.HttpPost(CUSTOM_SEND.replace("TOKEN", TokenHander.getAccessToken()), jsonData);
        return json;
    }

    /**
     * @param jsonData Parameter
     * @return void   return type
     * @throws
     * @Title: getMaterial
     * @Description: 获取素材
     */
    public static void getMaterial(String jsonData) {
        jsonData = "{\"type\":\"news\",\"offset\":0,\"count\":20}";
        JSONObject json = HttpAccessUtil.HttpPost(GET_MATERIAL.replace("TOKEN", TokenHander.getAccessToken()), jsonData);
        System.err.println(json.toString());
    }

    /**
     * @param mateId
     * @return JSONObject   return type
     * @throws
     * @Title: delMaterial
     * @Description: 删除素材
     */
    public static JSONObject delMaterial(String mateId) {
        String jsonData = "{\"media_id\":\"" + mateId + "\"}";
        JSONObject json = HttpAccessUtil.HttpPost(DEL_MATE.replace("TOKEN", TokenHander.getAccessToken()), jsonData);
        return json;
    }

    /**
     * @param siteId Parameter
     * @return void   return type
     * @throws
     * @Title: createMenu
     * @Description: 创建菜单
     */
    public static JSONObject createMenu(Long siteId) {
        Map<String, Object> map = new HashMap<String, Object>();
        List<Object> list1 = new ArrayList<Object>();
        List<WeChatMenuEO> lv1s = weChatMenuService.get1Leve(siteId);
        for (WeChatMenuEO lv1 : lv1s) {
            List<WeChatMenuEO> lv2s = weChatMenuService.get2Leve(lv1.getId());
            if (lv2s == null || lv2s.size() <= 0) {
                Menu1VO _vo = new Menu1VO();
                _vo.setName(lv1.getName());
                _vo.setType(lv1.getType());
                _vo.setUrl(lv1.getUrl());
                _vo.setKey(lv1.getKey());
                list1.add(_vo);
            } else {
                Menu2VO m2vo = new Menu2VO();
                List<Menu1VO> _vo1 = new ArrayList<Menu1VO>();
                m2vo.setName(lv1.getName());
                for (WeChatMenuEO lv2 : lv2s) {
                    Menu1VO _vo = new Menu1VO();
                    _vo.setName(lv2.getName());
                    _vo.setType(lv2.getType());
                    _vo.setUrl(lv2.getUrl());
                    _vo.setKey(lv2.getKey());
                    _vo1.add(_vo);
                }
                m2vo.setSub_button(_vo1);
                list1.add(m2vo);
            }
        }
        map.put("button", list1);
        String jsonData = JSONObject.fromObject(map).toString();
        logger.info(jsonData);
        JSONObject json = HttpAccessUtil.HttpPost(CREATE_MENU.replace("TOKEN", TokenHander.getAccessToken()), jsonData);
        System.err.println(json.toString());
        return json;
    }

    /**
     * @return List<String>   return type
     * @throws
     * @Title: getUserList
     * @Description: 删除菜单
     */
    public static JSONObject deleteMenu() {
        JSONObject json = HttpAccessUtil.HttpGet(DELETE_MENU.replace("TOKEN", TokenHander.getAccessToken()));
        return json;
    }

    /**
     * @param siteId
     * @param nextOpenId
     * @return List<String>   return type
     * @throws
     * @Title: getUserList
     * @Description: 关注用户列表
     */
    public static List<String> getUserList(Long siteId, String nextOpenId) {
        JSONObject json = HttpAccessUtil.HttpGet(GET_USER_LIST.replace("TOKEN", TokenHander.getAccessToken(siteId)).replace("NEXT_OPENID", nextOpenId));
        JSONArray list = json.getJSONObject("data").getJSONArray("openid");
        List<String> li = JSONArray.fromObject(list);
        logger.info(li.toString());
        return li;
    }

    /**
     * @param groupName
     * @return JSONObject   return type
     * @throws
     * @Title: createGroup
     * @Description: 创建粉丝组
     */
    public static JSONObject createGroup(String groupName) {
        String jsonData = "{\"group\":{\"name\":\"" + groupName + "\"}}";
        JSONObject json = HttpAccessUtil.HttpPost(CREATE_GROUP.replace("TOKEN", TokenHander.getAccessToken()), jsonData);
        return json;
    }

    /**
     * @return JSONArray   return type
     * @throws
     * @Title: getGroupList
     * @Description: 获取关注用户组
     */
    public static JSONArray getGroupList() {
        JSONObject json = HttpAccessUtil.HttpGet(GET_GROUP_LIST.replace("TOKEN", TokenHander.getAccessToken()));
        JSONArray list = json.getJSONArray("groups");
        return list;
    }

    /**
     * @param openid
     * @param remark
     * @return JSONObject   return type
     * @throws
     * @Title: remarkUser
     * @Description: 修改用户备注
     */
    public static JSONObject remarkUser(String openid, String remark) {
        String jsonData = "{\"openid\":\"" + openid + "\",\"remark\":\"" + remark + "\"}";
        JSONObject json = HttpAccessUtil.HttpPost(REMARK_USER.replace("TOKEN", TokenHander.getAccessToken()), jsonData);
        return json;

    }

    /**
     * @param openids
     * @param groupId
     * @return JSONObject   return type
     * @throws
     * @Title: batchUpdateGroup
     * @Description: 移动用户至分组
     */
    public static JSONObject batchUpdateGroup(String[] openids, Long groupId) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("openid_list", openids);
        map.put("to_groupid", groupId);
        JSONObject jsonData = JSONObject.fromObject(map);
        logger.info(jsonData.toString());
        JSONObject json = HttpAccessUtil.HttpPost(BATCH_UPDATA_GROUP.replace("TOKEN", TokenHander.getAccessToken()), jsonData.toString());
        return json;

    }

    /**
     * @param groupId
     * @return JSONObject   return type
     * @throws
     * @Title: deleteGroup
     * @Description: 删除组
     */
    public static JSONObject deleteGroup(Long groupId) {
        String jsonData = "{\"group\":{\"id\":" + groupId + "}}";
        JSONObject json = HttpAccessUtil.HttpPost(DELETE_GROUP.replace("TOKEN", TokenHander.getAccessToken()), jsonData);
        return json;
    }

    /**
     * @param id
     * @param name
     * @return JSONObject   return type
     * @throws
     * @Title: updateGroup
     * @Description: 修改关注分组
     */
    public static JSONObject updateGroup(Long id, String name) {
        String jsonData = "{\"group\":{\"id\":" + id + ",\"name\":\"" + name + "\"}}";
        JSONObject json = HttpAccessUtil.HttpPost(UPDATE_GROUP.replace("TOKEN", TokenHander.getAccessToken()), jsonData);
        return json;
    }

    /**
     * @param code
     * @param siteId
     * @return JSONObject   return type
     * @throws
     * @Title: getSnsUserInfo
     * @Description: 网页认证用户信息
     */
    public static JSONObject getSnsUserInfo(String code, Long siteId) {
        WebAccessToken webToken = TokenHander.getWebAccessToken(siteId, code);
        JSONObject json = HttpAccessUtil.HttpGet(SNS_USERINFO.replace("TOKEN", webToken.getAccess_token()).replace("OPENID", webToken.getOpenid()));
        return json;
    }

    /**
     * @param mateId
     * @return JSONObject   return type
     * @throws
     * @Title: deletePush
     * @Description:删除群发消息
     */
    public static JSONObject deletePush(Long msgId) {
        String jsonData = "{\"msg_id\":" + msgId + "}";
        return HttpAccessUtil.HttpPost(DELETE_PUSH.replace("TOKEN", TokenHander.getAccessToken()), jsonData);
    }
}
