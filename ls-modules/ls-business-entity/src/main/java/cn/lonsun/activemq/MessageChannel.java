package cn.lonsun.activemq;

/**
 * 消息频道
 * @author zhongjun
 */
public enum MessageChannel {

    STATIC_GENERATE_QUEUE("EX8.STATIC.GENERATE.QUEUE","生成静态队列"),

    STATIC_CANCEL_QUEUE("EX8.STATIC.CANCEL.QUEUE","生成静态取消队列"),

    MESSAGE_QUEUE("EX8.MESSAGE.QUEUE","系统消息队列"),

    IMPORT_PUBLIC_QUEUE("EX8.IMPORT.PUBLIC.QUEUE","信息公开导入消息队列"),
    ;

    private String channel;

    private String channelName;

    MessageChannel(String channel, String channelName) {
        this.channel = channel;
        this.channelName = channelName;
    }

    @Override
    public String toString() {
        return this.channel;
    }

    public String getChannel() {
        return channel;
    }

    public String getChannelName() {
        return channelName;
    }

}
