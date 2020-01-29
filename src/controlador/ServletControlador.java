package controlador;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
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
 * @author Pablo Oraa L&oacute;pez
 */
@WebServlet("/ServletControlador")
@Resource(name = "jdbs/miDataSource")
public class ServletControlador extends HttpServlet 
{
	private static final long serialVersionUID = 1L;
	
	/**
	 * Usuario que inicia sesi&oacute;n y/o se registra dentro de la Tienda Online.
	 */
	private UsuarioEntity usuario;
	/**
	 * Conexi&oacute;n con la base de datos para realizar las consultas m&iacute;nimas y necesarias dentro de la aplicaci&oacute;n.
	 */
	private TiendaBD tiendaBD;
	
	/**
     * @see HttpServlet#HttpServlet()
     */
    public ServletControlador()
	{
        super();
        usuario = new UsuarioEntity();
        tiendaBD = new TiendaBD();
    }

    public ServletControlador(boolean propio)
    {
    	System.out.print(propio);
    	usuario = new UsuarioEntity();
        tiendaBD = new TiendaBD();
    }

	/**
	 * M&eacute;todo para responder a las peticiones Get dentro del Servlet. <br> Utilizar&aacute; los pa&aacute;metros
	 * para saber que opci&oacute;n debe realizar. Si se llama existiendo alg&uacute;n tipo de Inicio en la Tienda, se
	 * pasar&aacute; al m&eacute;todo doPost
	 * ({@link ServletControlador#doPost ((HttpServletRequest request, HttpServletResponse response)}).
	 * <br>Por otra parte, si no hay ning&uacute;n tipo se mirar&aacute; si hay un Option (viene de Tienda.jsp) y el tipo
	 * que es para a&ntilde;adir productos a una base de datos o, en su defecto, eliminar el producto que se haya marcado
	 * dentro de la lista en Tienda.jsp.
	 * 
	 * <br>
	 * Por &uacute;ltimo, si no hay ning&uacute;n tipo, si no hay ning&uacute;n Option, o si lo hay pero este es Comprar,
	 * se llevar&aacute; a Index.jsp para comenzar con el proceso. En cambio, si se marc&oacute; Eliminar Producto volveremos
	 * a la tienda donde podremos continuar comprando.
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException
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
	 * Elimina un producto de las listas que ha seleccionado el usuario, y que a&uacute;n no se ha guardado en la base de datos.
	 * <br/>
	 * Se recupera la lista de productos desde la sesi&oacute;n (en un HashMap) y se eliminar&aacute; por la clave del
	 * producto que hayamos pasado por par&aacute;metro. Una vez hecho esto, se guarda el nuevo mapa en la sesi&oacute;n
	 * de nuevo.
	 * @param producto Producto que se desea eliminar de las listas.
	 * @param sesion Sesi&oacute;n de la aplicaci&oacute;n.
	 */
	private void deleteProduct(int producto, HttpSession sesion)
	{
		Map<Integer,Integer> productos = (Map<Integer, Integer>) sesion.getAttribute("productosSeleccionados");
		productos.remove(producto);
		sesion.setAttribute("productosSeleccionados", productos);
	}

