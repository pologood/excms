package cn.lonsun.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Util {
    public static String getMd5ByFile(File file) {
        String value = null;
        FileInputStream in = null;
    try {
        in = new FileInputStream(file);
        MappedByteBuffer byteBuffer = in.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, file.length());
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        md5.update(byteBuffer);
        BigInteger bi = new BigInteger(1, md5.digest());
        value = bi.toString(16);
    } catch (Exception e) {
        e.printStackTrace();
    } finally {
            if(null != in) {
                try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    return value;
    }
    
    public static String getMd5ByByte(byte[] bt){
        String value = null;
        MessageDigest md5;
		try {
			md5 = MessageDigest.getInstance("MD5");
	        md5.update(bt);
	        BigInteger bi = new BigInteger(1, md5.digest());
	        value = bi.toString(16);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		return value;
    	
    }
    
    public static void main(String[] args) {
    	String str=getMd5ByByte(new byte[]{1,2,1,1});
    	System.out.println(str);
	}
}
