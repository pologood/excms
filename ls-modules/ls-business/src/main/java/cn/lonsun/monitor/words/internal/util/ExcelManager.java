package cn.lonsun.monitor.words.internal.util;

import cn.lonsun.cache.client.CacheGroup;
import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.core.util.SpringContextHolder;
import cn.lonsun.indicator.internal.entity.IndicatorEO;
import cn.lonsun.monitor.words.internal.entity.WordsEasyerrEO;
import cn.lonsun.monitor.words.internal.entity.WordsSensitiveEO;
import cn.lonsun.monitor.words.internal.service.IWordsEasyerrService;
import cn.lonsun.monitor.words.internal.service.IWordsSensitiveService;
import cn.lonsun.site.site.internal.entity.SiteMgrEO;
import cn.lonsun.util.LoginPersonUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *  支持各种版本excel
 * @author gu.fei
 * @version 2015-9-29 14:34
 */
public class ExcelManager {

    private static Workbook wb;
    private static Sheet sheet;

    private static IWordsSensitiveService wordsSensitiveService = SpringContextHolder.getBean("wordsSensitiveService");
    private static IWordsEasyerrService wordsEasyerrService = SpringContextHolder.getBean("wordsEasyerrService");

    public static String excel2db(InputStream is,int rowFirst,int cellFirst,boolean rpl, String type, boolean qj) throws Exception {
        try {
            wb = new WorkbookFactory().create(is);
        } catch(Exception e) {
            e.printStackTrace();
        }

        if("sensitive".equals(type)) {
            return runSensitive(rowFirst-1,cellFirst-1,rpl, qj);
        } else if("easyerr".equals(type)) {
            return runEasyerr(rowFirst-1,cellFirst-1,rpl, qj);
        }
        return null;
    }

    public static String runSensitive(int rowFirst,int cellFirst,boolean rpl, boolean qj) throws Exception {
        Long siteId = LoginPersonUtil.getSiteId();
        Map<String,Object> map = wordsSensitiveService.getMaps();
        if(!qj){
            SiteMgrEO mgr = CacheHandler.getEntity(SiteMgrEO.class, siteId);
            return runSiteSensitive(rowFirst, cellFirst, rpl, siteId, mgr.getName(),map);
        }else{
            List<IndicatorEO> list = CacheHandler.getList(IndicatorEO.class, CacheGroup.CMS_TYPE.toString(), IndicatorEO.Type.CMS_Site.toString());
            if(list != null && !list.isEmpty()) {
                for (IndicatorEO eo : list) {
                    String msg = runSiteSensitive(rowFirst, cellFirst, rpl, eo.getIndicatorId(), eo.getName(), map);
                    if (StringUtils.isNotEmpty(msg)) {
                        return "主站词库导入异常：" + msg;
                    }
                }
            }
            List<IndicatorEO> list1 = CacheHandler.getList(IndicatorEO.class, CacheGroup.CMS_TYPE.toString(), IndicatorEO.Type.SUB_Site.toString());
            if(list1 != null && !list1.isEmpty()) {
                for (IndicatorEO eo : list1) {
                    String msg = runSiteSensitive(rowFirst, cellFirst, rpl, eo.getIndicatorId(), eo.getName(), map);
                    if (StringUtils.isNotEmpty(msg)) {
                        return "子站词库导入异常：" + msg;
                    }
                }
            }
            return null;
        }
    }

    public static String runEasyerr(int rowFirst,int cellFirst,boolean rpl, boolean qj) throws Exception {
        Long siteId = LoginPersonUtil.getSiteId();
        Map<String, Object> map = wordsEasyerrService.getMaps();
        if(!qj){
            SiteMgrEO mgr = CacheHandler.getEntity(SiteMgrEO.class, siteId);
            return runSiteEasyerr(rowFirst, cellFirst, rpl, siteId, mgr.getName(),map);
        }else{
            List<IndicatorEO> list = CacheHandler.getList(IndicatorEO.class, CacheGroup.CMS_TYPE.toString(), IndicatorEO.Type.CMS_Site.toString());
            if(list != null && !list.isEmpty()){
                for(IndicatorEO eo : list){
                    String msg =  runSiteEasyerr(rowFirst, cellFirst, rpl, eo.getIndicatorId(), eo.getName(),map);
                    if(StringUtils.isNotEmpty(msg)){
                        return "主站词库导入异常：" + msg;
                    }
                }
            }
            List<IndicatorEO> list1 = CacheHandler.getList(IndicatorEO.class, CacheGroup.CMS_TYPE.toString(), IndicatorEO.Type.SUB_Site.toString());
            if(list1 != null && !list1.isEmpty()) {
                for (IndicatorEO eo : list1) {
                    String msg = runSiteEasyerr(rowFirst, cellFirst, rpl, eo.getIndicatorId(), eo.getName(), map);
                    if (StringUtils.isNotEmpty(msg)) {
                        return "子站词库导入异常：" + msg;
                    }
                }
            }
            return null;
        }
    }

