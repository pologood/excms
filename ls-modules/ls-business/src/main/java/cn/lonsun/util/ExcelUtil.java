/*
 * ExcelUtil.java         2016年1月29日 <br/>
 *
 * Copyright (c) 1994-1999 AnHui LonSun, Inc. <br/>
 * All rights reserved.	<br/>
 *
 * This software is the confidential and proprietary information of AnHui	<br/>
 * LonSun, Inc. ("Confidential Information").  You shall not	<br/>
 * disclose such Confidential Information and shall use it only in	<br/>
 * accordance with the terms of the license agreement you entered into	<br/>
 * with Sun. <br/>
 */

package cn.lonsun.util;

import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.util.TipsMode;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.Map.Entry;

/**
 * excel解析工具类，默认第一个 <br/>
 *
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 * @date 2016年1月29日 <br/>
 */
public class ExcelUtil {

    // 日志
    private static final Logger logger = LoggerFactory.getLogger(ExcelUtil.class);

    /**
     * 读取excel内容，
     *
     * @param clazz
     * @param is
     * @param map
     * @param startRow
     * @return
     */
    public static <X> Map<String, List<X>> readExcel(Class<X> clazz, String fileName, InputStream is, Map<Integer, String> map, int startRow) {
        if (null == map || map.isEmpty() || startRow < 0) {
            return Collections.emptyMap();
        }
        boolean is2007 = false;//是否2007版本
        Workbook workbook = null;
        String fileType = fileName.substring(fileName.lastIndexOf(".") + 1);
        Map<String, List<X>> resultMap = new HashMap<String, List<X>>();
        try {
            if ("xls".equalsIgnoreCase(fileType)) {
                workbook = new HSSFWorkbook(is);
            } else if ("xlsx".equalsIgnoreCase(fileType)) {
                is2007 = true;
                workbook = new XSSFWorkbook(is);
            } else {
                return Collections.emptyMap();
            }
            int sheetIndex = workbook.getNumberOfSheets();
            for (int i = 0; i < sheetIndex; i++) {
                Sheet sheet = workbook.getSheetAt(i);
                if (null == sheet) {
                    continue;
                }
                List<X> resultList = processSheet(clazz, sheet, map, startRow);
                if (null != resultList && !resultList.isEmpty()) {
                    resultMap.put(sheet.getSheetName(), resultList);
                }
            }
        } catch (Throwable e) {
            logger.error("解析excel报错！", e);
            throw new BaseRunTimeException(TipsMode.Message.toString(), "解析excel报错！");
        } finally {
            if (null != workbook && !is2007) {
                try {
                    workbook.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return resultMap;
    }

    private static <X> List<X> processSheet(Class<X> clazz, Sheet sheet, Map<Integer, String> map, int startRow) throws Exception {
        List<X> resultList = new ArrayList<X>();
        for (int rowNum = startRow; rowNum <= sheet.getLastRowNum(); rowNum++) {
            Row row = sheet.getRow(rowNum);
            if (null == row) {
                continue;
            }
            X x = clazz.newInstance();// 创建对象
            for (Entry<Integer, String> entry : map.entrySet()) {
                Cell cell = row.getCell(entry.getKey());
                if (null == cell) {
                    continue;
                }
                String field = entry.getValue();
                BeanUtils.setProperty(x, field, getValue(cell));// 设置值
            }
            resultList.add(x);
        }
        return resultList;
    }

    /**
     * 获取值
     *
     * @param cell
     * @return
     * @author fangtinghua
     */
    private static String getValue(Cell cell) {
        if (cell.getCellType() == Cell.CELL_TYPE_BOOLEAN) {
            // 返回布尔类型的值
            return String.valueOf(cell.getBooleanCellValue());
        } else if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
            if (HSSFDateUtil.isCellDateFormatted(cell)) {
                return HSSFDateUtil.getJavaDate(cell.getNumericCellValue()).toString();
            }
            cell.setCellType(HSSFCell.CELL_TYPE_STRING);//设置为字符串类型
        }
        return cell.getStringCellValue();// 返回字符串类型的值
    }
}