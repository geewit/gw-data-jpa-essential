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
    /**
     * 操作人id
     */
    @JsonView(View.class)
    protected String operatorId;
    /**
     * 操作时间
     */
    @JsonView(View.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08")
    protected Date operateTime;

    @JsonView(View.class)
    protected Boolean delFlag;

    @Column(name = "operate_id")
    public String getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(String operatorId) {
        this.operatorId = operatorId;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "operate_time", columnDefinition = "datetime not null default current_timestamp on update current_timestamp")
    public Date getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(Date operateTime) {
        this.operateTime = operateTime;
    }

    @Column(name = "del_flag", columnDefinition = "bool not null default false")
    public Boolean getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(Boolean delFlag) {
        this.delFlag = delFlag;
    }
}
