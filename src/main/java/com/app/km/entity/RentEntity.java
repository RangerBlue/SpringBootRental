package com.app.km.entity;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Created by Kamil-PC on 17.05.2017.
 */
@Entity
@Table(name = "rent", schema = "mydb", catalog = "")
public class RentEntity {
    private int idrent;
    private Timestamp start;
    private Timestamp end;
    private UsersEntity userEntity;
    private CarEntity carEntity;

    @Id
    @Column(name = "idrent")
    public int getIdrent() {
        return idrent;
    }

    public void setIdrent(int idrent) {
        this.idrent = idrent;
    }

    @Basic
    @Column(name = "start")
    public Timestamp getStart() {
        return start;
    }

    public void setStart(Timestamp start) {
        this.start = start;
    }

    @Basic
    @Column(name = "end")
    public Timestamp getEnd() {
        return end;
    }

    public void setEnd(Timestamp end) {
        this.end = end;
    }

    @ManyToOne
    @JoinColumn(name = "users_idusers")
    public UsersEntity getUserEntity() {
        return userEntity;
    }

    public void setUserEntity(UsersEntity user) {
        this.userEntity = user;
    }

    @ManyToOne
    @JoinColumn(name = "car_idcar")
    public CarEntity getCarEntity() {
        return carEntity;
    }

    public void setCarEntity(CarEntity car) {
        this.carEntity = car;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RentEntity that = (RentEntity) o;

        if (idrent != that.idrent) return false;
        if (start != null ? !start.equals(that.start) : that.start != null) return false;
        return end != null ? end.equals(that.end) : that.end == null;
    }

    @Override
    public int hashCode() {
        int result = idrent;
        result = 31 * result + (start != null ? start.hashCode() : 0);
        result = 31 * result + (end != null ? end.hashCode() : 0);
        return result;
    }
}
