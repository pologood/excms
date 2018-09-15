package cn.lonsun.util;


import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ConvertFlvTest {

	@Before
	public void setUp() throws Exception {
	}

	/**
	 * 
	 * @Description 转换为AVI
	 * @author Hewbing
	 * @date 2015年10月21日 下午2:03:53
	 */
	@Test
	public void testProcessAVI () {
	}
	
	/**
	 * 
	 * @Description 视频转换
	 * @author Hewbing
	 * @date 2015年10月21日 下午2:01:04
	 */
	@Test
	public void testConvert() {
		boolean result = ConvertFlv.convert("D:/videoTrans/1234.wmv", "D:/videoTrans/target/11.flv");
		Assert.assertTrue(result);
	}

}
