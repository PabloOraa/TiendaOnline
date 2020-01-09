package clases;

/**
 * Clase Product con los valores que podemos encontrar en la base de datos
 * @version 1.2
 * @author Pablo Oraa L�pez
 */
public class Product
{
	/**
	 * N�mero identificativo del producto en cuesti�n con el que se reconoce con la base de datos.
	 */
	private int idProduct;
	/**
	 * Descripci�n del producto como el nombre que este tiene.
	 */
	private String descripcion;
	/**
	 * Precio unitario del producto en cuesti�n.
	 */
	private double precio;
	
	/**
	 * Constructor por defecto que situa todos los valores a 0.
	 */
	public Product()
	{
		descripcion = "";
		precio = 0;
	}
	
	/**
	 * Constructor parametrizado de Producto que dar� valor al Id, la descripci�n y el precio.
	 * @param i N�mero que har� referencia al ID del producto. 
	 * @param desc Descripci�n del producto.
	 * @param price Precio del producto sin ning�n tipo de localizaci�n.
	 */
	public Product(int i, String desc, double price)
	{
		idProduct = i;
		descripcion = desc;
		precio = price;
	}
	
	/**
	 * Obtiene el Id del producto
	 * @return N�mero identificativo del producto
	 */
	public int getId()
	{
		return idProduct;
	}
	
	/**
	 * Obtiene la descripci�n (nombre) del producto.
	 * @return Cadena que contiene la descripci�n del producto
	 */
	public String getDescripcion()
	{
		return descripcion;
	}
	
	/**
	 * Asigna la descripci�n pasada por par�metro al producto en cuesti�n.
	 * @param descripcion Nombre y/o caracter�sticas del producto
	 */
	public void setDescripcion(String descripcion)
	{
		this.descripcion = descripcion;
	}
	
	/**
	 * Obtiene el precio asignado por �nidad de cada producto.
	 * @return Num�rico con decimales qu representa el precio por unidad.
	 */
	public double getPrecio()
	{
		return precio;
	}
	
	/**
	 * Asigna el precio pasado por par�metro al producto en cuesti�n.
	 * @param precio valor monetario del product
	 */
	public void setPrecio(double precio)
	{
		this.precio = precio;
	}
	
	@Override
	/**
	 * Obtiene la representaci�n del producto en euros bajo la estructura "<descripci�n> - <precio> euros"
	 * @see Object#toString()
	 */
	public String toString()
	{
		return descripcion + " - " + precio + " euros";
	}
}
