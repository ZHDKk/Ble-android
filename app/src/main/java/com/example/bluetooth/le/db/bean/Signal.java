package com.example.bluetooth.le.db.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Property;

/**
 * Created by zhdk on 2017/8/16.
 */

@Entity
public class Signal {
    @Id(autoincrement = true)
    private Long id;
    private int singal;
    private String time;
    @Generated(hash = 264466214)
    public Signal(Long id, int singal, String time) {
        this.id = id;
        this.singal = singal;
        this.time = time;
    }
    @Generated(hash = 783005292)
    public Signal() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public int getSingal() {
        return this.singal;
    }
    public void setSingal(int singal) {
        this.singal = singal;
    }
    public String getTime() {
        return this.time;
    }
    public void setTime(String time) {
        this.time = time;
    }

}
