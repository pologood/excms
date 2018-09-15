package cn.lonsun.monitor.util;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.Version;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * @author gu.fei
 * @version 2017-12-04 15:30
 */
public class FreeMakerUtil {

    private static Configuration configuration = null;

    static{
        configuration = new Configuration(new Version("2.3.23"));
        //设置编码
        configuration.setDefaultEncoding("UTF-8");
        //ftl模板文件统一放至包下面
        try {
            configuration.setDirectoryForTemplateLoading(new File(FreeMakerUtil.class.getResource("/").getPath() + "tpl"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Configuration getInstance() {
        return configuration;
    }

    /**
     * @param dataMap      word中需要展示的动态数据，用map集合来保存
     * @param templateName word模板名称，例如：test.ftl
     * @param filePath     文件生成的目标路径，例如：D:/wordFile/
     * @param fileName     生成的文件名称，例如：test.doc
     * @Desc：生成word文件
     */
    public static void createDocument(Map dataMap, String templateName, String filePath, String fileName) {
        try {
            //获取模板
            Template template = FreeMakerUtil.getInstance().getTemplate(templateName);
            //输出文件
            File outFile = new File(filePath + File.separator + fileName);
            //如果输出目标文件夹不存在，则创建
            if (!outFile.getParentFile().exists()) {
                outFile.getParentFile().mkdirs();
            }
            //将模板和数据模型合并生成文件
            Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outFile), "UTF-8"));
            //生成文件
            template.process(dataMap, out);
            //关闭流
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static File createDoc(Map<?, ?> dataMap, Template template) {
        String name =  "sellPlan.doc";
        File f = new File(name);
        Template t = template;
        try {
            // 这个地方不能使用FileWriter因为需要指定编码类型否则生成的Word文档会因为有无法识别的编码而无法打开
            Writer w = new OutputStreamWriter(new FileOutputStream(f), "utf-8");
            t.process(dataMap, w);
            w.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
        return f;
    }

    /**
     * 导出word文档
     * @param dataMap      word中需要展示的动态数据，用map集合来保存
     * @param templateName word模板名称，例如：test.ftl
     * @param response
     * @param fileName     生成的文件名称，例如：test.doc
     * @Desc：生成word文件
     */
    public static void createDocument(HttpServletResponse response, Map dataMap, String templateName, String fileName,String suffix) {
        try {
            File file = null;
            InputStream fin = null;
            ServletOutputStream out = null;
            try {
                //获取模板
                Template template = FreeMakerUtil.getInstance().getTemplate(templateName);
                // 调用工具类的createDoc方法生成Word文档
                file = createDoc(dataMap,template);
                fin = new FileInputStream(file);
                response.setCharacterEncoding("utf-8");
                response.setContentType("application/msword");
                // 设置浏览器以下载的方式处理该文件名
                fileName = fileName + suffix;
                response.setHeader("Content-Disposition", "attachment;filename="
                        .concat(String.valueOf(new String(fileName.getBytes(), "ISO-8859-1"))));

                out = response.getOutputStream();
                byte[] buffer = new byte[512];  // 缓冲区
                int bytesToRead = -1;
                // 通过循环将读入的Word文件的内容输出到浏览器中
                while((bytesToRead = fin.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesToRead);
                }
            } finally {
                if(fin != null) fin.close();
                if(out != null) out.close();
                if(file != null) file.delete(); // 删除临时文件
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Map<String,Object> map = new HashMap<String, Object>();
        map.put("siteName","测试1121223");
        map.put("monitorResult","111");
        map.put("veto","111");
        map.put("score","111");
        map.put("siteVisitDesc","111");
        map.put("siteUpdateDesc","111");
        map.put("columnUpdateDesc","111");
        map.put("seriousErrorDesc","111");
        map.put("replyDesc","111");

        map.put("siteVisitVeto","111");
        map.put("siteUpdateVeto","111");
        map.put("columnUpdateVeto","111");
        map.put("seriousErrorVeto","111");
        map.put("replyVeto","111");

        map.put("indexUseDesc","111");
        map.put("linkUseableDesc","111");
        map.put("indexColumnDesc","111");
        map.put("baseInfoDesc","111");
        map.put("zwzxDesc","111");
        map.put("dczjDesc","111");
        map.put("hdftDesc","111");
        map.put("indexUseScore","111");
        map.put("linkUseableScore","111");
        map.put("indexColumnScore","111");
        map.put("baseInfoScore","111");
        map.put("zwzxScore","111");
        map.put("dczjScore","111");
        map.put("hdftScore","111");
        FreeMakerUtil.createDocument(map, "monitor_report.ftl","D:\\","test11.doc");
    }
}
