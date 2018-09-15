package cn.lonsun.staticcenter.exproject.anqing;

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
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.rbac.internal.entity.OrganEO;
import cn.lonsun.rbac.internal.entity.OrganPersonEO;
import cn.lonsun.rbac.internal.entity.PersonEO;
import cn.lonsun.rbac.internal.service.IOrganPersonService;
import cn.lonsun.rbac.internal.service.IOrganService;
import cn.lonsun.rbac.internal.service.IPersonService;
import cn.lonsun.staticcenter.util.JdbcUtils;
import cn.lonsun.system.member.internal.entity.MemberEO;
import cn.lonsun.system.member.internal.service.IMemberService;
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
@RequestMapping("/anqing/messageBoard")
public class AnQingMessageBoardController extends BaseController {
    private JdbcUtils jdbcUtils;

    @Autowired
    private IMessageBoardService messageBoardService;

    @Autowired
    private IBaseContentService baseContentService;

    @Autowired
    private IMemberService memberService;

    @Autowired
    private IMessageBoardForwardService forwardService;

    @Autowired
    private IMessageBoardReplyService replyService;

    @Autowired
    private IOrganPersonService organPersonService;

    @Autowired
    private IOrganService organService;

    @Autowired
    private IPersonService personService;

    // 长日期格式
    public static String TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";


    @ModelAttribute
    public void get(@RequestParam(required = false) String id) {
        jdbcUtils = JdbcUtils.getInstance();
        jdbcUtils.setDRIVER("org.gjt.mm.mysql.Driver");
//        jdbcUtils.setURLSTR("jdbc:mysql://61.191.61.136:21606/jzsfj?useUnicode=true&characterEncoding=UTF8");
        jdbcUtils.setURLSTR("jdbc:mysql://61.191.61.136:21606/anqing_old?useUnicode=true&characterEncoding=UTF8&useSSL=true");
        jdbcUtils.setUSERNAME("root");
        jdbcUtils.setUSERPASSWORD("12345678");
    }

