package cn.lonsun.site.site;

import cn.lonsun.cache.client.CacheGroup;
import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.internal.service.IBaseContentService;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.base.util.StringUtils;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.util.TipsMode;
import cn.lonsun.indicator.internal.entity.IndicatorEO;
import cn.lonsun.site.site.internal.entity.ColumnConfigEO;
import cn.lonsun.site.site.internal.entity.ColumnMgrEO;
import cn.lonsun.site.site.internal.entity.SiteMgrEO;
import cn.lonsun.site.site.internal.service.IColumnConfigService;
import cn.lonsun.site.site.internal.service.ISiteConfigService;
import cn.lonsun.site.site.vo.ColumnVO;
import cn.lonsun.system.role.internal.service.ISiteRightsService;
import cn.lonsun.util.LoginPersonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.List;

import static cn.lonsun.cache.client.CacheHandler.getEntity;

/**
 * <br/>
 *
 * @author wangss <br/>
 * @version v1.0 <br/>
 * @date 2016-4-5<br/>
 */
@Controller
@RequestMapping("comColumn")
public class ComColumnController extends BaseController {
    @Autowired
    private IColumnConfigService columnConfigService;

    @Autowired
    private ISiteConfigService siteConfigService;

    @Resource(name = "ex_8_IndicatorServiceImpl")
    private cn.lonsun.rbac.indicator.service.IIndicatorService indicatorService;

    @Autowired
    private ISiteRightsService siteRightsService;

    @Autowired
    private IBaseContentService baseContentService;

    /**
     * 去往栏目管理页面
     *
     * @return
     */
    @RequestMapping("listColumnTree")
    public String listColumnTree(@RequestParam(value = "indicatorId", required = false, defaultValue = "") Long indicatorId, HttpSession session
            , Model model) {
        if (indicatorId == null) {
            Long siteId = LoginPersonUtil.getSiteId();
            model.addAttribute("indicatorId", siteId);
        } else {
            model.addAttribute("indicatorId", indicatorId);
        }
        return "site/site/com_column";
    }

    @RequestMapping("toEdit")
    public String toEdit( Long indicatorId, Model model) {
        if(indicatorId==null){
            Integer sortNum=columnConfigService.getNewSortNum(null,true);
            model.addAttribute("sortNum",sortNum);
            model.addAttribute("name","");
            indicatorId=-1L;
        }else{
            IndicatorEO indicatorEO=CacheHandler.getEntity(IndicatorEO.class,indicatorId);
            if(indicatorEO!=null){
                model.addAttribute("name",indicatorEO.getName());
                model.addAttribute("sortNum",indicatorEO.getSortNum());
            }
        }
        model.addAttribute("indicatorId",indicatorId);
        return "site/site/com_column_edit";
    }

    /**
     * 保存一级公共栏目
     * @param mgrEO
     * @return
     */
    @RequestMapping("saveLevColumn")
    @ResponseBody
    public Object saveLevColumn(ColumnMgrEO mgrEO){
        if(StringUtils.isEmpty(mgrEO.getName())){
            throw new BaseRunTimeException(TipsMode.Message.toString(), "名称不能为空");
        }
        if(mgrEO.getSortNum()==null){
            throw new BaseRunTimeException(TipsMode.Message.toString(), "序号不能为空");
        }
        IndicatorEO indicatorEO=new IndicatorEO();
        if(mgrEO.getIndicatorId()==null){
            indicatorEO.setName(mgrEO.getName());
            indicatorEO.setParentId(null);
            indicatorEO.setIsParent(0);
            indicatorEO.setSiteId(null);
            indicatorEO.setSortNum(mgrEO.getSortNum());
            indicatorEO.setType(IndicatorEO.Type.COM_Section.toString());
            indicatorService.saveEntity(indicatorEO);
            ColumnConfigEO configEO=new ColumnConfigEO();
            configEO.setIndicatorId(indicatorEO.getIndicatorId());
            configEO.setColumnTypeCode("articleNews");
            columnConfigService.saveEntity(configEO);
        }else{
            indicatorEO=CacheHandler.getEntity(IndicatorEO.class,mgrEO.getIndicatorId());
            if(indicatorEO!=null){
                indicatorEO.setName(mgrEO.getName());
                indicatorEO.setSortNum(mgrEO.getSortNum());
                indicatorService.updateEntity(indicatorEO);
            }
        }
        return getObject(indicatorEO.getIndicatorId());


    }

