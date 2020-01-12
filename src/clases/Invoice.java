package clases;

import java.util.HashMap;

/**
 * Clase Invoice con todos los valores necesarios para obtener las compras de un usuario en cuestión.
 * @version 1.3
 * @author Pablo Oraa López
 */
public class Invoice 
{
	/**
	 * Fecha en la que se realiza la compra en formato texto
	 */
	private String date;
	/**
	 * Lista de productos incluidos en esa compra.
	 */
	private HashMap<Product,Integer> productList;
	/**
	 * Coste total de la compra
	 */
	private double total;
	
	/**
	 * Constructor por defecto de la clase Invoice que crea un objeto vacio.
	 */
	public Invoice()
	{
		date = "";
		productList = new HashMap<Product,Integer>();
	}
	
	/**
	 * Constructor parametrizado que crea un objeto de la clase Invoice con fecha,+
	 * productos y total de la compra.
	 * @param date Fecha en la que se produce la compra en formato texto.
	 * @param productList Lista de productos en un HashMap donde la clave es el producto y el valor el número de unidades del mismo.
	 * @param total Coste total de la compra.
	 */
	public Invoice(String date, HashMap<Product,Integer> productList, double total)
	{
		this.date = date;
		this.productList = productList;
		this.total = total;
	}

	/**
	 * Devuelve la fecha en el formato en el que se haya guardado a la hora de crearlo
	 * @return Texto que indica año-mes-dia en la que se realizó la compra.
	 */
	public String getDate() 
	{
		return date;
	}

	/**
	 * Indica la fecha en la que se realizó la compra a partir del parámetro date.
	 * @param date Fecha en la que se realiza la compra con formato año-mes-día.
	 */
	public void setDate(String date) 
	{
		this.date = date;
	}

	/**
	 * Obtiene el mapa con los productos/unidades que están incluidos en esa compra.
	 * @return HashMap&#60;Product,Integer&#62; de la factura en cuestión.
	 */
	public HashMap<Product,Integer> getProductList()
	{
		return productList;
	}

	/**
	 * Indica la lista de productos y unidades que están incluidos en la compra.
	 * @param productList HashMap con los productos como clave y sus unidades compradas como valor.
	 */
	public void setProductList(HashMap<Product,Integer> productList) 
	{
		this.productList = productList;
	}

	/**
	 * Coste total que ha tenido la compra en cuestión.
	 * @return Double con el coste de la factura.
	 */
	public double getTotal() 
	{
		return total;
	}

	/**
	 * Indica el coste de la factura.
	 * @param total Cantidad total a pagar por quien realice la compra.
	 */
	public void setTotal(double total) 
	{
		this.total = total;
	}
	
	
	
}