    @RequestMapping("convert")
//http://localhost:8081/anqing/messageBoard/convert?curColumnId=3724779&startTime=1356969601&endTime=1420041601
    @ResponseBody
    //1475251200    1477064151 == 2016-10-21 23:35:51  and addtime > " + startTime + " and addtime < " + endTime + "
    public Object convert(Long curColumnId, Long startTime, Long endTime) throws Exception {

        //导完后关闭，以防不测
        String sql = "SELECT * FROM letter where isdel=0 and unitid>0 and unitid !=172  and ispub=0";

        List<Object> list = jdbcUtils.excuteQuery(sql, null);
        if (list == null || list.size() <= 0) {
            return "没有导入的数据！<a href=\"/yingzhou/\">返回</a>";
        }

        String sql_22 = "SELECT * FROM reply where isdel=0 and (unitid>0 or userid>0)";
        List<Object> list_22 = jdbcUtils.excuteQuery(sql_22, null);

        Map<String, List<Object>> map_22 = new HashMap<String, List<Object>>();

        for (int i = 0, l = list_22.size(); i < l; i++) {

            Map<String, Object> map_2 = (HashMap<String, Object>) list_22.get(i);
            String latterId = String.valueOf((Integer) map_2.get("latterid"));

            if (map_22.containsKey(latterId)) {
                map_22.get(latterId).add(map_2);
            } else {
                List<Object> newList = new ArrayList<Object>();
                newList.add(map_2);
                map_22.put(latterId, newList);
            }
        }

        for (int i = 0, l = list.size(); i < l; i++) {
            Map<String, Object> map = (HashMap<String, Object>) list.get(i);
            Long createUserId = Long.valueOf((Integer) map.get("userid"));
            Long unitId = Long.valueOf((Integer) map.get("unitid"));
            String toUnitIds = (String) map.get("to_unitids");
            String unitName = (String) map.get("unitname");
            Integer docNum = (Integer) map.get("latterid");
            String personName = (String) map.get("username");
            String title = (String) map.get("title");
            String ip = (String) map.get("userip");
            String messageBoardContent = (String) map.get("content");
            Integer hit = (Integer) map.get("views");
            Integer m_Date = (Integer) map.get("addtime");
            Date addDate = new Date(new Long(m_Date) * 1000);
            String personIp = (String) map.get("userip");
            String personPhone = (String) map.get("tel");

            Integer typeCode = (Integer) map.get("typeid");

            String VC_ID = null;
            Integer latterid = (Integer) map.get("latterid");
            VC_ID = String.valueOf(latterid);

            MessageBoardEditVO vo = new MessageBoardEditVO();
            List<MessageBoardForwardVO> forwardVOList = new ArrayList<MessageBoardForwardVO>();
            List<MessageBoardReplyVO> replyVOList = new ArrayList<MessageBoardReplyVO>();

            vo.setTypeCode(BaseContentEO.TypeCode.messageBoard.toString());
            vo.setColumnId(curColumnId);
            vo.setSiteId(2653861L);
            vo.setDocNum(String.valueOf(docNum));
            vo.setTitle(title);
            vo.setHit(Long.valueOf(hit));
            vo.setPersonName(personName);
/*
            vo.setPersonPhone(personPhone);
*/
            vo.setMessageBoardContent(messageBoardContent);
            vo.setCreateUserId(createUserId);
            vo.setPersonIp(personIp);
            vo.setIsPublicInfo(0);//个人信息不公开
            vo.setCreateDate(addDate);
            vo.setAddDate(addDate);
            vo.setIsPublic(0);
            vo.setPersonIp(ip);

            if (!org.apache.commons.lang3.StringUtils.isEmpty(toUnitIds)) {
                MessageBoardForwardVO forwardVO = new MessageBoardForwardVO();
                if (toUnitIds.substring(0, 1).equals(",")) {
                    toUnitIds = toUnitIds.substring(1);
                }
                String[] receiveUnitId = toUnitIds.split(",");
                for (String receiveId : receiveUnitId) {
                    forwardVO.setReceiveOrganId(Long.valueOf(receiveId));
                    forwardVO.setCreateUserId(createUserId);
                    forwardVO.setDefaultDays(10);
                    forwardVO.setOperationStatus(MessageBoardForwardEO.OperationStatus.Normal.toString());
                    forwardVOList.add(forwardVO);
                }
            } else {
                MessageBoardForwardVO forwardVO = new MessageBoardForwardVO();
                forwardVO.setReceiveUnitName(unitName);
                forwardVO.setReceiveOrganId(Long.valueOf(unitId));
                forwardVO.setCreateUserId(createUserId);
                forwardVO.setDefaultDays(10);
                forwardVO.setOperationStatus(MessageBoardForwardEO.OperationStatus.Normal.toString());
                forwardVOList.add(forwardVO);
            }

            if (typeCode != null) {
                if (typeCode == 0) {
                    vo.setClassCode("do_consult");
                    vo.setClassName("咨询");
                } else if (typeCode == 1) {
                    vo.setClassCode("do_suggest");
                    vo.setClassName("建议");
                } else if (typeCode == 2) {
                    vo.setClassCode("do_commend");
                    vo.setClassName("表扬");
                } else if (typeCode == 3) {
                    vo.setClassCode("do_complain");
                    vo.setClassName("投诉");
                } else if (typeCode == 4) {
                    vo.setClassCode("others");
                    vo.setClassName("其他");
                } else {
                    vo.setClassCode("do_consult");
                    vo.setClassName("咨询");
                }
            }

            vo.setIsPublish(1);

            if (VC_ID != null && map_22.get(VC_ID) != null) {
                List<Object> replyList = map_22.get(VC_ID);
                if (replyList != null && replyList.size() > 0) {
                    for (int j = 0; j < replyList.size(); j++) {
                        MessageBoardReplyVO replyVO = new MessageBoardReplyVO();
                        Map<String, Object> map_0 = (Map<String, Object>) replyList.get(j);
                        Integer vc_replyuserid = (Integer) map_0.get("userid");
                        Integer vc_replyunitid = (Integer) map_0.get("unitid");

                        if (vc_replyunitid == 0) {

                            Map<String, Object> map1 = new HashMap<String, Object>();
                            map1.put("personId", Long.valueOf(vc_replyuserid));
                            map1.put("organDn", "USER");
                            OrganPersonEO organPersonEO = organPersonService.getEntity(OrganPersonEO.class, map1);

                            if (organPersonEO != null) {

                                PersonEO personEO = personService.getEntity(PersonEO.class, organPersonEO.getOrganId());

                                if (personEO != null) {
                                    replyVO.setCreateOrganId(Long.valueOf(personEO.getUnitId()));
                                    replyVO.setReceiveName(personEO.getUnitName());
                                } else {
                                    continue;
                                }

                            } else {
                                continue;
                            }
                        }

                        String replyContent = (String) map_0.get("content");
                        if (replyContent != null) {
                            Integer dt_handletime = (Integer) map_0.get("addtime");
                            Date replyDate = new Date(new Long(dt_handletime) * 1000);
                            String vc_replyunitname = (String) map_0.get("unitname");

                            if (vc_replyuserid != null) {
                                replyVO.setCreateUserId(Long.valueOf(vc_replyuserid));
                            }

                            String replyUserIp = (String) map_0.get("userip");

                            String vc_replyer = (String) map_0.get("username");
                            replyVO.setReplyContent(replyContent);
                            replyVO.setIp(replyUserIp);
                            replyVO.setCreateDate(replyDate);
                            replyVO.setDealStatus("handled");
                            replyVO.setUsername(vc_replyer);

                            if (vc_replyunitid != 0)
                                if (vc_replyunitid != null) {
                                    replyVO.setCreateOrganId(Long.valueOf(vc_replyunitid));
                                }

                            if (vc_replyunitname != null) {
                                replyVO.setReceiveName(vc_replyunitname);
                            }
                        }

                        vo.setDealStatus("handled");

                        replyVOList.add(replyVO);
                    }
                }
            }
            messageBoardService.exportAQOldMessageBoard(vo, forwardVOList, replyVOList);
        }

        Long s2 = System.currentTimeMillis();


        return "导入成功（" + list.size() + " 条，耗时：" + (s2 - s) / 1000 + "秒）！<a href=\"/changfeng/\">返回</a>";

    }