    /**
     * 获取栏目树的结构（异步加载）
     * 1、给栏目管理提供栏目树
     *
     * @param indicatorId
     * @return
     */
    @RequestMapping("getColumnTree")
    @ResponseBody
    public Object getColumnTree(@RequestParam(value = "indicatorId", required = false, defaultValue = "") Long indicatorId,
                                @RequestParam(value = "siteId", required = false, defaultValue = "") Long siteId) {

        List<ColumnVO> list = columnConfigService.getComColumnTree(indicatorId);
        return getObject(list);
    }

    /**
     * 保存一级一下的栏目信息
     * @param columnVO
     * @return
     */
    @RequestMapping("saveColumnEO")
    @ResponseBody
    public Object saveColumnEO(ColumnMgrEO columnVO){
        if(StringUtils.isEmpty(columnVO.getName())){
            throw new BaseRunTimeException(TipsMode.Message.toString(), "栏目名称不能为空");
        }
        if(columnVO.getSortNum()==null){
            throw new BaseRunTimeException(TipsMode.Message.toString(), "序号不能为空");
        }
        if(columnVO.getIsStartUrl()!=1&&StringUtils.isEmpty(columnVO.getContentModelCode())){
            throw new BaseRunTimeException(TipsMode.Message.toString(), "内容模型不能为空");
        }
        if(columnVO.getIsStartUrl()==1){
            columnVO.setColumnTypeCode("redirect");
        }
        Long indicatorId=columnConfigService.saveComEO(columnVO);
        return getObject(indicatorId);
    }

    public List<ColumnMgrEO> getColumnOpt( List<ColumnMgrEO> list) {
        return siteRightsService.getCurUserColumnOpt(list);
    }

    /**
     * 获取一级公共栏目树
     * @param siteId
     * @return
     */
    @RequestMapping("getLevComColumn")
    @ResponseBody
    public Object getLevComColumn(Long siteId){
        List<ColumnVO> list=columnConfigService.getComColumnTree(null);
        return getObject(list);
    }
    /**
     * 删除栏目
     * @param indicatorId
     * @return
     */
    @RequestMapping("delColumnEO")
    @ResponseBody
    public Object delColumnEO(Long indicatorId){
        if(indicatorId==null){
            throw new BaseRunTimeException(TipsMode.Message.toString(), "请选择删除要删除的栏目");
        }
        IndicatorEO indicatorEO= getEntity(IndicatorEO.class,indicatorId);
        ColumnConfigEO configEO= getEntity(ColumnConfigEO.class, CacheGroup.CMS_INDICATORID,indicatorId);
        if(indicatorEO==null){
            throw new BaseRunTimeException(TipsMode.Message.toString(), "栏目不存在");
        }
        if(BaseContentEO.TypeCode.ordinaryPage.toString().equals(configEO.getColumnTypeCode())){
            columnConfigService.deleteEO(indicatorId);
            return  getObject(0);
        }
        List<SiteMgrEO> list=siteConfigService.getByComColumnId(indicatorId);
        if(list!=null&&list.size()>0){
            throw new BaseRunTimeException(TipsMode.Message.toString(), "该栏目已被虚拟子站绑定");
        }
        if(indicatorEO.getIsParent()==1){
            throw new BaseRunTimeException(TipsMode.Message.toString(), "该栏目下有子栏目");
        }
        Long count=baseContentService.getCountByColumnId(indicatorId);
        if(count==null||count<=0){
            columnConfigService.deleteComEO(indicatorId);
            return  getObject(0);
        }else{
            throw new BaseRunTimeException(TipsMode.Message.toString(), "该栏目下面有内容");
        }
    }

}
