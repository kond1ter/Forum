package study.konditer.forum.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import study.konditer.forum.model.emun.UserRoles;

@Entity
@Table(name = "roles")
public class Role extends BaseEntity {

    private UserRoles name;

    public Role() {
    }

    public Role(UserRoles name) {
        this.name = name;
    }

    @Enumerated(EnumType.STRING)
    @Column(unique = true)
    public UserRoles getName() {
        return name;
    }

    public void setName(UserRoles name) {
        this.name = name;
    }
}
