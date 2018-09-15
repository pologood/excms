package cn.lonsun.core.util;

import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;

/**
 * 导出csv工具类
 *
 * @author Administrator
 */
public class CSVUtils {

    /**
     * csv下载
     *
     * @param filename 文件名,如果为空，那么按当前时间生成默认的文件名
     * @param titles   表头
     * @param datas    数据,数据的顺序需要与titles保持一致
     * @param response
     * @throws IOException
     */
    public static void download(String filename,
                                String[] titles, List<String[]> datas, HttpServletResponse response) throws IOException {
        // 输入验证
        if (response == null || titles == null || titles.length <= 0) {
            throw new NullPointerException();
        }
        // 如果文件名为空，那么生产一个文件名
        if (StringUtils.isEmpty(filename)) {
            filename = String.valueOf(new Date().getTime());
        }
        try {
            response.setHeader("Content-Disposition",
                    "attachment;fileName="
                            .concat(new String(filename.getBytes("GBK"),
                                    "ISO8859-1")).concat(".csv"));
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
            throw e1;
        }
        try {
            //response.getOutputStream().write(new byte[]{(byte) 0xEF, (byte) 0xBB, (byte) 0xBF});
            BufferedWriter csvFileOutputStream = new BufferedWriter(
                    new OutputStreamWriter(response.getOutputStream(), "GB2312"),
                    1024);
            // csvFileOutputStream.write(new String(new byte[]{(byte) 0xEF, (byte) 0xBB, (byte) 0xBF }));
            // 写入标题
            for (int i = 0; i < titles.length; i++) {
                csvFileOutputStream.write("\"" + titles[i] + "\"");
                if (i < titles.length - 1) {
                    csvFileOutputStream.write(",");
                }
            }
            // 写入内容
            if (datas != null && datas.size() > 0) {
                for (String[] data : datas) {
                    if (data != null) {
                        csvFileOutputStream.newLine();
                    }
                    for (int i = 0; i < data.length; i++) {
                        csvFileOutputStream.write("\"" + data[i] + "\"");
                        if (i < titles.length - 1) {
                            csvFileOutputStream.write(",");
                        }
                    }
                }
            }

            csvFileOutputStream.flush();
            csvFileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
    }
}
