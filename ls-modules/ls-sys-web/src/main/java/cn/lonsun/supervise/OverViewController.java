package cn.lonsun.supervise;

import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.base.util.StringUtils;
import cn.lonsun.supervise.column.internal.service.IColumnUpdateService;
import cn.lonsun.supervise.column.internal.service.IColumnUpdateWarnService;
import cn.lonsun.supervise.columnupdate.internal.entity.ColumnUpdateEO;
import cn.lonsun.supervise.columnupdate.internal.entity.ColumnUpdateWarnEO;
import cn.lonsun.supervise.errhref.internal.entity.ErrHrefEO;
import cn.lonsun.supervise.errhref.internal.service.IErrHrefService;
import cn.lonsun.supervise.errhref.internal.service.IHrefResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author gu.fei
 * @version 2017-03-17 9:33
 */
@Controller
@RequestMapping("/supervise")
public class OverViewController extends BaseController {

    private static final String FILE_BASE = "/supervise/overview/";

    @Autowired
    private IColumnUpdateService columnUpdateService;

    @Autowired
    private IColumnUpdateWarnService columnUpdateWarnService;

    @Autowired
    private IErrHrefService errHrefService;

    @Autowired
    private IHrefResultService hrefResultService;

    @RequestMapping("/overview")
    public String overview() {
        return FILE_BASE + "/overview";
    }
    /*public String overview() {
        return FILE_BASE + "/overview_new";
    }*/

    /**
     * 统计栏目更新
     * @return
     */
    @ResponseBody
    @RequestMapping("/getColumnUpdateStatic")
    public Object getColumnUpdateStatic() {
        Map<String,Object> param = new HashMap<String, Object>();
        param.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
        List<ColumnUpdateEO> list = columnUpdateService.getEntities(ColumnUpdateEO.class,param);
        return getObject(list);
    }

    /**
     * 统计栏目更新
     * @return
     */
    @ResponseBody
    @RequestMapping("/getColumnWarnStatic")
    public Object getColumnWarnStatic(String types) {
        List<String> ptyps = StringUtils.getListWithString(types,",");
        Map<String,Object> params = new HashMap<String, Object>();
        params.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
        params.put("taskType",ptyps);
        params.put("isQualified",1);
        List<ColumnUpdateWarnEO> list = columnUpdateWarnService.getEntities(ColumnUpdateWarnEO.class,params);
        return getObject(list);
    }

    /**
     * 统计错链
     * @return
     */
    @ResponseBody
    @RequestMapping("/getErrhrefStatic")
    public Object getErrhrefStatic() {
        Map<String,Object> param = new HashMap<String, Object>();
        param.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
        List<ErrHrefEO> list = errHrefService.getEntities(ErrHrefEO.class,param);
        if(null != list) {
            for(ErrHrefEO errHrefEO:list) {
                Long num = hrefResultService.getCountByTaskId(errHrefEO.getId());
                errHrefEO.setCheckResultNum(num);
            }
        }
        return getObject(list);
    }
}
