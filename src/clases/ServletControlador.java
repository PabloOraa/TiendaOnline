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
	 * Usuario que inicia sesi�n y/o se registra dentro de la Tienda Online.
	 */
	private User usuario;
	/**
	 * Conexi�n con la base de datos para realizar las consultas m�nimas y necesarias dentro de la aplicaci�n.
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
	 * M�todo para responder a las peticiones Get dentro del Servlet. <br> Utilizar� los par�metros para saber que opci�n debe
	 * realizar. Si se llama existiendo alg�n tipo de Inicio en la Tienda, se pasar� al m�todo doPost 
	 * ({@link ServletControlador#doPost ((HttpServletRequest request, HttpServletResponse response)}).
	 * <br>Por otra parte, si no hay ning�n tipo se mirar� si hay un Option (viene de Tienda.jsp) y el tipo que es para a�adir
	 * productos a una base de datos o, en su defecto, eliminar el producto que se haya marcado dentro de la lista en Tienda.jsp.
	 * 
	 * <br>
	 * Por �ltimo, si no hay ning�n tipo, si no hay ning�n Option, o si lo hay pero este es Comprar, se llevar� a Index.jsp para
	 * comenzar con el proceso. En cambio, si se marc� Eliminar Producto volveremos a la tienda donde podremos continuar comprando.
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
	 * Elimina un producto de las listas que ha seleccionado el usuario, y que a�n no se ha guardado en la base de datos.
	 * <br/>
	 * Localiza el producto dentro de la lista. Siempre y cuanto este exista, es decir, sea diferente que -1, eliminar� el producto de 
	 * listaPro, listaPrec y listaUnidades. Todas estas listas se encontrar�n dentro de la sesi�n.
	 * @param producto Producto que se desea eliminar de las listas.
	 * @param sesion Sesi�n de la aplicaci�n.
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
	 * Contiene toda la autenticaci�n desde los usuarios tanto de Validaci�n como de Alta. Para ello, utiliza el par�metro
	 * Tipo, que definir� de donde viene el usuario.
	 * <ul>
	 * 	<li>En caso de venir desde el registro, se llamar� al m�todo addUsuario del propio servlet, que nos devolver� un n�mero
	 * indicando el n�mero de filas que se han a�adido a la base de datos. Siempre y cuando este no sea 0, se considerar� que
	 * se ha creado correctamente, y por tanto debe avanzar. <br/> En caso de ser 0, volver� a Alta.jsp con un mensaje indicando
	 * al usuario que ya existe un usuario con ese nombre. </li>
	 * 
	 *  <li>En caso de venir desde el inicio de sesi�n, se comprobar� contra base de datos que existe un usuario que tenga ese
	 * nombre y esa contrase�a. Como este m�todo devuelve un booleano, si es true, se continuar� a la tienda configurando la 
	 * sesi�n de la aplicaci�n.<br/> En cambio, si es false nos devolver� a Validacion.jsp con un mensaje indicando que el nombre
	 * de usuario o la contrase�a son incorrectos. </li>
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
	 * Configura los par�metros de la sesi�n que queremos utilizar en Tienda.jsp. Se llama al m�todo cuando hemos podido iniciar
	 * sesi�n correctamente dentro de la Tienda Online.
	 * <br>
	 * Si la sesi�n es nueva, o en su defecto la lista de productos (y por tanto todas las listas) no existe, se a�aden las listas
	 * completamente nuevas para empezar a a�adir productos.
	 * <br>
	 * Como se inicia sesi�n siempre que se llama a este m�todo, se guarda el atributo User con todos los datos del usuario en cuesti�n.
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
	 * A�ade un usuario a la base de datos con el usuario y la contrase�a en cuesti�n.
	 * @param user Nombre de usuario con el que desea registrarse en la aplicaci�n
	 * @param pass Contrase�a del usuario para poder identificarse.
	 * @return Numero de lineas que se han a�adido dentro de la base de datos. Si no se ha a�adido ninguna fila devolver� 0.
	 * 			<br> Si no, devolver� un n�mero positivo, y asociar� al Usuario de ServletControlador el usuario que se ha a�adido.
	 */
	private int addUsuario(String user, String pass)
	{
		int resul = tiendaBD.addUsuario(user,pass);
		if(resul != 0)
			usuario = tiendaBD.recuperarUsuario(user, pass);
		return resul;
	}

	/**
	 * Recupera un usuario de la base de datos a partir de su nombre de usuario y de la contrase�a.
	 * @param user Nombre de usuario en formato String.
	 * @param pass Contrase�a de la aplicaci�n en formato String.
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
	 * Obtiene los productos que est�n guardados dentro de la base de datos.
	 * @return LIsta de productos.
	 */
	public List<Product> obtenerProducto()
	{
		return tiendaBD.obtenerProductos();
	}	
	
	/**
	 * A�ade la compra guardada en la sesi�n del usuario a la base de datos. Esto lo har� con el id del producto, id del usuario, fecha/hora, id de la compra, unidades y precio.
	 * @param sesion Sesi�n de la aplicaci�n
	 */
	private void addCompra(HttpSession sesion)
	{
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
		String fecha = sdf.format(new Date().getTime());
		int id = tiendaBD.obtenerIdUltimo();
		
		@SuppressWarnings("unchecked")
		List<String> listaPrec = (List<String>) sesion.getAttribute("listaPrec");
		@SuppressWarnings("unchecked")
		List<Integer> listaNum = (List<Integer>) sesion.getAttribute("listaUnidades");
		for(int i = 0; i < listaPrec.size();i++)
		{	
			String SQL = "INSERT INTO COMPRA(FECHAHORA,IDSHOPPING,IDPRODUCT,IDUSER,QUANTITY,TOTAL) VALUES ('"
							+ fecha + "'," + id + "," + getIdProduct(i,sesion) + "," + usuario.getId() + "," +
							listaNum.get(i) +"," + Double.parseDouble(listaPrec.get(i)) + ")";
			tiendaBD.addCompra(SQL);
		}
	}

	/**
	 * Obtiene el ID de un producto a partir de la lista completa de productos, compar�ndolo con la posici�n del producto que nos interese.
	 * @param i Posici�n del producto en el que estamos actualmente en la lista de productos seleccionados por el usuario.
	 * @param sesion Sesi�n de la aplicaci�n.
	 * @return N�mero que indica el id del producto en cuesti�n. Si no se ha entonctrado ning�n resultado devolver� -1.
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
	 * Recupera el usuario que, previamente, se haya iniciado sesi�n o registrado en la aplicaci�n.
	 * @return Usuario con el que se ha iniciado sesi�n en la aplicaci�n
	 */
	public User getUser()
	{
		return usuario;
	}
}
