package cn.lonsun.wechat.controller;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.wechatmgr.internal.entity.WechatMsgEO;
import cn.lonsun.wechatmgr.internal.service.IWeChatMsgService;
import cn.lonsun.wechatmgr.internal.wechatapiutil.SerializeXmlUtil;
import cn.lonsun.wechatmgr.vo.InputMessage;
import com.thoughtworks.xstream.XStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * @author gu.fei
 * @version 2016-09-29 18:09
 */
@RequestMapping("/wechat/interact")
public class WeChatInteract {

    private static final Logger logger = LoggerFactory.getLogger(WeChatInteract.class);

    @Autowired
    private IWeChatMsgService weChatMsgService;

    /**
     * @param request
     * @param response
     */
    @RequestMapping(value = "sendMsg", method = { RequestMethod.GET, RequestMethod.POST })
    @ResponseBody
    public void sendMsg(HttpServletRequest request, HttpServletResponse response) {
        boolean isGet = request.getMethod().toLowerCase().equals("get");
        if (isGet) {
            logger.info("--进入验证access--");
            String echostr = request.getParameter("echostr");
            if(!AppUtil.isEmpty(echostr)){
                logger.info("--服务器接入生效--");
                try {
                    //完成相互认证
                    response.getWriter().print(echostr);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            try {
                request.setCharacterEncoding("UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            response.setContentType("text/xml;charset=UTF-8");
            try {
                acceptMsg(request,response);
            } catch (Exception e) {
                logger.error("--返回数据失败--");
                e.printStackTrace();
            }
        }
    }

    private void acceptMsg(HttpServletRequest request, HttpServletResponse response) throws Exception {
        // 处理接收消息
        ServletInputStream in = request.getInputStream();
        // 将POST流转换为XStream对象
        XStream xs = SerializeXmlUtil.createXstream();
        xs.processAnnotations(InputMessage.class);
        // 将指定节点下的xml节点数据映射为对象
        xs.alias("xml", InputMessage.class);
        // 将流转换为字符串
        StringBuilder xmlMsg = new StringBuilder();
        byte[] b = new byte[4096];
        for (int n; (n = in.read(b)) != -1;) {
            xmlMsg.append(new String(b, 0, n, "UTF-8"));
        }
        // 将xml内容转换为InputMessage对象
        InputMessage inputMsg = (InputMessage) xs.fromXML(xmlMsg.toString());
        if(null != inputMsg) {
            WechatMsgEO eo = new WechatMsgEO();
            eo.setToUserName(inputMsg.getToUserName());
            eo.setOriginUserName(inputMsg.getFromUserName());
            eo.setCreateTime(inputMsg.getCreateTime());
            eo.setMsgType(inputMsg.getMsgType());
            eo.setMsgId(inputMsg.getMsgId());
            eo.setContent(inputMsg.getContent());
            eo.setPicUrl(inputMsg.getPicUrl());
            eo.setLocationX(inputMsg.getLocationX());
            eo.setLocationY(inputMsg.getLocationY());
            eo.setScale(inputMsg.getScale());
            eo.setLabel(inputMsg.getLabel());
            eo.setTitle(inputMsg.getTitle());
            eo.setDescription(inputMsg.getDescription());
            eo.setUrl(inputMsg.getURL());
            eo.setMediaId(inputMsg.getMediaId());
            eo.setFormat(inputMsg.getFormat());
            eo.setRecognition(inputMsg.getRecognition());
            eo.setEvent(inputMsg.getEvent());
            eo.setEventKey(inputMsg.getEventKey());
            eo.setTicket(inputMsg.getTicket());
            weChatMsgService.saveEntity(eo);
        }
    }
}
