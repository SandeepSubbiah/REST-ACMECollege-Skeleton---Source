package acmecollege.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "security_role")
@NamedQuery(name = SecurityRole.FIND_BY_NAME, query = "SELECT sr FROM SecurityRole sr WHERE sr.roleName = :roleName")
public class SecurityRole implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final String FIND_BY_NAME = "SecurityRole.findByName";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    protected int id;

    @Column(name = "role_name", nullable = false, unique = true, length = 50)
    protected String roleName;

    @ManyToMany(mappedBy = "roles")
    protected Set<SecurityUser> users = new HashSet<>();

    public SecurityRole() {
        super();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public Set<SecurityUser> getUsers() {
        return users;
    }

    public void setUsers(Set<SecurityUser> users) {
        this.users = users;
    }

    public void addUserToRole(SecurityUser user) {
        getUsers().add(user);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        return prime * result + Objects.hash(getId());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (obj instanceof SecurityRole otherSecurityRole) {
            return Objects.equals(this.getId(), otherSecurityRole.getId());
        }
        return false;
    }

    @Override
    public String toString() {
        return "SecurityRole [id=" + id + ", roleName=" + roleName + "]";
    }
}