    @RequestMapping("updateMessageBoard")
    @ResponseBody
    public Object updateMessageBoard() {

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("isPublic", 0);
        List<MessageBoardEO> list = messageBoardService.getEntities(MessageBoardEO.class, map);
        for (int i = 0; i < list.size(); i++) {
            MessageBoardEO messageBoardEO = list.get(i);
            Long createUserId = messageBoardEO.getCreateUserId();
            if (messageBoardEO != null && createUserId != null) {
                Map<String, Object> map1 = new HashMap<String, Object>();
                map1.put("personId", createUserId);
                OrganPersonEO organPersonEO = organPersonService.getEntity(OrganPersonEO.class, map1);
                if (organPersonEO != null) {
                    messageBoardEO.setCreateUserId(organPersonEO.getOrganId());
                    messageBoardService.updateEntity(messageBoardEO);
                }
            }
        }
        return "修改成功" + list.size() + " 条";
    }

    @RequestMapping("updateMessageBoardForward")
    @ResponseBody
    public Object updateMessageBoardForward() {

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("isPublic", 0);
        List<MessageBoardForwardVO> list = new ArrayList<MessageBoardForwardVO>();
        for (int i = 0; i < list.size(); i++) {
            Map<String, Object> forwardMap = new HashMap<String, Object>();
            forwardMap.put("messageBoardId", list.get(i).getId());
            forwardMap.put("operationStatus", MessageBoardForwardEO.OperationStatus.Normal.toString());
            List<MessageBoardForwardEO> forwardEOs = forwardService.getEntities(MessageBoardForwardEO.class, forwardMap);
            for (MessageBoardForwardEO forwardEO : forwardEOs) {
                if (forwardEO != null && forwardEO.getReceiveOrganId() != null) {
                    Map<String, Object> map1 = new HashMap<String, Object>();
                    map1.put("personId", forwardEO.getReceiveOrganId());
                    map1.put("organDn", "UNIT");
                    OrganPersonEO organPersonEO = organPersonService.getEntity(OrganPersonEO.class, map1);
                    if (organPersonEO != null) {
                        forwardEO.setReceiveOrganId(organPersonEO.getOrganId());
                        forwardService.updateEntity(forwardEO);
                    }
                }
            }
        }
        return "修改成功" + list.size() + " 条";
    }


