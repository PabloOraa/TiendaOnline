<%@page import="java.util.Map.Entry"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.HashMap"%>
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
	Map<Integer,Integer> productosSeleccionados = new HashMap<>();
	int numerUnidades;
	double total;
	double totalProd;
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
		<%	productos = servlet.obtenerProducto();
			sesion = request.getSession(true);
			if(!sesion.isNew())
			{
				productosSeleccionados = (HashMap<Integer,Integer>)sesion.getAttribute("productosSeleccionados");
				userIniciado = true;
				usuario = (User)sesion.getAttribute("User");
			}
			else
			{
				sesion.setAttribute("productosSeleccionados", productosSeleccionados);
				sesion.setAttribute("User", new User());
			}
			int productID = request.getParameter("producto") != null ? Integer.parseInt(request.getParameter("producto")) : 0;
			numerUnidades = request.getParameter("Unidades") != null && request.getParameter("Unidades") != "" ? Integer.parseInt(request.getParameter("Unidades")) : 0;
		if(userIniciado)
		{
			if(productID == 0 || numerUnidades == 0)
			{%>
				<div align="center">
				<h1>Bienvenido a la Tienda de DAM <%=usuario.getUsername() != "" ? usuario.getUsername() : "" %>.</h1>
					<form action="Tienda.jsp" method="post">
						<b>Listado productos:</b> <select name="producto">
							<%for(Product prod : productos) 
							  {%>
							  	<option value="<%=prod.getId()%>"><%=prod %></option>
							<%} %>
						</select>
						<b>Unidades: </b> <input type="Number" min=1 id="Unidades" Name="Unidades"/>
						<br/>
						<input type="submit" name="Opcion" value="Cestar"/>
						<input style="border:none; color:blue; background-color:white; cursor:pointer;" type="submit" name="Opcion" value="Ver Carrito" onclick="ajustarUnidades();"/>
					</form>
				</div>
		  <%}
			else
			{
				total = 0;
				if(request.getParameter("Opcion").equals("Cestar"))
				{
					if(!productosSeleccionados.containsKey(productID))
						productosSeleccionados.put(productID ,numerUnidades);
					else
						productosSeleccionados.put(productID, productosSeleccionados.get(productID)+numerUnidades);
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
							<% for(Entry<Integer, Integer> entry : productosSeleccionados.entrySet())
							   {
								    Product p = servlet.getProduct(entry.getKey());
								    totalProd = p.getPrecio() * entry.getValue();
							   		total += totalProd;%>
							   		<tr>
										<td><input type="radio" onchange="habilitar();" name="ProdElim" value="<%=entry.getKey() %>"></td> <!-- listaProductos.get(i) -->
										<td><%=p.getDescripcion() %></td>
										<td><%=totalProd %>euros</td>
										<td><%=entry.getValue() %></td>
									</tr>
									
							 <%} %>
							 <tr>
							 	<td colspan="4">TOTAL COMPRA: <%=total %> euros</td>
							 	<% sesion.setAttribute("Total", total); %>
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