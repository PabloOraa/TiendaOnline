package clases;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * POJO Compra de la base de datos
 * @version 2.0
 * @author Pablo Oraa L&oacute;
 */
@Entity
@Table(name = "compra", schema = "accesodatos", catalog = "")
@IdClass(CompraEntityPK.class)
public class CompraEntity {
    private int idShopping;
    private int idProduct;
    private Integer quantity;
    private BigDecimal total;

    @Id
    @Column(name = "IdShopping")
    public int getIdShopping() {
        return idShopping;
    }

    public void setIdShopping(int idShopping) {
        this.idShopping = idShopping;
    }

    @Id
    @Column(name = "IdProduct")
    public int getIdProduct() {
        return idProduct;
    }

    public void setIdProduct(int idProduct) {
        this.idProduct = idProduct;
    }

    @Basic
    @Column(name = "Quantity")
    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    @Basic
    @Column(name = "Total")
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
        CompraEntity that = (CompraEntity) o;
        return idShopping == that.idShopping &&
                idProduct == that.idProduct &&
                Objects.equals(quantity, that.quantity) &&
                Objects.equals(total, that.total);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idShopping, idProduct, quantity, total);
    }
}
