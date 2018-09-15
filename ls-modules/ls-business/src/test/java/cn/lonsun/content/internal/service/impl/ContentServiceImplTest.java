package cn.lonsun.content.internal.service.impl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import cn.lonsun.content.internal.service.IBaseContentService;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="classpath:applicationContext.xml")
public class ContentServiceImplTest {

	@Autowired
	private IBaseContentService baseContentService;
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * 
	 * @Description 新闻复制单元测试
	 * @author Hewbing
	 * @date 2015年10月21日 上午10:22:31
	 */
	@Test
	public void testSaveCopy() {
		//boolean b = baseContentService.saveCopy(new Long[]{1139L},4514L,null,null);
		//Assert.assertTrue(b);
	}

}
