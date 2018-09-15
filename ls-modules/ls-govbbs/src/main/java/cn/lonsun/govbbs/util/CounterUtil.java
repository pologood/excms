package cn.lonsun.govbbs.util;

import cn.lonsun.core.base.util.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.math.BigInteger;
import java.util.Date;
import java.util.Scanner;

/**
 *
 * 简单计数器 记录游客id
 * Created by zhangchao on 2017/1/5.
 */
public class CounterUtil {

    public static String COUNTER_TXT = "counter.txt";

    public static String PATH = "";

    public synchronized static Long getTouristId(){
        File file = new File(getFilePath());
        Long touristId = null;
        try{
            BigInteger count=loadFile(file);
            count=count.add(new BigInteger("1"));//自增操作
            save(file,count);  //保存修改后的数据
            touristId = count.longValue();
        }catch (Exception e){
            touristId = new Date().getTime();
        }
        return touristId;
    }

    private static String getFilePath() {
        if(StringUtils.isEmpty(PATH)){
            String filePath = Thread.currentThread().getContextClassLoader().getResource("/").toString();
            PATH = filePath.substring(filePath.indexOf("/")+1,filePath.length())+COUNTER_TXT;
        }
        return PATH;
    }



    private static BigInteger loadFile(File file) {
        BigInteger count = null;
        try{
            if (file.exists()) {
                Scanner scan=new Scanner(new FileInputStream(file));//从文件中读取
                if(scan.hasNext()){   //存在内容
                    count= new BigInteger(scan.next());//将内容放到BigInteger类中
                }
                scan.close();
            }else{
                count=new BigInteger("0");
                save(file,count);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return count;
    }

    private static void save(File file, BigInteger count) {
        try{
            PrintStream ps=null;  //定义输出流对象
            ps=new PrintStream(new FileOutputStream(file));//打印流对象
            ps.println(count);//保存数据
            ps.close();//关闭输出流
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
