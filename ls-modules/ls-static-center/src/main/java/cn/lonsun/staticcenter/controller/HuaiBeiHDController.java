
package cn.lonsun.staticcenter.controller;

import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.common.vo.TreeNodeVO;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.internal.service.IBaseContentService;
import cn.lonsun.content.messageBoard.controller.internal.entity.MessageBoardEO;
import cn.lonsun.content.messageBoard.controller.internal.entity.MessageBoardForwardEO;
import cn.lonsun.content.messageBoard.controller.internal.entity.MessageBoardReplyEO;
import cn.lonsun.content.messageBoard.controller.vo.MessageBoardEditVO;
import cn.lonsun.content.messageBoard.service.IMessageBoardForwardService;
import cn.lonsun.content.messageBoard.service.IMessageBoardReplyService;
import cn.lonsun.content.messageBoard.service.IMessageBoardService;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.util.TipsMode;
import cn.lonsun.indicator.internal.entity.IndicatorEO;
import cn.lonsun.indicator.internal.service.IIndicatorService;
import cn.lonsun.rbac.internal.entity.OrganEO;
import cn.lonsun.rbac.internal.service.IOrganService;
import cn.lonsun.site.site.internal.entity.ColumnMgrEO;
import cn.lonsun.util.RandomCode;
import com.mongodb.*;
import org.apache.commons.lang.*;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 淮北市长信箱数据导入 <br/>
 * @author xiayc <br/>
 * @version v1.0 <br/>
 * @date 2017年11月23日 <br/>
 */
@Controller
@RequestMapping("/huaibei/szxx")
public class HuaiBeiHDController extends BaseController {

    @Autowired
    private IIndicatorService indicatorService;

    @Autowired
    private IBaseContentService baseContentService;

    @Autowired
    private IMessageBoardService messageBoardService;

    @Autowired
    private IMessageBoardForwardService forwardService;

    @Autowired
    private IMessageBoardReplyService replyService;

    @Autowired
    private IOrganService organService;

    private MongoClient mongoClient = null;// 建立连接
    private DB get_db_credit = null;

    private Long siteId=null;
    private Map<String,String> codeMap = new HashMap<String, String>();
    private Map<String,OrganEO> organMap = new HashMap<String, OrganEO>();
    private Map<String,String> leaderMap = new HashMap<String, String>();
    private String mongo_siteId = "54630f529a05c220704b6194";// 老数据站点id

    private DB getDB() {
        if (null == get_db_credit) {
            try {
                mongoClient = new MongoClient("61.191.61.136", 21617);// 本地访问
                get_db_credit = mongoClient.getDB("hbgov1");// 数据库名
                if(get_db_credit.authenticate("hbgov1", "12345678".toCharArray())){
                    System.out.println("success");
                }
            } catch (UnknownHostException e) {
                throw new BaseRunTimeException("数据源获取错误！");
            }
        }
        return get_db_credit;
    }

    /**
     * 导入页面
     * @param map
     * @return
     */
    @RequestMapping("index")
    public String index(ModelMap map,Long sId) throws UnknownHostException {
        siteId=sId;
        map.put("type", "0");
        return "dbconvert/hb_szxx";
    }

    @RequestMapping("getOldTree")
    @ResponseBody
    public Object getOldTree() throws Exception {
        DBCollection collection = getDB().getCollection("site_channel_tree");
        Map<String, Object> paramMap = new HashMap<String, Object>();
//        paramMap.put("removed", Boolean.FALSE);
        paramMap.put("site_id", mongo_siteId);
        DBObject ref = new BasicDBObject(paramMap);
        DBCursor cursor = collection.find(ref);
        List<DBObject> templist = cursor.toArray();
        List<TreeNodeVO> list = new ArrayList<TreeNodeVO>();
        for(DBObject d : templist){
            TreeNodeVO vo = new TreeNodeVO();
            ObjectId objId = (ObjectId)d.get("_id");
            vo.setId(objId.toString());
            vo.setName(d.get("name").toString()+"("+objId+")");

            Object parent = d.get("parent");
            String pid = "";
            if(parent!=null){
                if (parent instanceof BasicDBObject) {
                    BasicDBObject o = (BasicDBObject) parent;
                    Set<Map.Entry<String, Object>> c = o.entrySet();
                    Iterator<Map.Entry<String, Object>> it = c.iterator();
                    int i = 1;
                    while (it.hasNext()){
                        Map.Entry<String, Object> map = it.next();
                        if(i==c.size()){
                            pid = map.getKey();
                        }
                        i+=1;
                    }
                }
            }
            vo.setPid(pid);
            list.add(vo);
        }
        return list;
    }

