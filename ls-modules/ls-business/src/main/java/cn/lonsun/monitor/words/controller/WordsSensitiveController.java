package cn.lonsun.monitor.words.controller;

import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.util.SpringContextHolder;
import cn.lonsun.monitor.words.internal.cache.SensitiveCache;
import cn.lonsun.monitor.words.internal.entity.WordsDto;
import cn.lonsun.monitor.words.internal.entity.WordsEasyerrEO;
import cn.lonsun.monitor.words.internal.entity.WordsSensitiveEO;
import cn.lonsun.monitor.words.internal.service.IWordsSensitiveService;
import cn.lonsun.monitor.words.internal.util.ExcelManager;
import cn.lonsun.monitor.words.internal.util.ExportExcel;
import cn.lonsun.monitor.words.internal.vo.MonitorSiteConfigVO;
import cn.lonsun.monitor.words.internal.vo.ParamDto;
import cn.lonsun.site.site.internal.entity.SiteMgrEO;
import cn.lonsun.site.site.internal.service.ISiteConfigService;
import cn.lonsun.site.template.util.ResponseData;
import cn.lonsun.monitor.words.internal.util.WordsSplitHolder;
import cn.lonsun.util.LoginPersonUtil;
import cn.lonsun.util.PropertiesHelper;
import cn.lonsun.webservice.vo.words.WordsVO;
import cn.lonsun.webservice.words.IWordsWebClient;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author chen.chao
 * @version 2017-09-21 17:22
 */
@Controller
@RequestMapping("/monitor/wordsSensitive")
public class WordsSensitiveController extends BaseController {


    private static final String FILE_BASE = "monitor/sensitive/";

    private static PropertiesHelper properties = SpringContextHolder.getBean("propertiesHelper");

    @Autowired
    private IWordsSensitiveService wordsSensitiveService;

    @Autowired
    private IWordsWebClient wordsWebClient;

    @Autowired
    private TaskExecutor taskExecutor;

    @Resource
    private ISiteConfigService siteConfigService;

    private Logger logger = org.slf4j.LoggerFactory.getLogger(this.getClass());

    @RequestMapping("/index")
    public String index() {
        return FILE_BASE + "/index";
    }

    @RequestMapping("/excel")
    public String excel(ModelMap map) {
        boolean superAdmin = LoginPersonUtil.isSuperAdmin();
        map.put("superAdmin", superAdmin);
        return FILE_BASE + "sensitive_excel";
    }

    @RequestMapping("/checkAll")
    public ModelAndView checkAll() {
        ModelAndView model = new ModelAndView(FILE_BASE + "sensitive_check_all");
        return model;
    }

    @RequestMapping("/addOrEdit")
    public String addOrEdit(ModelMap map) {
        boolean superAdmin = LoginPersonUtil.isSuperAdmin();
        map.put("superAdmin", superAdmin);
        return FILE_BASE + "/sensitive_edit";
    }

