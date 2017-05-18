package com.app.km.request;

import java.sql.Timestamp;

/**
 * Created by Kamil-PC on 18.05.2017.
 */
public class RentRequest {
    private Timestamp start;
    private Timestamp end;
    private int users_iduser;
    private int car_idcar;

    public Timestamp getStart() {
        return start;
    }

    public void setStart(Timestamp start) {
        this.start = start;
    }

    public Timestamp getEnd() {
        return end;
    }

    public void setEnd(Timestamp end) {
        this.end = end;
    }

    public int getUsers_iduser() {
        return users_iduser;
    }

    public void setUsers_iduser(int users_iduser) {
        this.users_iduser = users_iduser;
    }

    public int getCar_idcar() {
        return car_idcar;
    }

    public void setCar_idcar(int car_idcar) {
        this.car_idcar = car_idcar;
    }
}