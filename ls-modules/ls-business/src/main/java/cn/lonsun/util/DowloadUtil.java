package cn.lonsun.util;

import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.content.vo.GuestBookEditVO;
import cn.lonsun.indicator.internal.entity.IndicatorEO;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.usermodel.Range;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <br/>
 *
 * @author chenchao <br/>
 * @version v1.0 <br/>
 * @date 2016-9-6<br/>
 */

public class DowloadUtil {
    public static byte[] Write(GuestBookEditVO vo) throws Exception {
        String templatePath = null;
        if(null == vo.getRecType()) {
            templatePath = "/guest/template3.doc";
        }else if(vo.getRecType()==0){
            templatePath = "/guest/template2.doc";
        }else if(vo.getRecType()==1){
//            if(vo.getIsTurn()!=null&&vo.getIsTurn()==1){
//                templatePath = "/guest/template1.doc";
//            }else{
//                templatePath = "/guest/template.doc";
//            }
            templatePath = "/guest/template.doc";
        }
        InputStream is=null;
        Resource res = new ClassPathResource(templatePath);
        try {
            is= res.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        HWPFDocument doc = new HWPFDocument(is);
        Range range = doc.getRange();

        //把range范围内的${addDate}替换为当前的日期
        Date addDate = vo.getAddDate();
        String date = new SimpleDateFormat("yyyy-MM-dd").format(addDate);
        IndicatorEO indicatorEO=CacheHandler.getEntity(IndicatorEO.class,vo.getColumnId());
        String columnName=null;
        if(indicatorEO!=null){
            columnName=indicatorEO.getName();
        }
        range.replaceText("${columnName}", null == columnName? "" : columnName);
        range.replaceText("${className}", null == vo.getClassName() ? "" : vo.getClassName());
        range.replaceText("${addDate}", null == date ? "" : date);
        range.replaceText("${docNum}", null == vo.getDocNum() ? "" : vo.getDocNum());
        range.replaceText("${title}", null == vo.getTitle()? "" : vo.getTitle());
        if(null != vo.getRecType() && vo.getRecType()==1){
//            if(null!=vo.getIsTurn()&&vo.getIsTurn()==1){
//                range.replaceText("${replyUnitName}", null == vo.getReplyUnitName() ? "" : vo.getReplyUnitName());
//            }else{
//                range.replaceText("${receiveUserName}", null == vo.getReceiveUserName() ? "" : vo.getReceiveUserName());
//            }
            range.replaceText("${receiveUserName}", null == vo.getReceiveUserName() ? "" : vo.getReceiveUserName());
        }
        if( null != vo.getRecType() && vo.getRecType()==0) {
            range.replaceText("${receiveName}", null == vo.getReceiveName() ? "" : vo.getReceiveName());
        }
        range.replaceText("${personName}", null == vo.getPersonName() ? "" : vo.getPersonName());
        range.replaceText("${personPhone}", null == vo.getPersonPhone() ? "" : vo.getPersonPhone().toString());
        //去除样式
        StringBuffer sb = new StringBuffer();
        if (!StringUtils.isEmpty(vo.getGuestBookContent())) {
            Pattern pattern = Pattern.compile("<([^>]*)>");
            Matcher matcher = pattern.matcher(vo.getGuestBookContent());
            boolean result1 = matcher.find();
            while (result1) {
                matcher.appendReplacement(sb, "");
                result1 = matcher.find();
            }
            matcher.appendTail(sb);
        }
        range.replaceText("${guestBookContent}",null == vo.getGuestBookContent() ? "" : sb.toString());
        //去除样式
        StringBuffer sb1 = new StringBuffer();
        if (!StringUtils.isEmpty(vo.getResponseContent())) {
            Pattern pattern = Pattern.compile("<([^>]*)>");
            Matcher matcher = pattern.matcher(vo.getResponseContent());
            boolean result1 = matcher.find();
            while (result1) {
                matcher.appendReplacement(sb1, "");
                result1 = matcher.find();
            }
            matcher.appendTail(sb1);
        }
        range.replaceText("${responseContent}", null == vo.getResponseContent() ? "" : sb1.toString());
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        //把doc输出到输出流中
        doc.write(os);
        closeStream(os);
        closeStream(is);
        return os.toByteArray();
    }

    /**
     * 关闭输入流
     * @param is
     */
    private static void closeStream(InputStream is) {
        if (is != null) {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 关闭输出流
     * @param os
     */
    private static void closeStream(OutputStream os) {
        if (os != null) {
            try {
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