    @RequestMapping("getEX8Tree")
    @ResponseBody
    public Object getEX8Tree() throws Exception {
        Map<String, Object> map2 = new HashMap<String, Object>();
        map2.put("siteId", siteId);
        map2.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
        List<IndicatorEO> indicat = indicatorService.getEntities(IndicatorEO.class, map2);
        for(IndicatorEO eo:indicat){
            eo.setName(eo.getName()+"("+eo.getIndicatorId()+")");
        }
        getClassCode();//获取信件类型
        getOrgan();//获取平台单位
        getLeader();//获取收信领导
        //获取领导信息
        return indicat;
    }

    /**
     * 获取信件类型
     * @return
     */
    public void getClassCode() {
        DBCollection collection = getDB().getCollection("supervision_question");
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("removed", Boolean.FALSE);
        paramMap.put("site_id", mongo_siteId);
        DBObject ref = new BasicDBObject(paramMap);
        DBCursor cursor = collection.find(ref);
        List<DBObject> list = cursor.toArray();
        for(DBObject d : list) {
            ObjectId id = (ObjectId)d.get("_id");
            String idstr = id.toString();
            String name = (String)d.get("name");
            if(!codeMap.containsKey(idstr)) {
                codeMap.put(idstr,name);
            }
        }
    }

    /**
     * 获取收信领导
     * @return
     */
    public void getLeader() {
        DBCollection collection = getDB().getCollection("site_leader");
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("removed", Boolean.FALSE);
        paramMap.put("status", Boolean.TRUE);
        paramMap.put("site_id", mongo_siteId);
        DBObject ref = new BasicDBObject(paramMap);
        DBCursor cursor = collection.find(ref);
        List<DBObject> list = cursor.toArray();
        for(DBObject d : list) {
            ObjectId id = (ObjectId)d.get("_id");
            String idstr = id.toString();
            String name = (String)d.get("name");
            if(!leaderMap.containsKey(idstr)) {
                leaderMap.put(idstr,name);
            }
        }
    }

    /**
     * 获取新平台单位
     */
    public void getOrgan() {
        Map<String,Object> params = new HashMap<String, Object>();
        params.put("type","Organ");
        params.put("recordStatus","Normal");
        params.put("isPublic",1);
        organMap = new HashMap<String, OrganEO>();
        List<OrganEO> organEOs = organService.getEntities(OrganEO.class, new HashMap<String, Object>());
        if (organEOs != null && organEOs.size() > 0) {
            for (OrganEO eo : organEOs) {
                if(!organMap.containsKey(eo.getPositions())) {
                    organMap.put(eo.getPositions(),eo);
                }
            }
        }
    }