    @RequestMapping("updateMessageBoardReply")
    @ResponseBody
    public Object updateMessageBoardReply() {

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("isPublic", 0);
        List<MessageBoardEO> list = messageBoardService.getEntities(MessageBoardEO.class, map);
        for (int i = 0; i < list.size(); i++) {
            Map<String, Object> replyMap = new HashMap<String, Object>();
            replyMap.put("messageBoardId", list.get(i).getId());
            List<MessageBoardReplyEO> replyEOs = replyService.getEntities(MessageBoardReplyEO.class, replyMap);
            for (MessageBoardReplyEO replyEO : replyEOs) {
                if (replyEO.getCreateOrganId() != null) {
                    Map<String, Object> map1 = new HashMap<String, Object>();
                    map1.put("personId", replyEO.getCreateOrganId());
                    map1.put("organDn", "UNIT");
                    OrganPersonEO organPersonEO = organPersonService.getEntity(OrganPersonEO.class, map1);
                    if (organPersonEO != null) {
                        replyEO.setCreateOrganId(organPersonEO.getOrganId());
                        replyService.updateEntity(replyEO);
                    }
                }
            }
        }
        return "修改成功" + list.size() + " 条";
    }

   /* @RequestMapping("updateMessageBoardForwardForSZXX")
    @ResponseBody
    public Object updateMessageBoardForwardForSZXX() {

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("columnId", 3905033L);
        map.put("typeCode", "messageBoard");
        List<BaseContentEO> baseContentEOs = baseContentService.getEntities(BaseContentEO.class, map);
        for (int i = 0; i < baseContentEOs.size(); i++) {
            Map<String, Object> messageBoardMap = new HashMap<String, Object>();
            messageBoardMap.put("baseContentId", baseContentEOs.get(i).getId());
            MessageBoardEO messageBoardEO = messageBoardService.getEntity(MessageBoardEO.class, messageBoardMap);
            if (messageBoardEO != null) {
                Map<String, Object> replyMap = new HashMap<String, Object>();
                replyMap.put("messageBoardId", messageBoardEO.getId());
                MessageBoardReplyEO replyEO = replyService.getEntity(MessageBoardReplyEO.class, replyMap);

                Map<String, Object> forwardMap = new HashMap<String, Object>();
                forwardMap.put("messageBoardId", messageBoardEO.getId());
                forwardMap.put("operationStatus", MessageBoardForwardEO.OperationStatus.Normal.toString());
                MessageBoardForwardEO forwardEO = forwardService.getEntity(MessageBoardForwardEO.class, forwardMap);

                if (replyEO != null && replyEO.getCreateOrganId() != null) {
                    Map<String, Object> map1 = new HashMap<String, Object>();
                    map1.put("personId", replyEO.getCreateOrganId());
                    map1.put("organDn", "UNIT");
                    OrganPersonEO organPersonEO = organPersonService.getEntity(OrganPersonEO.class, map1);
                    if (organPersonEO != null) {
                        forwardEO.setReceiveOrganId(organPersonEO.getOrganId());
                        forwardService.updateEntity(forwardEO);
                    }
                }
            }
        }
        return "修改成功" + baseContentEOs.size() + " 条";
    }*/


