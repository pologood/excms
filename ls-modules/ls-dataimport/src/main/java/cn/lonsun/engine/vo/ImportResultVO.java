package cn.lonsun.engine.vo;

/**
 * 导入结果集
 * @param <P>
 */
public class ImportResultVO<P extends ExportQueryVO> {

    public ImportResultVO(CommandParamVO<P> queryVo, String resultMsg) {
        this.queryVo = queryVo;
        this.resultMsg = resultMsg;
    }

    private CommandParamVO<P> queryVo;

    private String resultMsg;

    public CommandParamVO<P> getQueryVo() {
        return queryVo;
    }

    public void setQueryVo(CommandParamVO<P> queryVo) {
        this.queryVo = queryVo;
    }

    public String getResultMsg() {
        return resultMsg;
    }

    public void setResultMsg(String resultMsg) {
        this.resultMsg = resultMsg;
    }
}
