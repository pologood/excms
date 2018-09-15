package cn.lonsun.key;

/**
 * @author gu.fei
 * @version 2016-07-21 11:24
 */
public class PrimaryKeyUtil {
	
	/**
	 * @param name
	 * @return
	 */
	public static Long getPrimaryKey(String name,String idName) {
		SingleSequence sequence = cn.lonsun.key.SequenceFactory.getSequence(name);
	    long num = sequence.getNextVal(name,idName);
		return num;
	}

	/**
	 * @param clazz
	 * @return
	 */
	public static Long getPrimaryKey(Class<?> clazz,String idName) {
		SingleSequence sequence = SequenceFactory.getSequence(clazz.getSimpleName());
	    long num = sequence.getNextVal(clazz.getSimpleName(),idName);
		return num;
	}
}