    @RequestMapping("updateMessageBoardForwardForSZXX")
    @ResponseBody
    public Object updateMessageBoardForwardForSZXX() {

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("receiveName", null);
        List<MessageBoardReplyEO> list = replyService.getEntities(MessageBoardReplyEO.class, map);
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                MessageBoardReplyEO replyEO = list.get(i);
                if (replyEO.getCreateOrganId() != null) {
                    Map<String, Object> map1 = new HashMap<String, Object>();
                    map1.put("organId", replyEO.getCreateOrganId());
                    OrganEO organEO = organService.getEntity(OrganEO.class, map1);
                    if (organEO != null) {
                        replyEO.setReceiveName(organEO.getName());
                        replyService.updateEntity(replyEO);
                    }
                }
            }
        }
        return "修改成功" + list.size() + " 条";
    }


    @RequestMapping("exportMemberMessageBoard")
    //http://localhost:8081/anqing/messageBoard/convert?curColumnId=3724779&startTime=1356969601&endTime=1420041601
    @ResponseBody
    public Object exportMemberMessageBoard(Long curColumnId) throws Exception {

        //导完后关闭，以防不测
        String sql = "SELECT * FROM reply where isdel=0 and unitid=0";

        List<Object> replyList = jdbcUtils.excuteQuery(sql, null);

        if (replyList == null || replyList.size() <= 0) {
            return "没有导入的回复数据！";
        }

        String sql_22 = "SELECT * FROM letter where isdel=0";
        List<Object> list = jdbcUtils.excuteQuery(sql_22, null);

        if (list == null || list.size() <= 0) {
            return "没有导入的留言数据！";
        }

        Map<String, Object> latterMap = new HashMap<String, Object>();

        for (int i = 0, l = list.size(); i < l; i++) {
            Map<String, Object> map = (HashMap<String, Object>) list.get(i);
            //把所有的留言数据用latterId作为键,map作为对象存到latterMap中
            String latterId = String.valueOf((Integer) map.get("latterid"));
            latterMap.put(latterId, map);
        }

        //查询所有中间表数据
        Map<String,Object> params = new HashMap<String, Object>();
        params.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
        List<OrganPersonEO> personEOList = organPersonService.getEntities(OrganPersonEO.class,params);
        if(null == personEOList || personEOList.size() <=0 ){
            return getObject("人中间表数据不存在");
        }
        Map<Long,OrganPersonEO> personEOMap = (Map<Long,OrganPersonEO>)AppUtil.parseListToMap(personEOList,"personId");

        //查询所有留言信息
        params.clear();
        params.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
        List<MessageBoardEO> messageBoards = messageBoardService.getEntities(MessageBoardEO.class,params);
        if(null == messageBoards || messageBoards.size() <=0 ){
            return getObject("留言数据不存在");
        }
        Map<Long,List<MessageBoardEO>> messageBoardGroupMap = new HashMap<Long, List<MessageBoardEO>>();
        List<MessageBoardEO> tempList = null;
        Long addTime = null;
        for(MessageBoardEO eo : messageBoards){
            if(null != eo.getCreateDate()){
                addTime = eo.getCreateDate().getTime();
            }
            if(messageBoardGroupMap.containsKey(addTime)){
                messageBoardGroupMap.get(addTime).add(eo);
            }else{
                tempList = new LinkedList<MessageBoardEO>();
                tempList.add(eo);
                messageBoardGroupMap.put(addTime,tempList);
            }
        }
        System.out.println("留言数据分组构建完成");

        //查询所有回复数据
        params.clear();
        params.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
        List<MessageBoardReplyEO> replys = replyService.getEntities(MessageBoardReplyEO.class,params);
        Map<Long,MessageBoardReplyEO> replyEOMap = new HashMap<Long, MessageBoardReplyEO>();
        if(null !=replys && replys.size() > 0){
            for(MessageBoardReplyEO eo : replys){
                if(null != eo.getCreateDate()){
                    replyEOMap.put(eo.getCreateDate().getTime(),eo);
                }
            }
        }


        System.out.println("开始处理老回复数据，数据总数："+replyList.size());

        List<MessageBoardReplyEO> replyEOList = new ArrayList<MessageBoardReplyEO>();

        for (int i = 0, l = replyList.size(); i < l; i++) {
            //把所有的回复数据转化为map
            Map<String, Object> replyMap = (HashMap<String, Object>) replyList.get(i);

            Integer m_Date = (Integer)replyMap.get("addtime");
            Long longDate = new Long(m_Date) * 1000;
            Date replyDate = new Date(longDate);
            if(replyEOMap.containsKey(longDate)){
                System.out.println("当前回复已存在,无需处理");
                continue;
            }
            Integer latterid = (Integer) replyMap.get("latterid");
            String messageBoardId = String.valueOf(latterid);

            if(messageBoardId !=null&& latterMap.get(messageBoardId)!=null){

                Map<String, Object> map_0 = (Map<String, Object>) latterMap.get(messageBoardId);

                Integer userid = (Integer) map_0.get("userid");
                Integer addtime = (Integer)map_0.get("addtime");
                Long lattertime = new Long(addtime) * 1000;
                String title = (String) map_0.get("title");

                MessageBoardEO messageBoardEO = null;
                List<MessageBoardEO> temList = null;
                if(messageBoardGroupMap.containsKey(lattertime)){
                    temList = messageBoardGroupMap.get(lattertime);
                    if(temList.size() == 1){
                        messageBoardEO = temList.get(0);
                    }else{
                        for(MessageBoardEO eo : temList){
                            BaseContentEO baseContentEO =  baseContentService.getEntity(BaseContentEO.class,eo.getBaseContentId());
                            if(null != baseContentEO){
                                if(baseContentEO.getTitle().equals(title)){
                                    messageBoardEO = eo;
                                    break;
                                }
                            }
                        }
                    }
                }
                if(null == messageBoardEO){
                    System.out.println("未找到该条回复所对应的留言");
                    continue;
                }
                if(messageBoardEO!=null){

                    Long createUserID = Long.valueOf(userid);

                    OrganPersonEO organPersonEO = personEOMap.get(createUserID);

                    Long id = messageBoardEO.getId();

                    String replyContent = (String) replyMap.get("content");
                    String replyUserIp = (String) replyMap.get("userip");
                    String username = (String) replyMap.get("username");
                    MessageBoardReplyEO replyEO1 = new MessageBoardReplyEO();
                    replyEO1.setMessageBoardId(id);
                    replyEO1.setDealStatus("handled");
                    replyEO1.setUsername(username);
                    replyEO1.setCreateDate(replyDate);
                    replyEO1.setIp(replyUserIp);
                    replyEO1.setReplyContent(replyContent);
                    replyEO1.setCreateUserId(null == organPersonEO ? createUserID : organPersonEO.getOrganId());
                    replyEO1.setAttachId("test");
                    replyEOList.add(replyEO1);
                }
            }
        }
        replyService.saveEntities(replyEOList);
        Long s2 = System.currentTimeMillis();

        return "导入成功（" + replyEOList.size() + " 条，耗时：" + (s2 - s) / 1000 + "秒）!";
    }


    @RequestMapping("updateBaseContent")
    @ResponseBody
    public Object updateBaseContent() {

        //导完后关闭，以防不测
        //String sql = "select * from letter  where unitid!=172 and isdel =0  and ischeck=2 and title in(select title from letter where unitid!=172  and isdel =0 GROUP BY title HAVING count(title)>1) GROUP BY addtime,views order by title";
        String sql = "select * from letter t where t.ischeck=2 and t.isdel=0";
        List<Object> list = jdbcUtils.excuteQuery(sql, null);
        if (list == null || list.size() <= 0) {
            return "没有导入的数据！<a href=\"/yingzhou/\">返回</a>";
        }else{
            System.out.println("查询出需要修改的记录数"+list.size());
        }
        List<BaseContentEO> baseContentEOList = new ArrayList<BaseContentEO>();
        for (int i = 0,j=0,l=list.size(); i < l; i++) {
            Map<String, Object> map = (Map<String, Object>)list.get(i);
            Integer m_Date = (Integer) map.get("addtime");
            String title = (String) map.get("title");
            Date addDate = new Date(new Long(m_Date) * 1000);
            Map<String ,Object> baseContentMap = new HashMap<String ,Object>();
            baseContentMap.put("typeCode",BaseContentEO.TypeCode.messageBoard.toString());
            baseContentMap.put("createDate",addDate);
            baseContentMap.put("title",title);
            baseContentMap.put("recordStatus", MessageBoardEO.RecordStatus.Normal.toString());
            List<BaseContentEO> list2 = baseContentService.getEntities(BaseContentEO.class,baseContentMap);
            BaseContentEO baseContentEO= null;
            if (list2 != null && list2.size() == 1) {
                baseContentEO = list2.get(0);
                baseContentEO.setIsPublish(0);
                baseContentEO.setEditor("xiugaipublish");
                baseContentService.updateEntity(baseContentEO);
                System.out.println("当前修改第"+(j++)+"条");
            }else{
                for(BaseContentEO eo : list2){
                    eo.setEditor("ischeck2");
                }
                baseContentService.updateEntities(list2);
                System.out.println("当前修改第"+(j++)+"条");
            }
        }
        return "修改成功" + list.size() + " 条";
    }


}
