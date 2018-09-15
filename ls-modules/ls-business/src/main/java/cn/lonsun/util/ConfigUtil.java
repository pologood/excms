package cn.lonsun.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

//获取参数配置
@Component
public class ConfigUtil {

    @Value("${fileServerPath}")
    private String fileServerPath;// 文件服务器地址

	public String getFileServerPath() {
		return fileServerPath;
	}

	public void setFileServerPath(String fileServerPath) {
		this.fileServerPath = fileServerPath;
	}
}