    public static String runSiteSensitive(int rowFirst,int cellFirst,boolean rpl, Long siteId, String siteName, Map<String,Object> map) throws Exception {
        String msg = null;
        List<WordsSensitiveEO> addlist = new ArrayList<WordsSensitiveEO>();
        List<WordsSensitiveEO> dellist = new ArrayList<WordsSensitiveEO>();
        sheet = wb.getSheetAt(0);
        int rowSize = sheet.getLastRowNum();
        if(rowSize < 0 || rowFirst > rowSize) {
            return "未读取到相关数据";
        }
        boolean lastIsEmpty = false;
        for(int i = rowFirst; i <= rowSize; i++) {
            Row row = sheet.getRow(i);
            if(row == null) {
                continue;
            }

            Cell cellF = row.getCell(cellFirst);
            Cell cellS = row.getCell(cellFirst + 1);
            Cell cell3 = row.getCell(cellFirst + 2);

            if(cellF == null || cellS == null) {
                //如果连续的两行错词为空，则认为是结束
                if(lastIsEmpty){
                    break;
                }
                lastIsEmpty = true;
                continue;
            }
            String key = getCellValue(cellF);
            //如果连续的两行错词为空，则认为是结束
            if(cn.lonsun.core.base.util.StringUtils.isEmpty(key)){
                if(lastIsEmpty){
                    break;
                }
                lastIsEmpty = true;
                continue;
            }
            lastIsEmpty = false;
            WordsSensitiveEO weEO = new WordsSensitiveEO();
            weEO.setWords(key);
            weEO.setReplaceWords(getCellValue(cellS));
            weEO.setSiteName(siteName);
            weEO.setSiteId(siteId);
            weEO.setProvenance(WordsSensitiveEO.Provenance.Other.toString());
            if(StringUtils.isEmpty(weEO.getWords()) || StringUtils.isEmpty(weEO.getReplaceWords())){
                return "第 " +(i + 1) +" 行敏感词或替换字为空";
            }
            if(weEO.getWords().equals(weEO.getReplaceWords())){
                return "第 " +(i + 1) +" 行敏感词和替换字相同";
            }
            //平台中存的是相反的值，0表示严重错误，1表示非严重错误
            String seriousErr = getCellValueNumber(cell3);
            if(cn.lonsun.core.base.util.StringUtils.isEmpty(seriousErr)){
                //默认非严重错误
                weEO.setSeriousErr(1);
            }else{
                weEO.setSeriousErr(Integer.valueOf(seriousErr).intValue() == 0?1:0);
            }
            boolean exist = map.containsKey(siteId.toString() + "_" + key);
            if(rpl && exist) {
                dellist.add((WordsSensitiveEO)map.get(siteId.toString() + "_" + key));
                addlist.add(weEO);
            }else if(!rpl && exist){
                continue;
            }else{
                addlist.add(weEO);
            }
        }
        wordsSensitiveService.deleteByWords(dellist, siteId);
        wordsSensitiveService.saveEntities(addlist);
        return msg;
    }

    public static String runSiteEasyerr(int rowFirst,int cellFirst,boolean rpl, Long siteId, String siteName, Map<String,Object> map) throws Exception {
        String msg = null;
        List<WordsEasyerrEO> addlist = new ArrayList<WordsEasyerrEO>();
        List<WordsEasyerrEO> dellist = new ArrayList<WordsEasyerrEO>();

        sheet = wb.getSheetAt(0);
        int rowSize = sheet.getLastRowNum();
        if(rowSize < 0 || rowFirst > rowSize) {
            return "未读取到相关数据";
        }
        boolean lastIsEmpty = false;
        for(int i = rowFirst; i <= rowSize; i++) {
            Row row = sheet.getRow(i);
            if(row == null) {
                continue;
            }
            Cell cellF = row.getCell(cellFirst);
            Cell cellS = row.getCell(cellFirst + 1);
            Cell cell3 = row.getCell(cellFirst + 2);
            if(cellF == null || cellS == null) {
                //如果连续的两行错词为空，则认为是结束
                if(lastIsEmpty){
                    break;
                }
                lastIsEmpty = true;
                continue;
            }
            String key = getCellValue(cellF);
            //如果连续的两行错词为空，则认为是结束
            if(cn.lonsun.core.base.util.StringUtils.isEmpty(key)){
                if(lastIsEmpty){
                    break;
                }
                lastIsEmpty = true;
                continue;
            }
            lastIsEmpty = false;
            WordsEasyerrEO weEO = new WordsEasyerrEO();
            weEO.setWords(key);
            weEO.setReplaceWords(getCellValue(cellS));
            weEO.setSiteName(siteName);
            weEO.setSiteId(siteId);
            weEO.setProvenance(WordsSensitiveEO.Provenance.Other.toString());
            if(StringUtils.isEmpty(weEO.getWords()) || StringUtils.isEmpty(weEO.getReplaceWords())){
                return "第 " +(i + 1) +" 行错词或替换字为空";
            }
            if(weEO.getWords().equals(weEO.getReplaceWords())){
                return "第 " +(i + 1) +" 行错词和替换字相同";
            }
            //平台中存的是相反的值，0表示严重错误，1表示非严重错误
            String seriousErr = getCellValueNumber(cell3);
            if(cn.lonsun.core.base.util.StringUtils.isEmpty(seriousErr)){
                //默认非严重错误
                weEO.setSeriousErr(1);
            }else{
                weEO.setSeriousErr(Integer.valueOf(seriousErr).intValue() == 0?1:0);
            }
            boolean exist = map.containsKey(siteId.toString() + "_" + key);
            if(rpl && exist) {
                dellist.add((WordsEasyerrEO)map.get(siteId.toString() + "_" + key));
                addlist.add(weEO);
            }else if(!rpl && exist){
                continue;
            }else{
                addlist.add(weEO);
            }
        }
        wordsEasyerrService.deleteByWords(dellist, siteId);
        wordsEasyerrService.saveEntities(addlist);
        return msg;
    }

    public static void close() throws IOException {
    }

    private static String getCellValue(Cell cell) throws Exception{
        if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
            return cell.getRichStringCellValue().getString();
        }
        else if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
            return Double.toString(cell.getNumericCellValue());
        }
        return null;
    }

    private static String getCellValueNumber(Cell cell) throws Exception{
        if(cell == null){
            return null;
        }
        if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
            return cell.getRichStringCellValue().getString();
        }
        else if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
            return String.valueOf((int)cell.getNumericCellValue());
        }
        return null;
    }
}
