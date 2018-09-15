package cn.lonsun.util;

import cn.lonsun.mongodb.vo.MongoFileVO;
import cn.lonsun.system.filecenter.internal.entity.FileCenterEO;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.poi.hslf.usermodel.*;
import org.apache.poi.xslf.usermodel.*;


import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public class POIReadPPTToHtml {


	private static String mongoId = "";

	public static String convertPptHtml(InputStream is,final Long siteId,final Long columnId,final String downLoadIp,final HttpServletRequest request)
			throws TransformerException, IOException,    
			ParserConfigurationException {
		String html = new String();
		try {
			HSLFSlideShow ppt = new HSLFSlideShow(is);

			Dimension pgsize = ppt.getPageSize();
			for (int i = 0; i < ppt.getSlides().size(); i++) {
				//防止中文乱码
				for(HSLFShape shape : ppt.getSlides().get(i).getShapes()){
					if(shape instanceof HSLFTextShape) {
						HSLFTextShape tsh = (HSLFTextShape)shape;
						for(HSLFTextParagraph p : tsh.getTextParagraphs()){
							for(HSLFTextRun r : p.getTextRuns()){
								r.setFontFamily("宋体");
							}
						}
					}
				}
				BufferedImage img = new BufferedImage(pgsize.width, pgsize.height, BufferedImage.TYPE_INT_RGB);
				Graphics2D graphics = img.createGraphics();
				graphics.setPaint(Color.white);
				graphics.fill(new Rectangle2D.Float(0, 0, pgsize.width, pgsize.height));

				//渲染
				ppt.getSlides().get(i).draw(graphics);

				//将转换的图片保存到数据库
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				javax.imageio.ImageIO.write(img, "png", out);
				MongoFileVO mgvo = FileUploadUtil.editorUpload(out.toByteArray(), i+".jpg", FileCenterEO.Type.EditorUpload.toString(), FileCenterEO.Code.EditorAttach.toString(), siteId, columnId, null, "编辑器上传附件", request);
				html += "<p><img src=\""+downLoadIp + mgvo.getMongoId()+"\"  /> </p>";
				out.close();

			}
			System.out.println("3success");
		} catch (Exception e) {
			// TODO: handle exception
		}
		return html;
	}

	public static String convertPptxHtml(InputStream is,final Long siteId,final Long columnId,final String downLoadIp,final HttpServletRequest request)
			throws TransformerException, IOException,
			ParserConfigurationException {
		String html = "";
		XMLSlideShow ppt = new XMLSlideShow(is);
		is.close();

		Dimension pgsize = ppt.getPageSize();
		System.out.println(pgsize.width+"--"+pgsize.height);

		for (int i = 0; i < ppt.getSlides().size(); i++) {
			try {
				//防止中文乱码
				for(XSLFShape shape : ppt.getSlides().get(i).getShapes()){
					if(shape instanceof XSLFTextShape) {
						XSLFTextShape tsh = (XSLFTextShape)shape;
						for(XSLFTextParagraph p : tsh){
							for(XSLFTextRun r : p){
								r.setFontFamily("宋体");
							}
						}
					}
				}
				BufferedImage img = new BufferedImage(pgsize.width, pgsize.height, BufferedImage.TYPE_INT_RGB);
				Graphics2D graphics = img.createGraphics();
				graphics.setPaint(Color.white);
				graphics.fill(new Rectangle2D.Float(0, 0, pgsize.width, pgsize.height));

				//渲染
				ppt.getSlides().get(i).draw(graphics);

				//将转换的图片保存到数据库
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				javax.imageio.ImageIO.write(img, "png", out);
				MongoFileVO mgvo = FileUploadUtil.editorUpload(out.toByteArray(), i+".jpg", FileCenterEO.Type.EditorUpload.toString(), FileCenterEO.Code.EditorAttach.toString(), siteId, columnId, null, "编辑器上传附件", request);
				html += "<p><img src=\""+downLoadIp + mgvo.getMongoId()+"\"  /> </p>";
				out.close();
			} catch (Exception e) {
				System.out.println("第"+i+"张ppt转换出错");
			}
		}
		System.out.println("7success");
		return html;
	}


	//去除head body
	public static String matchHtml(String html){
		try{
			String style = html.substring(html.indexOf("<style"), html.indexOf("</style>")+8);
			String bodyAll = html.substring(html.indexOf("<body"), html.indexOf("</body>"));
			html =style + bodyAll.substring(bodyAll.indexOf(">")+1);
		}catch(Exception e){
		}
		return html;
	}
}
