<%@page import="clases.TiquetEntity"%>
<%@page import="java.util.Map.Entry"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.HashMap"%>
<%@page import="clases.UsuarioEntity"%>
<%@page import="controlador.ServletControlador"%>
<%@page import="clases.ProductoEntity"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>
<%@ page import="clases.Invoice" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    
<%! List<ProductoEntity> productos = new ArrayList<>();
	Map<Integer,Integer> productosSeleccionados = new HashMap<>();
	int numerUnidades;
	double total;
	double totalProd;
	HttpSession sesion;
	ServletControlador servlet = new ServletControlador(true);
	UsuarioEntity usuario;%>
	
<!DOCTYPE html>
<html>
	<head>
		<meta charset="ISO-8859-1">
		<title>Tienda</title>
		<script type="text/javascript" src="scripts.js">
		</script>
		
	</head>
	<body>
		<%	
			sesion = request.getSession();
			if(!sesion.isNew())
			{
				productosSeleccionados = (HashMap<Integer,Integer>)sesion.getAttribute("productosSeleccionados");
				usuario = (UsuarioEntity)sesion.getAttribute("User");
			}
			else
			{
				sesion.setAttribute("productosSeleccionados", productosSeleccionados);
				sesion.setAttribute("User", new UsuarioEntity());
			}
			int productID = request.getParameter("producto") != null ? Integer.parseInt(request.getParameter("producto")) : 0;
			try
			{
				numerUnidades = request.getParameter("Unidades") != null ? Integer.parseInt(request.getParameter("Unidades")) : 0;
			}catch(NumberFormatException e)
			{
				numerUnidades = -1;
			}
		if(usuario != null)
		{
			if(productID == 0 || numerUnidades == 0)
			{
				productos = servlet.obtenerProducto();
		%>
				<div align="center">
					<form action="Tienda.jsp" method="post">
						<h1>Bienvenido a la Tienda de DAM 
								<b><input style="border:none; color:blue; background-color:white;
															 cursor:pointer; font-weight:bold; font-size:30px;" type="submit" name="Opcion" 
															 value="<%=usuario.getUsername()%>"/></b>.
						</h1>
						<b>Listado productos:</b> <select name="producto">
							<%for(ProductoEntity prod : productos)
							  {%>
							  	<option value="<%=prod.getIdProduct()%>"><%=prod %></option>
							<%} %>
						</select>
						<b>Unidades: </b> <input type="Number" min=1 id="Unidades" Name="Unidades"/>
						<br/>
						<input type="submit" name="Opcion" value="Cestar"/>
						<input style="border:none; color:blue; background-color:white; cursor:pointer;" type="submit" name="Opcion" value="Ver Carrito"/>
					</form>
				</div>
		  <%}
			else
			{
				if(request.getParameter("Opcion").equals(usuario.getUsername()))
				{
					%>
					<div align="center">
						<h1>Historial de compras de <%=usuario.getUsername() %>:</h1>
						<% 
							List<Invoice> invoiceList = servlet.getInvoice(usuario.getIdUser());
							if(invoiceList.size() == 0)
							{%>
								<p>El usuario <%=usuario.getUsername() %> no ha realizado ninguna compra</p>
						  <%}
							else
							{%>
								<%for(Invoice invoice : invoiceList)
								  {%>
									<table border="2">
										<tr align="center">
											<td colspan="2"><b>Compra realizada el <%=invoice.getDate() %></b></td>
										</tr>
										<tr>
											<td><b>PRODUCTO</b></td>
											<td><b>UNIDADES</b></td>
										</tr>
										<%for(Entry<ProductoEntity, Integer> entry : invoice.getProductList().entrySet())//Entry<ProductoEntity, Integer> entry : invoice.getProductList().entrySet())
										{%>
										<tr>
											<td><%=entry.getKey().getDescription() %></td>
											<td><%=entry.getValue() %></td>
										</tr>
										<%}%>
										 <tr>
										 	<td colspan="2">TOTAL COMPRA: <%=invoice.getTotal()%> euros</td>
										 </tr>
									 </table>
									 <br/><br/>
								<%}%>
							<%} %>
						<a href="">Comprar productos</a> <a href="Index.jsp">Cerrar sesi&oacute;n</a>
					</div>
					<%
				}
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
							<h1>Carrito de la compra de <%=usuario.getUsername()  %></h1>
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
								   	    ProductoEntity p = servlet.getProduct(entry.getKey());
									    totalProd = p.getPrice().doubleValue() * entry.getValue();
								   		total += totalProd;%>
								   		<tr>
											<td><input type="radio" onchange="habilitar();" name="ProdElim" value="<%=entry.getKey() %>"></td> <!-- listaProductos.get(i) -->
											<td><%=p.getDescription() %></td>
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
			  		
		  <%	}
			}
		}
		else
		{
			response.sendRedirect("Validacion.jsp");
		}%>
	</body>
</html>