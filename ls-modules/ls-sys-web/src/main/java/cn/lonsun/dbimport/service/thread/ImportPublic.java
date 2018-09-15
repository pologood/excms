package cn.lonsun.dbimport.service.thread;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.util.ThreadUtil;
import cn.lonsun.publicInfo.internal.service.IPublicApplyService;
import cn.lonsun.publicInfo.internal.service.IPublicContentService;
import cn.lonsun.publicInfo.vo.PublicApplyTypeVO;
import cn.lonsun.publicInfo.vo.PublicApplyVO;
import cn.lonsun.util.HibernateHandler;
import cn.lonsun.util.HibernateSessionUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

public class ImportPublic implements Callable<String>, Runnable {

    private IPublicContentService publicContentService;

    private IPublicApplyService publicApplyService;

    private Long siteId;

    private List<Map<String, Object>> data;

    private Long ex8organId;

    private Map<String, Object> sessionInfo;

    private Map<String, String> replyMap;

    public ImportPublic(IPublicContentService publicContentService, IPublicApplyService publicApplyService, Long siteId,
                        List<Map<String, Object>> data, Long ex8organId, Map<String, Object> sessionInfo) {
        this.publicContentService = publicContentService;
        this.publicApplyService = publicApplyService;
        this.siteId = siteId;
        this.data = data;
        this.ex8organId = ex8organId;
        this.sessionInfo = sessionInfo;
        replyMap = new HashMap<String, String>();
        replyMap.put("1", "not_handle");
        replyMap.put("2", "agree_public");
        replyMap.put("3", "agree_part_public");
        replyMap.put("4", "info_not_exist");
        replyMap.put("5", "not_the_organ_business");
        replyMap.put("6", "apply_content_not_sure");
        replyMap.put("7", "driving_public");
        replyMap.put("8", "not_agree_public");
    }

    @Override
    public String call() throws Exception {
        try {
            return excute();
        } catch (Exception e) {
            e.printStackTrace();
            return "导入依申请公开数据失败:" + e.getMessage();
        }
    }

    @Override
    public void run() {
        excute();
    }

