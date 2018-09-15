package cn.lonsun.content.controller;

import cn.lonsun.activemq.MessageSender;
import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.internal.service.IBaseContentService;
import cn.lonsun.content.internal.service.IOrdinaryPageService;
import cn.lonsun.content.vo.OrdinaryPageVO;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.util.TipsMode;
import cn.lonsun.message.internal.entity.MessageEnum;
import cn.lonsun.message.internal.entity.MessageStaticEO;
import cn.lonsun.mongodb.entity.ContentMongoEO;
import cn.lonsun.mongodb.service.ContentMongoServiceImpl;
import cn.lonsun.site.site.internal.entity.ColumnMgrEO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * p普通页面控制层br/>
 *
 * @author wangss <br/>
 * @version v1.0 <br/>
 * @date 2015-11-18<br/>
 */
@Controller
@RequestMapping(value = "ordinaryPage")
public class OrdinaryPageController extends BaseController {

    @Autowired
    private IOrdinaryPageService pageService;

    @Autowired
    private IBaseContentService baseContentService;

    @Autowired
    private ContentMongoServiceImpl contentMongoService;

    /**
     * 去往首页
     * @return
     */
    @RequestMapping("index")
    private String index(){
        return "/content/ordinary_page/ordinary_page_edit";
    }

    /**
     * 保存编辑
     * @param vo
     * @return
     */
    @RequestMapping("saveEO")
    @ResponseBody
    public Object saveEO(OrdinaryPageVO vo){
        /*if(!AppUtil.isEmpty(vo.getContent())){
            vo.setContent(WordsSplitHolder.wordsRplc(vo.getText(), vo.getContent(), Type.SENSITIVE.toString()));
        }*/

        if(!StringUtils.isEmpty(vo.getRemarks())&&vo.getRemarks().length()>1000){
            throw new BaseRunTimeException(TipsMode.Message.toString(), "摘要字数长度应为0～1000");
        }
        if(StringUtils.isEmpty(vo.getText())){
            throw new BaseRunTimeException(TipsMode.Message.toString(), "内容不能为空");
        }
        BaseContentEO eo=pageService.saveEO(vo);
        if(vo.getIsPublish()==1){
            //处理保存时发布,正式部署时启用
            boolean rel = MessageSender.sendMessage(
                    new MessageStaticEO(vo.getSiteId(), vo.getColumnId(), new Long[]{vo.getId()})
                    .setType(MessageEnum.PUBLISH.value()));
            if(!rel){
                throw new BaseRunTimeException(TipsMode.Message.toString(), "保存成功,发布失败");
            }
        }
        return getObject(eo);
    }

    /**
     * 根据栏目ID获取信息
     * @param columnId
     * @return
     */
    @RequestMapping("getEO")
    @ResponseBody
    public Object getEO(Long columnId) {
        OrdinaryPageVO vo = new OrdinaryPageVO();
        ColumnMgrEO columnMgrEO = CacheHandler.getEntity(ColumnMgrEO.class, columnId);
        HashMap map = new HashMap();
        map.put("columnId", columnId);
        map.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
        List<BaseContentEO> list = baseContentService.getEntities(BaseContentEO.class, map);
        if (list != null && list.size() > 0) {
            BaseContentEO eo = list.get(0);
            AppUtil.copyProperties(vo, eo);
            Criteria criteria = Criteria.where("_id").is(eo.getId());
            Query query = new Query(criteria);
            ContentMongoEO _eo = contentMongoService.queryOne(query);
            if (!AppUtil.isEmpty(_eo)) {
                vo.setContent(_eo.getContent());
            }
            if (null != columnMgrEO) {
                vo.setPath(columnMgrEO.getUrlPath());
            }
        } else {
            vo.setPublishDate(new Date());
            if (null != columnMgrEO) {
                vo.setPath(columnMgrEO.getUrlPath());
            }
        }
        return vo;
    }
}
