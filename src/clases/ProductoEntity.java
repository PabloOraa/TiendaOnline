package clases;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "producto", schema = "accesodatos", catalog = "")
public class ProductoEntity {
    private int idProduct;
    private String description;
    private BigDecimal price;

    @Id
    @Column(name = "IdProduct")
    public int getIdProduct() {
        return idProduct;
    }

    public void setIdProduct(int idProduct) {
        this.idProduct = idProduct;
    }

    @Basic
    @Column(name = "Description")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Basic
    @Column(name = "Price")
    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    /*@SuppressWarnings("JpaAttributeMemberSignatureInspection")
    public String getStringPrice()
    {
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        return decimalFormat.format(price.doubleValue());
    }*/

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductoEntity that = (ProductoEntity) o;
        return idProduct == that.idProduct &&
                Objects.equals(description, that.description) &&
                Objects.equals(price, that.price);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idProduct, description, price);
    }

    @Override
    public String toString() {
        return idProduct + " - " + description +  " - " + price;
    }
}
