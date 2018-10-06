package io.geewit.data.jpa.essential.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonView;
import io.geewit.core.jackson.view.View;
import io.geewit.data.jpa.essential.listener.PersistenceListener;
import javax.persistence.*;
import java.util.Date;

/**
 创建时间, 更新时间, 删除标志的公共实体类
 @author gelif
 @since  2015-5-18
 */
@MappedSuperclass
@EntityListeners({PersistenceListener.class})
public abstract class ListenedEntity implements java.io.Serializable {
    //protected Integer createrId;//创建人id
    /**
     * 创建时间
     */
    @JsonView(View.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08")
    protected Date createTime;
    //protected Integer updaterId;

    @JsonView(View.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08")
    protected Date updateTime;

    @JsonView(View.class)
    protected Boolean delFlag;

//    @Column(name = "creater_id")
//    public Integer getCreaterId() {
//        return createrId;
//    }

//    public void setCreaterId(Integer createrId) {
//        this.createrId = createrId;
//    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "create_time", columnDefinition = "datetime not null default current_timestamp", updatable = false)
    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

//    @Column(name = "updater_id")
//    public Integer getUpdaterId() {
//        return updaterId;
//    }
//
//    public void setUpdaterId(Integer updaterId) {
//        this.updaterId = updaterId;
//    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "update_time", columnDefinition = "datetime default current_timestamp on update current_timestamp")
    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    @Column(name = "del_flag", columnDefinition = "bool not null default false")
    public Boolean getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(Boolean delFlag) {
        this.delFlag = delFlag;
    }
}
