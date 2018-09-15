package cn.lonsun.staticcenter.exproject.ahszjj;

import cn.lonsun.content.interview.internal.entity.InterviewInfoEO;
import cn.lonsun.staticcenter.util.JdbcUtils;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by 1960274114 on 2016-10-12.
 */
public abstract class AbSyncInfo {

    protected final String imp_tag = "ahszjj导入";
    protected long limitSize =Long.MAX_VALUE;
    protected long step = 0;

    protected JdbcUtils jdbcUtils;
    protected Long siteId;
    protected Long createUserId;
    protected Long curColumdId;

    //默认发布
    protected Integer isPublish =1;
    //默认开启
    protected Integer isOpen = InterviewInfoEO.Status.Yes.getStatus();

    public AbSyncInfo(JdbcUtils jdbcUtils, Long siteId, Long createUserId, Long curColumdId) {
        this.jdbcUtils = jdbcUtils;
        this.siteId = siteId;
        this.createUserId = createUserId;
        this.curColumdId = curColumdId;
    }

    protected Date getDateValue(Object idate) {
        if(null !=idate){
            if(idate instanceof Timestamp){
                Timestamp date = (Timestamp)idate;
                return date;
            }else if(idate instanceof java.sql.Date){
                java.sql.Date date = (java.sql.Date)idate;
                return date;
            }else if(idate instanceof java.util.Date){
                java.util.Date date = (java.util.Date)idate;
                return date;
            }else if(idate instanceof String){
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                try {
                    java.util.Date date = sdf.parse(idate.toString());
                    return date;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    protected String formatDate(Object date,String format){
        Date d = null;
        if(null == date) return null;
        if(date instanceof Timestamp){
            d = (Timestamp)date;
        }else if(date instanceof java.sql.Date){
            d = (java.sql.Date)date;
        }else if(date instanceof java.util.Date){
            d = (java.util.Date)date;
        }else if(date instanceof String){
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            try {
                d = sdf.parse(date.toString());
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        if(null !=d){
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            return sdf.format(d);
        }
        return null;
    }
}
