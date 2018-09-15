package cn.lonsun.util;

import cn.lonsun.GlobalConfig;
import cn.lonsun.activemq.MessageSender;
import cn.lonsun.baidu.BaiduPushVO;
import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.util.SpringContextHolder;
import cn.lonsun.core.util.TipsMode;
import cn.lonsun.message.internal.entity.MessageSystemEO;
import cn.lonsun.site.site.internal.entity.ColumnMgrEO;
import com.alibaba.fastjson.JSONObject;
import com.baidu.yun.core.log.YunLogEvent;
import com.baidu.yun.core.log.YunLogHandler;
import com.baidu.yun.push.auth.PushKeyPair;
import com.baidu.yun.push.client.BaiduPushClient;
import com.baidu.yun.push.constants.BaiduPushConstants;
import com.baidu.yun.push.exception.PushClientException;
import com.baidu.yun.push.exception.PushServerException;
import com.baidu.yun.push.model.PushMsgToAllRequest;
import com.baidu.yun.push.model.PushMsgToAllResponse;
import com.baidu.yun.push.model.PushMsgToSingleDeviceRequest;
import com.baidu.yun.push.model.PushMsgToSingleDeviceResponse;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.task.TaskExecutor;

import java.util.Map;

/**
 * @author DooCal
 * @ClassName: BaiduPush
 * @Description:
 * @date 2016/6/13 15:06
 */
public class BaiduPushUtil {

    //获取配置
    private static GlobalConfig GLOBALCONFIG = SpringContextHolder.getBean(GlobalConfig.class);

    //日志
    private static Logger LOGGER = LoggerFactory.getLogger(CacheHandler.class);

    private static TaskExecutor taskExecutor = SpringContextHolder.getBean("taskExecutor");

    public static void pushMsg(final String dreviceType, final BaiduPushVO vo) {
        sendPushMsg(dreviceType, vo);
    }

    public static void pushAndroidMsg(Long columnId, String title, String description, String msgUrl) {
        pushMsg("android", "all", null, columnId, title, description, msgUrl);
    }

    public static void pushIosMsg(Long columnId, String title, String description, String msgUrl) {
        pushMsg("ios", "all", null, columnId, title, description, msgUrl);
    }

    public static void pushAndroidSingleMsg(String channelId, Long columnId, String title, String description, String msgUrl) {
        pushMsg("android", "single", channelId, columnId, title, description, msgUrl);
    }

    public static void pushIosSingleMsg(String channelId, Long columnId, String title, String description, String msgUrl) {
        pushMsg("ios", "single", channelId, columnId, title, description, msgUrl);
    }

    public static void pushMsg(final String dreviceType, String puthType, String channelId, Long columnId, String title, String description, String msgUrl) {
        BaiduPushVO vo = new BaiduPushVO();
        vo.setChannelId(channelId);
        vo.setPushType(puthType);
        vo.setTitle(title);
        vo.setDescription(description);
        vo.setMsgType("all");

        if (columnId != null) {
            ColumnMgrEO eo = CacheHandler.getEntity(ColumnMgrEO.class, columnId);
            vo.setMsgCode(eo.getColumnTypeCode());
        } else {
            vo.setMsgCode("articleNews");
        }
        vo.setMsgUrl(msgUrl);
        vo.setMsgColumnId(columnId);
        sendPushMsg(dreviceType, vo);
    }

