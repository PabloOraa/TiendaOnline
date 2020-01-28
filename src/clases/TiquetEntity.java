package clases;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "tiquet", schema = "accesodatos", catalog = "")
public class TiquetEntity {
    private Timestamp fechaHora;
    private int idShopping;
    private BigDecimal total;
    private int idUser;

    @Basic
    @Column(name = "FechaHora")
    public Timestamp getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(Timestamp fechaHora) {
        this.fechaHora = fechaHora;
    }

    @Id
    @Column(name = "IdShopping")
    public int getIdShopping() {
        return idShopping;
    }

    public void setIdShopping(int idShopping) {
        this.idShopping = idShopping;
    }

    @Basic
    @Column(name = "IdUser")
    public int getIdUser() {return idUser;}

    public void setIdUser(int idUser) {this.idUser = idUser;}

    @Basic
    @Column(name = "TOTAL")
    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TiquetEntity that = (TiquetEntity) o;
        return idShopping == that.idShopping && idUser == that.idUser &&
                Objects.equals(fechaHora, that.fechaHora) &&
                Objects.equals(total, that.total);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fechaHora, idShopping, total, idUser);
    }
}
