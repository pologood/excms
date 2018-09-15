package cn.lonsun.rbac.vo;

/**
 * 系统配置Vo
 * @author xujh
 * @version 1.0
 *
 */
public class SystemConfigVO {

	// 附件大小-单位kb
	private String appendixSize = "1024";
	// 是否附件加密，0：不加密，1：加密
	private String appendixEncryption = "0";
	// 是否邮件内容是否加密
	private String emailContentEncryption = "0";
	// 是否知识信息内容是否加密
	private String knowledgeEncryption = "0";
	// 是否支持日志回收
	private String logRecovery = "0";
	// 是否支持手写签名
	private String sign = "0";
	// 是否开启短信提醒
	private String smsReminder = "0";
	// 是否开启电子签章
	private String signatures = "0";
	// 电子签章供应商
	private String signaturesSupplier;
	
	private String maxCapacity = "0";
	public String getAppendixSize() {
		return appendixSize;
	}
	public void setAppendixSize(String appendixSize) {
		this.appendixSize = appendixSize;
	}
	public String getAppendixEncryption() {
		return appendixEncryption;
	}
	public void setAppendixEncryption(String appendixEncryption) {
		this.appendixEncryption = appendixEncryption;
	}
	public String getEmailContentEncryption() {
		return emailContentEncryption;
	}
	public void setEmailContentEncryption(String emailContentEncryption) {
		this.emailContentEncryption = emailContentEncryption;
	}
	public String getKnowledgeEncryption() {
		return knowledgeEncryption;
	}
	public void setKnowledgeEncryption(String knowledgeEncryption) {
		this.knowledgeEncryption = knowledgeEncryption;
	}
	public String getLogRecovery() {
		return logRecovery;
	}
	public void setLogRecovery(String logRecovery) {
		this.logRecovery = logRecovery;
	}
	public String getSign() {
		return sign;
	}
	public void setSign(String sign) {
		this.sign = sign;
	}
	public String getSmsReminder() {
		return smsReminder;
	}
	public void setSmsReminder(String smsReminder) {
		this.smsReminder = smsReminder;
	}
	public String getSignatures() {
		return signatures;
	}
	public void setSignatures(String signatures) {
		this.signatures = signatures;
	}
	public String getSignaturesSupplier() {
		return signaturesSupplier;
	}
	public void setSignaturesSupplier(String signaturesSupplier) {
		this.signaturesSupplier = signaturesSupplier;
	}
	public String getMaxCapacity() {
		return maxCapacity;
	}
	public void setMaxCapacity(String maxCapacity) {
		this.maxCapacity = maxCapacity;
	}
}
