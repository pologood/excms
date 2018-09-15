package cn.lonsun.util;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.content.vo.ConvertMsg;
import org.apache.oro.text.regex.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

/**
 * @author Hewbing
 * @ClassName: LocalConvertVideo
 * @Description: 调用本地服务视频转换
 * @date 2015年10月27日 下午7:54:06
 */
public class LocalConvertVideo {
    File file = new File("");

    private static final String ffmpegPath = "G:/testVideo/ffmpeg";
    static Logger logger = LoggerFactory.getLogger(LocalConvertVideo.class);


    public static void main(String[] args) {
        ConvertMsg result = processFLV("G:123123.mp4", "G:999.mp4", 0, ffmpegPath);
        // System.out.println(result.getMsg());

    }

    public static boolean processImg(String veido_path, Integer seconds, Integer width, Integer height, String ffmpeg_path) {

        if (AppUtil.isEmpty(seconds)) {
            seconds = 1;
        }

        File file = new File(veido_path);
        if (!file.exists()) {
            logger.error("路径[" + veido_path + "]对应的视频文件不存在!");
            return false;
        }
        List<String> commands = new java.util.ArrayList<String>();
        commands.add(ffmpeg_path);
        commands.add("-i");
        commands.add(veido_path);
        commands.add("-y");
        commands.add("-f");
        commands.add("image2");
        commands.add("-ss");
        commands.add(seconds + "");//这个参数是设置截取视频多少秒时的画面
        commands.add("-s");
        commands.add(width + "x" + height);
        commands.add(veido_path.substring(0, veido_path.lastIndexOf(".")).replaceFirst("vedio", "file") + ".jpg");

        try {
            ProcessBuilder builder = new ProcessBuilder();
            builder.command(commands);
            builder.redirectErrorStream(true);
            logger.info("视频截图开始...");
            Process process = builder.start();
            InputStream in = process.getInputStream();
            byte[] re = new byte[1024];
            logger.info("正在进行截图，请稍候");
            while (in.read(re) != -1) {
                System.out.print(".");
            }
            System.out.println("");
            in.close();
            logger.info("视频截图完成...");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("视频截图失败！");
            return false;
        }

        /*try {
            ProcessBuilder builder = new ProcessBuilder();
            builder.command(commands);
            builder.start();
            logger.info("截取成功");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }*/
    }

    // ffmpeg能解析的格式：（asx，asf，mpg，wmv，3gp，mp4，mov，avi，flv等）
    public static ConvertMsg processFLV(String inputPath, String outputPath, Integer quality, String path) {
        if (!checkfile(inputPath)) {
            logger.error("==============>读取文件出错:  inputPath <================" + inputPath);
            return getMsg(0, "读取文件出错");
        }
        if (checkContentType(inputPath) == 0) {
            List<String> commend = new java.util.ArrayList<String>();

            // commend.add("e:\\videoconver\\ffmpeg\\ffmpeg ");//可以设置环境变量从而省去这行
            commend.add(path);
            commend.add("-i");
            commend.add(inputPath);
            commend.add("-ar");
            commend.add("22050");
            /*commend.add("-qscale");//设置视频质量 取值0.01-255，越小质量越好
            commend.add(getQuality(quality));
	        commend.add("-ab");  //设置音频码率
	        commend.add("64k");
			commend.add("-f");
			commend.add("flv");
            commend.add("sameq");
            commend.add("mpeg4");
            commend.add("h264");
             commend.add("-acodec");  //指定音频编码
	        commend.add("libmp3lame");
	        commend.add("-ac");  //设置声道数
	        commend.add("2");
	        commend.add("-ar");  //设置声音的采样频率
	        commend.add("22050");
	        commend.add("-r");  //设置帧数
	        commend.add("29.97");
            commend.add("-vb");  //指定比特率(bits/s)
            commend.add("1000");*/

            commend.add("-vcodec");
            commend.add("libx264");
            commend.add("-acodec");
            commend.add("libfaac");
            commend.add("-movflags");
            commend.add("+faststart");

            commend.add("-y");  //覆盖
            commend.add(outputPath);
            logger.error("==============>  ffmpeg <================" + path);
            logger.error("==============>  inputPath <================" + inputPath);
            logger.error("==============>  outputPath <================" + outputPath);

            PatternCompiler compiler = new Perl5Compiler();
            try {
                String regexDuration = "Duration: (.*?), start: (.*?), bitrate: (\\d*) kb\\/s";
                //String regexVideo ="Video: (.*?), (.*?), (.*?)[,\\s]";
                //String regexAudio ="Audio: (\\w*), (\\d*) Hz";

                ProcessBuilder builder = new ProcessBuilder();
                builder.command(commend);
                builder.redirectErrorStream(true);
                Process p = builder.start();

                // 1. start
                BufferedReader buf = null; // 保存ffmpeg的输出结果流
                String line = null;
                // read the standard output

                buf = new BufferedReader(new InputStreamReader(p.getInputStream()));
                StringBuffer sb = new StringBuffer();
                //int runtime=0;
                while ((line = buf.readLine()) != null) {
                    System.out.println(line);
                    //pushPro.exec(line);
                    sb.append(line);
                    org.apache.oro.text.regex.Pattern patternDuration = compiler.compile(regexDuration, Perl5Compiler.CASE_INSENSITIVE_MASK);
                    PatternMatcher matcherDuration = new Perl5Matcher();
                    if (matcherDuration.contains(line, patternDuration)) {
                        MatchResult re = matcherDuration.getMatch();

                        System.out.println("提取出播放时间  ===" + re.group(1));
                        System.out.println("开始时间        =====" + re.group(2));
                        System.out.println("bitrate 码率 单位 kb==" + re.group(3));
                    }
                    continue;
                }
                //int av_seek_frame(AVFormatContext *s, int stream_index, int64_t timestamp,int flags);
                int ret = p.waitFor();// 这里线程阻塞，将等待外部转换进程运行成功运行结束后，才往下执行
                // 1. end
                return getMsg(1, "转换完成");
            } catch (Exception e) {
                logger.error("==============>  转换异常 <================" + e.getMessage());
                return getMsg(0, "转换异常");
            }
        } else {
            logger.error("==============>  该格式暂不支持转换 <================");
            return getMsg(0, "该格式暂不支持转换");
        }
    }

