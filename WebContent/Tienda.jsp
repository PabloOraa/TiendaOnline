<%@page import="clases.ServletControlador"%>
<%@page import="clases.User"%>
<%@page import="java.sql.ResultSet"%>
<%@page import="java.sql.SQLException"%>
<%@page import="java.sql.Connection"%>
<%@page import="java.sql.PreparedStatement"%>
<%@page import="javax.sql.DataSource"%>
<%@page import="javax.naming.NamingException"%>
<%@page import="javax.naming.InitialContext"%>
<%@page import="javax.naming.Context"%>
<%@page import="clases.Product"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    
<%! List<Product> productos = new ArrayList<>(); 
	List<String> listaProductos = new ArrayList<>();
	List<String> listaPrecios = new ArrayList<>();
	List<Integer> listaUnidades = new ArrayList<>();
	String producto;
	String precio;
	int numerUnidades;
	double total;
	boolean userIniciado = false;
	HttpSession sesion;
	ServletControlador servlet = new ServletControlador(true);
	User usuario;%>
	
<!DOCTYPE html>
<html>
	<head>
		<meta charset="ISO-8859-1">
		<title>Tienda</title>
		<script type="text/javascript" src="scripts.js">
		</script>
		<script type="text/javascript">
		function ajustarUnidades()
		{
			document.getElementById("Unidades").value = -1;
		}
		</script>
	</head>
	<body>
		<%	
			productos = servlet.obtenerProducto();
			
			if(((Product)productos.get(0)).getId() == 0)
				productos.remove(0);
			sesion = request.getSession(true);
			if(!sesion.isNew())
			{
				listaProductos = (List)sesion.getAttribute("listaProd");
				listaPrecios = (List)sesion.getAttribute("listaPrec");
				listaUnidades = (List)sesion.getAttribute("listaUnidades");
				userIniciado = true;
				usuario = (User)sesion.getAttribute("User");
			}
			else
			{
				sesion.setAttribute("listaProd", listaProductos);
				sesion.setAttribute("listaPrec", listaPrecios);
				sesion.setAttribute("listaUnidades", listaUnidades);
				sesion.setAttribute("User", new User());
			}
			String product = request.getParameter("producto") != null ? request.getParameter("producto") : "";
			producto = product.equals("") ? "" : product.substring(0,product.indexOf("-")-1);
			numerUnidades = request.getParameter("Unidades") != null && request.getParameter("Unidades") != "" ? Integer.parseInt(request.getParameter("Unidades")) : 0;
			precio = product.equals("") ? "" : String.valueOf(Double.parseDouble((product.substring(product.indexOf("-")+2)).substring(0,2))*numerUnidades);
		if(userIniciado)
		{
			if(producto == "" && precio == "" || numerUnidades == 0)
			{%>
				<div align="center">
				<h1>Bienvenido a la Tienda de DAM <%=usuario.getUsername() != "" ? usuario.getUsername() : "" %>.</h1>
					<form action="Tienda.jsp" method="post">
						<b>Listado productos:</b> <select name="producto">
							<%for(Product prod : productos) 
							  {%>
							  	<option><%=prod %></option>
							<%} %>
						</select>
						<b>Unidades: </b> <input type="Number" id="Unidades" Name="Unidades"/>
						<br/>
						<input type="submit" name="Opcion" value="Cestar"/>
						<input style="border:none; color:blue; background-color:white; cursor:pointer;" type="submit" name="Opcion" value="Ver Carrito" onclick="ajustarUnidades();"/>
					</form>
				</div>
		  <%}
			else
			{
				if(request.getParameter("Opcion").equals("Cestar"))
				{
					total = 0;
					if(!listaProductos.contains(producto))
					{
						listaProductos.add(producto);
						listaPrecios.add(precio);
						listaUnidades.add(numerUnidades);
					}
					else
					{
						int posicion = listaProductos.indexOf(producto);
						listaUnidades.set(posicion, listaUnidades.get(posicion)+numerUnidades);
						listaPrecios.set(posicion, String.valueOf(listaUnidades.get(posicion)*Double.parseDouble(precio)));
					}
				}
				%>
				<div align="center">
					<form action="ServletControlador" method="get" name="formFinal">
						<h1>Carrito de la compra de <%=usuario.getUsername() != "" ? usuario.getUsername() : ""  %></h1>
						<table border="2">
							<tr align="center">
								<td colspan="4"><b>PRODUCTOS COMPRADOS</b></td>
							</tr>
							<tr>
								<td></td>
								<td><b>PRODUCTO</b></td>
								<td><b>PRECIO (euros)</b></td>
								<td><b>UNIDADES</b></td>
							</tr>
							<% for(int i = 0; i < listaProductos.size(); i++)
							   {
							   		total += Double.parseDouble(listaPrecios.get(i));%>
							   		<tr>
										<td><input type="radio" onchange="habilitar();" name="ProdElim" value="<%=listaProductos.get(i) %>"></td>
										<td><%=listaProductos.get(i) %></td>
										<td><%=listaPrecios.get(i) %>euros</td>
										<td><%=listaUnidades.get(i) %></td>
									</tr>
									
							 <%} %>
							 <tr>
							 	<td colspan="4">TOTAL COMPRA: <%=total %> euros</td>
							 </tr>
						</table>
					
			  			<br/>
			  			<a href="Tienda.jsp">Seguir comprando</a>
				  		<input type="submit" id="deleteProd" disabled name="Option" Value="Eliminar producto"/>
				  		<input type="submit" id="shopProd" name="Option" Value="Comprar"/>
				  		<a href="Index.jsp"><button>Salir</button></a>
			  		</form>
		  		</div>
		  <%} 
		}
		else
		{
			response.sendRedirect("Validacion.jsp");
		}%>
	</body>
</html>