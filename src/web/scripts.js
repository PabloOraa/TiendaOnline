/**
 * Marca como habilitado el botón para eliminar productos dentro de la tabla de productos que ha seleccionado el usuario.
 * Este método solo se llamará cuando hayamos seleccionado uno de los productos del Radio Button en la primera columna de la tabla.
 */
function habilitar()
{
	document.getElementById("deleteProd").disabled = false;
}

/**
 * En función de los valores que se encuentren en el formulario del nombre de usuario y la contraseña, el botón para darse de alta/
 * validar el nombre de usuario y contraseña, se habilitará o se deshabilitara. No puede enviar un caracter vacío, por lo que estara
 * marcado com deshabiltado en caso de que estén vacios y se habilitará en cuanto se introduzcan ambos valores y se haya pulsado fuera de
 * uno de los elementos HTML para invocar al evento onchange.
 */
function habilitarBoton()
{
	if(document.getElementById("Name").value != "" &&
		document.getElementById("Pass").value != "")
		document.getElementById("Register").disabled = false;
	else
		document.getElementById("Register").disabled = true;
}

/**
 * Ajusta el número de unidades del producto a -1 para que no de ningún error si el usuario no completa todos
 * los datos que debería al seleccionar un producto o si desea ver el carrito sin comprar nada.
 */
function ajustarUnidades()
{
	document.getElementById("Unidades").value = -1;
}

/**
 * Comprueba la seguridad de la contraseña en función del número de caracteres y de cuales sean estos. Por ello, podemos encontrar
 * tres clasificaciones diferentes.
 * <ul>
 * 	<li><b>Poco Segura:</b> La contraseña dispone de menos de 7 caracteres, independiente de los que sean estos.</li>
 *  <li><b>Segura:</b> La contraseña dispone de más de 7 caracteres, pero no cuenta con al menos una letra mayúscula, una letra minúscula, un símbolo (!"·$%&/()=) y un número.</li>
 *  <li><b>Muy Segurda:</b> La contraseña dispone de más de 7 caracteres y si dispone de los cuatro elementos indispensables, es decir, mayúscula, minúscula, símbolo y número.</li>
 * </ul>
 * 
 * Bajo esta premisa, se comprueba la longitud del campo Pass dentro del formulario, y si es más de 7, se comprueba si cuenta con 
 * símbolos, mayúsculas, minúsculas números. Esto le dará un color asociado y un nombre que mostrar en el formulario.Poco Segura
 */
function probarPass()
{
	var contra = document.getElementById("Pass").value;
	var seguridad = "";
	var color = "";
	if(contra.length < 7)
	{
		seguridad = "Poco Segura";
		color = "red";
	}
	else if(contra.length == 0)
	{
		seguridad = "";
		color = "";
	}
    else
        if(comprobarSimbolo(contra) && comprobarLetraMayuscula(contra) 
                && comprobarLetraMinuscula(contra) && comprobarNumero(contra))
        {
        	seguridad = "Muy Segura";
        	color = "green";
        }
        else
        {
        	seguridad = "Segura";
        	color = "#e6ac00";
        }

    document.getElementById("textoPass").innerHTML = "<font color=" + color + " > Contrase&ntilde;a " + seguridad + "</font>";
}

/**
 * Comprueba que la cadena pasada por parámetro contiene al menos un símbolo de los asociados, es decir (!"·$%&/()=).
 * @param contra Contraseña a evaluar si continene un símbolo.
 * @returns True si existe un símbolo y false si no.
 */
function comprobarSimbolo(contra)
{
	var simbolo = false;
	var cadenaSimbolos = "!\"·$%&/()=";
    for (var i = 0; i < contra.length; i++) 
        for (var j = 0; j < cadenaSimbolos.length; j++) 
            if(contra.charAt(i) == cadenaSimbolos.charAt(j))
                return true;
    return false
}

/**
 * Comprueba que la cadena pasada por parámetro contiene al menos una minúscula de los asociados.
 * @param contra Contraseña a evaluar si continene una minúscula.
 * @returns True si existe una minúscula y false si no.
 */
function comprobarLetraMinuscula(contra)
{
	var contraMin = contra.toLowerCase();
	for (var i = 0; i < contra.length; i++) 
        if(contraMin.charAt(i) == contra.charAt(i))
            return true;

    return false;
}

/**
 * Comprueba que la cadena pasada por parámetro contiene al menos una mayúscula de los asociados.
 * @param contra Contraseña a evaluar si continene una mayúscula.
 * @returns True si existe una mayúscula y false si no.
 */
function comprobarLetraMayuscula(contra)
{
	var contraMin = contra.toUpperCase();
	for (var i = 0; i < contra.length; i++) 
        if(contraMin.charAt(i) == contra.charAt(i))
            return true;

    return false;
}

/**
 * Comprueba que la cadena pasada por parámetro contiene al menos un número.
 * @param contra Contraseña a evaluar si continene un número.
 * @returns True si existe un número y false si no.
 */
function comprobarNumero(contra)
{
	var cadenaNumeros = "0123456789";
    for (var i = 0; i < contra.length; i++) 
        for (var j = 0; j < cadenaNumeros.length; j++) 
            if(contra.charAt(i) == cadenaNumeros.charAt(j))
                return true;
    return false
}