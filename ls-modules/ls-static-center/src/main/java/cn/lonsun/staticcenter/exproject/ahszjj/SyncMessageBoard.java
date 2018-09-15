package cn.lonsun.staticcenter.exproject.ahszjj;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.messageBoard.controller.vo.MessageBoardEditVO;
import cn.lonsun.content.messageBoard.controller.vo.MessageBoardForwardVO;
import cn.lonsun.content.messageBoard.controller.vo.MessageBoardReplyVO;
import cn.lonsun.content.messageBoard.service.IMessageBoardService;
import cn.lonsun.core.base.util.StringUtils;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.util.SpringContextHolder;
import cn.lonsun.core.util.TipsMode;
import cn.lonsun.staticcenter.exproject.ahszjj.AbSyncInfo;
import cn.lonsun.staticcenter.util.JdbcUtils;
import cn.lonsun.system.datadictionary.vo.DataDictVO;
import cn.lonsun.util.LoginPersonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static cn.lonsun.util.DataDictionaryUtil.getItem;

/**
 * Created by 1960274114 on 2016-10-10.
 * 政民互动.领导邮箱
 */
public class SyncMessageBoard extends AbSyncInfo {
    private Logger logger = LoggerFactory.getLogger(getClass());

    private static Pattern pattern = Pattern.compile("\\&\\#(\\d+)");

    //留言类别
    private final  String classCode = "do_consult";//do_consult/do_complain/do_suggest/do_report/others
    private final String className = "我要咨询";//我要咨询/我要投诉/我要建议/我要举报/其他
    //回复
    private final Long replyUnitId = 66760L;
    private final String replyUnitName= "省质监局";
    private final String dealStatus = "handled";//handled:已办理/unhandle:未办理;//handling:办理中//回复状态
    //收信领导
    private final String receiveUserCode = "mayor";//mayor:市长/magistrate:县长/deputy_magistrate :副县长/deputy_mayor:副市长/secretary:书记

    public SyncMessageBoard(JdbcUtils jdbcUtils, Long siteId, Long createUserId, Long curColumdId) {
        super(jdbcUtils,siteId,createUserId,curColumdId);
    }

    /**
     * 获取无效数据记录总数
     * @return
     */
    public long getValidateCount(){
        long count = 0;
        String sql = "select count(*) from SYS_Jzxx where title is null";
        Object object = jdbcUtils.executeQuerySingle(sql,null);
        if(null !=object){
            count = AppUtil.getlong(object);
        }
        return count;

    }

    /**
     * 局长信箱
     * @param idstr
     * @return
     */
    private List<Object> getSysJzxxList(String idstr){
        String orderby = " order by idate asc";
        String sql = "select title,content,Isread,idate,isshow,userip,username,tel,mail,address,post,replay,replaydate,code,pwd,isshow from SYS_Jzxx where title is not null";
        if(!StringUtils.isEmpty(idstr)){
            sql = sql.concat(" and id in(").concat(idstr).concat(")");
        }
        sql = sql.concat(orderby);
        return jdbcUtils.excuteQuery(sql, null);
    }

    /**
     * 	12365质量热线
     * @param idstr
     * @return
     */
    private List<Object> getSysZxbsinfoList(String idstr){
        String orderby = " order by idate asc";
        String sql = "select title,content,Isread,idate,isshow,userip,username,tel,email,address,post,replay,replaydate,code,pwd,isshow,isbl,slidate,classid from SYS_Zxbsinfo where title is not null";
        if(!StringUtils.isEmpty(idstr)){
            sql = sql.concat(" and id in(").concat(idstr).concat(")");
        }
        sql = sql.concat(orderby);
        return jdbcUtils.excuteQuery(sql, null);
    }
    public List<Object> getloadDetailList(){
        String sql = "select id,title,replaydate from SYS_Jzxx order by idate asc";
        return jdbcUtils.excuteQuery(sql, null);
    }

    /**
     * 表格导入
     * @param tableName
     */
    public void impByTable(String tableName){
        List<Map> maps = new ArrayList<Map>();

        if(tableName.equalsIgnoreCase("sys_jzxx")){//领导邮箱
            List<Object> list = getSysJzxxList(null);
            for (int i = 0, l = list.size(); i < l; i++) {
                Map<String, Object> map = (HashMap<String, Object>) list.get(i);
                maps.add(map);
            }
        }else if(tableName.equalsIgnoreCase("sys_zxbsinfo")){//12365质量热线
            List<Object> list = getSysZxbsinfoList(null);
            for (int i = 0, l = list.size(); i < l; i++) {
                Map<String, Object> map = (HashMap<String, Object>) list.get(i);
                maps.add(map);
            }
        }
        save(maps);
    }