    /**
     * 检查视频类型
     *
     * @param inputFile
     * @return ffmpeg 能解析返回0，不能解析返回1
     */
    private static int checkContentType(String inputFile) {
        String type = inputFile.substring(inputFile.lastIndexOf(".") + 1,
            inputFile.length()).toLowerCase();
        // ffmpeg能解析的格式：（asx，asf，mpg，wmv，3gp，mp4，mov，avi，flv等）
        if (type.equals("avi")) {
            return 0;
        } else if (type.equals("mpg")) {
            return 0;
        } else if (type.equals("wmv")) {
            return 0;
        } else if (type.equals("3gp")) {
            return 0;
        } else if (type.equals("mov")) {
            return 0;
        } else if (type.equals("mp4")) {
            return 0;
        } else if (type.equals("asf")) {
            return 0;
        } else if (type.equals("asx")) {
            return 0;
        } else if (type.equals("flv")) {
            return 0;
        }
        // 对ffmpeg无法解析的文件格式(wmv9，rm，rmvb等),
        // 可以先用别的工具（mencoder）转换为avi(ffmpeg能解析的)格式.
        else if (type.equals("wmv9")) {
            return 1;
        } else if (type.equals("rm")) {
            return 1;
        } else if (type.equals("rmvb")) {
            return 1;
        }
        return 9;
    }

    /**
     * @param @param  path
     * @param @return 设定文件
     * @return boolean    返回类型
     * @throws
     * @Title: checkfile
     * @Description: TODO
     */
    private static boolean checkfile(String path) {
        File file = new File(path);
        if (!file.isFile()) {
            return false;
        }
        return true;
    }

    /**
     * @param @param  status
     * @param @param  msg
     * @param @return 设定文件
     * @return ConvertMsg    返回类型
     * @throws
     * @Title: getMsg
     * @Description: 转换信息
     */
    private static ConvertMsg getMsg(Integer status, String msg) {
        ConvertMsg cMsg = new ConvertMsg();
        cMsg.setStatus(status);
        cMsg.setMsg(msg);
        return cMsg;
    }

    /**
     * @param @param  quality
     * @param @return 设定文件
     * @return String    返回类型
     * @throws
     * @Title: getQuality
     * @Description: 转换品质
     */
    private static String getQuality(Integer quality) {
        if (quality == 3) {
            return "40";//低品质
        } else if (quality == 2) {
            return "20";//中品质
        } else {
            return "8";//高品质
        }
    }


}