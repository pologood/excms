package cn.lonsun.content.publicInfo.vo;

/**
 * Created by 1960274114 on 2016-9-23.
 */
public class PublicApplyStatisticsVO {
    private Long not_handle = 0L;//尚未办理
    private Long agree_public = 0L;//同意公开

    private Long agree_part_public = 0L;//同意部分公开
    private Long notify_change_repair = 0L;//告知作出更改补充数
    private Long notify_other_way_handle = 0L;//告知通过其他途径办理数


    private Long info_not_exist = 0L;//申请信息不存在
    private Long not_the_organ_business = 0L;//非本部门掌握
    private Long apply_content_not_sure = 0L;//申请内容不明确
    private Long not_agree_public = 0L;//不予公开
    private Long infoTotal = 0L;//收到总申请数
    private Long driving_public = 0L;//已主动公开

    public Long getInfoTotal() {
        return infoTotal;
    }

    public void setInfoTotal(Long infoTotal) {
        this.infoTotal = infoTotal;
    }

    public Long getDriving_public() {
        return driving_public;
    }

    public void setDriving_public(Long driving_public) {
        this.driving_public = driving_public;
    }

    public Long getNot_handle() {
        return not_handle;
    }

    public void setNot_handle(Long not_handle) {
        this.not_handle = not_handle;
    }

    public Long getAgree_public() {
        return agree_public;
    }

    public void setAgree_public(Long agree_public) {
        this.agree_public = agree_public;
    }

    public Long getNot_agree_public() {
        return not_agree_public;
    }

    public void setNot_agree_public(Long not_agree_public) {
        this.not_agree_public = not_agree_public;
    }

    public Long getApply_content_not_sure() {
        return apply_content_not_sure;
    }

    public void setApply_content_not_sure(Long apply_content_not_sure) {
        this.apply_content_not_sure = apply_content_not_sure;
    }

    public Long getNot_the_organ_business() {
        return not_the_organ_business;
    }

    public void setNot_the_organ_business(Long not_the_organ_business) {
        this.not_the_organ_business = not_the_organ_business;
    }

    public Long getInfo_not_exist() {
        return info_not_exist;
    }

    public void setInfo_not_exist(Long info_not_exist) {
        this.info_not_exist = info_not_exist;
    }

    public Long getAgree_part_public() {
        return agree_part_public;
    }

    public void setAgree_part_public(Long agree_part_public) {
        this.agree_part_public = agree_part_public;
    }

    public Long getNotify_change_repair() {
        return notify_change_repair;
    }

    public void setNotify_change_repair(Long notify_change_repair) {
        this.notify_change_repair = notify_change_repair;
    }

    public Long getNotify_other_way_handle() {
        return notify_other_way_handle;
    }

    public void setNotify_other_way_handle(Long notify_other_way_handle) {
        this.notify_other_way_handle = notify_other_way_handle;
    }
}
