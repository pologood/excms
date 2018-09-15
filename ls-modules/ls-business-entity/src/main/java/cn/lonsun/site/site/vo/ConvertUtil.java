package cn.lonsun.site.site.vo;

import java.io.*;
import java.util.List;

/**
 * 视频转换（未使用）<br/>
 *
 * @author wangss <br/>
 * @version v1.0 <br/>
 * @date 2015-9-30<br/>
 */

public class ConvertUtil {


    public static void main(String[] args) {
        ConvertUtil.convert("G:\\Wildlife.wmv", "G:\\Wildlife.flv");
    }

    /**
     * 功能函数
     *
     * @param inputFile  待处理视频，需带路径
     * @param outputFile 处理后视频，需带路径
     * @return
     */
    public static boolean convert(String inputFile, String outputFile) {
        if (!checkfile(inputFile)) {
            System.out.println(inputFile + " is not file");
            return false;
        }
        if (process(inputFile, outputFile)) {
            System.out.println("ok");
            return true;
        }
        return false;
    }

    //检查文件是否存在
    private static boolean checkfile(String path) {
        File file = new File(path);
        if (!file.isFile()) {
            return false;
        }
        return true;
    }

    /**
     * 转换过程 ：先检查文件类型，在决定调用 processFlv还是processAVI
     *
     * @param inputFile
     * @param outputFile
     * @return
     */
    private static boolean process(String inputFile, String outputFile) {
        int type = checkContentType(inputFile);
        boolean status = false;
        if (type == 0) {
            status = processFLV(inputFile, outputFile);// 直接将文件转为flv文件
        } else if (type == 1) {
            String avifilepath = processAVI(type, inputFile);
            if (avifilepath == null)
                return false;// avi文件没有得到
            status = processFLV(avifilepath, outputFile);// 将avi转为flv
        }
        return status;
    }

    /**
     * 检查视频类型
     *
     * @param inputFile
     * @return ffmpeg 能解析返回0，不能解析返回1
     */
    private static int checkContentType(String inputFile) {
        String type = inputFile.substring(inputFile.lastIndexOf(".") + 1,
                inputFile.length())
                .toLowerCase();
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
     * ffmepg: 能解析的格式：（asx，asf，mpg，wmv，3gp，mp4，mov，avi，flv等）
     *
     * @param inputFile
     * @param outputFile
     * @return
     */
    private static boolean processFLV(String inputFile, String outputFile) {
        if (!checkfile(inputFile)) {
            System.out.println(inputFile + " is not file");
            return false;
        }
        List<String> commend = new java.util.ArrayList<String>();
        //低精度
        commend.add("G:\\Wildlife.wmv");
        commend.add("-i");
        commend.add(inputFile);
        commend.add("-ab");
        commend.add("128");
        commend.add("-acodec");
        commend.add("libmp3lame");
        commend.add("-ac");
        commend.add("1");
        commend.add("-ar");
        commend.add("22050");
        commend.add("-r");
        commend.add("29.97");
        commend.add("-b");
        commend.add("512");
        commend.add("-y");
        commend.add(outputFile);
        StringBuffer test = new StringBuffer();
        for (int i = 0; i < commend.size(); i++)
            test.append(commend.get(i) + " ");
        System.out.println(test);
        try {
            ProcessBuilder builder = new ProcessBuilder();
            builder.command(commend);
            builder.start();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Mencoder:
     * 对ffmpeg无法解析的文件格式(wmv9，rm，rmvb等),
     * 可以先用别的工具（mencoder）转换为avi(ffmpeg能解析的)格式.
     *
     * @param type
     * @param inputFile
     * @return
     */
    private static String processAVI(int type, String inputFile) {
        File file = new File("G:\\Wildlife.avi");
        if (file.exists()) file.delete();
        List<String> commend = new java.util.ArrayList<String>();
        commend.add("E:\\mencoder");
        commend.add(inputFile);
        commend.add("-oac");
        commend.add("mp3lame");
        commend.add("-lameopts");
        commend.add("preset=64");
        commend.add("-ovc");
        commend.add("xvid");
        commend.add("-xvidencopts");
        commend.add("bitrate=600");
        commend.add("-of");
        commend.add("avi");
        commend.add("-o");
        commend.add("E://temp.avi");
        StringBuffer test = new StringBuffer();
        for (int i = 0; i < commend.size(); i++)
            test.append(commend.get(i) + " ");
        System.out.println(test);
        try {
            ProcessBuilder builder = new ProcessBuilder();
            builder.command(commend);
            Process p = builder.start();
            /**
             * 清空Mencoder进程 的输出流和错误流
             * 因为有些本机平台仅针对标准输入和输出流提供有限的缓冲区大小，
             * 如果读写子进程的输出流或输入流迅速出现失败，则可能导致子进程阻塞，甚至产生死锁。
             */
            final InputStream is1 = p.getInputStream();
            final InputStream is2 = p.getErrorStream();
            new Thread() {
                public void run() {
                    BufferedReader br = new BufferedReader(new
                            InputStreamReader(is1));
                    try {
                        String lineB = null;
                        while ((lineB = br.readLine()) != null) {
                            if (lineB != null) System.out.println(lineB);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }.start();
            new Thread() {
                public void run() {
                    BufferedReader br2 = new BufferedReader(new
                            InputStreamReader(is2));
                    try {
                        String lineC = null;
                        while ((lineC = br2.readLine()) != null) {
                            if (lineC != null) System.out.println(lineC);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }.start();
            //等Mencoder进程转换结束，再调用ffmepg进程
            p.waitFor();
            System.out.println("who cares");
            return "G:\\Wildlife.avi";
        } catch (Exception e) {
            System.err.println(e);
            return null;
        }
    }

}
