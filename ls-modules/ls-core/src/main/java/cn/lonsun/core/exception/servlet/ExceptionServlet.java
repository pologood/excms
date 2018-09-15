package cn.lonsun.core.exception.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import cn.lonsun.core.exception.util.ExceptionTipsMessage;

/**
 * 异常提示信息加载器
 * @author 徐建华
 *
 */
public class ExceptionServlet extends HttpServlet {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;
	
	private String path = "/exceptionTipsMessage.properties";

	@Override
	public void init() throws ServletException {
		// 初始化异常提示信息到exceptionTipsMessages容器中
		InputStream in = null;
		Properties prop = new Properties();
		in = ExceptionTipsMessage.class
				.getResourceAsStream(path);
		try {
			prop.load(in);
		} catch (IOException e) {
			throw new RuntimeException();
		}
		Set<Object> keys = prop.keySet();
		if (keys != null && keys.size() > 0) {
			ExceptionTipsMessage etm = ExceptionTipsMessage.getInstance();
			for (Object key : keys) {
				if (key != null) {
					String value = prop.getProperty(key.toString()).trim();
					//设置异常提示信息
					etm.add(key.toString(), value);
				}
			}
			// 异常信息容器设置为不可修改
			etm.unmodifiable();
		}
	}
}