    @RequestMapping("toNews")
    @ResponseBody
    public Object toNews(String oldColumnId, Long curColumdId,String dateStr) throws Exception {
        Long s = System.currentTimeMillis();
       /* if (AppUtil.isEmpty(oldColumnId) || AppUtil.isEmpty(curColumdId)) {
            throw new BaseRunTimeException(TipsMode.Message.toString(),"请选择要导入的目录");
        }*/
        ColumnMgrEO columnMgrEO = CacheHandler.getEntity(ColumnMgrEO.class, curColumdId);
        DBCollection collection = getDB().getCollection("supervision");

        // 查询条件
        BasicDBObject condition = new BasicDBObject();
        condition.put("removed", Boolean.FALSE);
        condition.put("site_id", mongo_siteId);
        if(!AppUtil.isEmpty(dateStr)){
            condition.put("create_date", BasicDBObjectBuilder.start("$gte", new SimpleDateFormat("yyyy-MM-dd").parse(dateStr).getTime()/1000).get());
        }
        //排序
        DBObject sort = new BasicDBObject();
        sort.put("create_date",-1);

        long count = collection.getCount(condition);
        System.out.println("总共"+count+"条数据..............");
        Integer i = 0;
        if(count > 0l) {
            int pageSize = 200;
            long divisor = count / pageSize;
            int startIndex = 0;
            long pageIndex = count % pageSize == 0L ? divisor : divisor + 1L;

            while (startIndex < pageIndex) {//分页导入
                DBCursor cursor = collection.find(condition).skip(startIndex++ * pageSize).limit(pageSize).sort(sort);
                final Iterator<DBObject> iterator = cursor.iterator();
                while (iterator.hasNext()) {
                    DBObject d = iterator.next();
                    i++;
                    this.convertData(d,i,columnMgrEO);
                }
            }
        }
        //Long s2 = System.currentTimeMillis();
        return getObject("导入成功，共计导入"+count+"条数据！");
    }

