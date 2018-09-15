package cn.lonsun.staticcenter.exproject.tonglijiaoqu;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.internal.service.IBaseContentService;
import cn.lonsun.content.messageBoard.controller.internal.entity.MessageBoardEO;
import cn.lonsun.content.messageBoard.controller.internal.entity.MessageBoardForwardEO;
import cn.lonsun.content.messageBoard.controller.internal.entity.MessageBoardReplyEO;
import cn.lonsun.content.messageBoard.controller.vo.MessageBoardEditVO;
import cn.lonsun.content.messageBoard.controller.vo.MessageBoardForwardVO;
import cn.lonsun.content.messageBoard.controller.vo.MessageBoardReplyVO;
import cn.lonsun.content.messageBoard.service.IMessageBoardForwardService;
import cn.lonsun.content.messageBoard.service.IMessageBoardReplyService;
import cn.lonsun.content.messageBoard.service.IMessageBoardService;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.rbac.internal.entity.OrganEO;
import cn.lonsun.rbac.internal.entity.OrganPersonEO;
import cn.lonsun.rbac.internal.entity.PersonEO;
import cn.lonsun.rbac.internal.service.IOrganPersonService;
import cn.lonsun.rbac.internal.service.IOrganService;
import cn.lonsun.rbac.internal.service.IPersonService;
import cn.lonsun.staticcenter.util.JdbcUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

import static oracle.net.aso.C01.s;

/**
 * <br/>
 *
 * @version v1.0 <br/>
 */
@Controller
@RequestMapping("/tlsjq/messageBoard")
public class TLJQMessageBoardController extends BaseController {
    private JdbcUtils jdbcUtils;

    @Autowired
    private IMessageBoardService messageBoardService;

    @ModelAttribute
    public void get(@RequestParam(required = false) String id) {
        jdbcUtils = JdbcUtils.getInstance();
        //jdbcUtils.setURLSTR("jdbc:jtds:sqlserver://61.191.91.136:31433;DatabaseName=tljqzf_old;useLOBs=false;");
        jdbcUtils.setURLSTR("jdbc:jtds:sqlserver://10.0.0.253:1433;DatabaseName=tljqzf_old;useLOBs=false;");
        jdbcUtils.setUSERNAME("tljqzf_old");
        jdbcUtils.setUSERPASSWORD("tljqzf_old");
    }

    @RequestMapping("convert")
    @ResponseBody
    //http://localhost:8081/anqing/messageBoard/convert?curColumnId=3724779&startTime=1356969601&endTime=1420041601
    public Object convert(Long curColumnId) throws Exception {

        //导完后关闭，以防不测
        String sql = "SELECT * FROM MAILBOX";

        List<Object> list = jdbcUtils.excuteQuery(sql, null);
        if (list == null || list.size() <= 0) {
            return "没有导入的数据！<a href=\"/yingzhou/\">返回</a>";
        }

        for (int i = 0, l = list.size(); i < l; i++) {
            Map<String, Object> map = (HashMap<String, Object>) list.get(i);
            String personName = (String) map.get("USER_NAME");
            String title = (String) map.get("NAME");//标题
            String messageBoardContent = (String) map.get("CONTENT");//留言内容
            String replyContent = (String) map.get("REPLY_CONTENT");//回复内容
            Date addDate = (Date) map.get("CREATE_TIME");//留言时间
            Date replyDate = (Date) map.get("REPLY_TIME");//回复时间
            Boolean isCheck = (Boolean) map.get("IS_CHECK");
            String personPhone =(String) map.get("TEL");

            MessageBoardEditVO vo = new MessageBoardEditVO();
            MessageBoardForwardVO forwardVO = new MessageBoardForwardVO();
            MessageBoardReplyVO replyVO = new MessageBoardReplyVO();

            vo.setTypeCode(BaseContentEO.TypeCode.messageBoard.toString());
            vo.setColumnId(curColumnId);
            vo.setSiteId(3900463L);
            vo.setTitle(title);
            vo.setPersonName(personName);
/*
            vo.setPersonPhone(personPhone);
*/
            vo.setMessageBoardContent(messageBoardContent);
            vo.setIsPublicInfo(1);//个人信息不公开
            vo.setCreateDate(addDate);
            vo.setAddDate(addDate);
            vo.setIsPublic(1);
            vo.setClassCode("do_consult");
            vo.setClassName("咨询");
            if (isCheck == true) {
                vo.setIsPublish(1);
            }

            if (!StringUtils.isEmpty(replyContent)) {
                replyVO.setCreateDate(replyDate);
                replyVO.setReplyContent(replyContent);
                replyVO.setDealStatus("replyed");
                replyVO.setIsSuper(1);
                vo.setDealStatus("replyed");
            }

            messageBoardService.exportOldMessageBoard(vo, replyVO);
        }

        Long s2 = System.currentTimeMillis();


        return "导入成功（" + list.size() + " 条，耗时：" + (s2 - s) / 1000 + "秒）！<a href=\"/changfeng/\">返回</a>";

    }

}
