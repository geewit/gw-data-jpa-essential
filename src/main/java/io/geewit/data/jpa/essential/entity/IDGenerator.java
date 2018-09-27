package io.geewit.data.jpa.essential.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 id生成表
 @author gelif
 @since  2015-5-18
 */
@Entity
@Table(name = "id_generators")
public class IDGenerator implements java.io.Serializable {

    private static final long serialVersionUID = 1L;

    private String tableName;
    @JsonIgnore
    private Long currentValue;
    private String formatter;

    @SuppressWarnings({"unused"})
    @Id
    @Column(name = "table_name", columnDefinition = "varchar(255)")
    public String getTableName() {
        return tableName;
    }

    @SuppressWarnings({"unused"})
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    @SuppressWarnings({"unused"})
    @Column(name = "current_value", columnDefinition = "bigint(20)")
    public Long getCurrentValue() {
        return currentValue;
    }

    @SuppressWarnings({"unused"})
    public void setCurrentValue(Long currentValue) {
        this.currentValue = currentValue;
    }

    @SuppressWarnings({"unused"})
    @Column(name = "formatter", columnDefinition = "varchar(255)")
    public String getFormatter() {
        return formatter;
    }

    @SuppressWarnings({"unused"})
    public void setFormatter(String formatter) {
        this.formatter = formatter;
    }


}
