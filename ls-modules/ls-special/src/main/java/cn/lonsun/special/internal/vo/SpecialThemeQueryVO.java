package cn.lonsun.special.internal.vo;

import cn.lonsun.common.vo.PageQueryVO;
import cn.lonsun.special.internal.entity.SpecialSkinsEO;

import java.util.List;

/**
 * Created by doocal on 2016-10-15.
 */
public class SpecialThemeQueryVO extends PageQueryVO {

    public enum TimesType{
        oneWeekAgo("1周内"),
        twoWeekAgo("2周内"),
        oneMonthAgo("1个月内"),
        twoMonthAgo("2个月内"),
        threeMonthAgo("3个月内"),
        halfYearAgo("半年内"),
        yearAgo("一年内")
        ;

        private String text;

        TimesType(String text) {
            this.text = text;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getName(){
            return this.name();
        }

        @Override
        public String toString() {
            return name();
        }
    }

    private Long id;

    private String name;

    private Long siteId;

    private List<SpecialSkinsEO> skins;

    private Long themeId;

    private Long specialType;

    private String timesAgo;

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

    public Long getSiteId() {
        return siteId;
    }

    public void setSiteId(Long siteId) {
        this.siteId = siteId;
    }

    public List<SpecialSkinsEO> getSkins() {
        return skins;
    }

    public void setSkins(List<SpecialSkinsEO> skins) {
        this.skins = skins;
    }

    public Long getThemeId() {
        return themeId;
    }

    public void setThemeId(Long themeId) {
        this.themeId = themeId;
    }

    public Long getSpecialType() {
        return specialType;
    }

    public void setSpecialType(Long specialType) {
        this.specialType = specialType;
    }

    public String getTimesAgo() {
        return timesAgo;
    }

    public void setTimesAgo(String timesAgo) {
        this.timesAgo = timesAgo;
    }
}
