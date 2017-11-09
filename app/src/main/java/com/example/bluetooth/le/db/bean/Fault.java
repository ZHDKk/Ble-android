package com.example.bluetooth.le.db.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by zhdk on 2017/7/20.
 */
@Entity
public class Fault {
    @Id(autoincrement = true)
    private Long id;
    @Property(nameInDb = "TIME")
    private String time;
    @Property(nameInDb = "STATUS")
    private String status;
    @Generated(hash = 827982338)
    public Fault(Long id, String time, String status) {
        this.id = id;
        this.time = time;
        this.status = status;
    }
    @Generated(hash = 703762044)
    public Fault() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getTime() {
        return this.time;
    }
    public void setTime(String time) {
        this.time = time;
    }
    public String getStatus() {
        return this.status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

}
