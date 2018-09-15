package cn.lonsun.staticcenter.controller;

import cn.lonsun.core.base.controller.BaseController;
import com.aspose.words.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by lonsun on 2016-9-23.
 *
 */
@Controller
@RequestMapping( "/pageDom")
public class PageDomController extends BaseController {
    static{
        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("license.xml");
        License aposeLic = new License();
        try {
            aposeLic.setLicense(is);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * 下载页面内容
     * @param title
     * @param content
     * @param response
     */
    @RequestMapping(value = "downWord",method = RequestMethod.POST)
    public void downWord(String title,String content,HttpServletResponse response){
        content = content == null?"":content;
        String fileName = (title==null || "".equals(title.trim()))?"page":title;
        fileName = "page";
        response.setContentType("application/msword");
        response.setHeader("Content-Disposition", "attachment;fileName=".concat(fileName).concat(".doc"));
        try {
            content = content.replaceAll("</?[^>]+>", ""); //剔出<html>的标签
           OutputStream os = response.getOutputStream();
            try {
                Document doc = new Document();
                DocumentBuilder builder = new DocumentBuilder(doc);
                builder.insertHtml(content);
                doc.save(os, SaveOptions.createSaveOptions(SaveFormat.DOC));
            } catch (Exception e) {
                e.printStackTrace();
            }
           /*

            ByteArrayInputStream bais = new ByteArrayInputStream(b);
            POIFSFileSystem poifs = new POIFSFileSystem();
            DirectoryEntry directory = poifs.getRoot();
            DocumentEntry documentEntry = directory.createDocument("WordDocument", bais);

            poifs.writeFilesystem(os);
            bais.close();*/
            os.flush();
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequestMapping("test")
    public void test(){
        String aa = "downWord.test";

        System.out.println(aa);
    }



}
