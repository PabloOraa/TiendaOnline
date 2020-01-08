package clases;

import java.io.IOException;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


/**
 * Servlet implementation class ServletControlador
 */
@WebServlet("/ServletControlador")
@Resource(name = "jdbs/miDataSource")
public class ServletControlador extends HttpServlet 
{
	private static final long serialVersionUID = 1L;
	
	/**
	 * Usuario que inicia sesión y/o se registra dentro de la Tienda Online.
	 */
	private User usuario;
	/**
	 * Conexión con la base de datos para realizar las consultas mínimas y necesarias dentro de la aplicación.
	 */
	private TiendaBD tiendaBD;
	
	/**
     * @throws ClassNotFoundException 
     * @see HttpServlet#HttpServlet()
     */
    public ServletControlador() throws ClassNotFoundException 
    {
        super();
        usuario = new User();
        tiendaBD = new TiendaBD();
    }

    public ServletControlador(boolean propio)
    {
    	usuario = new User();
        tiendaBD = new TiendaBD();
    }

	/**
	 * Método para responder a las peticiones Get dentro del Servlet. <br> Utilizará los parámetros para saber que opción debe
	 * realizar. Si se llama existiendo algún tipo de Inicio en la Tienda, se pasará al método doPost 
	 * ({@link ServletControlador#doPost ((HttpServletRequest request, HttpServletResponse response)}).
	 * <br>Por otra parte, si no hay ningún tipo se mirará si hay un Option (viene de Tienda.jsp) y el tipo que es para añadir
	 * productos a una base de datos o, en su defecto, eliminar el producto que se haya marcado dentro de la lista en Tienda.jsp.
	 * 
	 * <br>
	 * Por último, si no hay ningún tipo, si no hay ningún Option, o si lo hay pero este es Comprar, se llevará a Index.jsp para
	 * comenzar con el proceso. En cambio, si se marcó Eliminar Producto volveremos a la tienda donde podremos continuar comprando.
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		boolean irTienda = false;
		if(request.getParameter("Tipo") != null)
				doPost(request, response);
		else
		{
			if(request.getParameter("Option") != null)
				if(request.getParameter("Option").equals("Comprar"))
					addCompra(request.getSession(true));
				else if(request.getParameter("Option").equals("Eliminar producto"))
				{
					deleteProduct(request.getParameter("ProdElim"), request.getSession(true));
					irTienda = true;
				}
		}
		response.sendRedirect(irTienda ? "Tienda.jsp":"Index.jsp");
	}

	/**
	 * Elimina un producto de las listas que ha seleccionado el usuario, y que aún no se ha guardado en la base de datos.
	 * <br/>
	 * Localiza el producto dentro de la lista. Siempre y cuanto este exista, es decir, sea diferente que -1, eliminará el producto de 
	 * listaPro, listaPrec y listaUnidades. Todas estas listas se encontrarán dentro de la sesión.
	 * @param producto Producto que se desea eliminar de las listas.
	 * @param sesion Sesión de la aplicación.
	 */
	private void deleteProduct(String producto, HttpSession sesion)
	{
		int pos = ((List<?>)sesion.getAttribute("listaProd")).indexOf(producto);
		if(pos != -1)
		{
			((List<?>)sesion.getAttribute("listaProd")).remove(pos);
			((List<?>)sesion.getAttribute("listaPrec")).remove(pos);
			((List<?>)sesion.getAttribute("listaUnidades")).remove(pos);
		}
	}

	/**
	 * Contiene toda la autenticación desde los usuarios tanto de Validación como de Alta. Para ello, utiliza el parámetro
	 * Tipo, que definirá de donde viene el usuario.
	 * <ul>
	 * 	<li>En caso de venir desde el registro, se llamará al método addUsuario del propio servlet, que nos devolverá un número
	 * indicando el número de filas que se han añadido a la base de datos. Siempre y cuando este no sea 0, se considerará que
	 * se ha creado correctamente, y por tanto debe avanzar. <br/> En caso de ser 0, volverá a Alta.jsp con un mensaje indicando
	 * al usuario que ya existe un usuario con ese nombre. </li>
	 * 
	 *  <li>En caso de venir desde el inicio de sesión, se comprobará contra base de datos que existe un usuario que tenga ese
	 * nombre y esa contraseña. Como este método devuelve un booleano, si es true, se continuará a la tienda configurando la 
	 * sesión de la aplicación.<br/> En cambio, si es false nos devolverá a Validacion.jsp con un mensaje indicando que el nombre
	 * de usuario o la contraseña son incorrectos. </li>
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		boolean seguir = false;
		String origen = "Index.jsp";
		if(request.getParameter("Tipo").equals("Alta"))
		{
			if(addUsuario(request.getParameter("User"),request.getParameter("Pass")) != 0)
				seguir = true;
			else
				origen = "Alta.jsp";
		}
		else
		{
			if(recuperarUsuario(request.getParameter("User"),request.getParameter("Pass")))
				seguir = true;
			else
				origen= "Validacion.jsp";
		}
		
		if(seguir)
		{
			configurarSesion(request);
			response.sendRedirect("Tienda.jsp");
		}
		else
		{
			HttpSession sesion = request.getSession(true);
			sesion.setAttribute("ErrorAuth", true);
			response.sendRedirect(origen);
		}
	}

	/**
	 * Configura los parámetros de la sesión que queremos utilizar en Tienda.jsp. Se llama al método cuando hemos podido iniciar
	 * sesión correctamente dentro de la Tienda Online.
	 * <br>
	 * Si la sesión es nueva, o en su defecto la lista de productos (y por tanto todas las listas) no existe, se añaden las listas
	 * completamente nuevas para empezar a añadir productos.
	 * <br>
	 * Como se inicia sesión siempre que se llama a este método, se guarda el atributo User con todos los datos del usuario en cuestión.
	 * @param request Request del servlet que recibimos de doPost.
	 */
	private void configurarSesion(HttpServletRequest request) 
	{
		HttpSession sesion = request.getSession(true);
		List<String> listaProductos = new ArrayList<>();
		List<String> listaPrecios = new ArrayList<>();
		List<Integer> listaUnidades = new ArrayList<>();
		if(sesion.isNew() || (!sesion.isNew() && sesion.getAttribute("listaProd") == null))
		{
			sesion.setAttribute("listaProd", listaProductos);
			sesion.setAttribute("listaPrec", listaPrecios);
			sesion.setAttribute("listaUnidades", listaUnidades);
		}
		sesion.setAttribute("User", usuario);
	}