	/**
	 * Contiene toda la autenticaci&oacute;n desde los usuarios tanto de Validaci&oacute;n como de Alta. Para ello, utiliza
	 * el par&aacute;metro Tipo, que definir&aacute; de donde viene el usuario.
	 * <ul>
	 * 		<li>En caso de venir desde el registro, se llamar&aacute; al m&eacute;todo addUsuario del propio servlet, que nos
	 *	 	devolver&aacute; un n&uacute;mero indicando el n&uacute;mero de filas que se han a&ntilde;adido a la base de datos.
	 *	 	Siempre y cuando este no sea 0, se considerar&aacute;M que se ha creado correctamente, y por tanto debe avanzar.
	 *	 	<br> En caso de ser 0, volver&aacute; a Alta.jsp con un mensaje indicando al usuario que ya existe un usuario con
	 *	 	ese nombre. </li>
		 *
	 *	  <li>En caso de venir desde el inicio de sesi&oacute;n, se comprobar&aacute; contra base de datos que existe un
	 *	  usuario que tenga ese nombre y esa contrase&ntilde;a. Como este m&eacute;todo devuelve un booleano, si es true,
	 *	  se continuar&aacute; a la tienda configurando la sesi&oacute;n de la aplicaci&oacute;n.<br> En cambio, si es
	 *	  false nos devolver&aacute; a Validacion.jsp con un mensaje indicando que el nombre de usuario o la contrase&ntilde;a
	 *	  son incorrectos. </li>
	 *  </ul>
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
	 * Configura los par&aacute;metros de la sesi&oacute;n que queremos utilizar en Tienda.jsp. Se llama al m&eacute;todo
	 * cuando hemos podido iniciar sesi&oacute;n correctamente dentro de la Tienda Online.
	 * <br>
	 * Si la sesi&oacute;n es nueva, o en su defecto la lista de productos (en forma de HashMap) no existe, se
	 * a&ntilde;adir&aacute; ese HashMap a la sesi&oacute;n.
	 * <br>
	 * Como se inicia sesi&oacute;n siempre que se llama a este m&eacute;todo, se guarda el atributo User con todos los
	 * datos del usuario en cuesti&oacute;n.
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
	 * A&ntilde;ade un usuario a la base de datos con el usuario y la contrase&ntilde;a en cuesti&oacute;n.
	 * @param user Nombre de usuario con el que desea registrarse en la aplicaci&oacute;n
	 * @param pass Contrase&ntilde;a del usuario para poder identificarse.
	 * @return Numero de lineas que se han a&ntilde;adido dentro de la base de datos. Si no se ha a&ntilde;adido ninguna
	 * 			fila devolver&aacute; 0. <br> Si no, devolver&aacute; un n&uacute;mero positivo, y asociar&aacute; al
	 * 			Usuario de ServletControlador el usuario que se ha a&ntilde;adido.
	 */
	private int addUsuario(String user, String pass)
	{
		int resul = tiendaBD.addUsuario(user,pass);
		if(resul != 0)
			usuario = tiendaBD.recuperarUsuario(user, pass);
		return resul;
	}

	/**
	 * Recupera un usuario de la base de datos a partir de su nombre de usuario y de la contrase&ntilde;a.
	 * @param user Nombre de usuario en formato String.
	 * @param pass Contrase&ntilde;a de la aplicaci&oacute;n en formato String.
	 * @return True si ha encontrado el usuario y False si no ha encontrado nada.
	 */
	private boolean recuperarUsuario(String user, String pass)
	{
		usuario = tiendaBD.recuperarUsuario(user,pass);
		return usuario != null;
	}

	/**
	 * Obtiene los productos que est&aacute;n guardados dentro de la base de datos.
	 * @return LIsta de productos.
	 */
	public List<ProductoEntity> obtenerProducto()
	{
		return tiendaBD.obtenerProductos();
	}	
	
	/**
	 * A&ntilde;ade la compra guardada en la sesi&oacute;n del usuario a la base de datos. Lo primero que har&aacute;
	 * ser&aacute; crear el tiquet de la compra a partir del usuario y el total, devolviendo el ID que se acaba de insertar.
	 * <br/>
	 * A continuaci&oacute;n se insertar&aacute; en Compra cada producto con su cantidad y el precio total gastado.
	 * @param sesion Sesi&oacute;n de la aplicaci&oacute;n
	 */
	private void addCompra(HttpSession sesion)
	{
		int id = tiendaBD.addTiquet((UsuarioEntity) sesion.getAttribute("User"), (Double)sesion.getAttribute("Total"));
		Map<Integer,Integer> productos = (Map<Integer, Integer>) sesion.getAttribute("productosSeleccionados");
		productos.forEach((u,v) -> tiendaBD.addCompra(id, u, v, getProduct(u).getPrice().doubleValue()*v));
	}

	/**
	 * Recupera el usuario que, previamente, se haya iniciado sesi&oacute;n o registrado en la aplicaci&oacute;n.
	 * @return Usuario con el que se ha iniciado sesi&oacute;n en la aplicaci&oacute;n
	 */
	public UsuarioEntity getUser()
	{
		return usuario;
	}
	
	/**
	 * Recupera el producto que est&aacute; asociado al ID que se le haya pasado por par&aacute;metro.
	 * @param productID ID del producto a buscar
	 * @return Producto encontrado en la base de datos.
	 */
	public ProductoEntity getProduct(int productID)
	{
		return tiendaBD.getProduct(productID);
	}
	
	/**
	 * Obtiene las compras pasadas del usuario que haya iniciado sesi&oacute;n dentro de la aplicaci&oacute;n.
	 * @param userID ID del usuario del que se quieren recuperar las facturas (compras) pasadas.
	 * @return Lista con todas las compras pasadas bajo la clase Invoice.
	 */
	public List<Invoice> getInvoice(int userID)
	{
		return tiendaBD.getInvoice(userID);
	}
}
