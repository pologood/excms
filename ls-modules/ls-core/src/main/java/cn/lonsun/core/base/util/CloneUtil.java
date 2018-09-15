package cn.lonsun.core.base.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class CloneUtil {
	
	/**
	 * 序列化克隆对象
	 *
	 * @param obj
	 * @return
	 */
	public static Object cloneObject(Object obj){
		if(obj==null){
			throw new NullPointerException();
		}
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Object target = null;
        try{
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(obj);
            oos.close();
            ByteArrayInputStream bais = new ByteArrayInputStream(
                    baos.toByteArray());
            ObjectInputStream ois = new ObjectInputStream(bais);
            target = ois.readObject();
            ois.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        return target;
	}
	
}
