package cn.lonsun.supervise;

import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.site.contentModel.vo.ColumnTypeConfigVO;
import cn.lonsun.site.contentModel.vo.ContentModelParaVO;
import cn.lonsun.site.template.internal.entity.ParamDto;
import cn.lonsun.site.template.util.ResponseData;
import cn.lonsun.supervise.column.internal.service.IColumnResultService;
import cn.lonsun.supervise.column.internal.service.IUnreplyGuestService;
import cn.lonsun.supervise.columnupdate.internal.entity.ColumnResultEO;
import cn.lonsun.supervise.columnupdate.internal.entity.UnreplyGuestEO;
import cn.lonsun.util.LoginPersonUtil;
import cn.lonsun.util.ModelConfigUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

import static cn.lonsun.util.ModelConfigUtil.getCongfigVO;

/**
 * @author gu.fei
 * @version 2016-11-18 14:35
 */
@Controller
@RequestMapping("/unreply/guest")
public class UnreplyGuestController extends BaseController {

    private static final String FILE_BASE = "/supervise/columnupdate/";

    @Autowired
    private IUnreplyGuestService unreplyGuestService;

    @Autowired
    private IColumnResultService columnResultService;

    @RequestMapping("/index")
    public String index() {
        return FILE_BASE + "/unreply_guest";
    }

    /**
     * 打开回复页面
     * @param id
     * @param columnId
     * @param model
     * @return
     */
    @RequestMapping(value = "guestBookReply")
    public String guestBookReply(Long id, Long columnId, Model model) {
        Integer recType = null;
        Integer isTurn = null;
        ColumnTypeConfigVO configVO = getCongfigVO(columnId, LoginPersonUtil.getSiteId());
        if (configVO != null) {
            recType = configVO.getRecType();
            isTurn = configVO.getTurn();
        }
        List<ContentModelParaVO> statusList = ModelConfigUtil.getDealStatus(columnId, LoginPersonUtil.getSiteId());
        if (statusList == null || statusList.size() <= 0) {
            model.addAttribute("status", 0);
        } else {
            model.addAttribute("status", 1);
        }
        model.addAttribute("statusList", statusList);
        model.addAttribute("baseContentId", id);
        model.addAttribute("recType", recType);
        model.addAttribute("isTurn", isTurn);
        Boolean isAdmin=false;
        isAdmin=LoginPersonUtil.isSuperAdmin()||LoginPersonUtil.isSiteAdmin();
        model.addAttribute("isAdmin",isAdmin);
        return FILE_BASE + "/guestbook_reply";
    }

    /**
     * 分页获取列表
     * @param dto
     * @return
     */
    @ResponseBody
    @RequestMapping("/getPageEOs")
    public Object getPageEOs(ParamDto dto) {
        return unreplyGuestService.getPageEOs(dto);
    }

    /**
     * 物理删除
     * @param id
     * @return
     */
    @ResponseBody
    @RequestMapping("/deleteEO")
    public Object deleteEO(Long id,Long resultId) {
        unreplyGuestService.delete(UnreplyGuestEO.class,id);
        ColumnResultEO eo = columnResultService.getEntity(ColumnResultEO.class,resultId);
        if(null != eo) {
            eo.setGuestNum(eo.getGuestNum() - 1);
            columnResultService.saveEntity(eo);
        }
        return ResponseData.success("删除成功");
    }
}
