package cn.lonsun.content.vo;

/**
 * Created by lonsun on 2017-4-24.
 */
public class GuestBookCountVO {
    private Long receiveId;
    private Long createOrganId;

    public Long getCreateOrganId() {
        return createOrganId;
    }

    public void setCreateOrganId(Long createOrganId) {
        this.createOrganId = createOrganId;
    }

    public Long getReceiveId() {
        return receiveId;
    }

    public void setReceiveId(Long receiveId) {
        this.receiveId = receiveId;
    }
}
