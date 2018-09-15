package cn.lonsun.lsrobot.entity;

import cn.lonsun.core.base.entity.ABaseEntity;

import javax.persistence.*;

/**
 * 搜索关键词过滤
 * @author zhongjun
 * @createtime 2017-11-20
 */
@Entity
@Table(name = "cms_ls_robot_filter")
public class LsRobotFilterEO extends ABaseEntity {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    /**关键字*/
    @Column(name = "key_words")
    private String keyWords;//开头提示问候语
    /**创建人姓名*/
    @Column(name = "create_user_name")
    private String createUserName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getKeyWords() {
        return keyWords;
    }

    public void setKeyWords(String keyWords) {
        this.keyWords = keyWords;
    }

    public String getCreateUserName() {
        return createUserName;
    }

    public void setCreateUserName(String createUserName) {
        this.createUserName = createUserName;
    }
}
