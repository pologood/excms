package cn.lonsun.util;


import cn.lonsun.mongodb.vo.MongoFileVO;
import cn.lonsun.system.filecenter.internal.entity.FileCenterEO;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.pdfbox.PDFToImage;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSString;
import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.pdfparser.PDFStreamParser;
import org.apache.pdfbox.pdfwriter.ContentStreamWriter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.common.PDStream;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.xobject.PDXObjectImage;
import org.apache.pdfbox.util.PDFOperator;
import org.apache.pdfbox.util.PDFText2HTML;
import org.apache.pdfbox.util.PDFTextStripper;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * http://pdfbox.apache.org/
 *
 * @author fish
 *
 */
public class PdfBoxReadPdfToHtml {

	public PdfBoxReadPdfToHtml()
	{
//		createHelloPDF();
//		readPDF();
//		editPDF();
	}

	public static String convertPdfHtml(InputStream is, final Long siteId, final Long columnId, final String downLoadIp, final HttpServletRequest request)
			throws Exception {
		String html = "";
		PDDocument helloDocument;
		try {
			helloDocument = PDDocument.load(is);
			/** 文档页面信息 **/
			//获取PDDocumentCatalog文档目录对象
			PDDocumentCatalog catalog = helloDocument.getDocumentCatalog();
			//获取文档页面PDPage列表
			List pages = catalog.getAllPages();
			int count = 1;
			int pageNum=pages.size();   //文档页数//遍历每一页
			for( int i = 0; i < pageNum; i++ ){
				//取得第i页
				PDPage page = ( PDPage ) pages.get( i );
				if( null != page ){
					PDResources resource = page.findResources();
					//获取页面图片信息
					Map<String,PDXObjectImage> imgs = resource.getImages();
					if(imgs.entrySet().size()>0){
						ByteArrayOutputStream out = new ByteArrayOutputStream();
						BufferedImage image = page.convertToImage();
						Iterator iter = ImageIO.getImageWritersBySuffix("jpg");
						ImageWriter writer = (ImageWriter) iter.next();
//						File outFile = new File("E:\\"
//								+ i + ".jpg");
//						FileOutputStream out1 = new FileOutputStream(outFile);
						ImageOutputStream outImage = ImageIO
								.createImageOutputStream(out);
						writer.setOutput(outImage);
						writer.write(new IIOImage(image, null, null));
						javax.imageio.ImageIO.write(image, "jpg", out);
						MongoFileVO mgvo = FileUploadUtil.editorUpload(out.toByteArray(), i+".jpg", FileCenterEO.Type.EditorUpload.toString(), FileCenterEO.Code.EditorAttach.toString(), siteId, columnId, null, "编辑器上传附件", request);
						String content = "<p><img src=\""+downLoadIp + mgvo.getMongoId()+"\"  /></p>";
						PDPageContentStream contentStream = new PDPageContentStream(helloDocument, page);
						contentStream.beginText();
						contentStream.setFont(PDType1Font.HELVETICA, 12);
						contentStream.moveTextPositionByAmount(0, 0);
						contentStream.drawString(content);

						contentStream.endText();
						contentStream.close();
					}

				}
			}

			PDFText2HTML text2HTML = new PDFText2HTML("utf-8");
			html = text2HTML.getText(helloDocument);
			helloDocument.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return matchHtml(html);
	}


	public void createHelloPDF() {
		PDDocument doc = null;
		PDPage page = null;

		try {
			doc = new PDDocument();
			page = new PDPage();

			doc.addPage(page);
			PDFont font = PDType1Font.HELVETICA_BOLD;
			PDPageContentStream content = new PDPageContentStream(doc, page);
			content.beginText();
			content.setFont(font, 12);
			content.moveTextPositionByAmount(100, 700);
			content.drawString("Hello");

			content.endText();
			content.close();
			doc.save("D:\\gloomyfish\\pdfwithText.pdf");
			doc.close();
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	public void readPDF()
	{
		PDDocument helloDocument;
		try {
			helloDocument = PDDocument.load(new File(
					"E:\\1.pdf"));
			PDFTextStripper textStripper = new PDFTextStripper();
			PDFText2HTML text2HTML = new PDFText2HTML("utf-8");
			String content = text2HTML.getText(helloDocument);
//			text2HTML.getText()
			System.out.println(content);
			helloDocument.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	public void editPDF() {

		try {
			// pdfwithText
			PDDocument helloDocument = PDDocument.load(new File("D:\\gloomyfish\\pdfwithText.pdf"));
			// PDDocument helloDocument = PDDocument.load(new File("D:\\gloomyfish\\hello.pdf"));
			// int pageCount = helloDocument.getNumberOfPages();
			PDPage firstPage = (PDPage)helloDocument.getDocumentCatalog().getAllPages().get(0);
			// PDPageContentStream content = new PDPageContentStream(helloDocument, firstPage);
			PDStream contents = firstPage.getContents();

			PDFStreamParser parser = new PDFStreamParser(contents.getStream());
			parser.parse();
			List tokens = parser.getTokens();
			for (int j = 0; j < tokens.size(); j++)
			{
				Object next = tokens.get(j);
				if (next instanceof PDFOperator)
				{
					PDFOperator op = (PDFOperator) next;
					// Tj and TJ are the two operators that display strings in a PDF
					if (op.getOperation().equals("Tj"))
					{
						// Tj takes one operator and that is the string
						// to display so lets update that operator
						COSString previous = (COSString) tokens.get(j - 1);
						String string = previous.getString();
						string = string.replaceFirst("Hello", "Hello World, fish");
						//Word you want to change. Currently this code changes word "Solr" to "Solr123"
						previous.reset();
						previous.append(string.getBytes("ISO-8859-1"));
					}
					else if (op.getOperation().equals("TJ"))
					{
						COSArray previous = (COSArray) tokens.get(j - 1);
						for (int k = 0; k < previous.size(); k++)
						{
							Object arrElement = previous.getObject(k);
							if (arrElement instanceof COSString)
							{
								COSString cosString = (COSString) arrElement;
								String string = cosString.getString();
								string = string.replaceFirst("Hello", "Hello World, fish");

								// Currently this code changes word "Solr" to "Solr123"
								cosString.reset();
								cosString.append(string.getBytes("ISO-8859-1"));
							}
						}
					}
				}
			}
			// now that the tokens are updated we will replace the page content stream.
			PDStream updatedStream = new PDStream(helloDocument);
			OutputStream out = updatedStream.createOutputStream();
			ContentStreamWriter tokenWriter = new ContentStreamWriter(out);
			tokenWriter.writeTokens(tokens);
			firstPage.setContents(updatedStream);
			helloDocument.save("D:\\gloomyfish\\helloworld.pdf"); //Output file name
			helloDocument.close();
//          PDFTextStripper textStripper = new PDFTextStripper();
//          System.out.println(textStripper.getText(helloDocument));
//          helloDocument.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (COSVisitorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 提取图片并保存
	 * @param imgSavePath 图片保存路径
	 */
	public static void extractImage(String imgSavePath) throws Exception{
			//打开pdf文件流
			//加载 pdf 文档,获取PDDocument文档对象
			PDDocument document = PDDocument.load(new File(
					"E:\\1.pdf"));
			/** 文档页面信息 **///获取PDDocumentCatalog文档目录对象
			PDDocumentCatalog catalog = document.getDocumentCatalog();
			//获取文档页面PDPage列表
			List pages = catalog.getAllPages();
			int count = 1;
			int pageNum=pages.size();   //文档页数//遍历每一页
			for( int i = 0; i < pageNum; i++ ){
				//取得第i页
				PDPage page = ( PDPage ) pages.get( i );
				if( null != page ){
					PDResources resource = page.findResources();
					//获取页面图片信息
					Map<String,PDXObjectImage> imgs = resource.getImages();
					for(Map.Entry<String,PDXObjectImage> me: imgs.entrySet()){
						//System.out.println(me.getKey());
						PDXObjectImage img = me.getValue();
						ByteArrayOutputStream out = new ByteArrayOutputStream();
						javax.imageio.ImageIO.write(img.getRGBImage(), "png", out);
						//将图片保存到mongoDB
//						MongoFileVO mgvo = FileUploadUtil.editorUpload(out.toByteArray(), i+".jpg", FileCenterEO.Type.EditorUpload.toString(), FileCenterEO.Code.EditorAttach.toString(), siteId, columnId, null, "编辑器上传附件", request);
//						html += "<p><img src=\""+downLoadIp + mgvo.getMongoId()+"\"  /> </p>";
						//保存图片，会自动添加图片后缀类型
						img.write2file( imgSavePath + count );
						count++;

					}

				}
			}
		document.close();
	}

	//去除head body
	public static String matchHtml(String html){
		try{
			String style = html.substring(html.indexOf("<style"), html.indexOf("</style>")+8);
			String bodyAll = html.substring(html.indexOf("<body"), html.indexOf("</body>"));
			html =style + bodyAll.substring(bodyAll.indexOf(">")+1);
			html = html.replace("&lt;","<").replace("&gt;",">");
		}catch(Exception e){
		}
		return html;
	}

	public static void main(String[] args) throws Exception {
		new PdfBoxReadPdfToHtml();
//		extractImage("E:\\");
		try {
			        PDDocument doc = PDDocument
							.load("E:\\2.pdf");
			         int pageCount = doc.getPageCount();
			         System.out.println(pageCount);
			         List pages = doc.getDocumentCatalog().getAllPages();
			         for (int i = 0; i < pages.size(); i++) {
				             PDPage page = (PDPage) pages.get(i);
				             BufferedImage image = page.convertToImage();
				             Iterator iter = ImageIO.getImageWritersBySuffix("jpg");
				             ImageWriter writer = (ImageWriter) iter.next();
				            File outFile = new File("E:\\1\\"
						                     + i + ".jpg");
				             FileOutputStream out = new FileOutputStream(outFile);
				            ImageOutputStream outImage = ImageIO
				                     .createImageOutputStream(out);
				            writer.setOutput(outImage);
				             writer.write(new IIOImage(image, null, null));
				         }
			         doc.close();
			         System.out.println("over");
			     } catch (FileNotFoundException e) {
			         // TODO Auto-generated catch block
			         e.printStackTrace();
			     } catch (IOException e) {
			         // TODO Auto-generated catch block
			         e.printStackTrace();
			     }
	}
}
