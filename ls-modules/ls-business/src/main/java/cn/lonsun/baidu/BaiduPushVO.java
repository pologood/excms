package cn.lonsun.baidu;

/**
 * @author DooCal
 * @ClassName: BaiduPush
 * @Description:
 * @date 2016/6/13 15:06
 */
public class BaiduPushVO {

    //推送类型
    private String pushType;

    //通知标题，可以为空；如果为空则设为appid对应的应用名
    private String title;

    //通知文本内容，不能为空
    private String description;

    //android客户端自定义通知样式，如果没有设置默认为0
    private Integer notification_builder_id;

    //只有notification_builder_id为0时有效，可以设置通知的基本样式包括(响铃：0x04;振动：0x02;可清除：0x01;),这是一个flag整形，每一位代表一种样式,如果想选择任意两种或三种通知样式，notification_basic_style的值即为对应样式数值相加后的值
    private Integer notification_basic_style;

    //点击通知后的行为(1：打开Url; 2：自定义行为；)
    private Integer openType;

    //需要打开的Url地址，open_type为1时才有效
    private String url;

    //设置消息类型,0表示透传消息,1表示通知,默认为0
    private Integer MessageType;

    //设置消息的有效时间,单位秒,默认3600*5.
    private Integer MsgExpires;

    //设置设备类型，deviceType => 1 for web, 2 for pc, 3 for android, 4 for ios, 5 for wp.
    private Integer DeviceType;

    // 设置定时推送时间，必需超过当前时间一分钟，单位秒.实例2分钟后推送
    //System.currentTimeMillis() / 1000 + 120
    private Integer SendTime;

    //仅IOS应用推送时使用，默认值为null，取值如下：1：开发状态2：生产状态
    private Integer DeployStatus;

    //单个推送的设备ID
    private String channelId;

    //栏目类型
    private String msgCode;

    //设备类型-备用
    private String msgType;

    //自定义链接
    private String msgUrl;

    //栏目ID
    private Long msgColumnId;

    //当前用户userID,用于线程给自己推送消息
    private Long userId;

    //角标
    private Long badge;

    public String getPushType() {
        if (pushType == null) {
            pushType = "all";
        }
        return pushType;
    }

    public void setPushType(String pushType) {
        this.pushType = pushType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getNotification_builder_id() {
        return notification_builder_id;
    }

    public void setNotification_builder_id(Integer notification_builder_id) {
        this.notification_builder_id = notification_builder_id;
    }

    public Integer getNotification_basic_style() {
        return notification_basic_style;
    }

    public void setNotification_basic_style(Integer notification_basic_style) {
        this.notification_basic_style = notification_basic_style;
    }

    public Integer getOpenType() {
        if (openType == null) {
            openType = 0;
        }
        return openType;
    }

    public void setOpenType(Integer open_type) {
        this.openType = open_type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getMessageType() {
        if (MessageType == null) {
            MessageType = 1;
        }
        return MessageType;
    }

    public void setMessageType(Integer messageType) {
        MessageType = messageType;
    }

    public Integer getMsgExpires() {
        if (MsgExpires == null) {
            MsgExpires = new Integer(3600);
        }
        return MsgExpires;
    }

    public void setMsgExpires(Integer msgExpires) {
        MsgExpires = msgExpires;
    }

    public Integer getDeviceType() {
        if (DeviceType == null) {
            DeviceType = 3;
        }
        return DeviceType;
    }

    public void setDeviceType(Integer deviceType) {
        DeviceType = deviceType;
    }

    public Integer getSendTime() {
        return SendTime;
    }

    public void setSendTime(Integer sendTime) {
        SendTime = sendTime;
    }

    public Integer getDeployStatus() {
        if (DeployStatus == null) {
            DeployStatus = 3;
        }
        return DeployStatus;
    }

    public void setDeployStatus(Integer deployStatus) {
        DeployStatus = deployStatus;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getMsgCode() {
        if (msgCode == null) {
            msgCode = "articleNews";//guestBook
        }
        return msgCode;
    }

    public void setMsgCode(String msgCode) {
        this.msgCode = msgCode;
    }

    public String getMsgType() {
        if (msgType == null) {
            msgType = "all";
        }
        return msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }

    public String getMsgUrl() {
        return msgUrl;
    }

    public void setMsgUrl(String msgUrl) {
        this.msgUrl = msgUrl;
    }

    public Long getMsgColumnId() {
        return msgColumnId;
    }

    public void setMsgColumnId(Long msgColumnId) {
        this.msgColumnId = msgColumnId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getBadge() {
        return badge;
    }

    public void setBadge(Long badge) {
        this.badge = badge;
    }
}