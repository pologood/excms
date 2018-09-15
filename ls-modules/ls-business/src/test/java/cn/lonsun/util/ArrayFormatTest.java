package cn.lonsun.util;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class ArrayFormatTest {

	@Before
	public void setUp() throws Exception {
	}

	/**
	 * 
	 * @Description 数组转换单元测试
	 * @author Hewbing
	 * @date 2015年10月21日 下午1:37:42
	 */
	@Test
	public void testArrayToString() {
		String af=ArrayFormat.ArrayToString(new String[]{});
		assertEquals("", af);
	}

}
