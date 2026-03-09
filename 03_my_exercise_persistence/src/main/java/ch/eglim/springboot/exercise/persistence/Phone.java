package ch.eglim.springboot.exercise.persistence;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Phone {

    @Id
    private int id;
    private String phonenumber;
    private String type;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
