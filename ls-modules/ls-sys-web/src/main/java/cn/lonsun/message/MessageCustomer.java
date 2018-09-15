/*
 * MessagePush.java         2015年11月28日 <br/>
 *
 * Copyright (c) 1994-1999 AnHui LonSun, Inc. <br/>
 * All rights reserved.	<br/>
 *
 * This software is the confidential and proprietary information of AnHui	<br/>
 * LonSun, Inc. ("Confidential Information").  You shall not	<br/>
 * disclose such Confidential Information and shall use it only in	<br/>
 * accordance with the terms of the license agreement you entered into	<br/>
 * with Sun. <br/>
 */

package cn.lonsun.message;

import cn.lonsun.cache.client.CacheGroup;
import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.core.AbstractCustomer;
import cn.lonsun.message.internal.entity.MessageReceiveEO;
import cn.lonsun.message.internal.entity.MessageSystemEO;
import cn.lonsun.message.internal.service.IMessageReceiveService;
import cn.lonsun.message.internal.service.IMessageSystemService;
import cn.lonsun.rbac.login.InternalAccount;
import cn.lonsun.shiro.session.OnlineSession;
import cn.lonsun.shiro.util.OnlineSessionUtil;
import cn.lonsun.system.datadictionary.internal.entity.DataDictEO;
import cn.lonsun.system.datadictionary.internal.entity.DataDictItemEO;
import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.directwebremoting.Browser;
import org.directwebremoting.ScriptBuffer;
import org.directwebremoting.ScriptSession;
import org.directwebremoting.ScriptSessionFilter;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 消息推送到页面 <br/>
 *
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 * @date 2015年11月28日 <br/>
 */
@Component
public class MessageCustomer extends AbstractCustomer<MessageSystemEO> {
    @Resource
    private IMessageSystemService messageSystemService;
    @Resource
    private IMessageReceiveService messageReceiveService;

    @Override
    public void execute(MessageSystemEO messageEO) {
        String recUserIds = messageEO.getRecUserIds();// 接受人信息
        if (StringUtils.isEmpty(recUserIds)) {
            return;
        }
        // 模块编码
        DataDictEO dictEO = CacheHandler.getEntity(DataDictEO.class, CacheGroup.CMS_CODE, "column_type");
        List<DataDictItemEO> dictItemList = CacheHandler.getList(DataDictItemEO.class, CacheGroup.CMS_PARENTID, dictEO.getDictId());
        Map<String, DataDictItemEO> map = new HashMap<String, DataDictItemEO>();
        if (null != dictItemList && !dictItemList.isEmpty()) {
            for (DataDictItemEO item : dictItemList) {
                map.put(item.getCode(), item);
            }
        }
        // 构建消息接受人对象
        final Map<Long, MessageReceiveEO> messageReceiveMap = new HashMap<Long, MessageReceiveEO>();
        if (messageEO.isTodb()) {// 需要入库
            // 消息入库
            Long messageId = messageSystemService.saveEntity(messageEO);
            // 构建消息入库对象
            this.buildReceiveEO(messageId, messageEO, messageReceiveMap, map);
            // 消息接受人入库
            messageReceiveService.saveEntities(messageReceiveMap.values());
        } else {
            this.buildReceiveEO(null, messageEO, messageReceiveMap, map);
        }
        // 执行推送
        Browser.withAllSessionsFiltered(new ScriptSessionFilter() {
            @Override
            public boolean match(ScriptSession scriptSession) {
                try {
                    String sessionId = scriptSession.getHttpSessionId();
                    OnlineSession onlineSession = OnlineSessionUtil.getSessionById(sessionId);//抛异常
                    Long userId = (Long) onlineSession.getAttribute(InternalAccount.USER_USERID);
                    scriptSession.setAttribute(InternalAccount.USER_USERID, userId);
                    return messageReceiveMap.containsKey(userId);// 判断接受消息的人id是否在消息列表中
                } catch (Throwable e) {
                    // 这里可能存在scriptSession中httpsessionId为空
                    // 或者根据当前sessionId查询不到session，session已过期
                    return false;
                }
            }
        }, new Runnable() {
            @Override
            public void run() {
                // 得到满足推送条件的ScriptSession
                Collection<ScriptSession> sessions = Browser.getTargetSessions();
                // 遍历每一个ScriptSession
                for (ScriptSession scriptSession : sessions) {
                    ScriptBuffer script = new ScriptBuffer();
                    Long userId = (Long) scriptSession.getAttribute(InternalAccount.USER_USERID);
                    String json = JSON.toJSONString(messageReceiveMap.get(userId));
                    // 设置要调用的 js及参数
                    script.appendCall("indexMgr.reversCallBack", JSON.parseObject(json));
                    // 推送
                    scriptSession.addScript(script);
                }
            }
        });
    }

    /**
     * 构建消息接受对象
     *
     * @param messageId
     * @param messageEO
     * @param messageReceiveMap
     * @param map
     * @author fangtinghua
     */
    private void buildReceiveEO(Long messageId, MessageSystemEO messageEO, Map<Long, MessageReceiveEO> messageReceiveMap, Map<String, DataDictItemEO> map) {
        // 接受人id列表
        String[] userIds = messageEO.getRecUserIds().split(",");
        for (String userId : userIds) {
            Long uId = Long.valueOf(userId);
            if (!messageReceiveMap.containsKey(uId)) {
                // 构建接收人对象
                MessageReceiveEO receive = new MessageReceiveEO();
                receive.setMessageId(messageId);
                receive.setRecUserId(uId);
                if (map.containsKey(messageEO.getModeCode())) {
                    receive.setModeName(map.get(messageEO.getModeCode()).getName());
                } else {
                    receive.setModeName(messageEO.getModeCode());
                }
                receive.setModeCode(messageEO.getModeCode());
                receive.setMessageStatus(MessageReceiveEO.NO_READ);
                receive.setMessageType(messageEO.getMessageType());
                receive.setMessageSystemEO(messageEO);
                receive.setDateDiff("0秒前");// 消息处理设置时间
                messageReceiveMap.put(uId, receive);
            }
        }
    }
}