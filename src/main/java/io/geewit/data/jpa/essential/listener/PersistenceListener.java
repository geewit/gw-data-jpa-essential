package io.geewit.data.jpa.essential.listener;

import io.geewit.data.jpa.essential.entity.ListenedEntity;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.util.Calendar;
import java.util.Date;

/**
 创建时间, 更新时间, 删除标志自动保存/更新的JPA 监听器
 @author geewit
 @since  2015-05-18
 */
public class PersistenceListener {
    @PreUpdate
    @PrePersist
    public void prePersist(ListenedEntity listenedEntity) {
        if(listenedEntity.getOperateTime() == null) {
            Date now = Calendar.getInstance().getTime();
            listenedEntity.setOperateTime(now);
        }
        if(listenedEntity.getDelFlag() == null) {
            listenedEntity.setDelFlag(false);
        }
    }
}
