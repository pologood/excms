package cn.lonsun.util;

/**
 * <br/>
 *
 * @author wangss <br/>
 * @version v1.0 <br/>
 * @date 2015-12-14<br/>
 */

public class CC {
    public  static void main(String[] args){
        String str="";
        str=getLevel(2,str);
        System.out.print(str);
    }
    public static String getLevel(Integer level,String str){
        if(level==1){
            str+=level;
        }else{
            for(int i=level;i>=1;i--){
                str+=i+",";
                if(i==1){
                    str=str.substring(0,str.length()-1);
                }
            }
        }
        return str;

    }
}
