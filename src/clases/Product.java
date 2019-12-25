package clases;

public class Product
{
	/**
	 * Número identificativo del producto en cuestión con el que se reconoce con la base de datos.
	 */
	private int idProduct;
	/**
	 * Descripción del producto como el nombre que este tiene.
	 */
	private String descripcion;
	/**
	 * Precio unitario del producto en cuestión.
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
	 * Constructor parametrizado de Producto que dará valor al Id, la descripción y el precio.
	 * @param i Número que hará referencia al ID del producto. 
	 * @param desc Descripción del producto.
	 * @param price Precio del producto sin ningún tipo de localización.
	 */
	public Product(int i, String desc, double price)
	{
		idProduct = i;
		descripcion = desc;
		precio = price;
	}
	
	/**
	 * Obtiene el Id del producto
	 * @return Número identificativo del producto
	 */
	public int getId()
	{
		return idProduct;
	}
	
	/**
	 * Obtiene la descripción (nombre) del producto.
	 * @return Cadena que contiene la descripción del producto
	 */
	public String getDescripcion()
	{
		return descripcion;
	}
	
	/**
	 * Asigna la descripción pasada por parámetro al producto en cuestión.
	 * @param descripcion Nombre y/o características del producto
	 */
	public void setDescripcion(String descripcion)
	{
		this.descripcion = descripcion;
	}
	
	/**
	 * Obtiene el precio asignado por únidad de cada producto.
	 * @return Numérico con decimales qu representa el precio por unidad.
	 */
	public double getPrecio()
	{
		return precio;
	}
	
	/**
	 * Asigna el precio pasado por parámetro al producto en cuestión.
	 * @param precio valor monetario del product
	 */
	public void setPrecio(double precio)
	{
		this.precio = precio;
	}
	
	@Override
	/**
	 * Obtiene la representación del producto en euros bajo la estructura "<descripción> - <precio> euros"
	 * @see Object#toString()
	 */
	public String toString()
	{
		return descripcion + " - " + precio + " euros";
	}
}
