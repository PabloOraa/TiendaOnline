package clases;

/**
 * Clase User con todos los valores necesarios para recuperar de la base de datos
 * @version 1.2
 * @author Pablo Oraa López
 */
public class User 
{
	/**
	 * Número identificativo del usuario dentro del sistema.
	 */
	private int idUser;
	/**
	 * Nombre de usuario que ha elegido aquel que se quiera registrar, y que debe ser único dentro de la aplicación.
	 */
	private String username;
	/**
	 * Contraseña elegida por el usuario para su identificación en el inicio de sesión en la aplicación. 
	 */
	private String password;
	
	/**
	 * Constructor por defecto de la aplicación que crea un usuario completamente vacío.
	 */
	public User()
	{
		username = "";
		password = "";
	}
	
	/**
	 * Constructor parametrizado que crea un usuario con el id de registrado en la aplicacón, el nombre de usuario y la contraseña.
	 * @param id Identificador del usuario en cuestión.
	 * @param user Nombre de usuario en formato texto.
	 * @param pass Contraseña del usuario que inicia sesión y/o se registra.
	 */
	public User(int id, String user, String pass)
	{
		idUser = id;
		username = user;
		password = pass;
	}
	
	/**
	 * Obtiene el ID del usuario que está utilizando la aplicación.
	 * @return Número identificativo del mismo.
	 */
	public int getId()
	{
		return idUser;
	}

	/**
	 * Obtiene el nombre de usuario con el que se ha registrado e/o iniciado sesión.
	 * @return Nombre de usuario de la persona que utiliza la aplicación.
	 */
	public String getUsername()
	{
		return username;
	}

	/**
	 * Asigna un nombre de usuario diferente al que ya tenga en el momento de la creación.
	 * @param username Nuevo nombre de usuario a asignar.
	 */
	public void setUsername(String username)
	{
		this.username = username;
	}

	/**
	 * Obtiene la contraseña con la que se ha registrado dentro de la aplicación.
	 * @return Contraseña del usuario.
	 */
	public String getPassword()
	{
		return password;
	}

	/**
	 * Asigna una contraseña diferente a la que tenga en el momento de la creación del usuario.
	 * @param password Nueva contraseña por la que desea sustituir la actual.
	 */
	public void setPassword(String password)
	{
		this.password = password;
	}
	
	/**
	 * Obtiene el código Hash generado automáticamente a partir del nombre de usuario y de la contraseña.
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
	 * misma contraseña.
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
	 * Devuelve la representación del usuario en formato texto. En este caso la representación se hace únicamente con el 
	 * nombre de usuario.
	 * @see Object#toString()
	 */
	@Override
	public String toString()
	{
		return username;
	}
}
