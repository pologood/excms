package cn.lonsun.demo.entity;

import cn.lonsun.core.base.entity.AMockEntity;

import javax.persistence.*;
import java.io.Serializable;

/**
 * demo实体类<br/>
 *
 * @author wangshibao <br/>
 * @version v1.0 <br/>
 * @date 2018-8-2<br/>
 */
@Entity
@Table(name = "demo_table1")
public class DemoEO extends AMockEntity implements Serializable {
    /**
     * serialVersionUID:TODO.
     */
    private static final long serialVersionUID = 179226142996763383L;
    //主键ID
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="id")
    private Long id;

    //code
    @Column(name="code")
    private String code;

    //name
    @Column(name="name")
    private String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
