package com.example.bluetooth.le.db.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by zhdk on 2017/7/20.
 */
@Entity
public class Cutting {
    @Id(autoincrement = true)
    private Long id;
    @Property(nameInDb = "STARTTIME")
    private String startTime;
    @Property(nameInDb = "ENDTIME")
    private String endTime;
    @Generated(hash = 2103015336)
    public Cutting(Long id, String startTime, String endTime) {
        this.id = id;
        this.startTime = startTime;
        this.endTime = endTime;
    }
    @Generated(hash = 1448219365)
    public Cutting() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getStartTime() {
        return this.startTime;
    }
    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }
    public String getEndTime() {
        return this.endTime;
    }
    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
}
