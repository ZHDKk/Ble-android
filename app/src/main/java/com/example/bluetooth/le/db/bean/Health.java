package com.example.bluetooth.le.db.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by zhdk on 2017/7/20.
 */
@Entity
public class Health {
    @Id(autoincrement = true)
    private Long id;
    @Property(nameInDb = "TITLE")
    private String title;
    @Property(nameInDb = "TIMES")
    private String times;
    @Generated(hash = 1885032873)
    public Health(Long id, String title, String times) {
        this.id = id;
        this.title = title;
        this.times = times;
    }
    @Generated(hash = 667493954)
    public Health() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getTitle() {
        return this.title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getTimes() {
        return this.times;
    }
    public void setTimes(String times) {
        this.times = times;
    }
}
