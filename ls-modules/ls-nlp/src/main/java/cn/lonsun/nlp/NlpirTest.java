package cn.lonsun.nlp;

import cn.lonsun.nlp.utils.NlpirUtil;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;

import javax.net.ssl.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NlpirTest {


    public static String readStr(){
    	StringBuilder sb = new StringBuilder();
    	BufferedReader bf;
    	try {
	    	File file = new File(NlpirUtil.getResourcePath()+"/test.txt");
			bf = new BufferedReader(new FileReader(file));
	        String content = "";
	        while(content != null){
	        	content = bf.readLine();
	        	if(content == null){
	        		break;
	        	}
	        	sb.append(content.trim());
	        }
	        bf.close();
    	} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
        return sb.toString();
    }
    
    public static void main(String[] args) throws Exception {  
//        int init_flag = NlpirUtil.init();
//        String nativeBytes;
//        // 初始化失败提示
//        if (0 == init_flag) {
//            nativeBytes = NlpirUtil.getLastErrorMsg();
//            System.err.println("初始化失败！原因："+nativeBytes);
//            return;
//        }

        String sInput = readStr();
        System.out.println(NlpirUtil.getEncoding(sInput));

        try {
//            nativeBytes = NlpirUtil.paragraphProcess(sInput, 1);      // 运行分词函数  第二个参数设为0时不带词性
//            System.out.println("分词结果为： " + nativeBytes);      // 输出分词结果
            List<String> nativeByte_keyword = NlpirUtil.getKeyWords(sInput, 10);//运行关键词提取函数
            System.out.println("关键词提取结果是： " + nativeByte_keyword);
//            String nativeByte_cp = NlpirUtil.wordFreqStat(sInput);//运行词频分析函数
//            System.out.println("词频分析结果是： " + nativeByte_cp);
//            int num = NlpirUtil.getParagraphProcessAWordCount(sInput);
//            System.out.println("词汇数量分析结果是： " + num);
//            String nativaByte_xc = NlpirUtil.getNewWords(sInput, 10, false);
//            System.out.println("新词分析结果是： " + nativaByte_xc);
            NlpirUtil.exit();     // 退出
        } catch (Exception ex) {
            // TODO Auto-generated catch block
            ex.printStackTrace();
        }

        int state = -1;
//        try {
//            String urlStr = "https://www.chadianhua.net/";
//            urlStr = urlStr.replaceAll(" ", "%20");
//            URL url = new URL(urlStr);
//            HttpURLConnection con = (HttpURLConnection) url.openConnection();
//            con.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
//            con.setConnectTimeout(30000);
//            con.setReadTimeout(30000);
//            state = con.getResponseCode();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            String exceptionName = e.getClass().getSimpleName().toString();
//            if("UnknownHostException".equals(exceptionName)){
//                state = 404;
//            }else if("SSLException".equals(exceptionName)){
//                state = 403;
//            }else{
//                String msg = e.getMessage();
//                if(msg.contentEquals("connect timed out")) {
//                    state = 408;
//                } else {
//                    state = -1;
//                }
//            }
//        }
//        System.out.println(state);
//        String url = "www.chuzhou.gov.cn/2653902.html";
//        Pattern pattern = Pattern.compile("^[\\w]{1,}(?:\\.?[\\w]{1,})+[/]{1}[\\d]+(?:\\.html{1})[\\w-_/?&=#%:]*$");
//        Matcher matcher = pattern.matcher(url);
//        if(matcher.find()){
//            System.out.println(true);
//        }else{
//            System.out.println(false);
//        }

    }



    /**
     * 处理https请求被拒的情况
     */
    public static CloseableHttpClient createSSLClientDefault() {
        try {
            SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
                // 信任所有
                @Override
                public boolean isTrusted(X509Certificate[] chain, String authType) {
                    return true;
                }
            }).build();
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext);
            return HttpClients.custom().setSSLSocketFactory(sslsf).build();
        } catch (KeyManagementException e) {
            System.out.println("SSLUtilsErrorKetManage");
        } catch (NoSuchAlgorithmException e) {
            System.out.println("SSLUtilsErrorNOAlgorithm");
        } catch (KeyStoreException e) {
            System.out.println("SSLUtilsErrorKeyStore");
        }
        return HttpClients.createDefault();
    }
    
}