	/**
	 * Añade un usuario a la base de datos con el usuario y la contraseña en cuestión.
	 * @param user Nombre de usuario con el que desea registrarse en la aplicación
	 * @param pass Contraseña del usuario para poder identificarse.
	 * @return Numero de lineas que se han añadido dentro de la base de datos. Si no se ha añadido ninguna fila devolverá 0.
	 * 			<br> Si no, devolverá un número positivo, y asociará al Usuario de ServletControlador el usuario que se ha añadido.
	 */
	private int addUsuario(String user, String pass)
	{
		int resul = tiendaBD.addUsuario(user,pass);
		if(resul != 0)
			usuario = tiendaBD.recuperarUsuario(user, pass);
		return resul;
	}

	/**
	 * Recupera un usuario de la base de datos a partir de su nombre de usuario y de la contraseña.
	 * @param user Nombre de usuario en formato String.
	 * @param pass Contraseña de la aplicación en formato String.
	 * @return True si ha encontrado el usuario y False si no ha encontrado nada.
	 */
	private boolean recuperarUsuario(String user, String pass)
	{
		usuario = tiendaBD.recuperarUsuario(user,pass);
		if(usuario == null)
			return false;
		else
			return true;
	}

	/**
	 * Obtiene los productos que estén guardados dentro de la base de datos.
	 * @return LIsta de productos.
	 */
	public List<Product> obtenerProducto()
	{
		return tiendaBD.obtenerProductos();
	}	
	
	/**
	 * Añade la compra guardada en la sesión del usuario a la base de datos. Lo primero que hará será crear el
	 * tiquet de la compra a partir del usuario y el total, devolviendo el ID que se acaba de insertar.
	 * <br/>
	 * A continuación se insertará en Compra cada producto con su cantidad y el precio total gastado.
	 * @param sesion Sesión de la aplicación
	 */
	private void addCompra(HttpSession sesion)
	{
		int id = tiendaBD.addTiquet((User)sesion.getAttribute("User"), (Double)sesion.getAttribute("Total"));
		
		@SuppressWarnings("unchecked")
		List<String> listaPrec = (List<String>) sesion.getAttribute("listaPrec");
		@SuppressWarnings("unchecked")
		List<Integer> listaNum = (List<Integer>) sesion.getAttribute("listaUnidades");
		for(int i = 0; i < listaPrec.size();i++)
		{	
			String SQL = "INSERT INTO COMPRA(IDSHOPPING,IDPRODUCT,QUANTITY,TOTAL) VALUES (" + id + "," + getIdProduct(i,sesion) +
					"," + listaNum.get(i) +"," + Double.parseDouble(listaPrec.get(i)) + ")";
			tiendaBD.addCompra(SQL);
		}
	}

	/**
	 * Obtiene el ID de un producto a partir de la lista completa de productos, comparándolo con la posición del producto que nos interese.
	 * @param i Posición del producto en el que estamos actualmente en la lista de productos seleccionados por el usuario.
	 * @param sesion Sesión de la aplicación.
	 * @return Número que indica el id del producto en cuestión. Si no se ha entonctrado ningún resultado devolverá -1.
	 */
	private int getIdProduct(int i, HttpSession sesion)
	{
		List<Product> listaProductos = tiendaBD.obtenerProductos();
		@SuppressWarnings("unchecked")
		List<String> listaProd = (List<String>) sesion.getAttribute("listaProd");
		for(Product p : listaProductos)
			if(p.getDescripcion().equals(listaProd.get(i)))
				return p.getId();
		return -1;
	}
	
	/**
	 * Recupera el usuario que, previamente, se haya iniciado sesión o registrado en la aplicación.
	 * @return Usuario con el que se ha iniciado sesión en la aplicación
	 */
	public User getUser()
	{
		return usuario;
	}
}
