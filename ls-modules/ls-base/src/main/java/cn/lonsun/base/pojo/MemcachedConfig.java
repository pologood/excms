package cn.lonsun.base.pojo;

/**
 * Memcached配置信息
 *
 * @author xujh
 * @version 1.0
 * 2015年4月6日
 *
 */
public class MemcachedConfig {
	
	private String host;
	
	private int port;

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}
}
