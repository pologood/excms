package cn.lonsun.source.dataexport.impl.ex7.content;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.content.messageBoard.controller.internal.entity.MessageBoardForwardEO;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.base.util.StringUtils;
import cn.lonsun.jdbc.JdbcAble;
import cn.lonsun.rbac.internal.entity.OrganEO;
import cn.lonsun.rbac.internal.entity.UserEO;
import cn.lonsun.rbac.internal.service.IOrganService;
import cn.lonsun.rbac.internal.service.IUserService;
import cn.lonsun.source.dataexport.content.IMessageBoardExportService;
import cn.lonsun.source.dataexport.vo.ContentQueryVO;
import cn.lonsun.target.datamodel.content.MessageBoardForwardVO;
import cn.lonsun.target.datamodel.content.MessageBoardReplyVO;
import cn.lonsun.target.datamodel.content.MessageBoardVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;

/**
 * @author caohaitao
 * @Title: Ex7MessageBoardExportService
 * @Package cn.lonsun.source.dataexport.impl.ex7
 * @Description: EX7产品信箱留言数据查询导出service
 * @date 2018/2/9 9:33
 */
@Service("ex7MessageBoardExportService")
public class Ex7MessageBoardExportService extends JdbcAble<MessageBoardVO> implements IMessageBoardExportService {

    private static final String QUERY_OLD_UNIT_SQL = "select UI_Name,UI_ID from UnitsInfo";
    private static final String QUERY_OLD_USER_SQL = "select UA_Name,UA_ID from UserAccount";
    private static final String QUERY_OLD_RECORD_SQL = "select UA_ID,UA_Name,m_ID,EM_RemoteIP,EM_Description,EM_Date from EventsMove where EM_type=100 order by m_ID asc, EM_Date asc";
    private static final String QUERY_MESSAGE_SQL = "select c.m_ID,c.m_Name,c.m_Tel,c.m_Type,c.m_Numbers,c.m_Subject,c.m_Contents,c.m_Date,c.m_RemoteIp,c.m_OpenIs,c.m_ShowIs,c.m_RevertIS,c.m_Revert,c.m_RevertUserid,c.m_RevertDate,c.m_RevertBranch,c.m_QueryPasswd,c.m_UnitID,c.m_LeaderID,c.m_changed from MessageList c where  c.m_isDel=0 and c.SS_ID = ?";

    private static final Map<Long, String> oldUnitMap = new HashMap<Long, String>();
    private static final Map<Long, String> oldUserMap = new HashMap<Long, String>();
    private static final Map<Long, Map<String, Object>> recordMap = new HashMap<Long, Map<String, Object>>();//信件ID---转办记录
    private static final Map<String, OrganEO> newUnitMap = new HashMap<String, OrganEO>();
    private static final Map<String, UserEO> newUserMap = new HashMap<String, UserEO>();

    //新老平台单位名称对应关系(这里已金寨的EX7为例)
    private static final Map<String, String> old_new_unitMap = new HashMap<String, String>();

    @Autowired
    private IUserService userService;

    @Autowired
    private IOrganService organService;

