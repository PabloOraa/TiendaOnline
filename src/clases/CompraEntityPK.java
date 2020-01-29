package clases;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Objects;
/**
 * Implementaci&oacute;n de la clave primaria de CompraEntity
 * @version 2.0
 * @author Pablo Oraa L&oacute;pez
 */
public class CompraEntityPK implements Serializable {
    private int idShopping;
    private int idProduct;

    @Column(name = "IdShopping")
    @Id
    public int getIdShopping() {
        return idShopping;
    }

    public void setIdShopping(int idShopping) {
        this.idShopping = idShopping;
    }

    @Column(name = "IdProduct")
    @Id
    public int getIdProduct() {
        return idProduct;
    }

    public void setIdProduct(int idProduct) {
        this.idProduct = idProduct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CompraEntityPK that = (CompraEntityPK) o;
        return idShopping == that.idShopping &&
                idProduct == that.idProduct;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idShopping, idProduct);
    }
}
