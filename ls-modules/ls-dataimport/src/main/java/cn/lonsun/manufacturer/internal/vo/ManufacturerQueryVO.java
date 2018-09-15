package cn.lonsun.manufacturer.internal.vo;

import cn.lonsun.common.vo.PageQueryVO;

/**
 * @author caohaitao
 * @Title: ManufacturerQueryVO
 * @Package cn.lonsun.manufacturer.internal.vo
 * @Description: 厂商管理查询VO
 * @date 2018/2/1 16:43
 */
public class ManufacturerQueryVO extends PageQueryVO{
    //厂商名称
    private String name;

    //产品名称
    private String productName;

    //唯一编码
    private String uniqueCode;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getUniqueCode() {
        return uniqueCode;
    }

    public void setUniqueCode(String uniqueCode) {
        this.uniqueCode = uniqueCode;
    }
}
