package controlador;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import clases.*;
import modelo.TiendaBD;

/**
 * Servlet implementation class ServletControlador
 * @version 2.0
 * @author Pablo Oraa L�pez
 */
@WebServlet("/ServletControlador")
@Resource(name = "jdbs/miDataSource")
public class ServletControlador extends HttpServlet 
{
	private static final long serialVersionUID = 1L;
	
	/**
	 * Usuario que inicia sesi�n y/o se registra dentro de la Tienda Online.
	 */
	private UsuarioEntity usuario;
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
        usuario = new UsuarioEntity();
        tiendaBD = new TiendaBD();
    }

    public ServletControlador(boolean propio)
    {
    	usuario = new UsuarioEntity();
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
					deleteProduct(Integer.parseInt(request.getParameter("ProdElim")), request.getSession(true));
					irTienda = true;
				}
		}
		response.sendRedirect(irTienda ? "Tienda.jsp":"Index.jsp");
	}

	/**
	 * Elimina un producto de las listas que ha seleccionado el usuario, y que a�n no se ha guardado en la base de datos.
	 * <br/>
	 * Se recupera la lista de productos desde la sesi�n (en un HashMap) y se eliminar� por la clave del producto que hayamos
	 * pasado por par�metro. Una vez hecho esto, se guarda el nuevo mapa en la sesi�n de nuevo.
	 * @param producto Producto que se desea eliminar de las listas.
	 * @param sesion Sesi�n de la aplicaci�n.
	 */
	private void deleteProduct(int producto, HttpSession sesion)
	{
		Map<Integer,Integer> productos = (Map<Integer, Integer>) sesion.getAttribute("productosSeleccionados");
		productos.remove(producto);
		sesion.setAttribute("productosSeleccionados", productos);
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
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException
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
	 * Si la sesi�n es nueva, o en su defecto la lista de productos (en forma de HashMap) no existe, se a�adir� ese HashMap a la sesi�n.
	 * <br>
	 * Como se inicia sesi�n siempre que se llama a este m�todo, se guarda el atributo User con todos los datos del usuario en cuesti�n.
	 * @param request Request del servlet que recibimos de doPost.
	 */
	private void configurarSesion(HttpServletRequest request) 
	{
		HttpSession sesion = request.getSession(true);
		if(sesion.isNew() || (!sesion.isNew() && sesion.getAttribute("productosSeleccionados") == null))
			sesion.setAttribute("productosSeleccionados", new HashMap<Integer,Integer>());
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
		return usuario != null;
	}

	/**
	 * Obtiene los productos que est�n guardados dentro de la base de datos.
	 * @return LIsta de productos.
	 */
	public List<ProductoEntity> obtenerProducto()
	{
		return tiendaBD.obtenerProductos();
	}	
	
	/**
	 * A�ade la compra guardada en la sesi�n del usuario a la base de datos. Lo primero que har� ser� crear el
	 * tiquet de la compra a partir del usuario y el total, devolviendo el ID que se acaba de insertar.
	 * <br/>
	 * A continuaci�n se insertar� en Compra cada producto con su cantidad y el precio total gastado.
	 * @param sesion Sesi�n de la aplicaci�n
	 */
	private void addCompra(HttpSession sesion)
	{
		int id = tiendaBD.addTiquet((UsuarioEntity) sesion.getAttribute("User"), (Double)sesion.getAttribute("Total"));
		Map<Integer,Integer> productos = (Map<Integer, Integer>) sesion.getAttribute("productosSeleccionados");
		productos.forEach((u,v) -> tiendaBD.addCompra(id, u, v, getProduct(u).getPrice().doubleValue()*v));
	}

	/**
	 * Recupera el usuario que, previamente, se haya iniciado sesi�n o registrado en la aplicaci�n.
	 * @return Usuario con el que se ha iniciado sesi�n en la aplicaci�n
	 */
	public UsuarioEntity getUser()
	{
		return usuario;
	}
	
	/**
	 * Recupera el producto que est� asociado al ID que se le haya pasado por par�metro.
	 * @param productID ID del producto a buscar
	 * @return Producto encontrado en la base de datos.
	 */
	public ProductoEntity getProduct(int productID)
	{
		return tiendaBD.getProduct(productID);
	}
	
	/**
	 * Obtiene las compras pasadas del usuario que haya iniciado sesi�n dentro de la aplicaci�n.
	 * @param userID ID del usuario del que se quieren recuperar las facturas (compras) pasadas.
	 * @return Lista con todas las compras pasadas bajo la clase Invoice.
	 */
	public List<Invoice> getInvoice(int userID)
	{
		return tiendaBD.getInvoice(userID);
	}
}