    @SuppressWarnings("unchecked")
    @PostConstruct
    private void initMap() {
       /* //获取老平台的单位信息
        List<Object> oldUnitList = queryBeanList(QUERY_OLD_UNIT_SQL, null);
        for (Object oldUnit : oldUnitList) {
            Map<String, Object> map = (HashMap<String, Object>) oldUnit;
            oldUnitMap.put((Long) map.get("UI_ID"), (String) map.get("UI_Name"));
        }
        //获取老平台的用户信息
        List<Object> oldUserList = queryBeanList(QUERY_OLD_USER_SQL, null);
        for (Object oldUser : oldUnitList) {
            Map<String, Object> map = (HashMap<String, Object>) oldUser;
            oldUserMap.put((Long) map.get("UI_ID"), (String) map.get("UI_Name"));
        }
        //获取转办记录信息，获取最新的转办信息
        List<Object> recordList = queryBeanList(QUERY_OLD_RECORD_SQL, null);
        for (Object record : recordList) {
            Map<String, Object> map = (HashMap<String, Object>) record;//转办记录
            recordMap.put((Long) map.get("m_ID"), map);
        }
        //获取新平台的单位信息
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
        List<OrganEO> unitList = organService.getEntities(OrganEO.class, param);
        for (OrganEO organEO : unitList) {
            newUnitMap.put(organEO.getName(), organEO);
        }
        //获取新平台的人员信息
        List<UserEO> userList = userService.getEntities(UserEO.class, param);
        for (UserEO userEO : userList) {
            newUserMap.put(userEO.getPersonName(), userEO);
        }*/
        old_new_unitMap.put("县政府办", "政府办");
        old_new_unitMap.put("县委办", "县委办");
        old_new_unitMap.put("县人大办", "人大办");
        old_new_unitMap.put("县政协办", "政协办");
        old_new_unitMap.put("县委组织部", "组织部");
        old_new_unitMap.put("县委宣传部", "宣传部");
        old_new_unitMap.put("县委统战部", "统战部");
        old_new_unitMap.put("县编办", "编办");
        old_new_unitMap.put("县直工委", "县直工委");
        old_new_unitMap.put("县总工会", "县总工会");
        old_new_unitMap.put("团县委", "团县委");
        old_new_unitMap.put("县妇联", "妇联");
        old_new_unitMap.put("县委党校", "党校");
        old_new_unitMap.put("县科协", "县科协");
        old_new_unitMap.put("县供销社", "县供销社");
        old_new_unitMap.put("机关事务管理局", "机关事务管理局");
        old_new_unitMap.put("县法院", "法院");
        old_new_unitMap.put("县检察院", "检察院");
        old_new_unitMap.put("梅山水库管理处", "梅山水库管理处");
        old_new_unitMap.put("医疗保险管理中心", "医保中心");
        old_new_unitMap.put("县商贸流通协会", "商贸流通协会");
        old_new_unitMap.put("矿管办", "矿管办");
        old_new_unitMap.put("县发展和改革委员会（物价局）", "发改委（物价局）");
        old_new_unitMap.put("县经济和信息化委员会", "经信委");
        old_new_unitMap.put("县教育局", "教育局");
        old_new_unitMap.put("县科技局", "科技局");
        old_new_unitMap.put("县公安局", "公安局");
        old_new_unitMap.put("县纪委（监察局）", "纪委（监察局）");
        old_new_unitMap.put("县民政局", "民政局");
        old_new_unitMap.put("县司法局", "司法局");
        old_new_unitMap.put("县人力资源和社会保障局", "人社局");
        old_new_unitMap.put("县国土资源局", "国土资源局");
        old_new_unitMap.put("县财政局", "财政局");
        old_new_unitMap.put("县交通运输局", "交通运输局");
        old_new_unitMap.put("县农业发展委员会", "农委");
        old_new_unitMap.put("县水利局", "水利局");
        old_new_unitMap.put("县林业局", "林业局");
        old_new_unitMap.put("县商务和粮食局", "商务和粮食局");
        old_new_unitMap.put("县文化广电新闻出版局（体育局）", "文广新局（体育局）");
        old_new_unitMap.put("县卫计委", "卫计委");
        old_new_unitMap.put("县审计局", "审计局");
        old_new_unitMap.put("县地税局", "地税局");
        old_new_unitMap.put("县环保局", "环保局");
        old_new_unitMap.put("县广播电视台", "广播电视台");
        old_new_unitMap.put("县统计局", "统计局");
        old_new_unitMap.put("县市场监督管理局", "市场监管局");
        old_new_unitMap.put("县安全生产监督管理局", "安监局");
        old_new_unitMap.put("县旅游委", "旅游委");
        old_new_unitMap.put("县住房公积金管理部", "住房公积金管理部");
        old_new_unitMap.put("县党史县志档案局", "党史县志档案局");
        old_new_unitMap.put("县地方志编纂办公室", "地方志");
        old_new_unitMap.put("县信访局", "信访局");
        old_new_unitMap.put("县国税局", "国税局");
        old_new_unitMap.put("县气象局", "气象局");
        old_new_unitMap.put("县招商局", "招商局");
        old_new_unitMap.put("县政府政务服务中心", "行政审批局（政务服务中心）");
        old_new_unitMap.put("县扶贫和移民局", "扶贫和移民局");
        old_new_unitMap.put("县公路局", "公路局");
        old_new_unitMap.put("县残疾人联合会", "残联");
        old_new_unitMap.put("县综合行政执法局", "城管执法局");
        old_new_unitMap.put("公共资源交易监督管理局", "公共资源交易监管局");
        old_new_unitMap.put("县烟草局", "烟草局");
        old_new_unitMap.put("县邮政局", "邮政局");
        old_new_unitMap.put("县安居办", "安居办");
        old_new_unitMap.put("县住房和城乡建设局", "住建局");
        old_new_unitMap.put("县重点工程局", "重点工程局");
        old_new_unitMap.put("县规划局", "规划局");
        old_new_unitMap.put("县金融办", "金融办");
        old_new_unitMap.put("县医院", "县医院");
        old_new_unitMap.put("金寨县中医院", "县中医院");
        old_new_unitMap.put("县妇幼保健院", "妇幼保健院");
        old_new_unitMap.put("县疾病预防控制中心", "疾控中心");
        old_new_unitMap.put("金寨一中", "一中");
        old_new_unitMap.put("金寨二中", "二中");
        old_new_unitMap.put("梅山镇", "梅山镇");
        old_new_unitMap.put("南溪镇", "南溪镇");
        old_new_unitMap.put("古碑镇", "古碑镇");
        old_new_unitMap.put("双河镇", "双河镇");
        old_new_unitMap.put("汤家汇镇", "汤家汇镇");
        old_new_unitMap.put("青山镇", "青山镇");
        old_new_unitMap.put("燕子河镇", "燕子河镇");
        old_new_unitMap.put("吴家店镇", "吴家店镇");
        old_new_unitMap.put("天堂寨镇", "天堂寨镇");
        old_new_unitMap.put("白塔畈镇", "白塔畈镇");
        old_new_unitMap.put("油坊店乡", "油坊店乡");
        old_new_unitMap.put("槐树湾乡", "槐树湾乡");
        old_new_unitMap.put("桃岭乡", "桃岭乡");
        old_new_unitMap.put("长岭乡", "长岭乡");
        old_new_unitMap.put("花石乡", "花石乡");
        old_new_unitMap.put("沙河乡", "沙河乡");
        old_new_unitMap.put("果子园乡", "果子园乡");
        old_new_unitMap.put("张冲乡", "张冲乡");
        old_new_unitMap.put("关庙乡", "关庙乡");
        old_new_unitMap.put("铁冲乡", "铁冲乡");
        old_new_unitMap.put("全军乡", "全军乡");
        old_new_unitMap.put("斑竹园镇", "斑竹园镇");
        old_new_unitMap.put("麻埠镇", "麻埠镇");
        old_new_unitMap.put("现代产业园区", "现代产业园区");
        old_new_unitMap.put("县银监办", "银监办");
        old_new_unitMap.put("农发行金寨支行", "农发行金寨支行");
        old_new_unitMap.put("工行金寨支行", "工行金寨支行");
        old_new_unitMap.put("农行金寨支行", "农行金寨支行");
        old_new_unitMap.put("中行金寨支行", "中行金寨支行");
        old_new_unitMap.put("建行金寨支行", "建行金寨支行");
        old_new_unitMap.put("县人行", "人行金寨支行");
        old_new_unitMap.put("县邮储银行", "邮储银行");
        old_new_unitMap.put("金寨县徽商银行", "徽商银行");
        old_new_unitMap.put("县农村商业银行", "农村商业银行");
        old_new_unitMap.put("中国人寿保险金寨分公司", "中国人寿保险金寨分公司");
        old_new_unitMap.put("中国人民财产保险金寨分公司", "中国人民财产保险金寨分公司");
        old_new_unitMap.put("金叶供水有限公司", "金叶供水有限公司");
        old_new_unitMap.put("县供电公司", "供电公司");
        old_new_unitMap.put("县电信公司", "电信公司");
        old_new_unitMap.put("县移动公司", "移动公司");
        old_new_unitMap.put("县联通公司", "联通公司");
        old_new_unitMap.put("安广网络金寨分公司", "安广网络金寨分公司");
        old_new_unitMap.put("安徽金寨职业学校", "金寨职业学校");
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<MessageBoardVO> getDataList(ContentQueryVO queryVO) {
        List<MessageBoardVO> messageBoardVOList = new ArrayList<MessageBoardVO>();
        List<Object> list = queryBeanList(QUERY_MESSAGE_SQL, null, queryVO.getOldColumnId());
        for (Object object : list) {
            Map<String, Object> map = (HashMap<String, Object>) object;

            /*
             * BaseContent表插入字段
             */
            Long oldId = (Long) map.get("m_ID");//老id
            String title = (String) map.get("m_Subject");//标题-> baseContent title
            Date addDate = (Date) map.get("m_Date");//添加时间 -> baseContent:createDate messegeBoard:addDate
            Integer isPublish = (Integer) map.get("m_ShowIs");//是否发布-> baseContent isPublish


            /*
             * messageBoard、messageBoardForward表插入字段
             */
            String messageBoardContent = (String) map.get("m_Contents");//留言内容
            String personIp = (String) map.get("m_RemoteIp");//留言人ip
            String personName = (String) map.get("m_Name");//留言人姓名
            String personPhone = (String) map.get("m_Tel");//留言人电话
            Integer m_type = (Integer) map.get("m_Type");//留言类型
            String classCode = null;
            String className = null;
            //EX7留言类型转换成EX8留言类型
            if (!AppUtil.isEmpty(m_type)) {
                if (m_type == 1) {
                    classCode = MessageBoardVO.Class.do_consult.toString();
                    className = MessageBoardVO.Class.do_consult.getText();
                } else if (m_type == 2) {
                    classCode = MessageBoardVO.Class.do_complain.toString();
                    className = MessageBoardVO.Class.do_complain.getText();
                } else if (m_type == 3) {
                    classCode = MessageBoardVO.Class.do_suggest.toString();
                    className = MessageBoardVO.Class.do_suggest.getText();
                } else if (m_type == 4) {
                    classCode = MessageBoardVO.Class.do_report.toString();
                    className = MessageBoardVO.Class.do_report.getText();
                } else if (m_type == 5) {
                    classCode = MessageBoardVO.Class.others.toString();
                    className = MessageBoardVO.Class.others.getText();
                }
            }
            Integer isPublic = (Integer) map.get("m_OpenIs");//是否公开留言内容
            String docNum = (String) map.get("m_Numbers");//查询编号
            String randomCode = (String) map.get("m_QueryPasswd");//查询密码
            Long receiveUnitId = (Long) map.get("m_UnitID");//接收单位ID
            String receiveUnitName = null;//接收单位名称
            if (receiveUnitId != null && receiveUnitId != 0) {
                if (oldUnitMap.containsKey(receiveUnitId)) {
                    receiveUnitName = oldUnitMap.get(receiveUnitId);
                    if (!StringUtils.isEmpty(old_new_unitMap.get(receiveUnitName))) {
                        if (newUnitMap.containsKey(old_new_unitMap.get(receiveUnitName))) {
                            receiveUnitId = newUnitMap.get(old_new_unitMap.get(receiveUnitName)).getOrganId();
                        }
                    }
                }
            }
            Integer isReply = (Integer) map.get("m_RevertIS");//是否回复了 ->messageboead:办理状态
            String dealStatus = null;//办理状态
            if (isReply != null && isReply == 1) {
                dealStatus = MessageBoardVO.DealStatus.replyed.toString();
            }
            String commentCode = (String) map.get("m_Appraise");//满意度(评价编码)
            if (!StringUtils.isEmpty(commentCode)) {
                if ((1 + "").equals(commentCode)) {
                    commentCode = MessageBoardVO.CommentCode.satisfactory.toString();
                } else if ((2 + "").equals(commentCode)) {
                    commentCode = MessageBoardVO.CommentCode.relatively_satisfactory.toString();
                } else if ((3 + "").equals(commentCode)) {
                    commentCode = MessageBoardVO.CommentCode.general_satisfactory.toString();
                } else if ((4 + "").equals(commentCode)) {
                    commentCode = MessageBoardVO.CommentCode.unsatisfactory.toString();
                } else if ((5 + "").equals(commentCode)) {
                    commentCode = MessageBoardVO.CommentCode.very_unsatisfactory.toString();
                }
            }
            Date replyDate = (Date) map.get("m_RevertDate");//回复日期
            String replyUnitName = (String) map.get("m_RevertBranch");//回复单位名称
            Long replyUnitId = null;//回复单位id
            if (!StringUtils.isEmpty(replyUnitName)) {
                if (newUnitMap.containsKey(replyUnitName)) {
                    replyUnitId = newUnitMap.get(replyUnitName).getOrganId();
                }
            }

            /*
             * messageBoardReply表插入字段 部分包含于messageBoard
             */
            String replyContent = (String) map.get("m_Revert");//回复内容
            Long replyUserId = (Long) map.get("m_RevertUserid");//回复人ID
            String replyUserName = null;
            if (replyUserId != null) {
                String username = oldUserMap.get(replyUserId);
                if (!StringUtils.isEmpty(username)) {
                    if (newUserMap.containsKey(username)) {
                        replyUserId = newUserMap.get(username).getUserId();
                        replyUserName = newUserMap.get(username).getPersonName();
                    }
                }
            }

            /*
             * 创建MessageBoardVO数据模型并赋值
             */
            MessageBoardVO messageBoardVO = new MessageBoardVO();
            messageBoardVO.setOldId(String.valueOf(oldId));//老的id
            messageBoardVO.setTitle(title);
            messageBoardVO.setCreateDate(addDate);
            messageBoardVO.setIsPublish(isPublish);
            messageBoardVO.setPublishDate(addDate);
            messageBoardVO.setPersonName(personName);
            messageBoardVO.setPersonPhone(personPhone);
            messageBoardVO.setClassCode(classCode);
            messageBoardVO.setClassName(className);
            messageBoardVO.setDocNum(docNum);
            messageBoardVO.setMessageBoardContent(messageBoardContent);
            messageBoardVO.setAddDate(addDate);
            messageBoardVO.setCreateDate(addDate);
            messageBoardVO.setPersonIp(personIp);
            messageBoardVO.setIsPublic(isPublic);
            messageBoardVO.setDealStatus(dealStatus);
            messageBoardVO.setRandomCode(randomCode);
            messageBoardVO.setReceiveUnitId(receiveUnitId);
            messageBoardVO.setReceiveUnitName(receiveUnitName);
            Integer isChange = (Integer) map.get("m_changed");//是否有转办撤回操作
            if (isChange == null || isChange == 0) {//没有进行任何转办撤回操作
                List<MessageBoardForwardVO> forwardVOList = new ArrayList<MessageBoardForwardVO>();
                MessageBoardForwardVO forwardVO = new MessageBoardForwardVO();
                forwardVO.setOperationStatus(MessageBoardForwardEO.OperationStatus.Normal.toString());
                forwardVO.setReceiveOrganId(receiveUnitId);
                forwardVO.setReceiveUnitName(receiveUnitName);
                forwardVO.setRemarks("请快速办理");
                forwardVO.setUsername(personName);
                forwardVO.setCreateDate(addDate);
                forwardVO.setIp(personIp);
                forwardVOList.add(forwardVO);
                messageBoardVO.setForwardList(forwardVOList);
                if (isReply != null && isReply == 1) {
                    List<MessageBoardReplyVO> replyVOList = new ArrayList<MessageBoardReplyVO>();
                    MessageBoardReplyVO replyVO = new MessageBoardReplyVO();
                    replyVO.setDealStatus(dealStatus);
                    replyVO.setReplyContent(replyContent);
                    replyVO.setReceiveName(replyUnitName);
                    replyVO.setCreateUserId(replyUserId);
                    replyVO.setCreateOrganId(replyUnitId);
                    replyVO.setUsername(replyUserName);
                    replyVO.setCreateDate(replyDate);
                    messageBoardVO.setReplyList(replyVOList);
                }
            } else if (isChange == 1) {//有转办或撤回等操作
                Date forwardDate = null;
                String forwardIp = null;
                Long forwardUserId = null;
                String fUserName = null;
                String remark = null;
                if (recordMap.containsKey(oldId)) {
                    Map<String, Object> map_ = recordMap.get(oldId);
                    forwardDate = (Date) map_.get("EM_Date");//转办日期 ->转办记录表createDate
                    forwardIp = (String) map_.get("EM_RemoteIP");//转办人ip
                    forwardUserId = (Long) map_.get("UA_ID");//转办人id
                    if (forwardUserId != null && forwardUserId != 0) {
                        if (oldUserMap.containsKey(forwardUserId)) {
                            fUserName = oldUnitMap.get(forwardUserId);
                            if (!StringUtils.isEmpty(fUserName)) {
                                if (newUserMap.containsKey(fUserName)) {
                                    forwardUserId = newUserMap.get(fUserName).getUserId();
                                }
                            }
                        }
                    }
                    remark = (String) recordMap.get(oldId).get("EM_Description");//备注
                }
                List<MessageBoardForwardVO> forwardVOList = new ArrayList<MessageBoardForwardVO>();
                MessageBoardForwardVO forwardVO = new MessageBoardForwardVO();
                forwardVO.setOperationStatus(MessageBoardForwardEO.OperationStatus.Normal.toString());
                forwardVO.setReceiveOrganId(receiveUnitId);
                forwardVO.setReceiveUnitName(receiveUnitName);
                forwardVO.setRemarks(remark);
                forwardVO.setUsername(fUserName);
                forwardVO.setCreateDate(forwardDate);
                forwardVO.setCreateUserId(forwardUserId);
                forwardVO.setIp(forwardIp);
                forwardVOList.add(forwardVO);
                messageBoardVO.setForwardList(forwardVOList);
                if (isReply != null && isReply == 1) {
                    List<MessageBoardReplyVO> replyVOList = new ArrayList<MessageBoardReplyVO>();
                    MessageBoardReplyVO replyVO = new MessageBoardReplyVO();
                    replyVO.setDealStatus(dealStatus);
                    replyVO.setReplyContent(replyContent);
                    replyVO.setReceiveName(replyUnitName);
                    replyVO.setCreateUserId(replyUserId);
                    replyVO.setCreateOrganId(replyUnitId);
                    replyVO.setUsername(replyUserName);
                    replyVO.setCreateDate(replyDate);
                    messageBoardVO.setReplyList(replyVOList);
                }
            }
            messageBoardVOList.add(messageBoardVO);
        }
        return messageBoardVOList;
    }

    @Override
    public List<MessageBoardVO> getDataByIds(ContentQueryVO queryVO, Object... ids) {
        return null;
    }


}