    public static void sendPushMsg(final String dreviceType, final BaiduPushVO vo) {

        if (AppUtil.isEmpty(dreviceType)) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "未指定设备类型");
        }

        if (AppUtil.isEmpty(vo.getTitle())) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "推送消息标题不能为空");
        }

        if (AppUtil.isEmpty(vo.getDescription())) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "推送消息描述不能为空");
        }

        if (AppUtil.isEmpty(vo.getMsgUrl())) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "推送消息链接不能为空");
        }

        if (AppUtil.isEmpty(vo.getMsgColumnId())) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "推送消息栏目ID不能为空");
        }

        vo.setUserId(LoginPersonUtil.getUserId());
        taskExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    // 绑定session至当前线程中
                    SessionFactory sessionFactory = SpringContextHolder.getBean(SessionFactory.class);
                    boolean participate = ConcurrentUtil.bindHibernateSessionToThread(sessionFactory);
                    pushSendMsg(dreviceType, vo);
                    ConcurrentUtil.closeHibernateSessionFromThread(participate, sessionFactory);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private static void pushSendMsg(String dreviceType, BaiduPushVO vo) {
        try {
            if (dreviceType.indexOf("ios") != -1) {
                vo.setDeviceType(4);
                if (vo.getPushType() != null && vo.getPushType().equals("single")) {
                    PushMsgToSingleDevice(vo);
                } else {
                    PushMsgToAllDevice(vo);
                }
            }
            if (dreviceType.indexOf("android") != -1) {
                vo.setDeviceType(3);
                if (vo.getPushType() != null && vo.getPushType().equals("single")) {
                    BaiduPushUtil.PushMsgToSingleDevice(vo);
                } else {
                    BaiduPushUtil.PushMsgToAllDevice(vo);
                }
            }
        } catch (PushClientException e) {
            e.printStackTrace();
        } catch (PushServerException e) {
            e.printStackTrace();
        }
    }

    private static void pushSysMsg(BaiduPushVO vo) {
        // 构建消息对象
        MessageSystemEO messageSystemEO = new MessageSystemEO();
        messageSystemEO.setMessageType(1L);// 提示消息
        String title = "";
        if (vo.getDeviceType() != null && vo.getDeviceType() == 4) {
            title = "苹果设备消息推送成功！";
        } else {
            title = "安卓设备消息推送成功！";
        }
        messageSystemEO.setTitle(title);
        messageSystemEO.setMessageStatus(MessageSystemEO.MessageStatus.success.toString());
        messageSystemEO.setContent(title);
        messageSystemEO.setLink("");
        messageSystemEO.setRecUserIds(String.valueOf(vo.getUserId()));
        MessageSender.sendMessage(messageSystemEO);
    }

    private static BaiduPushClient baiduPushClient(BaiduPushVO vo) {

        // 1. get apiKey and secretKey from developer console
        String apiKey = "", secretKey = "";
        if (vo.getDeviceType() != null && vo.getDeviceType() == 4) {
            apiKey = GLOBALCONFIG.getBaiduIOSApiKey();
            secretKey = GLOBALCONFIG.getBaiduIOSSecretKey();
        } else {
            apiKey = GLOBALCONFIG.getBaiduAndroidApiKey();
            secretKey = GLOBALCONFIG.getBaiduAndroidSecretKey();
        }

        PushKeyPair pair = new PushKeyPair(apiKey, secretKey);

        // 2. build a BaidupushClient object to access released interfaces
        BaiduPushClient pushClient = new BaiduPushClient(pair, BaiduPushConstants.CHANNEL_REST_URL);

        // 3. register a YunLogHandler to get detail interacting information
        // in this request.
        pushClient.setChannelLogHandler(new YunLogHandler() {
            @Override
            public void onHandle(YunLogEvent event) {
                System.out.println(event.getMessage());
            }
        });

        return pushClient;
    }

    //创建 Android的通知
    private static Map<String, Object> buildMessage(BaiduPushVO vo) {
        //创建 Android的通知
        JSONObject notification = new JSONObject();

        //通知标题，可以为空；如果为空则设为appid对应的应用名
        notification.put("title", vo.getTitle());

        //通知文本内容，不能为空
        notification.put("description", vo.getDescription());

        //android客户端自定义通知样式，如果没有设置默认为0
        notification.put("notification_builder_id", 0);

        //只有notification_builder_id为0时有效，可以设置通知的基本样式包括(响铃：0x04;振动：0x02;可清除：0x01;),这是一个flag整形，每一位代表一种样式,如果想选择任意两种或三种通知样式，notification_basic_style的值即为对应样式数值相加后的值
        notification.put("notification_basic_style", 7);

        //点击通知后的行为(1：打开Url; 2：自定义行为；)
        notification.put("open_type", vo.getOpenType());

        //需要打开的Url地址，open_type为1时才有效
        //notification.put("url", "");

        //自定义消息
        JSONObject jsonCustormCont = new JSONObject();

        //对应EX8后台栏目类型编码
        jsonCustormCont.put("code", vo.getMsgCode());

        //推送类型 all 全推 single 单推
        jsonCustormCont.put("msgType", vo.getMsgType());

        //自定义链接
        jsonCustormCont.put("msgUrl", vo.getMsgUrl());

        //栏目ID
        jsonCustormCont.put("msgColumnId", vo.getMsgColumnId());

        //构建角标
        JSONObject aps = new JSONObject();
        aps.put("badge", vo.getBadge());

        //自定义内容，键值对，Json对象形式(可选)；在android客户端，这些键值对将以Intent中的extra进行传递
        notification.put("custom_content", jsonCustormCont);
        notification.put("aps", aps);


        return notification;
    }

    //全推 request 对象
    private static PushMsgToAllRequest buildPushMsgToAllRequest(BaiduPushVO vo) {
        //创建 Android的通知
        Map<String, Object> notification = buildMessage(vo);
        // 4. specify request arguments
        PushMsgToAllRequest request = new PushMsgToAllRequest()
            //设置消息的有效时间,单位秒,默认3600*5.
            .addMsgExpires(vo.getMsgExpires())
            //设置消息类型,0表示透传消息,1表示通知,默认为0
            .addMessageType(vo.getMessageType())
            //消息内容
            .addMessage(notification.toString()) //添加透传消息
            //.addSendTime(System.currentTimeMillis() / 1000 + 120) // 设置定时推送时间，必需超过当前时间一分钟，单位秒.实例2分钟后推送
            //设置设备类型，deviceType => 1 for web, 2 for pc, 3 for android, 4 for ios, 5 for wp.
            .addDeviceType(vo.getDeviceType());

        //仅IOS应用推送时使用，默认值为null，取值如下：1：开发状态2：生产状态
        if (vo.getPushType() != null && vo.getPushType().equals("ios")) {
            request.setDeployStatus(GLOBALCONFIG.getBaiduIOSDeployStatus());
        }
        return request;
    }

    //单推 request 对象
    private static PushMsgToSingleDeviceRequest buildPushMsgToSingleRequest(BaiduPushVO vo) {
        //创建 Android的通知
        Map<String, Object> notification = buildMessage(vo);

        PushMsgToSingleDeviceRequest request = new PushMsgToSingleDeviceRequest()
            .addChannelId(vo.getChannelId())
            // message有效时间
            .addMsgExpires(vo.getMsgExpires())
            // 1：通知,0:透传消息. 默认为0 注：IOS只有通知.
            .addMessageType(vo.getMessageType())
            .addMessage(notification.toString())
            // deviceType => 3:android, 4:ios
            .addDeviceType(vo.getDeviceType());

        //仅IOS应用推送时使用，默认值为null，取值如下：1：开发状态2：生产状态
        if (vo.getPushType() != null && vo.getPushType().equals("ios")) {
            request.setDeployStatus(GLOBALCONFIG.getBaiduIOSDeployStatus());
        }

        return request;
    }

    //消息全推
    public static void PushMsgToAllDevice(BaiduPushVO vo) throws PushClientException, PushServerException {

        try {
            //http request
            PushMsgToAllResponse response = baiduPushClient(vo).pushMsgToAll(buildPushMsgToAllRequest(vo));

            //推送系统内部消息
            pushSysMsg(vo);

            // Http请求结果解析打印
            LOGGER.info("msgId: " + response.getMsgId() + ",sendTime: " + response.getSendTime() + ",timerId: " + response.getTimerId());

        } catch (PushClientException e) {
            if (BaiduPushConstants.ERROROPTTYPE) {
                throw e;
            } else {
                e.printStackTrace();
            }
        } catch (PushServerException e) {
            if (BaiduPushConstants.ERROROPTTYPE) {
                throw e;
            } else {
                LOGGER.error(String.format("requestId: %d, errorCode: %d, errorMessage: %s", e.getRequestId(), e.getErrorCode(), e.getErrorMsg()));
            }
        }
    }

    public static void PushMsgToSingleDevice(BaiduPushVO vo) throws PushClientException, PushServerException {

        try {

            // 5. http request
            PushMsgToSingleDeviceResponse response = baiduPushClient(vo).pushMsgToSingleDevice(buildPushMsgToSingleRequest(vo));

            //推送系统内部消息
            pushSysMsg(vo);

            // Http请求结果解析打印
            LOGGER.info("msgId: " + response.getMsgId() + ",sendTime: " + response.getSendTime());

        } catch (PushClientException e) {
            /*
             * ERROROPTTYPE 用于设置异常的处理方式 -- 抛出异常和捕获异常,'true' 表示抛出, 'false' 表示捕获。
			 */
            if (BaiduPushConstants.ERROROPTTYPE) {
                throw e;
            } else {
                e.printStackTrace();
            }
        } catch (PushServerException e) {
            if (BaiduPushConstants.ERROROPTTYPE) {
                throw e;
            } else {
                LOGGER.error(String.format("requestId: %d, errorCode: %d, errorMessage: %s", e.getRequestId(), e.getErrorCode(), e.getErrorMsg()));
            }
        }
    }

    public static void main(String[] args) throws PushClientException, PushServerException {

    }

}
