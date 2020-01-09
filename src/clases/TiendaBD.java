package clases;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

/**
 * Conexi�n con la base de datos y sus m�todos necesarios
 * @version 1.2
 * @author Pablo Oraa L�pez
 */
public class TiendaBD
{
	/**
	 * Contexto del proyecto.
	 */
	private Context ctx;
	/**
	 * Fuente de los datos que har� uso del jdbc que se haya indicado dentro de META-INF en el Contexto gracias a Context.
	 */
	DataSource fuenteDatos;
	
	/**
	 * Constructor de la tienda que inicializa el Contexto y el DataSource seg�n lo indicado dentro de context.xml (en este caso
	 * java:comp/env/jdbc/miDataSource")
	 */
	public TiendaBD() 
	{
		try
		{
			ctx = new InitialContext();
			fuenteDatos = (DataSource) ctx.lookup("java:comp/env/jdbc/miDataSource");
		} catch (NamingException e)
		{
			e.printStackTrace();
		}
	}
	
	/**
     * Obtiene el �ltimo ID de las Compras guardadas en la base de datos. Para ello, realizar� una consulta a todas las compras que haya
     * dentro de la tabla COMPRA. A partir de aqui, saltar� a la �ltima linea de resultados y mirar� la fila que es. En caso de ser -1, es decir,
     * no tener elementos la tabla, devolver� 0. Si no, devolver� el �ltimo n�mero ocupado + 1.
     * @return Integer con el ID a ocupar en la inserci�n.
     */
    public int obtenerIdUltimo()
    {
		int id = 0;
		String getId = "SELECT IdShopping FROM TIQUET";
		try (Connection con = fuenteDatos.getConnection();
			 PreparedStatement ps1 = con.prepareStatement(getId, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);)
		{
			ResultSet rs = ps1.executeQuery();
			rs.last();
			if(rs.getRow() != -1)
				id = rs.getInt("IdShopping");
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
		return id;
    }
    
    /**
     * Se a�ade una nueva linea de compra para un producto en concreto de una compra que se haya guardado con anterioridad.
     * @param idShoopping ID de la compra que se ha creado en el momento de dar a Comprar.
     * @param idProduct ID del producto que se ha comprado
     * @param unidades Unidades del producto comprado
     * @param total Coste total del producto que ha comprado el usuario.
     */
	public void addCompra(int idShoopping, int idProduct, int unidades, double total)
	{
		String addLineaCompra = "INSERT INTO COMPRA(IDSHOPPING,IDPRODUCT,QUANTITY,TOTAL) "
							  + "VALUES (" + idShoopping + "," + idProduct + "," + unidades +"," + total + ")";
		try(Connection con = fuenteDatos.getConnection();
			PreparedStatement ps1 = con.prepareStatement(addLineaCompra);)
		{
			ps1.executeUpdate();
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
	}

	/**
	 * Obtiene una lista de todos los productos que hay almacenados dentro de la base de datos. Primero realiza una consulta a la
	 * base de datos para obtener todos los resultados que contenga la tabla PRODUCTO. A continuaci�n, los va introduciendo dentro
	 * de una lista de Product.
	 * @return Lista de productos que dispone la Tienda Online. Si no hay ning�n resultado, devolver� una lista vacia (no null).
	 */
	public List<Product> obtenerProductos()
	{
		String searchProducts = "SELECT IdProduct, DESCRIPTION, PRICE FROM PRODUCTO";
		List<Product> listaResul = new ArrayList<>();
		try (Connection con = fuenteDatos.getConnection();
			 PreparedStatement ps = con.prepareStatement(searchProducts, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);)
		{
			ResultSet rs = ps.executeQuery();
			rs.first();
			while(rs.next())
				listaResul.add(new Product(rs.getInt("IdProduct"),rs.getString("DESCRIPTION"),rs.getDouble("PRICE")));
			rs.close();
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
		return listaResul;
	}
	
	/**
	 * Obtiene un usuario de la base de datos con un nombre de usuario y una contrase�a determinadas.
	 * @param user Nombre de usuario a buscar.
	 * @param pass Contrase�a a buscar.
	 * @return Usuario que contiene ese nombre de usuario y esa contrase�a. Al ser un nombre de usuario �nico e irrepetible,
	 * solo podr� devolver uno o ning�n resultado. 
	 */
	public User recuperarUsuario(String user, String pass)
	{
		String addUser = "SELECT IdUser, Username, Password "
				   + "FROM USUARIO "
				   + "WHERE Username='"+user+"' "
				   + "AND Password='"+pass+"';";
		try(Connection con = fuenteDatos.getConnection(); 
			PreparedStatement ps = con.prepareStatement(addUser);)
		{
			ResultSet rs = ps.executeQuery();
			while(rs.next())
				return new User(rs.getInt("IdUser"),rs.getString("Username"), rs.getString("Password"));
			rs.close();
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * A�ade un usuario nuevo a la base de datos en funci�n del nombre de usuario y la contrase�a que se le pase por par�metro.
	 * @param user Nombre de usuario que se desea guardar en la base de datos. Debe ser �nico para cada usuario.
	 * @param pass Contrase�a del usuario que se desea guardar en la base de datos para completar el registro.
	 * @return N�mero de filas que se han insertado. Si todo ha ido correctamente deber� devolver 1 fila, mientras que si ya existe
	 * el nombre de usuario en cuesti�n, devolver� 0 usuarios.
	 */
	public int addUsuario(String user, String pass)
	{
		String addUser = "INSERT INTO USUARIO(Username,Password) VALUES ('"+user+"','"+pass+"')";
		try (Connection con = fuenteDatos.getConnection();
			 PreparedStatement ps = con.prepareStatement(addUser);)
		{
			return ps.executeUpdate();
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
		return 0;
	}
	
	/**
	 * A�ade un nuevo tiquet a la base de datos con la fecha y hora de la compra, el usuario y el total que le ha costado la 
	 * compra al cliente
	 * @param user Usuario que realiza la compra
	 * @param total Cantidad total gastada por el cliente
	 * @return ID del Tiquet que se acaba de crear
	 */
	public int addTiquet(User user, double total)
	{
		String addTiquet = "INSERT INTO TIQUET VALUES(NOW(), 0," + user.getId() + "," + total +")";
		try(Connection con = fuenteDatos.getConnection();
				 PreparedStatement ps = con.prepareStatement(addTiquet);)
		{
			ps.executeUpdate();
		} catch(SQLException e)
		{
			e.printStackTrace();
		}
		return obtenerIdUltimo();
	}
	
	/**
	 * Recupera el producto que est� asociado al ID que se le haya pasado por par�metro.
	 * @param productID ID del producto a buscar
	 * @return Producto encontrado en la base de datos.
	 */
	public Product getProduct(int productID)
	{
		String searchProduct = "SELECT * FROM PRODUCTO WHERE IDPRODUCT = " + productID;
		try(Connection con = fuenteDatos.getConnection();
				 PreparedStatement ps = con.prepareStatement(searchProduct);)
		{
			ResultSet rs = ps.executeQuery();
			while(rs.next())
				return new Product(rs.getInt("IDPRODUCT"),rs.getString("DESCRIPTION"), rs.getDouble("PRICE"));
		} catch(SQLException e)
		{
			e.printStackTrace();
		}
		return null;
	}
}
