package cn.lonsun.site.site;

import cn.lonsun.common.upload.fileManager.FileManager;
import cn.lonsun.common.upload.internal.service.IFileServer;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.content.internal.service.IBaseContentService;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.base.util.StringUtils;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.util.TipsMode;
import cn.lonsun.indicator.internal.entity.IndicatorEO;
import cn.lonsun.rbac.internal.entity.OrganEO;
import cn.lonsun.rbac.internal.service.IOrganService;
import cn.lonsun.site.site.internal.entity.SiteConfigEO;
import cn.lonsun.site.site.internal.entity.SiteMgrEO;
import cn.lonsun.site.site.internal.service.IColumnConfigService;
import cn.lonsun.site.site.internal.service.ISiteConfigService;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import static cn.lonsun.cache.client.CacheHandler.getEntity;

/**
 * 站点配置控制层<br/>
 *
 * @author wangss <br/>
 * @version v1.0 <br/>
 * @date 2015-8-24 <br/>
 */
@Controller
@RequestMapping("siteConfig")
public class SiteConfigController extends BaseController {

    @Autowired
    private ISiteConfigService siteConfigService;

    @Autowired
    private IBaseContentService baseContentService;

    @Autowired
    private IOrganService organService;

    @Autowired
    private IColumnConfigService columnConfigService;


    /**
     * 新增或者修改站点信息
     *
     * @param siteVO
     * @return
     */
    @RequestMapping("saveSiteConfigEO")
    @ResponseBody
    public Object saveSiteConfigEO(SiteMgrEO siteVO) {
        if (siteVO.getName() == null || siteVO.getName().trim() == "") {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "站点名称不能为空");
        }
        if (siteVO.getSortNum() == null) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "序号不能为空");
        }
        if (StringUtils.isEmpty(siteVO.getUnitNames()) || StringUtils.isEmpty(siteVO.getUnitIds())) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "单位不能为空");
        }
        if (StringUtils.isEmpty(siteVO.getUri())) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "绑定域名不能为空");
        }
        if (StringUtils.isEmpty(siteVO.getUnitIds())) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "单位不能为空");
        }
        if (!StringUtils.isEmpty(siteVO.getKeyWords()) && siteVO.getKeyWords().length() > 300) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "关键词的长度应小于300");
        }

      /*  if(!StringUtils.isEmpty(siteVO.getStationPwd())&&siteVO.getStationPwd().length()>0&&siteVO.getStationPwd().length()<31){
            throw new BaseRunTimeException(TipsMode.Message.toString(), "查询密码的长度应大于31");
        }*/
        SiteConfigEO eo = siteConfigService.saveEO(siteVO);
        return getObject(eo);
    }

    /**
     * 检查站点名称是否存在：是：true，否;false
     *
     * @param siteName
     * @return
     */
    @RequestMapping("checkSiteNameExist")
    @ResponseBody
    public Object checkSiteNameExist(String siteName, Long parentId, Long indicatorId) {
        boolean b = siteConfigService.checkSiteNameExist(siteName, parentId, indicatorId);
        return getObject(b);
    }

    /**
     * 根据主键ID 删除站点实体类
     *
     * @param indicatorId
     * @return
     */
    @RequestMapping("delSiteNode")
    @ResponseBody
    public Object delSiteNode(Long indicatorId) {
        if (indicatorId == null) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "请选择要删除的站点");
        }
        IndicatorEO indicatorEO = getEntity(IndicatorEO.class, indicatorId);
        if (indicatorEO == null) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "删除的站点不存在");
        }
        if (indicatorEO.getIsParent() == 1) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "该站点下面有栏目");
        }

































































































        Long count = baseContentService.getCountBySiteId(indicatorId);
        if (count == null || count <= 0) {
            siteConfigService.deleteEO(indicatorId);
            return getObject();
        } else {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "该站点下面有内容");
        }

    }

    /**
     * 根据主键ID 获取站点实体类
     *
     * @param indicatorId
     * @return
     */
    @RequestMapping("getSiteEO")
    @ResponseBody
    public Object getSiteEO(Long indicatorId) {
        // 新增站点根节点
        if (indicatorId == null) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "请选择站点");
        }
        SiteMgrEO siteVO = getEntity(SiteMgrEO.class, indicatorId);
        if (siteVO == null) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "请选择站点");
        }
        if (!StringUtils.isEmpty(siteVO.getUnitIds())) {
            Long[] idArr = AppUtil.getLongs(siteVO.getUnitIds(), ",");
            OrganEO organEO = organService.getEntity(OrganEO.class, idArr[0]);
            if (organEO != null) {
                siteVO.setUnitNames(organEO.getName());
            } else {
                siteVO.setUnitNames(null);
            }
        }
        Boolean isHave = columnConfigService.getIsHaveColumn(indicatorId);
        siteVO.setIsHave(isHave);
        return getObject(siteVO);
    }

    /**
     * 生成序列号
     *
     * @param parentId
     * @return
     */
    @RequestMapping("getNewSortNum")
    @ResponseBody
    public Object getNewSortNum(Long parentId, boolean isSub) {
        Integer sortNum = siteConfigService.getNewSortNum(parentId, isSub);
        return getObject(sortNum);
    }

    /**
     * 上传图片
     *
     * @param request
     * @param response
     * @param file
     * @throws IOException
     */
    @RequestMapping("uploadPic")
    public void uploadPic(HttpServletRequest request, HttpServletResponse response, @RequestParam(value = "file", required = false) MultipartFile file)
        throws IOException {

        IFileServer fileServer = FileManager.getFileServer();
        String path = FileManager.getDownloadIP();
        try {
            path = path + "/" + fileServer.uploadMultipartFile(file);
            response.setContentType("text/html");
            response.getWriter().write(JSON.toJSONString(ajaxOk(path)));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 选择上传图片
     *
     * @return
     */
    @RequestMapping("chooseVideoPic")
    public String chooseVideoPic() {
        return "site/site/choose_pic";
    }

    @RequestMapping("getUnits")
    @ResponseBody
    public Object getUnits() {
        List<OrganEO> list = organService.getUnits();
        return list;
    }
}
