package cn.lonsun.site.words;

import cn.lonsun.monitor.words.internal.util.WordsSplitHolder;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.site.template.internal.entity.ParamDto;
import cn.lonsun.site.template.util.ResponseData;
import cn.lonsun.site.words.internal.cache.HotCache;
import cn.lonsun.site.words.internal.entity.WordsHotConfEO;
import cn.lonsun.site.words.internal.service.IWordsHotConfService;
import cn.lonsun.util.LoginPersonUtil;

/**
 * @author gu.fei
 * @version 2015-9-1 9:05
 */
@Controller
@RequestMapping("/words")
public class WordsHotConfController extends BaseController {

    private static final String FILE_BASE = "site/words/";

    private static final String URL_BASE = "/hot";

    @Autowired
    private IWordsHotConfService wordsHotConfService;

    @Autowired
    private TaskExecutor taskExecutor;

    private static Logger logger = org.slf4j.LoggerFactory.getLogger(WordsHotConfController.class);

    @RequestMapping("/index")
    public String index() {
        return FILE_BASE + "index";
    }

    @RequestMapping(URL_BASE + "/dealTypeWin")
    public ModelAndView dealTypeWin(String node,String opt) {
        ModelAndView model = new ModelAndView(FILE_BASE + "/hot_type_win");
        model.addObject("node",node);
        model.addObject("opt", opt);
        return model;
    }

    @RequestMapping(URL_BASE + "/addOrEdit")
    public String addOrEdit(ModelMap map) {
        boolean superAdmin = LoginPersonUtil.isSuperAdmin();
        map.put("superAdmin", superAdmin);
        return FILE_BASE + "/hot_edit";
    }

    @ResponseBody
    @RequestMapping(URL_BASE + "/getHotConfEOList")
    public Object getHotConfEOList(ParamDto paramDto) {
        return wordsHotConfService.getPageListByTplId(paramDto);
    }

    @ResponseBody
    @RequestMapping(URL_BASE + "/save")
    public Object save(WordsHotConfEO eo) {
        if(eo.getSiteId() == null || eo.getSiteId() != -1) {
            Long siteId = LoginPersonUtil.getSiteId();
            eo.setSiteId(siteId);
        }
        wordsHotConfService.saveEntity(eo);
        asyncThread();
        return ResponseData.success("保存成功!");
    }

    @ResponseBody
    @RequestMapping(URL_BASE + "/update")
    public Object update(WordsHotConfEO eo) {
        if(eo.getSiteId() == null || eo.getSiteId() != -1) {
            Long siteId = LoginPersonUtil.getSiteId();
            eo.setSiteId(siteId);
        }
        wordsHotConfService.updateEntity(eo);
        asyncThread();
        return ResponseData.success("保存成功!");
    }

    @ResponseBody
    @RequestMapping(URL_BASE + "/delHotConfEO")
    public Object delHotConfEO(Long id) {
        wordsHotConfService.delEO(id);
        asyncThread();
        return ResponseData.success("删除成功!");
    }

    private void asyncThread() {
        taskExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    WordsSplitHolder.synHotDic();
                    HotCache.refresh();
                    logger.info(">>>>>>>>>>>>>>>>数据同步成功!");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
