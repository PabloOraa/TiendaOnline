package clases;

/**
 * Clase User con todos los valores necesarios para recuperar de la base de datos
 * @version 1.2
 * @author Pablo Oraa L�pez
 */
public class User 
{
	/**
	 * N�mero identificativo del usuario dentro del sistema.
	 */
	private int idUser;
	/**
	 * Nombre de usuario que ha elegido aquel que se quiera registrar, y que debe ser �nico dentro de la aplicaci�n.
	 */
	private String username;
	/**
	 * Contrase�a elegida por el usuario para su identificaci�n en el inicio de sesi�n en la aplicaci�n. 
	 */
	private String password;
	
	/**
	 * Constructor por defecto de la aplicaci�n que crea un usuario completamente vac�o.
	 */
	public User()
	{
		username = "";
		password = "";
	}
	
	/**
	 * Constructor parametrizado que crea un usuario con el id de registrado en la aplicac�n, el nombre de usuario y la contrase�a.
	 * @param id Identificador del usuario en cuesti�n.
	 * @param user Nombre de usuario en formato texto.
	 * @param pass Contrase�a del usuario que inicia sesi�n y/o se registra.
	 */
	public User(int id, String user, String pass)
	{
		idUser = id;
		username = user;
		password = pass;
	}
	
	/**
	 * Obtiene el ID del usuario que est� utilizando la aplicaci�n.
	 * @return N�mero identificativo del mismo.
	 */
	public int getId()
	{
		return idUser;
	}

	/**
	 * Obtiene el nombre de usuario con el que se ha registrado e/o iniciado sesi�n.
	 * @return Nombre de usuario de la persona que utiliza la aplicaci�n.
	 */
	public String getUsername()
	{
		return username;
	}

	/**
	 * Asigna un nombre de usuario diferente al que ya tenga en el momento de la creaci�n.
	 * @param username Nuevo nombre de usuario a asignar.
	 */
	public void setUsername(String username)
	{
		this.username = username;
	}

	/**
	 * Obtiene la contrase�a con la que se ha registrado dentro de la aplicaci�n.
	 * @return Contrase�a del usuario.
	 */
	public String getPassword()
	{
		return password;
	}

	/**
	 * Asigna una contrase�a diferente a la que tenga en el momento de la creaci�n del usuario.
	 * @param password Nueva contrase�a por la que desea sustituir la actual.
	 */
	public void setPassword(String password)
	{
		this.password = password;
	}
	
	/**
	 * Obtiene el c�digo Hash generado autom�ticamente a partir del nombre de usuario y de la contrase�a.
	 * @see Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((password == null) ? 0 : password.hashCode());
		result = prime * result + ((username == null) ? 0 : username.hashCode());
		return result;
	}

	/**
	 * Comprueba que dos usuarios son iguales o no. Para que sean iguales deben disponer del mismo nombre de usuario y la 
	 * misma contrase�a.
	 * @see Object#equals(Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		if (password == null)
		{
			if (other.password != null)
				return false;
		} else if (!password.equals(other.password))
			return false;
		if (username == null)
		{
			if (other.username != null)
				return false;
		} else if (!username.equals(other.username))
			return false;
		return true;
	}

	/**
	 * Devuelve la representaci�n del usuario en formato texto. En este caso la representaci�n se hace �nicamente con el 
	 * nombre de usuario.
	 * @see Object#toString()
	 */
	@Override
	public String toString()
	{
		return username;
	}
}
