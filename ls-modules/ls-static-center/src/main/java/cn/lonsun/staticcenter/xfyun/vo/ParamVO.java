package cn.lonsun.staticcenter.xfyun.vo;

/**
 * Created by huangxx on 2018/7/4.
 */
public class ParamVO {

    private byte[] bytes;//二进制

    private String text;

    private String engineType;//引擎类型，可选值：sms16k（16k采样率普通话音频）、sms8k（8k采样率普通话音频）等，其他参见引擎类型说明

    private String aue;//音频编码，可选值：raw（未压缩的pcm或wav格式）、speex（speex格式）、speex-wb（宽频speex格式）

    private String speexSize;//speex音频帧率，speex音频必传

    private String scene;//情景模式

    private String vadEos;//后端点检测（单位：ms），默认1800

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getEngineType() {
        return engineType;
    }

    public void setEngineType(String engineType) {
        this.engineType = engineType;
    }

    public String getAue() {
        return aue;
    }

    public void setAue(String aue) {
        this.aue = aue;
    }


    public String getScene() {
        return scene;
    }

    public void setScene(String scene) {
        this.scene = scene;
    }


    public String getSpeexSize() {
        return speexSize;
    }

    public void setSpeexSize(String speexSize) {
        this.speexSize = speexSize;
    }

    public String getVadEos() {
        return vadEos;
    }

    public void setVadEos(String vadEos) {
        this.vadEos = vadEos;
    }
}