    private String excute(){
        ThreadUtil.set(sessionInfo);
        return HibernateSessionUtil.execute(new HibernateHandler<String>() {
            @Override
            public String execute() throws Throwable {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                for (Map<String, Object> map : data) {
                    PublicApplyVO publicApplyVO = new PublicApplyVO();
                    publicApplyVO.setIsPublish(1);
                    Date date = null;
                    try {
                        date = map.get("dataLine") == null ? new Date() : sdf.parse(map.get("dataLine").toString());
                    } catch (ParseException e) {
                        date = new Date();
                    }
                    publicApplyVO.setApplyDate(date);
                    publicApplyVO.setCreateDate(date);
                    publicApplyVO.setPublishDate(date);
                    publicApplyVO.setSiteId(siteId);
                    //保存老数据库ID
                    publicApplyVO.setOldSchemaId(AppUtil.getValue(map.get("applyId")));
                    // 设置单位
                    publicApplyVO.setOrganId(ex8organId);
                    // author_open是否公开 1-公开 0-不公开
                    String author_open = map.get("IsShow").toString();
                    if ("true".equals(author_open)) {
                        publicApplyVO.setIsPublic(true);
                    } else {
                        publicApplyVO.setIsPublic(false);
                    }
                    // 设置公共属性
                    publicApplyVO.setContent(AppUtil.isEmpty(map.get("description")) ? "" : map.get("description").toString());
                    publicApplyVO.setUse(AppUtil.isEmpty(map.get("infoUse")) ? "" : map.get("infoUse").toString());
                    publicApplyVO.setQueryPassword(AppUtil.isEmpty(map.get("password")) ? "" : map.get("password").toString());
                    publicApplyVO.setConfirmPassword(AppUtil.isEmpty(map.get("password")) ? "" : map.get("password").toString());
                    publicApplyVO.setProvideType(AppUtil.isEmpty(map.get("provide")) ? "" : map.get("provide").toString());
                    publicApplyVO.setReceiveType(AppUtil.isEmpty(map.get("getting")) ? "" : map.get("getting").toString());
                    String applytype = map.get("applyType").toString();
                    // as_type 0-公民 1-法人
                    if ("0".equals(applytype)) {
                        publicApplyVO.setType(PublicApplyTypeVO.PERSON.getCode());
                        publicApplyVO.setTypeName(PublicApplyTypeVO.PERSON.getName());
                        publicApplyVO.setName(AppUtil.isEmpty(map.get("userName")) ? "" : map.get("userName").toString());
                        publicApplyVO.setOrganName(AppUtil.isEmpty(map.get("userUnits")) ? "" : map.get("userUnits").toString());
                        publicApplyVO.setCardType("idCard");// 统一设置为身份证
                        publicApplyVO.setCardTypeName("身份证");
                        publicApplyVO.setCardNum(AppUtil.isEmpty(map.get("cardNum")) ? "" : map.get("cardNum").toString());
                        publicApplyVO.setMail(AppUtil.isEmpty(map.get("eMail")) ? "" : map.get("eMail").toString());
                        publicApplyVO.setPostalNum(AppUtil.isEmpty(map.get("postCode")) ? "" : map.get("postCode").toString());
                        publicApplyVO.setFax("");
                        publicApplyVO.setPhone(AppUtil.isEmpty(map.get("phone")) ? "" : map.get("phone").toString());
                        publicApplyVO.setAddress(AppUtil.isEmpty(map.get("address")) ? "" : map.get("address").toString());
                    } else {
                        publicApplyVO.setType(PublicApplyTypeVO.ORGAN.getCode());
                        publicApplyVO.setTypeName(PublicApplyTypeVO.ORGAN.getName());
                        publicApplyVO.setOrganName(AppUtil.isEmpty(map.get("userUnits")) ? "" : map.get("userUnits").toString());
                        publicApplyVO.setOrganCode("");
                        publicApplyVO.setLegalName(AppUtil.isEmpty(map.get("frdb")) ? "" : map.get("frdb").toString());
                        publicApplyVO.setContactName(AppUtil.isEmpty(map.get("unit_contact")) ? "" : map.get("unit_contact").toString());
//                    publicApplyVO.setContactPhone(AppUtil.isEmpty(map.get("unit_phone")) ? "" : map.get("unit_phone").toString());
//                    publicApplyVO.setFax(AppUtil.isEmpty(map.get("unit_fax")) ? "" : map.get("unit_fax").toString());
                        publicApplyVO.setAddress(AppUtil.isEmpty(map.get("address")) ? "" : map.get("address").toString());
//                    publicApplyVO.setContactMail(AppUtil.isEmpty(map.get("unit_email")) ? "" : map.get("unit_email").toString());
                    }
                    // 回复状态
                    if (!AppUtil.isEmpty(map.get("revertContent"))) {
                        publicApplyVO.setReplyStatus(replyMap.get(map.get("revertType").toString()));
                        publicApplyVO.setReplyContent(null == map.get("revertContent") ? "" : map.get("revertContent").toString());
                    }
                    publicApplyService.saveEntity(publicApplyVO);
                }
                return null;
            }

            @Override
            public String complete(String result, Throwable exception) {
                if(exception != null){
                    return exception.getMessage();
                }
                return result;
            }
        });
    }


    public Long getSiteId() {
        return siteId;
    }

    public void setSiteId(Long siteId) {
        this.siteId = siteId;
    }

    public IPublicContentService getPublicContentService() {
        return publicContentService;
    }

    public void setPublicContentService(IPublicContentService publicContentService) {
        this.publicContentService = publicContentService;
    }

    public List<Map<String, Object>> getData() {
        return data;
    }

    public void setData(List<Map<String, Object>> data) {
        this.data = data;
    }
}