    private void save(List<Map> maps) {
        IMessageBoardService messageBoardService = SpringContextHolder.getBean(IMessageBoardService.class);

        for(Map map:maps){
            if(step>limitSize){
                break;
            }
            step ++;

            //留言日期
            Date addDate = getDateValue(map.get("idate"));
            String title = (String) map.get("title");
            logger.info("导入标题:" + title);
            if (title!=null&&title.length()-1>=0&&title.substring(0, 1).equals("&")){
                title = unicodeToString(title);
            }

            MessageBoardEditVO vo = new MessageBoardEditVO();
            MessageBoardForwardVO forwardVO = new MessageBoardForwardVO();
            MessageBoardReplyVO replyVO = new MessageBoardReplyVO();
            vo.setSiteId(siteId);
            vo.setTitle(title);
            vo.setTypeCode(BaseContentEO.TypeCode.messageBoard.name());
            //留言类别
            //主题内容
            String messageBoardContent = (String) map.get("content");
            Long hit = 0L;
            //公开留言
            Integer isPublic =AppUtil.getint(map.get("isshow"));
            //公开个人信息
            Integer isPublicInfo = 0;
            //是否发布
            Integer isPublish = 0;
           /* //是否已读
            Integer isRead = AppUtil.getInteger(map.get("Isread"));*/
           /* Integer isTurn = 0;*/
            //您的姓名
            String personName = (String)map.get("username");
            if (personName!=null&&personName.length()-1>=0&&personName.substring(0, 1).equals("&")){
                personName = unicodeToString(personName);
            }
            String personPhone = (String) map.get("tel");
            //回复类型
            Integer recType = null !=map.get("replaydate")?1:0;
            //IP地址
            String userip = AppUtil.getValue(map.get("userip"));
            //回复内容
            String replay = (String)map.get("replay");
            Date replaydate = getDateValue(map.get("replaydate"));
            //查询编号
            String docNum = (String)map.get("code");
            String randomCode = (String)map.get("pwd");

            //收信领导
            Integer resourceType = 0;

            vo.setAddDate(addDate);
            vo.setClassCode(classCode);
            if(null !=map.get("classid")){
                if("1".equalsIgnoreCase(map.get("classid").toString())){//业务咨询
                    vo.setClassCode("do_consult");
                }else  if("2".equalsIgnoreCase(map.get("classid").toString())) {//质量申诉
                    vo.setClassCode("do_complain");
                } else  if("3".equalsIgnoreCase(map.get("classid").toString())) {//打假举报
                    vo.setClassCode("do_report");
                }
            }
            vo.setColumnId(curColumdId);
            vo.setMessageBoardContent(messageBoardContent);
            vo.setHit(hit);
            vo.setIsPublic(isPublic);
            vo.setIsPublicInfo(isPublicInfo);
            vo.setIsPublish(isPublish);
         /*   vo.setIsRead(isRead);*/
         /*   vo.setIsTurn(isTurn);*/
            vo.setPersonName(personName);
/*
            vo.setPersonPhone(personPhone);
*/
            vo.setRecType(recType);
            vo.setReceiveUserCode(receiveUserCode);
            vo.setResourceType(resourceType);
            vo.setDocNum(docNum);
            vo.setRandomCode(randomCode);
            vo.setPersonIp(userip);
            vo.setCreateUnitId(LoginPersonUtil.getUnitId());


           /* BaseContentEO contentEO = new BaseContentEO();
            AppUtil.copyProperties(contentEO, vo);
            //导入标识
            contentEO.setEditor(imp_tag);*/
            // CacheHandler.saveOrUpdate(BaseContentEO.class, contentEO);
            //回复内容
            replyVO.setCreateDate(replaydate);
            replyVO.setReplyContent(replay);
            if(null !=replaydate){
                replyVO.setCreateOrganId(replyUnitId);
                replyVO.setReceiveName(replyUnitName);
                replyVO.setDealStatus(dealStatus);
                vo.setDealStatus(dealStatus);
                replyVO.setIsSuper(1);
                forwardVO.setReceiveUnitName(replyUnitName);
                forwardVO.setReceiveOrganId(replyUnitId);
            }
            DataDictVO dictVO = getItem("petition_purpose", vo.getClassCode());
            if (dictVO == null) {
                throw new BaseRunTimeException(TipsMode.Message.toString(), "留言类型未配置");
            } else {
                vo.setClassName(dictVO.getKey());
            }
            messageBoardService.exportOldMessageBoard(vo,replyVO);
        }
    }

