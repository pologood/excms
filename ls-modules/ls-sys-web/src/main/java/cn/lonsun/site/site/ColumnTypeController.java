package cn.lonsun.site.site;

import cn.lonsun.activemq.MessageSenderUtil;
import cn.lonsun.cache.client.CacheGroup;
import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.internal.service.IBaseContentService;
import cn.lonsun.content.vo.ContentPageVO;
import cn.lonsun.content.vo.CopyReferVO;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.util.TipsMode;
import cn.lonsun.mongodb.entity.ContentMongoEO;
import cn.lonsun.mongodb.service.ContentMongoServiceImpl;
import cn.lonsun.site.site.internal.entity.ColumnConfigEO;
import cn.lonsun.site.site.internal.entity.ColumnTypeEO;
import cn.lonsun.site.site.internal.service.IColumnTypeService;
import cn.lonsun.system.datadictionary.vo.DataDictVO;
import cn.lonsun.util.ColumnUtil;
import cn.lonsun.util.DataDictionaryUtil;
import cn.lonsun.webservice.internal.entity.WebServiceEO;
import org.apache.commons.collections4.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Hewbing
 * @ClassName: ArticleNewsController
 * @Description: article news
 * @date 2015年11月4日 上午11:06:50
 */
@Controller
@RequestMapping(value = "columnType")
public class ColumnTypeController extends BaseController {

    @Autowired
    private IColumnTypeService icts;

    @RequestMapping("index")
    public String index(Long pageIndex, ModelMap map) {
        if (pageIndex == null)
            pageIndex = 0L;
        map.put("pageIndex", pageIndex);
        return "/site/site/column_type_index";
    }


    /**
     * @param pageVO
     * @return
     * @Description get news page by pageVO
     * @author Hewbing
     * @date 2015年9月14日 下午2:48:11
     */
    @RequestMapping("getPage")
    @ResponseBody
    public Object getPage(ContentPageVO pageVO) {
        return getObject(icts.getPage(pageVO));
    }

    @RequestMapping("getList")
    @ResponseBody
    public Object getList(Long siteId) {
        return getObject(icts.getColumnTypeEOs(siteId));
    }

    @RequestMapping("edit")
    public String edit(Long id, ModelMap map) {
        map.put("typeid", id);
        ColumnTypeEO cte = icts.getEntity(ColumnTypeEO.class, id);
        map.put("ColumnTypeEO", cte);
        return "/site/site/column_type_edit";
    }

    @ResponseBody
    @RequestMapping("getColumnType")
    public Object getColumnType(Long id) {
        ColumnTypeEO cte = icts.getEntity(ColumnTypeEO.class, id);
        if (AppUtil.isEmpty(cte)) {
            cte = new ColumnTypeEO();
        }
        return getObject(cte);
    }

    @ResponseBody
    @RequestMapping("update")
    public Object update(ColumnTypeEO cte) {
        if (icts.isHave(cte.getSiteId(), cte.getId(), cte.getTypeName()))
            return getObject("0");
        icts.updateEntity(cte);
        return getObject("1");
    }

    @ResponseBody
    @RequestMapping("save")
    public Object save(ColumnTypeEO cte) {
        Map<String, Object> params = new HashedMap<String, Object>();
        params.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
        params.put("typeName", cte.getTypeName());
        List<ColumnTypeEO> ctes = icts.getEntities(ColumnTypeEO.class, params);
        if (ctes != null && !ctes.isEmpty())
            return getObject("0");
        icts.saveEntity(cte);
        return getObject("1");
    }

    @RequestMapping("delete")
    @ResponseBody
    public Object delete(@RequestParam("ids[]") Long[] ids) {
        if (ids != null && ids.length > 0) {
            icts.delete(ColumnTypeEO.class, ids);
        }
        return getObject();
    }
}
