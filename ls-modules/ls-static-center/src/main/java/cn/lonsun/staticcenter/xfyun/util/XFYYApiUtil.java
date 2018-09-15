package cn.lonsun.staticcenter.xfyun.util;

import cn.lonsun.common.upload.csource.common.Base64;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.util.SpringContextHolder;
import cn.lonsun.staticcenter.xfyun.vo.APIProperties;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;


/**
 * 讯飞语音api
 * Created by huangxx on 2018/7/3.
 */
@Component
public class XFYYApiUtil {

    private static APIProperties properties = SpringContextHolder.getBean("apiProperties");

    private static Base64 base64 = new Base64();

    /**
     * 返回转换结果
     *
     * @param xparam json字符串
     * @param bytes  音频二进制码
     * @throws Exception
     */
    public static JSONObject getData(String xparam, byte[] bytes) throws Exception {
        //构建header
        Map<String, String> header = buildHeader(xparam);
        //构建body
        String buildBody = buildBody(bytes);
        String result = httpPost(properties.getAPI_URL(), header, buildBody);
        JSONObject resultJson = JSON.parseObject(result);
        return resultJson;
    }

    /**
     * 请求头
     * @param json:接口参数的json字符串
     * @throws IOException
     */
    public static Map<String, String> buildHeader(String json) throws IOException {
        String curTime = System.currentTimeMillis() / 1000L + "";
        String param = base64.encode(json.getBytes("utf-8"));
        String checkSum = DigestUtils.md5Hex(properties.getAPI_KEY() + curTime + param);
        Map<String, String> header = new HashMap<String, String>();
        header.put("X-Appid", properties.getAPI_ID());
        header.put("X-CurTime", curTime);
        header.put("X-Param", param);
        header.put("X-CheckSum", checkSum);
        return header;
    }

    /**
     * 请求体,将二进制BASE64编码
     * @param bytes:音频二进制
     * @throws Exception
     */
    private static String buildBody(byte[] bytes) throws Exception {
        String fileData = null;
        if (AppUtil.isEmpty(bytes)) {
            throw new BaseRunTimeException("无音频数据");
        }
        fileData = base64.encode(bytes);
        return fileData;
    }

    /**
     * post请求
     * @param url
     * @param header
     * @param body
     * @return
     */
    public static String httpPost(String url, Map<String, String> header, String body) {
        String result = "";
        BufferedReader in = null;
        OutputStreamWriter out = null;
        try {
            URL realUrl = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) realUrl.openConnection();
            for (String key : header.keySet()) {
                connection.setRequestProperty(key, header.get(key));
            }
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setDoInput(true);

            out = new OutputStreamWriter(connection.getOutputStream());
            out.write("audio=" + URLEncoder.encode(body, "UTF-8"));
            out.flush();
            in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));//这里不加编码，会造成乱码
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {//使用finally块来关闭输出流、输入流
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return result;
    }

    /**
     * 测试用读取本地音频
     * @return
     * @throws Exception
     */
    public static byte[] readByte() throws Exception {
        File file = new File("F:\\gitworkspace\\ls-wannianxian\\ls-modules\\ls-static-center\\src\\main\\java\\cn\\lonsun\\staticcenter\\xfyun\\16k.pcm");
        byte[] bytes = null;
        InputStream is = null;
        try {
            is = new FileInputStream(file);
            bytes = IOUtils.toByteArray(is);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != is) {
                is.close();
            }
        }
        return bytes;
    }

    /**
     * 测试用，读取本地文档
     * @param filePath
     * @param charset
     * @throws Exception
     */
    public static String readFile(String filePath, String charset) throws Exception {
        charset = "UTF-8";
        filePath = "F:\\gitworkspace\\ls-ex8-master\\ls-modules\\ls-sys-web\\src\\main\\java\\cn\\lonsun\\xfyun\\pcm.txt";
        StringBuilder lineTxt = new StringBuilder();
        File file = new File(filePath);
        try {
            if (file.isFile() && file.exists()) { //判断文件是否存在
                InputStreamReader read = new InputStreamReader(
                        new FileInputStream(file), charset);//考虑到编码格式
                BufferedReader bufferedReader = new BufferedReader(read);
                String s = null;
                while ((s = bufferedReader.readLine()) != null) {
                    lineTxt.append(s + "\r\n");
                    return lineTxt.toString();
                }
                read.close();
            } else {
                System.out.println("找不到指定的文件");
            }
        } catch (Exception e) {
            System.out.println("读取文件内容出错");
            e.printStackTrace();
        }
        return lineTxt.toString();
    }

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {

        /*String xparam = "{\"engine_type\": \"sms16k\",\"aue\": \"raw\"}";
        String url = "http://api.xfyun.cn/v1/service/v1/iat";
        //构建header
        Map<String, String> header = buildHeader(xparam);
        //构建body
        String buildBody = buildBody(readByte());
        String result = httpPost(url, header, buildBody);
        JSONObject resultJson = JSON.parseObject(result);

        String code = resultJson.getString("code");
        if(code.equals("0")) { // 成功
            String data = resultJson.getString("data");
            System.out.println("data:"+data);
        }
        else { // 失败
            String desc = resultJson.getString("desc");
            throw new BaseRunTimeException("语音接口调用失败："+desc);
        }*/

    }
}