    public void imp(String idstr){
        IMessageBoardService messageBoardService = SpringContextHolder.getBean(IMessageBoardService.class);
        List<Object> list = getSysJzxxList(idstr);

        for (int i = 0, l = list.size(); i < l; i++) {
            Map<String, Object> map = (HashMap<String, Object>) list.get(i);
            //留言日期
            Date addDate = getDateValue(map.get("idate"));
            String title = (String) map.get("title");
            if (title!=null&&title.length()-1>=0&&title.substring(0, 1).equals("&")){
                title = unicodeToString(title);
            }
            MessageBoardEditVO vo = new MessageBoardEditVO();
            MessageBoardForwardVO forwardVO = new MessageBoardForwardVO();
            MessageBoardReplyVO replyVO = new MessageBoardReplyVO();
            vo.setSiteId(siteId);
            vo.setTitle(title);
            vo.setTypeCode(BaseContentEO.TypeCode.messageBoard.name());


            //留言类别
            //主题内容
            String messageBoardContent = (String) map.get("content");
            Long hit = 0L;
            //公开留言
            Integer isPublic =AppUtil.getint(map.get("isshow"));
            //公开个人信息
            Integer isPublicInfo = 0;
            //是否发布
            Integer isPublish = 0;
            //是否已读
            Integer isRead = AppUtil.getInteger(map.get("Isread"));
            Integer isTurn = 0;
            //您的姓名
            String personName = (String)map.get("username");
            if (personName!=null&&personName.length()-1>=0&&personName.substring(0, 1).equals("&")){
                personName = unicodeToString(personName);
            }
            String personPhone =(String)map.get("tel");
            //回复类型
            Integer recType = null !=map.get("replaydate")?1:0;
            //IP地址
            String userip = AppUtil.getValue(map.get("userip"));
            //回复内容
            String replay = (String)map.get("replay");
            Date replaydate = getDateValue(map.get("replaydate"));
            //查询编号
            String docNum = (String)map.get("code");
            String randomCode = (String)map.get("pwd");

            //收信领导
            Integer resourceType = 0;

            vo.setAddDate(addDate);
            vo.setClassCode(classCode);
            vo.setColumnId(curColumdId);
            vo.setMessageBoardContent(messageBoardContent);
            vo.setHit(hit);
            vo.setIsPublic(isPublic);
            vo.setIsPublicInfo(isPublicInfo);
            vo.setIsPublish(isPublish);
           /* vo.setIsRead(isRead);
            vo.setIsTurn(isTurn);*/
            vo.setPersonName(personName);
/*
            vo.setPersonPhone(personPhone);
*/
            vo.setRecType(recType);
            vo.setReceiveUserCode(receiveUserCode);
            vo.setResourceType(resourceType);
            vo.setDocNum(docNum);
            vo.setRandomCode(randomCode);

           // CacheHandler.saveOrUpdate(BaseContentEO.class, contentEO);
            vo.setPersonIp(userip);
            vo.setCreateUnitId(LoginPersonUtil.getUnitId());
            //回复内容
            replyVO.setCreateDate(replaydate);
            replyVO.setReplyContent(replay);
            if(null !=replaydate){
                replyVO.setCreateOrganId(replyUnitId);
                replyVO.setReceiveName(replyUnitName);
                replyVO.setDealStatus(dealStatus);
                vo.setDealStatus(dealStatus);
                replyVO.setIsSuper(1);
                forwardVO.setReceiveUnitName(replyUnitName);
                forwardVO.setReceiveOrganId(replyUnitId);
            }
            DataDictVO dictVO = getItem("petition_purpose", vo.getClassCode());
            if (dictVO == null) {
                throw new BaseRunTimeException(TipsMode.Message.toString(), "留言类型未配置");
            } else {
                vo.setClassName(dictVO.getKey());
            }
            messageBoardService.exportOldMessageBoard(vo,replyVO);
            //guestBookService.saveGuestBook(guestBookEO, vo.getSiteId(), vo.getColumnId());
        }
    }

    //编码unicode转中文
    public static String unicodeToString(String str){
        StringBuilder sb = new StringBuilder();
        Matcher m =pattern.matcher(str);

        while (m.find()){
            sb.append((char)Integer.valueOf(m.group(1)).intValue());
        }
        return sb.toString();
    }
}