    /**
     * 转换数据
     * @param d
     * @param i
     * @param columnMgrEO
     */
    public void convertData(DBObject d,Integer i,ColumnMgrEO columnMgrEO) {
        MessageBoardEditVO vo = new MessageBoardEditVO();
        vo.setSiteId(siteId);
        String old_id = d.get("_id").toString();//主键
        String is_publish = d.get("share_on").toString();//是否发布
        String is_public = d.get("submitter_share_on").toString();//是否公开
        String question_id = (String)d.get("question_id");//主键
        String person_phone = (String)d.get("VC_MOBILEPHONE");//留言人号码
        String ip = (String)d.get("client_ip");//留言人ip
        String hit = d.get("hit").toString();//点击量
        String leader_id = (String)d.get("leader_id");//点击量
        String messageContent = (String)d.get("message");//内容
        String docNum = (String)d.get("no");//编码
        String title = (String)d.get("subject");//标题
        String reply_confirmed = d.get("reply_confirmed").toString();//是否回复
        String randmCode = (String)d.get("no_password");//密码
        String person_name = "";
        String person_id = (String)d.get("member_id");
        String receive_unit_id = (String)d.get("branch_id");//回复单位id
        Object creator = d.get("creator");//添加人
        if(!AppUtil.isEmpty(creator)) {
            if(creator instanceof BasicDBObject) {
                try{
                    BasicDBObject o = (BasicDBObject) creator;
                    person_name = (String)o.get("name");//留言人姓名
                    String pid = (String)o.get("id");//留言人id
                    if(AppUtil.isEmpty(person_id)) {
                        person_id = pid;
                    }
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        vo.setCreateDate(getDate(d.get("create_date")));
        vo.setUpdateDate(getDate(d.get("update_date")));
        try {
            vo.setDueDate(getDate(d.get("due_date")));
            vo.setPublishDate(getDate(d.get("confirm_date")));
        } catch (Exception e) {
            e.printStackTrace();
            vo.setDueDate(vo.getUpdateDate());
            vo.setPublishDate(vo.getUpdateDate());
        }
        vo.setForwardDate(vo.getPublishDate());
        vo.setColumnId(columnMgrEO.getIndicatorId());
        vo.setMessageBoardContent(messageContent);
        vo.setTitle(title);
        vo.setAddDate(vo.getCreateDate());
        vo.setDocNum(docNum.toString());
        vo.setRandomCode(randmCode);
        vo.setIsPublic(getNum(is_public));
        vo.setIsPublish(getNum(is_publish));
        vo.setReceiveUserCode(leader_id);
        vo.setHit(Long.parseLong(getNum(hit).toString()));
        vo.setPersonName(person_name);
        vo.setOldPersonId(person_id);
        if(!StringUtils.isEmpty(person_phone)) {
            vo.setPersonPhone(person_phone);
        }else {
            getPerson(vo);
        }
        vo.setPersonIp(ip);
        vo.setOldId(old_id);
        vo.setDealStatus(getNum(reply_confirmed)==1?"replyed":null);
        try {
            if(!AppUtil.isEmpty(vo.getDealStatus()) && "replyed".equals(vo.getDealStatus())) {
                vo.setReplyDate(getDate(d.get("reply_date")));

            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            vo.setReplyDate(vo.getPublishDate());
        }
        if(!StringUtils.isEmpty(leader_id)) {
            if(leaderMap.containsKey(leader_id)) {
                vo.setReceiveUserName(leaderMap.get(leader_id));
            }
        }
        if(!StringUtils.isEmpty(question_id)) {
            if(codeMap.containsKey(question_id)) {
                String code = codeMap.get(question_id);
                if(code.contains("咨询")) {
                    vo.setClassCode("do_consult");
                }else if(code.contains("申诉")) {
                    vo.setClassCode("do_complain");
                }else if(code.contains("控告")) {
                    vo.setClassCode("do_report");
                }else if(code.contains("意见")) {
                    vo.setClassCode("do_suggest");
                }else if(code.contains("求决")) {
                    vo.setClassCode("do_decide");
                }else {
                    vo.setClassCode("others");
                }
            }
        }
        if(!StringUtils.isEmpty(receive_unit_id)) {
            if(organMap.containsKey(receive_unit_id)) {
                vo.setReceiveUnitId(organMap.get(receive_unit_id).getOrganId());
                vo.setReceiveUnitName(organMap.get(receive_unit_id).getName());
            }
        }
        toBaseContent(vo,columnMgrEO);
        System.out.println("正在导入第"+i+"条数据......"+"id:"+vo.getOldId());
    }

    /**
     * 获取写信人
     * @param vo
     * @return
     */
    public void getPerson(MessageBoardEditVO vo) {
        if(!AppUtil.isEmpty(vo.getOldPersonId())) {

            DBCollection collection = getDB().getCollection("site_account");
            // 查询条件
            BasicDBObject condition = new BasicDBObject();
            condition.put("status", Boolean.TRUE);
            condition.put("removed", Boolean.FALSE);
            condition.put("_id", new ObjectId(vo.getOldPersonId()));

            List<DBObject> list = null;
            try {
                DBCursor cursor = collection.find(condition);
                list = cursor.toArray();
                for(DBObject o:list) {
                    String phone = (String)o.get("phone");
                    String email = (String)o.get("email");

                    vo.setPersonPhone(phone);
                    vo.setEmail(email);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 保存主表
     * @param vo
     * @param columnMgrEO
     * @return
     */
    public Object toBaseContent(MessageBoardEditVO vo,ColumnMgrEO columnMgrEO) {
        BaseContentEO eo = new BaseContentEO();
        eo.setEditor("导入");
        eo.setSiteId(siteId);
        eo.setTypeCode(columnMgrEO.getColumnTypeCode());
        eo.setIsPublish(vo.getIsPublish());
        eo.setColumnId(vo.getColumnId());
        eo.setTitle(vo.getTitle());
        eo.setHit(vo.getHit());
        eo.setCreateDate(vo.getCreateDate());
        eo.setUpdateDate(vo.getUpdateDate());
        eo.setPublishDate(vo.getPublishDate());
        eo.setRecordStatus("Normal");
        Long id = baseContentService.saveEntity(eo);
        vo.setBaseContentId(id);
        toMessageBoard(vo);
        return vo;
    }

    /**
     * 保存信件主表
     * @param vo
     * @return
     */
    public Object toMessageBoard(MessageBoardEditVO vo) {
        MessageBoardEO eo = new MessageBoardEO();
        AppUtil.copyProperties(eo,vo);

        eo.setIsPublic(vo.getIsPublic());
        eo.setReceiveUnitId(15l);//领导信箱，默认首发政府办公室单位
        eo.setReceiveUserCode(vo.getReceiveUserCode());//有问题
        if(StringUtils.isEmpty(vo.getRandomCode())) {
            eo.setRandomCode(RandomCode.shortUrl(vo.getBaseContentId() + ""));
        }

        Long id = null;
        try {
            id = messageBoardService.saveEntity(eo);
        } catch (Exception e) {
            e.printStackTrace();
        }
        vo.setId(id);
        toForward(vo);
        return vo;
    }
    /**
     * 保存信件分办表
     * @param vo
     * @return
     */
    public Object toForward(MessageBoardEditVO vo) {
        MessageBoardForwardEO eo = new MessageBoardForwardEO();

        eo.setMessageBoardId(vo.getId());
        eo.setIp(vo.getPersonIp());
        eo.setDealStatus(vo.getDealStatus());
        eo.setDueDate(vo.getDueDate());
        eo.setReceiveOrganId(vo.getReceiveUnitId());
        eo.setReceiveUnitName(vo.getReceiveUnitName());
        eo.setReplyDate(vo.getReplyDate());
        eo.setUsername(vo.getReceiveUserName());
        eo.setRemarks("请快速办理");
        eo.setCreateDate(vo.getForwardDate());
        eo.setReceiveUserName(vo.getReceiveUserName());
        eo.setOperationStatus(AMockEntity.RecordStatus.Normal.toString());
        Long id = forwardService.saveEntity(eo);
        vo.setForwardId(id);
        toReply(vo);
        return vo;
    }

    /**
     * 保存信件回复表
     * @param vo
     * @return
     */
    public Object toReply(MessageBoardEditVO vo) {
        DBCollection collection = getDB().getCollection("supervision_reply");
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("status", Boolean.TRUE);
        paramMap.put("type", Integer.valueOf(1));
        paramMap.put("supervision_id", vo.getOldId());
        DBObject ref = new BasicDBObject(paramMap);
        DBCursor cursor = collection.find(ref);
        List<DBObject> list = cursor.toArray();
        for(DBObject d : list) {
            MessageBoardReplyEO eo = new MessageBoardReplyEO();
            String message = (String)d.get("message");
            String attach_id = (String)d.get("supervision_attach_id");

            eo.setMessageBoardId(vo.getId());
            eo.setIp(vo.getPersonIp());
            eo.setDealStatus(vo.getDealStatus());
            eo.setForwardId(vo.getForwardId());
            eo.setReceiveUserCode(vo.getReceiveUserCode());
            eo.setUsername(vo.getReceiveUserName());
            eo.setCreateDate(getDate(d.get("create_date")));
            eo.setUpdateDate(getDate(d.get("update_date")));
            eo.setReplyDate(eo.getCreateDate());
            eo.setAttachId(attach_id);
            eo.setReplyContent(message);
            eo.setReceiveName(vo.getReceiveUnitName());
            Long id = replyService.saveEntity(eo);
        }

        return ajaxOk();
    }

    /**
     * 删除指定栏目的数据
     * @param curColumdId
     * @return
     * @throws UnknownHostException
     */
    @ResponseBody
    @RequestMapping("delete")
    public Object delete(Long curColumdId) {
        if (AppUtil.isEmpty(curColumdId)) {
            throw new BaseRunTimeException(TipsMode.Message.toString(),"请选择要清空的目录");
        }
        baseContentService.deleteNewsByColumnId(curColumdId);
        return getObject();
    }

    public Integer getNum(String str) {
        Integer flag = 0;
        if("True".equals(str) || "true".equals(str) || "1".equals(str)) {
            flag = 1;
        }else if("False".equals(str) || "false".equals(str)) {
            flag = 0;
        }else if(str.contains(".")) {
            str = str.substring(0,str.indexOf("."));
            if(str =="1") {
                flag = 1;
            }else {
                flag = 0;
            }
        }else {
            flag = 0;
        }
        return flag;
    }

    public Date getDate(Object o) {
        Date date = null;
        if(null == o) {
            date = new Date(new BigDecimal("0").longValue()*1000);
        }else {
            date = new Date(new BigDecimal(o.toString()).longValue()*1000);
        }
        return date;
    }
}