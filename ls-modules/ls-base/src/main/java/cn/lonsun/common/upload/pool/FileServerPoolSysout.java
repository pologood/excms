package cn.lonsun.common.upload.pool;

import java.util.logging.Logger;

public class FileServerPoolSysout {
	
	private static Logger logger=Logger.getLogger("FileServerPoolSysout");
	
	public static void info(Object o){
		logger.info(o.toString());
	}
	public static void warn(Object o) {
		logger.warning(o.toString());
	}

}
