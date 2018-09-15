package cn.lonsun.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
public class ConvertFlv {  
    public static void main(String[] args) {  
        ConvertFlv.convert("D:/videoTrans/1234.wmv", "D:/videoTrans/target/11.flv");  
    }  
    /** 
     *  功能函数 
     * @param inputFile 待处理视频，需带路径 
     * @param outputFile 处理后视频，需带路径 
     * @return 
     */  
    public static boolean convert(String inputFile, String outputFile)  
    {  
        if (!checkfile(inputFile)) {  
            System.out.println(inputFile + " is not file");  
            return false;  
        }  
        if (process(inputFile,outputFile)) {  
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
     * @param inputFile 
     * @param outputFile 
     * @return 
     */  
    private static boolean process(String inputFile,String outputFile) {  
        int type = checkContentType( inputFile);  
        boolean status = false;  
        if (type == 0) {  
            status = processFLV(inputFile,outputFile);// 直接将文件转为flv文件  
        } else if (type == 1) {  
            String avifilepath = processAVI(type,inputFile);  
            if (avifilepath == null)  
                return false;// avi文件没有得到  
            status = processFLV(avifilepath,outputFile);// 将avi转为flv  
        }  
        return status;  
    }  
    /** 
     * 检查视频类型 
     * @param inputFile 
     * @return ffmpeg 能解析返回0，不能解析返回1 
     */  
    private static int checkContentType(String inputFile) {  
        String type = inputFile.substring(inputFile.lastIndexOf(".") + 1,inputFile.length()).toLowerCase();  
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
     *  ffmepg: 能解析的格式：（asx，asf，mpg，wmv，3gp，mp4，mov，avi，flv等） 
     * @param inputFile 
     * @param outputFile 
     * @return 
     */  
    private static boolean processFLV(String inputFile,String outputFile) {  
        if (!checkfile(inputFile)) {  
            System.out.println(inputFile + " is not file");  
            return false;  
        }  
        List<String> commend = new java.util.ArrayList<String>();  
        commend.add("D:/videoTrans/ffmpeg");  
        commend.add("-i");  
        commend.add(inputFile);  
        commend.add("-qscale");//设置视频质量 取值0.01-255，越小质量越好
        commend.add("8");
//        commend.add("-s");	//设置分辨率
//        commend.add("960*640");
        commend.add("-ab");  //设置音频码率
        commend.add("20");  
        commend.add("-acodec");  //指定音频编码
        commend.add("libmp3lame");  
        commend.add("-ac");  //设置声道数
        commend.add("2");  
        commend.add("-ar");  //设置声音的采样频率
        commend.add("22050");  
        commend.add("-r");  //设置帧数
        commend.add("28");  
        commend.add("-b");  //指定比特率(bits/s)
        commend.add("512");  
        commend.add("-y");  //覆盖
        commend.add(outputFile);  
        StringBuffer info=new StringBuffer();  
        for(int i=0;i<commend.size();i++)  
        	info.append(commend.get(i)+" ");  
        System.out.println("视频转换信息："+info);  
        
        // 创建一个List集合来保存从视频中截取图片的命令
        List<String> cutpic = new ArrayList<String>();
        cutpic.add("D:/videoTrans/ffmpeg");
        cutpic.add("-i");
        cutpic.add(inputFile); // 同上（指定的文件即可以是转换为flv格式之前的文件，也可以是转换的flv文件）
        cutpic.add("-y");
        cutpic.add("-f");
        cutpic.add("image2");
        cutpic.add("-ss"); // 添加参数＂-ss＂，该参数指定截取的起始时间
        cutpic.add("1"); // 添加起始时间为第17秒
        cutpic.add("-t"); // 添加参数＂-t＂，该参数指定持续时间
        cutpic.add("0.001"); // 添加持续时间为1毫秒
        cutpic.add("-s"); // 添加参数＂-s＂，该参数指定截取的图片大小
        cutpic.add("1024*720"); // 添加截取的图片大小为350*240
        cutpic.add("D:/videoTrans/pic/pic.jpg"); // 添加截取的图片的保存路径
        StringBuffer picInfo=new StringBuffer();
        for(int i=0;i<cutpic.size();i++)  
        	picInfo.append(cutpic.get(i)+" ");  
        System.out.println("生成视频图片："+picInfo); 
        try {  
            ProcessBuilder builder = new ProcessBuilder();  
            builder.command(commend);  
            builder.start();  
            
            builder.command(cutpic);
            builder.redirectErrorStream(true);
            // 如果此属性为 true，则任何由通过此对象的 start() 方法启动的后续子进程生成的错误输出都将与标准输出合并，
            //因此两者均可使用 Process.getInputStream() 方法读取。这使得关联错误消息和相应的输出变得更容易
            builder.start();
            return true;  
        } catch (Exception e) {  
            e.printStackTrace();  
            return false;  
        }  
    }  
    /** 
     * Mencoder: 
     *  对ffmpeg无法解析的文件格式(wmv9，rm，rmvb等),  
	 *	可以先用别的工具（mencoder）转换为avi(ffmpeg能解析的)格式. 
     * @param type 
     * @param inputFile 
     * @return 
     */  
    private static String processAVI(int type,String inputFile) {  
        File file =new File("D:/videoTrans/temp.avi");  
        if(file.exists())   file.delete();  
        List<String> commend = new java.util.ArrayList<String>();  
        commend.add("D:/videoTrans/mencoder");  
        commend.add(inputFile);  
        commend.add("-oac");  //音频编码
        commend.add("mp3lame");  
        commend.add("-lameopts");  //
        commend.add("preset=64");  
        commend.add("-ovc");  //视频编码
        commend.add("xvid");  
        commend.add("-xvidencopts");  //视频编码率 kbps
        commend.add("bitrate=600");  
        commend.add("-of");  
        commend.add("avi");  
        commend.add("-o");  
        commend.add("D:/videoTrans/temp/temp.avi");  
        StringBuffer test=new StringBuffer();  
        for(int i=0;i<commend.size();i++)  
            test.append(commend.get(i)+" ");  
        System.out.println(test);  
        try   
        {  
            ProcessBuilder builder = new ProcessBuilder();  
            builder.command(commend);  
            Process p=builder.start();  
            /** 
             * 清空Mencoder进程 的输出流和错误流 
             * 因为有些本机平台仅针对标准输入和输出流提供有限的缓冲区大小， 
             * 如果读写子进程的输出流或输入流迅速出现失败，则可能导致子进程阻塞，甚至产生死锁。  
             */  
            final InputStream is1 = p.getInputStream();  
            final InputStream is2 = p.getErrorStream();  
            new Thread() {  
                public void run() {  
                    BufferedReader br = new BufferedReader( new InputStreamReader(is1));  
                    try {  
                        String lineB = null;  
                        while ((lineB = br.readLine()) != null ){  
                            if(lineB != null)System.out.println(lineB);  
                        }  
                    } catch (IOException e) {  
                        e.printStackTrace();  
                    }  
                }  
            }.start();   
            new Thread() {  
                public void run() {  
                    BufferedReader br2 = new BufferedReader( new InputStreamReader(is2));  
                    try {  
                        String lineC = null;  
                        while ( (lineC = br2.readLine()) != null){  
                            if(lineC != null)System.out.println(lineC);  
                        }  
                    } catch (IOException e) {  
                        e.printStackTrace();  
                    }  
                }  
            }.start();   
            //等Mencoder进程转换结束，再调用ffmepg进程  
            p.waitFor();  
             System.out.println("who cares");  
            return "D:/videoTrans/temp/temp.avi";  
        }catch (Exception e){   
            System.err.println(e);   
            return null;  
        }   
    }  
}  