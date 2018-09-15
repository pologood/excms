package cn.lonsun.util;

import com.mongodb.gridfs.GridFSDBFile;
import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.util.NumberToTextConverter;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author gu.fei
 * @version 2017-08-24 17:13
 */
public class FileUtil {

    /**
     * 获取mingdb中文件文本内容
     * @param file
     * @return
     */
    public static String getFileString(GridFSDBFile file) {
        String string = null;
        if(null != file && null != file.getInputStream()) {
            String type = file.getContentType();
            if(type.equalsIgnoreCase(FileType.doc.toString())) {
                try {
                    WordExtractor wordExtractor = new WordExtractor(file.getInputStream());
                    string = wordExtractor.getText();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if(type.equalsIgnoreCase(FileType.docx.toString())) {
                try {
                    XWPFDocument doc = new XWPFDocument(file.getInputStream());
                    XWPFWordExtractor wordExtractor = new XWPFWordExtractor(doc);
                    string = wordExtractor.getText();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if(type.equalsIgnoreCase(FileType.xls.toString())) {
                StringBuffer content = new StringBuffer();
                HSSFWorkbook workbook = null;//创建对Excel工作簿文件的引用
                try {
                    workbook = new HSSFWorkbook(file.getInputStream());
                    for (int numSheets = 0; numSheets < workbook.getNumberOfSheets(); numSheets++) {
                        if (null != workbook.getSheetAt(numSheets)) {
                            HSSFSheet aSheet = workbook.getSheetAt(numSheets);//获得一个sheet
                            for (int rowNumOfSheet = 0; rowNumOfSheet <= aSheet.getLastRowNum(); rowNumOfSheet++) {
                                if (null != aSheet.getRow(rowNumOfSheet)) {
                                    HSSFRow aRow = aSheet.getRow(rowNumOfSheet); //获得一个行
                                    for (short cellNumOfRow = 0; cellNumOfRow <= aRow.getLastCellNum(); cellNumOfRow++) {
                                        if (null != aRow.getCell(cellNumOfRow)) {
                                            HSSFCell aCell = aRow.getCell(cellNumOfRow);//获得列值
                                            content.append(getCellValue(aCell));
                                        }
                                    }
                                }
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                string = content.toString();
            } else if(type.equalsIgnoreCase(FileType.xlsx.toString())) {
                StringBuffer content = new StringBuffer();
                XSSFWorkbook workbook = null;//创建对Excel工作簿文件的引用
                try {
                    workbook = new XSSFWorkbook(file.getInputStream());
                    for (int numSheets = 0; numSheets < workbook.getNumberOfSheets(); numSheets++) {
                        if (null != workbook.getSheetAt(numSheets)) {
                            XSSFSheet aSheet = workbook.getSheetAt(numSheets);//获得一个sheet
                            for (int rowNumOfSheet = 0; rowNumOfSheet <= aSheet.getLastRowNum(); rowNumOfSheet++) {
                                if (null != aSheet.getRow(rowNumOfSheet)) {
                                    XSSFRow aRow = aSheet.getRow(rowNumOfSheet); //获得一个行
                                    for (short cellNumOfRow = 0; cellNumOfRow <= aRow.getLastCellNum(); cellNumOfRow++) {
                                        if (null != aRow.getCell(cellNumOfRow)) {
                                            XSSFCell aCell = aRow.getCell(cellNumOfRow);//获得列值
                                            content.append(getCellValue(aCell));
                                        }
                                    }
                                }
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                string = content.toString();
            } else if(type.equalsIgnoreCase(FileType.pdf.toString())) {
                COSDocument cosDoc = null;
                try {
                    cosDoc = parseDocument(file.getInputStream());
                } catch (IOException e) {
                    closeCOSDocument(cosDoc);
                    e.printStackTrace();
                }
                if (cosDoc.isEncrypted()) {
                    if (cosDoc != null)
                        closeCOSDocument(cosDoc);
                }
                try {
                    PDFTextStripper stripper = new PDFTextStripper();
                    string = stripper.getText(new PDDocument(cosDoc));
                } catch (IOException e) {
                    closeCOSDocument(cosDoc);
                    e.printStackTrace();
                }
            }
        }

        return string;
    }

    private static COSDocument parseDocument(InputStream is) throws IOException {
        PDFParser parser = new PDFParser(is);
        parser.parse();
        return parser.getDocument();
    }


    private static void closeCOSDocument(COSDocument cosDoc) {
        if (cosDoc != null) {
            try {
                cosDoc.close();
            } catch (IOException e) {
            }
        }
    }

    /**
     * 关闭
     * @param pdDoc
     */
    private static void closePDDocument(PDDocument pdDoc) {
        if (pdDoc != null) {
            try {
                pdDoc.close();
            } catch (IOException e) {
            }
        }
    }

    /**
     * 根据excel单元格类型获取excel单元格值
     * @param cell
     * @return
     */
    private static String getCellValue(Cell cell) {
        String cellvalue = "";
        if (cell != null) {
            // 判断当前Cell的Type
            switch (cell.getCellType()) {
                // 如果当前Cell的Type为NUMERIC
                case HSSFCell.CELL_TYPE_NUMERIC: {
                    short format = cell.getCellStyle().getDataFormat();
                    if(format == 14 || format == 31 || format == 57 || format == 58){   //excel中的时间格式
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        double value = cell.getNumericCellValue();
                        Date date = org.apache.poi.ss.usermodel.DateUtil.getJavaDate(value);
                        cellvalue = sdf.format(date);
                    }
                    // 判断当前的cell是否为Date
                    else if (HSSFDateUtil.isCellDateFormatted(cell)) {  //先注释日期类型的转换，在实际测试中发现HSSFDateUtil.isCellDateFormatted(cell)只识别2014/02/02这种格式。
                        // 如果是Date类型则，取得该Cell的Date值           // 对2014-02-02格式识别不出是日期格式
                        Date date = cell.getDateCellValue();
                        DateFormat formater = new SimpleDateFormat("yyyy-MM-dd");
                        cellvalue= formater.format(date);
                    } else { // 如果是纯数字
                        // 取得当前Cell的数值
                        cellvalue = NumberToTextConverter.toText(cell.getNumericCellValue());

                    }
                    break;
                }
                // 如果当前Cell的Type为STRIN
                case Cell.CELL_TYPE_STRING:
                    // 取得当前的Cell字符串
                    cellvalue = cell.getStringCellValue().replaceAll("'", "''");
                    break;
                case  Cell.CELL_TYPE_BLANK:
                    cellvalue = null;
                    break;
                // 默认的Cell值
                default:{
                    cellvalue = " ";
                }
            }
        } else {
            cellvalue = "";
        }
        return cellvalue;
    }

    /**
     * 文件类型
     */
    public enum FileType {
        doc,
        docx,
        xls,
        xlsx,
        pdf
    }
}
