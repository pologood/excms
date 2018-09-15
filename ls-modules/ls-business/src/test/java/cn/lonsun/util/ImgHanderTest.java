package cn.lonsun.util;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.awt.*;
import java.io.*;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:applicationContext.xml")
public class ImgHanderTest {

	File file = new File("D:/bg.jpg");

	@Before
	public void setUp() throws Exception {

	}

	@Test
	public void testImgTrans() {
		InputStream input = null;
		try {
			input = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImgHander.ImgTrans(input, 255, 0,baos);
		byte[] b = baos.toByteArray();
		System.out.println(b.length);
	}

	/**
	 * 
	 * @Description 文字水印测试
	 * @author Hewbing
	 * @date 2015年10月22日 上午11:40:54
	 */
	@Test
	public void testWaterMarkByWord() {
		InputStream input = null;
		try {
			input = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		byte[] b = ImgHander.waterMarkByWord(input, Color.blue, "", 20,
				"CHINA", 0.7F, 1, 30, 5,"jpg");
		File file = new File("D:/11.JPG");
		OutputStream output;
		try {
			output = new FileOutputStream(file);
			@SuppressWarnings("resource")
			BufferedOutputStream bufferedOutput = new BufferedOutputStream(
					output);
			bufferedOutput.write(b);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Test
	public void testWaterMarkByPic() {
	}

}
