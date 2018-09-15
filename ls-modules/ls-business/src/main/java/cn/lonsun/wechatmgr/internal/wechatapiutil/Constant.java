package cn.lonsun.wechatmgr.internal.wechatapiutil;

public class Constant {
	// 响应消息类型：图片
	public static final String RESP_MESSAGE_TYPE_IMAGE = "image";
	// 响应消息类型：语音
	public static final String RESP_MESSAGE_TYPE_VOICE = "voice";
	// 响应消息类型：视频
	public static final String RESP_MESSAGE_TYPE_VIDEO = "video";
	// 响应消息类型：音乐
	public static final String RESP_MESSAGE_TYPE_MUSIC = "music";
	// 响应消息类型：图文
	public static final String RESP_MESSAGE_TYPE_NEWS = "news";
	
	//请求 Request
	public static final String REQ_MESSAGE_TYPE_TEXT = "text";				//文本消息
	public static final String REQ_MESSAGE_TYPE_NEWS="news";
	public static final String REQ_MESSAGE_TYPE_EVENT = "event";			//事件消息
	public static final String REQ_EVENT_TYPE_SUBSCRIBE = "subscribe";		//关注事件
	public static final String REQ_EVENT_TYPE_UNSUBSCRIBE = "unsubscribe";	//取消关注事件
	public static final String REQ_EVENT_TYPE_CLICK = "CLICK";	//取消关注事件
	public static final String REQ_EVENT_TYPE_VIEW = "VIEW";	//取消关注事件
	public static final String REQ_EVENT_FROMUSERNAME = "FromUserName";
	public static final String REQ_EVENT_EVENTKEY = "EventKey";
	//回复 Response
	public static final String RESP_MESSAGE_TYPE_TEXT = "text";				//回复文本
	public static final String RESP_MESSAGE_TOUSERNAME = "ToUserName";
	public static final String RESP_MESSAGE_MSGTYPE = "MsgType";
	public static final String RESP_MESSAGE_CONTENT = "Content";
	public static final String RESP_MESSAGE_CREATETIME = "CreateTime";


}
