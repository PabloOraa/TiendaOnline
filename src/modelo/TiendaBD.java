package modelo;

import clases.*;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Conexi&oacute;n con la base de datos y sus m&eacute;todos necesarios
 * @version 2.0.1
 * @author Pablo Oraa L&oacute;pez
 */
public class TiendaBD
{
	private Session session;

	/**
	 * Constructor de la tienda que inicializa la Sesi&oacute;n
	 */
	public TiendaBD() 
	{
		configSession();
	}

	private void configSession()
	{
		if(session != null)
			session.close();
		session = HibernateUtils.getSession();
	}

	/**
     * Obtiene el último ID de las Compras guardadas en la base de datos. Para ello, realizar&aacute; una consulta a todas las compras que haya
     * dentro de la tabla COMPRA. Para ello se utilizar&aacute; una sentencia hql que recuperar&aacute; el último ID disponible directamente.
     * @return Integer con el ID a ocupar en la inserci&oacute;n.
     */
    public int obtenerIdUltimo()
    {
    	configSession();
		return (Integer)session.createQuery("select max(idShopping) FROM TiquetEntity").uniqueResult();
    }
    
    /**
     * Se a&ntilde;ade una nueva linea de compra para un producto en concreto de una compra que se haya guardado con anterioridad.
     * @param idShoopping ID de la compra que se ha creado en el momento de dar a Comprar.
     * @param idProduct ID del producto que se ha comprado
     * @param unidades Unidades del producto comprado
     * @param total Coste total del producto que ha comprado el usuario.
     */
	public void addCompra(int idShoopping, int idProduct, int unidades, double total)
	{
		configSession();
		CompraEntity compraEntity = new CompraEntity();
		compraEntity.setIdShopping(idShoopping);
		compraEntity.setIdProduct(idProduct);
		compraEntity.setQuantity(unidades);
		compraEntity.setTotal(BigDecimal.valueOf(total));
		session.beginTransaction();

		session.save(compraEntity);
		session.getTransaction().commit();
	}

	/**
	 * Obtiene una lista de todos los productos que hay almacenados dentro de la base de datos. Primero realiza una consulta a la
	 * base de datos para obtener todos los resultados que contenga la tabla PRODUCTO. A continuaci&oacute;n, los va introduciendo dentro
	 * de una lista de Product.
	 * @return Lista de productos que dispone la Tienda Online. Si no hay ning&uacute;n resultado, devolver&aacute; una lista vac&iacute;Ma (no null).
	 */
	public List<ProductoEntity> obtenerProductos()
	{
		configSession();
		List<ProductoEntity> listaResul = new ArrayList<>();
		Query<?> query = session.createQuery("from ProductoEntity");
		for(Object o : query.list())
			listaResul.add((ProductoEntity)o);
		return listaResul;
	}
	
	/**
	 * Obtiene un usuario de la base de datos con un nombre de usuario y una contrase&ntilde;a determinadas.
	 * @param user Nombre de usuario a buscar.
	 * @param pass Contrase&ntilde;a a buscar.
	 * @return Usuario que contiene ese nombre de usuario y esa contrase?a. Al ser un nombre de usuario único e irrepetible,
	 * solo podr&aacute; devolver uno o ning&uacute;n resultado.
	 */
	public UsuarioEntity recuperarUsuario(String user, String pass)
	{
		configSession();
		Query<?> query = session.createQuery("from UsuarioEntity where username=?1 and password=?2");
		query.setParameter(1,user);
		query.setParameter(2,pass);
		return (UsuarioEntity)query.uniqueResult();
	}

	/**
	 * A&ntilde;ade un usuario nuevo a la base de datos en funci&oacute;n del nombre de usuario y la contrase&ntilde;a que se le pase por par&aacute;metro.
	 * @param user Nombre de usuario que se desea guardar en la base de datos. Debe ser &uacute;nico para cada usuario.
	 * @param pass Contrase&ntilde;a del usuario que se desea guardar en la base de datos para completar el registro.
	 * @return N&uacute;mero de filas que se han insertado. Si todo ha ido correctamente deber&aacute; devolver 1 fila, mientras que si ya existe
	 * el nombre de usuario en cuesti&oacute;n, devolver&aacute; 0 usuarios.
	 */
	public int addUsuario(String user, String pass)
	{
		configSession();
		UsuarioEntity usuarioEntity = new UsuarioEntity();
		usuarioEntity.setUsername(user);
		usuarioEntity.setPassword(pass);
		session.beginTransaction();
		session.save(usuarioEntity);
		session.getTransaction().commit();
		return 1;
	}
	
	/**
	 * A&ntilde;ade un nuevo tiquet a la base de datos con la fecha y hora de la compra, el usuario y el total que le ha costado la
	 * compra al cliente
	 * @param user Usuario que realiza la compra
	 * @param total Cantidad total gastada por el cliente
	 * @return ID del Tiquet que se acaba de crear
	 */
	public int addTiquet(UsuarioEntity user, double total)
	{
		configSession();
		TiquetEntity tiquetEntity = new TiquetEntity();
		tiquetEntity.setFechaHora(new Timestamp(new java.util.Date().getTime()));
		tiquetEntity.setTotal(BigDecimal.valueOf(total));
		tiquetEntity.setIdUser(user.getIdUser());
		session.beginTransaction();
		session.save(tiquetEntity);
		session.getTransaction().commit();
		return obtenerIdUltimo();
	}
	
	/**
	 * Recupera el producto que est&aacute; asociado al ID que se le haya pasado por par&aacute;metro.
	 * @param productID ID del producto a buscar
	 * @return Producto encontrado en la base de datos.
	 */
	public ProductoEntity getProduct(int productID)
	{
		configSession();
		Query<?> query = session.createQuery("from ProductoEntity where idProduct = ?1");
		query.setParameter(1,productID);
		return (ProductoEntity)query.uniqueResult();
	}

	/**
	 * Obtiene todas las facturas del cliente que se haya pasado por par&aacute;metro utilizando Tiquet y Compra.
	 * Para ello, hace uso de una consulta que una Tiquet y Compra por el IDShopping y crea una lista de 
	 * Invoice para indicar fecha, productos y unidades de cada producto y coste total.
	 * @param userID ID del usuario del que se quieren obtener las facturas
	 * @return Lista de Invoice con todos los datos pasados del usuario.
	 */
	public List<Invoice> getInvoice(int userID)
	{
		configSession();
		List<Invoice> invoiceList = new ArrayList<>();
		Query<?> query = session.createQuery(" from TiquetEntity where idUser=?1");
		query.setParameter(1,userID);
		for(Object o : query.list())
		{
			Query<?> queryProducts = session.createQuery("select producto.idProduct, producto.description, producto.price, compra.quantity " +
															"from ProductoEntity as producto, CompraEntity as compra " +
															"where producto.idProduct = compra.idProduct and idShopping = ?1");

			queryProducts.setParameter(1, ((TiquetEntity)o).getIdShopping());
			HashMap<ProductoEntity, Integer> productList = new HashMap<>();
			for(Object ob : queryProducts.list())
			{
				Object[] obs = ((Object[])ob);
				ProductoEntity productoEntity = new ProductoEntity();
				productoEntity.setDescription((String) obs[1]);
				productoEntity.setIdProduct((Integer) obs[0]);
				productoEntity.setPrice((BigDecimal) obs[2]);
				productList.put(productoEntity, (Integer)obs[3]);
			}
			invoiceList.add(new Invoice(((TiquetEntity) o).getFechaHora().toString(),productList,((TiquetEntity) o).getTotal().doubleValue()));
		}
		return invoiceList;
	}
}