    @RequestMapping("/dealConfWin")
    public ModelAndView dealTypeWin(String opt, String record) {

        String obj = "";
        try {
            if (!AppUtil.isEmpty(record))
                obj = new String(record.getBytes("ISO-8859-1"), "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        ModelAndView model = new ModelAndView(FILE_BASE + "sensitive_conf_win");
        model.addObject("opt", opt);
        model.addObject("record", obj);
        return model;
    }

    @ResponseBody
    @RequestMapping("/getPageEOList")
    public Object getPageEOList(ParamDto paramDto) {
        paramDto.setSiteId(LoginPersonUtil.getSiteId());
        return wordsSensitiveService.getPageEOList(paramDto);
    }


    @ResponseBody
    @RequestMapping("/save")
    public Object save(WordsSensitiveEO eo) {
        if(eo.getWords().equals(eo.getReplaceWords())){
            return ResponseData.fail("敏感词和替换词不能相同！");
        }
        if (eo.getSiteId() == null || eo.getSiteId() != -1) {
            Long siteId = LoginPersonUtil.getSiteId();
            eo.setSiteId(siteId);
            SiteMgrEO siteMgrEO = siteConfigService.getById(siteId);
            eo.setSiteName(siteMgrEO.getName());
        }
        eo.setProvenance(WordsEasyerrEO.Provenance.Other.toString());
        //验证是否已存在
        WordsSensitiveEO exit = wordsSensitiveService.getCurSiteHas(eo.getSiteId(), eo.getWords());
        //不为空且id不相等，则说明重复
        if(exit != null && !exit.getId().equals(eo.getId())){
            return ResponseData.fail("敏感词已存在！");
        }
        try {
            if(eo.getId() != null && exit != null){
                BeanUtils.copyProperties(eo, exit);
                wordsSensitiveService.saveEO(exit);
            }else{
                wordsSensitiveService.saveEO(eo);
            }
        } catch (BaseRunTimeException e) {
            return ajaxErr(e.getMessage());
        }
        asyncThread();
        return ResponseData.success("保存成功!");
    }

    @ResponseBody
    @RequestMapping("/update")
    public Object update(WordsSensitiveEO eo) {
        //  wordsEasyErrConfService.updateEntity(eo);
        if (eo.getSiteId() == null || eo.getSiteId() != -1) {
            Long siteId = LoginPersonUtil.getSiteId();
            eo.setSiteId(siteId);
            SiteMgrEO siteMgrEO = CacheHandler.getEntity(SiteMgrEO.class, siteId);
            eo.setSiteName(siteMgrEO.getName());
        }
        eo.setProvenance(WordsEasyerrEO.Provenance.Other.toString());
        //验证是否已存在
        WordsSensitiveEO exit = wordsSensitiveService.getCurSiteHas(eo.getSiteId(), eo.getWords());
        //不为空且id不相等，则说明重复
        if(exit != null && !exit.getId().equals(eo.getId())){
            return ResponseData.fail("错词已存在！");
        }
        try {
            if(eo.getId() != null && exit != null){
                BeanUtils.copyProperties(eo, exit);
                wordsSensitiveService.updateEO(exit);
            }else{
                wordsSensitiveService.updateEO(eo);
            }
        } catch (BaseRunTimeException e) {
            return ajaxErr(e.getMessage());
        }
        asyncThread();
        return ResponseData.success("修改成功!");
    }


    @ResponseBody
    @RequestMapping("/delEO")
    public Object delEO(Long id) {
        wordsSensitiveService.delEO(id);
        asyncThread();
        return ResponseData.success("删除成功!");
    }

    @ResponseBody
    @RequestMapping("/delEOs")
    public Object delEOs(String ids) {
        String[] idstr = ids.split(",");

        for (int i = 0; i < idstr.length; i++) {
            Long id = Long.parseLong(idstr[i]);
            wordsSensitiveService.delEO(id);
        }
        asyncThread();
        return ResponseData.success("删除成功!");
    }

    @ResponseBody
    @RequestMapping("/upload")
    public Object uploadST(HttpServletRequest request, HttpServletResponse response,
                           @RequestParam(value = "file", required = false) MultipartFile file,
                           int rowFirst, int cellFirst, boolean rpl, boolean qj) {

        String msg = "导入成功!";
        if (file.getSize() == 0) {
            msg = "请选择导入文件!";
            return ajaxErr(msg);
        }
        try {
            String rst = ExcelManager.excel2db(file.getInputStream(), rowFirst, cellFirst, rpl, "sensitive", qj);
            if (rst != null) {
                return ajaxErr(rst);
            }
        } catch (Exception e) {

        }

        asyncThread();
        return ajaxOk(msg);
    }

    @RequestMapping("/import")
    public void importST(HttpServletRequest request, HttpServletResponse response, String keys, String keyValue) {
        String[] titles = new String[]{"敏感词", "替换词"};

        ParamDto paramDto = new ParamDto();
        paramDto.setKeys(keys);
        paramDto.setKeyValue(keyValue);
        paramDto.setSiteId(LoginPersonUtil.getSiteId());
        List<WordsSensitiveEO> list = wordsSensitiveService.getEOs(paramDto);

        List<Object> datas = new ArrayList<Object>();
        if (!AppUtil.isEmpty(list)) {
            for (WordsSensitiveEO eo : list) {
                WordsDto dto = new WordsDto();
                dto.setWords(eo.getWords());
                dto.setReplaceWords(eo.getReplaceWords());
                datas.add(dto);
            }
        }

        //导出
        String name = "敏感词(" + dateFormat(new Date()) + ")";
        try {
            String suffic = "xls";
            ExportExcel.exportExcel(name, suffic, titles, datas, response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequestMapping("/getImportTemplate")
    public void getImportTemplate(HttpServletRequest request, HttpServletResponse response){
        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("/importtmpl/sensitive.xlsx");
        java.io.BufferedInputStream bis = new BufferedInputStream(is);
        java.io.BufferedOutputStream bos = null;
        try {
            response.setContentType("application/x-msdownload;");
            response.setHeader("Content-disposition", "attachment; filename=" + URLEncoder.encode("敏感词导入模板.xlsx", "UTF-8"));
            bos = new BufferedOutputStream(response.getOutputStream());
            byte[] buff = new byte[2048];
            int bytesRead;
            while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
                bos.write(buff, 0, bytesRead);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseRunTimeException("获取模板失败");
        } finally {
            if (bis != null){
                try {
                    bis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (bos != null){
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return;
    }

    /**
     * 同步云平台词库
     *
     * @return
     */
    @ResponseBody
    @RequestMapping("getWordsSensitiveList")
    public Object getWordsSensitiveList(Long siteId) {
        if(siteId == null || siteId == 0){
            siteId = LoginPersonUtil.getSiteId();
        }
        //获取站点注册码
        MonitorSiteConfigVO siteConfigVO = wordsSensitiveService.getSiteRegisterInfo(null);
//        SiteConfigEO siteConfigEO = CacheHandler.getEntity(SiteConfigEO.class, CacheGroup.CMS_INDICATORID, siteId);
        if(siteConfigVO == null){
            return ajaxErr("未找到站点！");
        }
        if(siteConfigVO.getIsRegistered() == null || siteConfigVO.getIsRegistered() == 0 || StringUtils.isEmpty(siteConfigVO.getRegisteredCode())){
            return ajaxErr("站点尚未注册云监控服务，请先注册！");
        }
        List<WordsVO> list = wordsWebClient.getWordsSensitiveList(siteConfigVO.getSiteId(), siteConfigVO.getRegisteredCode());
        wordsSensitiveService.saveWords(list, -1l);
        asyncThread();
        return getObject(list);
    }


  /*  @ResponseBody
    @RequestMapping("/rplWords")
    public Object rplWords() {
        String text = "党治不了的.非典都能治 ";
        List<String> list = new ArrayList<String>();
        list.add("新安晚报");
        List<WordsSensitiveEO> str  = WordsSplitHolder.wordsCheck(text, Type.EASYERR.toString());
        return str;
    }*/

    /* @ResponseBody
     @RequestMapping("/synWords")
     public Object synWords() {
         try {
             InputStream is = FileUtil.getFileFromUrl(properties.getSynSensitiveUrl());
             String rst = ExcelManager.excel2db(is, 1, 1, true, false,"sensitive");
             if(rst != null) {
                 return ajaxErr(rst);
             }
         } catch (Exception e) {
             return ajaxErr("远程同步易错词异常!");
         }
         asyncThread();
         return ajaxOk("远程同步易错词成功!");
     }
 */
    private void asyncThread() {
        taskExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    WordsSplitHolder.synSensitiveDic();
                    SensitiveCache.refresh();
                    logger.info(">>>>>>>>>>>>>>>>数据同步成功!");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private String dateFormat(Date date) {
        SimpleDateFormat time = new SimpleDateFormat("yyyy.MM.dd");
        return time.format(date);
    }
}
