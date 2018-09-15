package cn.lonsun.util;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.converter.PicturesManager;
import org.apache.poi.hwpf.converter.WordToHtmlConverter;
import org.apache.poi.hwpf.usermodel.PictureType;
import org.apache.poi.xwpf.converter.core.IImageExtractor;
import org.apache.poi.xwpf.converter.core.IURIResolver;
import org.apache.poi.xwpf.converter.xhtml.XHTMLConverter;
import org.apache.poi.xwpf.converter.xhtml.XHTMLOptions;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.w3c.dom.Document;

import cn.lonsun.mongodb.vo.MongoFileVO;
import cn.lonsun.system.filecenter.internal.entity.FileCenterEO;

public class POIReadWordToHtml {

	//	public static void main(String[] args) {
	//		try {
	//			String html = convertDocHtml(new FileInputStream("d://test//ddddd.doc"));
	//			//			System.out.println(html.substring(html.indexOf("<style"), html.indexOf("</style>")+8));
	//
	//			//			System.out.println(html.substring(html.indexOf("<body"), html.indexOf("</body>")));
	//			String str = html.substring(html.indexOf("<body"), html.indexOf("</body>"));
	//			System.out.println(str.substring(str.indexOf(">")+1));
	//			//			System.out.println(HtmlUtils.htmlEscapeDecimal(html));
	//			//			String html2 = convertDocxHtml(new FileInputStream("d://test//aaa.docx"));
	//			//			System.out.println(HtmlUtils.htmlEscapeDecimal(html));
	//		} catch (Exception e) {
	//			// TODO Auto-generated catch block
	//			e.printStackTrace();
	//		}
	//
	//	}

	private static String mongoId = "";

	public static String convertDocHtml(InputStream is,final Long siteId,final Long columnId,final String downLoadIp,final HttpServletRequest request)    
			throws TransformerException, IOException,    
			ParserConfigurationException {    
		HWPFDocument wordDocument = new HWPFDocument(is);//WordToHtmlUtils.loadDoc(new FileInputStream(inputFile));    
		WordToHtmlConverter wordToHtmlConverter = new WordToHtmlConverter(    
				DocumentBuilderFactory.newInstance().newDocumentBuilder()    
				.newDocument());    
		//图片处理
		wordToHtmlConverter.setPicturesManager( new PicturesManager()    
		{    
			public String savePicture( byte[] content,    
					PictureType pictureType, String suggestedName,    
					float widthInches, float heightInches )    
			{    
				String picPage = "";
				try {
					MongoFileVO mgvo = FileUploadUtil.editorUpload(content, suggestedName, FileCenterEO.Type.EditorUpload.toString(), FileCenterEO.Code.EditorAttach.toString(), siteId, columnId, null, "编辑器上传附件", request);
					picPage = downLoadIp + mgvo.getMongoId();
				} catch (Exception e) {
					e.printStackTrace();
				}

				return picPage;    
			}    
		} );    
		wordToHtmlConverter.processDocument(wordDocument);    
		Document htmlDocument = wordToHtmlConverter.getDocument();    
		ByteArrayOutputStream out = new ByteArrayOutputStream();    
		DOMSource domSource = new DOMSource(htmlDocument);    
		StreamResult streamResult = new StreamResult(out);    

		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer serializer = tf.newTransformer();
		serializer.setOutputProperty(OutputKeys.INDENT, "yes");
		serializer.setOutputProperty(OutputKeys.ENCODING, "utf-8");
		serializer.setOutputProperty(OutputKeys.METHOD, "html");
		serializer.transform(domSource, streamResult);    
		out.close();    
		String html = new String(out.toByteArray());
		return matchHtml(html);
	}    

	public static String convertDocxHtml(InputStream is,final Long siteId,final Long columnId,final String downLoadIp,final HttpServletRequest request) throws TransformerException, IOException,    
	ParserConfigurationException {    

		XWPFDocument document = new XWPFDocument(is);
		XHTMLOptions options = XHTMLOptions.create().indent( 4 );

		options.setExtractor(new IImageExtractor() {
			@Override
			public void extract(String imagePath, byte[] imageData) throws IOException {
				try {
					String image = imagePath.substring(imagePath.lastIndexOf("/")+1);
					MongoFileVO mgvo = FileUploadUtil.editorUpload(imageData, image, FileCenterEO.Type.EditorUpload.toString(), FileCenterEO.Code.EditorAttach.toString(), siteId, columnId, null, "编辑器上传附件", request);
					mongoId  = downLoadIp + mgvo.getMongoId();
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		});
		options.URIResolver(new IURIResolver() {
			@Override
			public String resolve(String uri) {
				return mongoId;
			}
		});
		ByteArrayOutputStream out = new ByteArrayOutputStream();    
		XHTMLConverter.getInstance().convert( document, out, options );
		out.close();    
		String html =  new String(out.toByteArray());
		return matchHtml(html);
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
