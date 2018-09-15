package cn.lonsun.staticcenter.generate.tag.impl.guestbook;

import cn.lonsun.base.anno.DbInject;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.content.internal.dao.IGuestBookDao;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.internal.entity.GuestBookEO;
import cn.lonsun.content.internal.entity.GuestBookForwardRecordEO;
import cn.lonsun.content.internal.service.IBaseContentService;
import cn.lonsun.content.internal.service.IForwardRecordService;
import cn.lonsun.content.internal.service.IGuestBookService;
import cn.lonsun.content.leaderwin.internal.service.ILeaderInfoService;
import cn.lonsun.content.leaderwin.vo.LeaderInfoVO;
import cn.lonsun.content.vo.GuestBookEditVO;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.util.ContextHolderUtils;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.indicator.internal.entity.IndicatorEO;
import cn.lonsun.rbac.internal.entity.OrganEO;
import cn.lonsun.rbac.internal.entity.PersonEO;
import cn.lonsun.rbac.internal.service.IPersonService;
import cn.lonsun.site.contentModel.vo.ColumnTypeConfigVO;
import cn.lonsun.site.contentModel.vo.ContentModelParaVO;
import cn.lonsun.staticcenter.generate.GenerateConstant;
import cn.lonsun.staticcenter.generate.GenerateException;
import cn.lonsun.staticcenter.generate.tag.impl.AbstractBeanService;
import cn.lonsun.staticcenter.generate.thread.Context;
import cn.lonsun.staticcenter.generate.thread.ContextHolder;
import cn.lonsun.staticcenter.generate.util.PathUtil;
import cn.lonsun.staticcenter.service.HtmlEnum;
import cn.lonsun.system.datadictionary.vo.DataDictVO;
import cn.lonsun.system.member.internal.entity.MemberEO;
import cn.lonsun.system.member.internal.service.IMemberService;
import cn.lonsun.system.member.vo.MemberSessionVO;
import cn.lonsun.util.DataDictionaryUtil;
import cn.lonsun.util.ModelConfigUtil;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static cn.lonsun.cache.client.CacheHandler.getEntity;
import static cn.lonsun.util.ModelConfigUtil.getCongfigVO;

/**
 * @author hujun
 * @ClassName: GuestBookPageListBeanService
 * @Description: 分页标签
 * @date 2015年12月1日 下午5:36:41
 */
@Component
public class GuestBookPfwInfoBeanService extends AbstractBeanService {

    /**
     * 查询分页结果对象
     *
     * @see AbstractBeanService#getObject(JSONObject)
     */

    @Autowired
    private IGuestBookService guestBookService;

    @Autowired
    private IBaseContentService contentService;

    @Autowired
    private IPersonService personService;

    @Autowired
    private ILeaderInfoService leaderInfoService;

    @Override
    public Object getObject(JSONObject paramObj) {
        Context context = ContextHolder.getContext();
        String id = context.getParamMap().get("id");
        Long contentId = null;
        if (StringUtils.isEmpty(id)) {
            contentId = context.getContentId();
        } else {
            contentId = Long.parseLong(id);
        }

        if (AppUtil.isEmpty(contentId) || contentId == 0) {
            return "1";
        }
        BaseContentEO contentEO = contentService.getEntity(BaseContentEO.class, contentId);
        if (null == contentEO) {
            return "1";
        }
        Map<String, Object> result = new HashMap<String, Object>();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("baseContentId", contentId);
        map.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
        List<GuestBookEO> list = guestBookService.getEntities(GuestBookEO.class, map);
        if (null == list || list.size() <= 0) {
            return "1";
        }
        GuestBookEO eo = list.get(0);
        GuestBookEditVO vo = new GuestBookEditVO();
        AppUtil.copyProperties(vo, eo);
        vo.setIsPublish(contentEO.getIsPublish());
        vo.setTitle(contentEO.getTitle());
        vo.setColumnId(contentEO.getColumnId());
        vo.setSiteId(contentEO.getSiteId());
        ColumnTypeConfigVO configVO = ModelConfigUtil.getCongfigVO(contentEO.getColumnId(), contentEO.getSiteId());
        if (configVO != null) {
            if (configVO.getRecType() != null && configVO.getRecType() != 2) {
                if (configVO.getRecType().equals(0)) {
                    if (vo.getReceiveId() != null) {
                        OrganEO organEO = getEntity(OrganEO.class, vo.getReceiveId());
                        if (organEO != null) {
                            vo.setReceiveName(organEO.getName());
                        }
                    }
                } else {
                    if (vo.getReplyUnitId() != null) {
                        OrganEO organEO = getEntity(OrganEO.class, vo.getReplyUnitId());
                        if (organEO != null) {
                            vo.setReplyUnitName(organEO.getName());
                        }
                    } else {
                        if (vo.getReceiveId() != null) {
                            OrganEO organEO = getEntity(OrganEO.class, vo.getReceiveId());
                            if (organEO != null) {
                                vo.setReceiveName(organEO.getName());
                            }
                        }
                    }
                    if (!StringUtils.isEmpty(vo.getReceiveUserCode())) {
                        DataDictVO dictVO = DataDictionaryUtil.getItem("guest_book_rec_users", vo.getReceiveUserCode());
                        if (dictVO != null) {
                            vo.setReceiveUserName(dictVO.getKey());
                        }
                        if (StringUtils.isEmpty(vo.getReceiveUserName())) {
                            LeaderInfoVO infoVO = leaderInfoService.getLeaderInfoVO(Long.parseLong(vo.getReceiveUserCode()));
                            if (infoVO != null) {
                                vo.setReceiveUserName(infoVO.getName());
                            }
                        }
                    }
                }
                if(eo!=null&&eo.getPersonId()!=null) {
                    PersonEO personEO = personService.getEntity(PersonEO.class, eo.getPersonId());
                    if(personEO!=null){
                        result.put("desc", personEO.getDesc());
                        result.put("photoId", personEO.getJpegPhoto());
                    }
                }
            }
            vo.setRecType(configVO.getRecType());
        }
        if (!StringUtils.isEmpty(vo.getCommentCode())) {
            DataDictVO dictVO = DataDictionaryUtil.getItem("guest_comment", vo.getCommentCode());
            if (dictVO != null) {
                vo.setCommentName(dictVO.getKey());
            }
        } else {
            DataDictVO dictVO = DataDictionaryUtil.getDefuatItem("guest_comment", vo.getSiteId());
            if (dictVO != null) {
                vo.setCommentCode(dictVO.getCode());
            }
        }
        if (!StringUtils.isEmpty(vo.getClassCode())) {
            DataDictVO dictVO = DataDictionaryUtil.getItem("petition_purpose", vo.getClassCode());
            if (dictVO != null) {
                vo.setClassName(dictVO.getKey());
            }
        }

        if (configVO != null) {
            result.put("isAssess", configVO.getIsAssess());
        }
        List<DataDictVO> commentList = DataDictionaryUtil.getItemList("guest_comment", vo.getSiteId());
        result.put("commentList", commentList);
        result.put("vo", vo);
        return result;
    }

}
