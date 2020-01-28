package clases;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "usuario", schema = "accesodatos", catalog = "accesodatos")
public class UsuarioEntity {
    private int idUser;
    private String username;
    private String password;

    public UsuarioEntity()
    {

    }

    public UsuarioEntity(int id, String user, String pass)
    {
        idUser = id;
        username = user;
        password = pass;
    }

    @Id
    @Column(name = "IdUser")
    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    @Basic
    @Column(name = "Username")
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Basic
    @Column(name = "Password")
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UsuarioEntity that = (UsuarioEntity) o;
        return idUser == that.idUser &&
                Objects.equals(username, that.username) &&
                Objects.equals(password, that.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idUser, username, password);
    }

    @Override
    public String toString() {
        return idUser + " - " + username + " - " + password;
    }
}
