package com.example.bluetooth.le.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by zhdk on 2017/7/20.
 */
@Entity
public class Charge {
    @Id(autoincrement = true)
    private Long id;
    @Property(nameInDb = "ENDTIME")
    private String endTime;
    @Property(nameInDb = "VOLTAGE")
    private String voltage;
    @Generated(hash = 719521005)
    public Charge(Long id, String endTime, String voltage) {
        this.id = id;
        this.endTime = endTime;
        this.voltage = voltage;
    }
    @Generated(hash = 1698010988)
    public Charge() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getEndTime() {
        return this.endTime;
    }
    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
    public String getVoltage() {
        return this.voltage;
    }
    public void setVoltage(String voltage) {
        this.voltage = voltage;
    }
}
