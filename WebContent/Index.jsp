<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
	<head>
		<meta charset="ISO-8859-1">
		<title>Inicio</title>
	</head>
	<body>
		<% if(request.getParameter("OptionInit") == null)
		{
			request.getSession(true).invalidate();%>
			<p>Bienvenido a la Tienda de DAM, donde podremos acceder a una selecta cantidad de productos exclusivos. Por ello, es 
				necesario que nos registremos en el servicio. Seleccione la opción que desea para acceder a la tienda. Muchas gracias
				por visitarnos.</p>
			<div align="center">
				<form action="Index.jsp" method="post">
					<input type="submit" name="OptionInit" value="Acceso Tienda DAM"/>
					<input type="submit" name="OptionInit" value="Alta cliente"/>
				</form>
			</div>
		<%}
		  else
		  {
			  if(request.getParameter("OptionInit").equals("Acceso Tienda DAM"))
				  response.sendRedirect("Validacion.jsp");
			  else if(request.getParameter("OptionInit").equals("Alta cliente"))
				  response.sendRedirect("Alta.jsp");
			  else
				  response.sendRedirect("Index.jsp");
		  }%>
	</body>
</html>