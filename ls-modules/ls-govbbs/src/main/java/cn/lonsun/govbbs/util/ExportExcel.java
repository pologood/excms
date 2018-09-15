package cn.lonsun.govbbs.util;

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddressList;

import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by lonsun on 2016-11-1.
 */
public class ExportExcel<T> {
    public   void exportExcel(String title, String[] colums, String[] headers,
                              List<T> objList,HttpServletResponse response
    ){

        // 判断有多少个sheet
        int totalNum = objList.size();
        int everyNum = 5000;
        int sheetNum = 1;
        int beginNum = 0;
        int endNum = 0;
        List<T> tmpList = null;
        sheetNum = totalNum % everyNum == 0 ? totalNum / everyNum : (totalNum
                / everyNum + 1);
        sheetNum = sheetNum == 0 ? 1 : sheetNum;
        try {
            OutputStream out = response.getOutputStream();
            // 声明一个工作薄
            HSSFWorkbook workbook = new HSSFWorkbook();
            for (int j = 0; j < sheetNum; j++) {
                beginNum = j * everyNum;
                endNum = (j + 1) * everyNum > totalNum ? totalNum : (j + 1)
                        * everyNum;
                tmpList = objList.subList(beginNum, endNum);
                // 生成一个表格
                HSSFSheet sheet = workbook.createSheet(title + "_"
                        + (j == 0 ? "" : j));
                // 设置表格默认列宽度为15个字节
                sheet.setDefaultColumnWidth(25);
//                sheet.addMergedRegion(new Region(0,(short) 0, 0 ,(short)(headers.length-1)));
                sheet.addMergedRegion(new CellRangeAddress((short) 0,(short) 0, (short) 0, (short) (headers.length-1)));

                // 生成一个样式
                HSSFCellStyle style = workbook.createCellStyle();
                // 设置这些样式
                style.setAlignment(HSSFCellStyle.ALIGN_CENTER);


                //标题
                HSSFRow row = sheet.createRow(0);
                HSSFCell cell = row.createCell(0);
                cell.setCellValue(title);
                cell.setCellStyle(style);
                //列名
                row = sheet.createRow(1);
                for(int i=0;i<headers.length;i++){
                    cell = row.createCell(i);
                    cell.setCellValue(headers[i]);
                    cell.setCellStyle(style);

                }
                // 遍历集合数据，产生数据行
                int index = 2;
                for( T t : objList){
                    row = sheet.createRow(index);
                    for (int i = 0; i < colums.length; i++) {
                        cell = row.createCell(i);
                        cell.setCellStyle(style);
                        String fieldName = colums[i];
                        String getMethodName = "get"
                                + fieldName.substring(0, 1).toUpperCase()
                                + fieldName.substring(1);


                        Class tCls = t.getClass();
                        Method getMethod = tCls.getMethod(getMethodName,
                                new Class[]{});
                        Object value = getMethod.invoke(t, new Object[]{});
                        value = value == null ? "" : value;
                        cell.setCellValue(value.toString());

                    }
                    index++;
                }

            }
            workbook.write(out);
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }





    }


}
