<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
	<head>
		<meta charset="ISO-8859-1">
		<title>Validaci�n</title>
		<script type="text/javascript" src="scripts.js">
			
		</script>
	</head>
	<body>
		<div style='margin: 200px auto; width: 300px'>
			<h1>Inicio de sesi�n en la Tienda de DAM</h1>
			<form action=ServletControlador method=POST>
				<%HttpSession sesion = request.getSession(true);
					if(sesion.getAttribute("ErrorAuth") != null && (boolean)sesion.getAttribute("ErrorAuth"))
					{%>
						<p><font color="red">El usuario o la contrase&ntilde;a introducida no es correcto.</font></p>
						<% sesion.setAttribute("ErrorAuth", false);
				    } %>
				Usuario:	<input id="Name" onchange="habilitarBoton();" type=text name=User><br><br>
				Contrase&ntilde;a: <input id="Pass" onchange="habilitarBoton();" type=password name=Pass><br><br> 
				<input id="Register" disabled type=submit name="Tipo" value="Ir a tienda"/>
				<a href="Alta.jsp">Crear cuenta</a>
				<div  align="right">
					<h6>*Si se han introducido los datos y sigue deshabilitado, pulsar en la p&aacute;gina</h6>
				</div>
			</form>
		</div>
	</body>
</html>