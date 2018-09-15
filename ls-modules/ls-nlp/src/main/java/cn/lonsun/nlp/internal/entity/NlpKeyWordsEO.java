package cn.lonsun.nlp.internal.entity;

import cn.lonsun.core.base.entity.ABaseEntity;

import javax.persistence.*;

/**
 * 关键词表
 * @author: liuk
 * @version: v1.0
 * @date:2018/5/18 9:28
 */
@Entity
@Table(name="CMS_NLP_KEY_WORDS")
public class NlpKeyWordsEO extends ABaseEntity{


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private Long id;

    @Column(name = "NAME")
    private String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
