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

public class TiendaBD
{
	/**
	 * Contexto del proyecto.
	 */
	private Context ctx;
	/**
	 * Fuente de los datos que hará uso del jdbc que se haya indicado dentro de META-INF en el Contexto gracias a Context.
	 */
	DataSource fuenteDatos;
	
	/**
	 * Constructor de la tienda que inicializa el Contexto y el DataSource según lo indicado dentro de context.xml (en este caso
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
     * Obtiene el último ID de las Compras guardadas en la base de datos. Para ello, realizará una consulta a todas las compras que haya
     * dentro de la tabla COMPRA. A partir de aqui, saltará a la última linea de resultados y mirará la fila que es. En caso de ser -1, es decir,
     * no tener elementos la tabla, devolverá 0. Si no, devolverá el último número ocupado + 1.
     * @return Integer con el ID a ocupar en la inserción.
     */
    public int obtenerIdUltimo()
    {
		int id = 0;
		String getId = "SELECT IdShopping FROM COMPRA";
		try (Connection con = fuenteDatos.getConnection();
			 PreparedStatement ps1 = con.prepareStatement(getId, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);)
		{
			ResultSet rs = ps1.executeQuery();
			rs.last();
			if(rs.getRow() != -1)
				id = rs.getInt("IdShopping")+1;
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
		return id;
    }
    
    /**
     * Añade una compra nueva al que haya iniciado sesión dentro de la aplicación. Debiddo a que una compra puede contener más de
     * un producto, y solo varían algunos datos, se ha preferido psar por parámetro la sentencia SQL completa de los datos a añadir
     * en lugar de formarla como en otras funciones.
     * @param SQL Sentencia SQL a ejecutar para el añadido de la linea de compra
     */
	public void addCompra(String SQL)
	{
		try(Connection con = fuenteDatos.getConnection();
			PreparedStatement ps1 = con.prepareStatement(SQL);)
		{
			ps1.executeUpdate();
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
	}

	/**
	 * Obtiene una lista de todos los productos que hay almacenados dentro de la base de datos. Primero realiza una consulta a la
	 * base de datos para obtener todos los resultados que contenga la tabla PRODUCTO. A continuación, los va introduciendo dentro
	 * de una lista de Product.
	 * @return Lista de productos que dispone la Tienda Online. Si no hay ningún resultado, devolverá una lista vacia (no null).
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
	 * Obtiene un usuario de la base de datos con un nombre de usuario y una contraseña determinadas.
	 * @param user Nombre de usuario a buscar.
	 * @param pass Contraseña a buscar.
	 * @return Usuario que contiene ese nombre de usuario y esa contraseña. Al ser un nombre de usuario único e irrepetible,
	 * solo podrá devolver uno o ningún resultado. 
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
	 * Añade un usuario nuevo a la base de datos en función del nombre de usuario y la contraseña que se le pase por parámetro.
	 * @param user Nombre de usuario que se desea guardar en la base de datos. Debe ser único para cada usuario.
	 * @param pass Contraseña del usuario que se desea guardar en la base de datos para completar el registro.
	 * @return Número de filas que se han insertado. Si todo ha ido correctamente deberá devolver 1 fila, mientras que si ya existe
	 * el nombre de usuario en cuestión, devolverá 0 usuarios.
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
}
