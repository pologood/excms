package cn.lonsun.net.service.controller;

import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.net.service.entity.CmsRelatedRuleEO;
import cn.lonsun.net.service.entity.CmsTableResourcesEO;
import cn.lonsun.net.service.service.IRelatedRuleService;
import cn.lonsun.net.service.service.ITableResourcesService;
import cn.lonsun.site.template.util.ResponseData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @author gu.fei
 * @version 2016-10-26 11:26
 */
@Controller
@RequestMapping("/aqguide/import")
public class AnQingGuideImportController extends BaseController {

    private static Logger logger = LoggerFactory.getLogger(AnQingGuideImportController.class);

    @Autowired
    private IRelatedRuleService relatedRuleService;

    @Autowired
    private ITableResourcesService tableResourcesService;

    @ResponseBody
    @RequestMapping("/updateRuleTable")
    public Object updateRuleTable() {
        logger.info("------------------------------导数据开始了-----------------------------");
        List<CmsRelatedRuleEO> rules = relatedRuleService.getEOs();

        List<CmsTableResourcesEO> tables = tableResourcesService.getEOs();

        String cids = "3903594,3903627,3903651,3903660,3903733,3903818,3903598,3903631,3903655,3903664,3903737,3903822,3903602" +
                ",3903635,3903668,3903741,3903826,3903606,3903639,3903672,3903745,3903830,3903610,3903643,3903676,3903749" +
                ",3903614,3903647,3903680,3903753,3903618,3903684,3903757,3903622,3903688,3903761,3903692,3903765,3903696" +
                ",3903769,3903700,3903773,3903704,3903777,3903708,3903781,3903712,3903785,3903716,3903789,3903720,3903793" +
                ",3903724,3903797,3903801,3903805,3903809,3903813,3903534,3903538,3903542,3903546,3903550,3903554";
        for(CmsRelatedRuleEO eo : rules) {
            relatedRuleService.updateEO(eo,cids);
            logger.info("--更新法规【" + eo.getName() + "】完成--");
        }

        for(CmsTableResourcesEO eo : tables) {
            tableResourcesService.updateEO(eo,cids);
            logger.info("--更新表格资源【" + eo.getName() + "】完成--");
        }

        return ResponseData.success("处理中!");
    }
}
