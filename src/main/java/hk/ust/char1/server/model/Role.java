package hk.ust.char1.server.model;

import javax.persistence.*;

@Entity
@Table(name = "ROLES")
public class Role {

    @Id
    @GeneratedValue
    @Column(name = "ROLE_ID")
    private long id;

    @Column(name = "ROLE_NAME")
    private String role;

    public Role() {
    }

    public Role(String role){
        this.role = role;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

}
