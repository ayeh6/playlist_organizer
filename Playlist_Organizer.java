import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

public class Lab07
{
	public static Connection connection = null;

	public static void connect_to_database()
	{
		try
		{
			connection = DriverManager.getConnection("jdbc:sqlite:playlist_organizer.db");
			Statement statement = connection.createStatement();
			statement.setQueryTimeout(30);
		}
		catch(SQLException e)
		{
			System.err.println(e.getMessage());
		}
	}

	public static void disconnect_from_database()
	{
		try
		{
			if(connection!=null)
			{
				connection.close();
			}
		}
		catch(SQLException e)
		{
			System.err.println(e.getMessage());
		}
	}

	public static void add_song()
	{	
		boolean fail = false;
		String input_songname, input_artistname, input_albumname, input_genrename, input_language;
		int u_userID = 0;
		Scanner input = new Scanner(System.in);

		System.out.print("Song: ");
		input_songname = input.nextLine();

		System.out.print("Artist: ");
		input_artistname = input.nextLine();	

		System.out.print("Album: ");
		input_albumname = input.nextLine();

		System.out.print("Genre: ");
		input_genrename = input.nextLine();
		
		System.out.print("Language ");
		input_lanuage = input.nextLine();

		try
		{
				Statement statement = connection.createStatement();
				statement.setQueryTimeout(30);
				ResultSet artistSet = statement.executeQuery("select a")
				ResultSet rs = statement.executeQuery("select u_userID from users where u_username like '"+input_username+"' ");
				
				while(rs.next())
				{
					u_userID = rs.getInt("u_userID");
				}
				if(u_userID != 0)
				{
					System.out.println("Username Already Exists");
					fail = true;
				}
				else
				{
					statement.executeUpdate("insert into users(u_username, u_fullname, u_age, u_country, u_admin, u_password) values ("+u_username+",'"+u_fullname+"',"+u_age+","+u_country+",'"+u_admin+"',"+u_password+")");
				}
			}
			catch(SQLException e)
			{
				System.err.println(e.getMessage());
			}
			if(fail == false)
			{
				System.out.println("User added");
			}
	}

	public static delete_user()
	{
		String input_username;
		int u_userID;
		Scanner input = new Scanner(System.in);

		System.out.print("Username: ");
		input_username = input.nextLine();

		try
		{
			Statement statement = connection.createStatement();
			statement.setQueryTimeout(30);
			ResultSet rs = statement.executeQuery("select u_userID from users where u_username='"+input_username+"'");
			while(rs.next())
			{
				u_userID = rs.getInt("u_userID");
			}
			if(u_userID==0)
			{
				System.out.println("User does not exist");
			}
			else
			{
				statement.executeUpdate("delete from users where u_username='"+input_username+"'");
			}
		}
	}
	
	public static void add_user()
	{	//Manage user : add or delete
		boolean fail = false;
		String input_username, input_fullname, input_age, input_country, input_password;
		int input_admin = -1;
		int u_userID = 0;
		Scanner input = new Scanner(System.in);
		System.out.println("Enter details of new user");
		System.out.print("Username: ");
		input_username = input.nextLine();

		System.out.print("Password: ");
		input_password = input.nextLine();		

		System.out.print("Fullname: ");
		input_fullname = input.nextLine();

		System.out.print("Age: ");
		input_age = input.nextLine();

		System.out.print("Country: ");
		input_country = input.nextLine();

		do
		{
			System.out.print("Admin Power (Y/N): ");
			String input_admin_str = input.nextLine();	
			if(input_admin_str == 'Y' || input_admin_str == 'y')
			{
				input_admin = 1;
			}
			else if(input_admin_str == 'N' || input_admin_str == 'n')
			{
				input_admin = 0;
			}
		} while(input_admin == -1)

		try
		{
			Statement statement = connection.createStatement();
			statement.setQueryTimeout(30);
			ResultSet rs = statement.executeQuery("select u_userID from users where u_username like '"+input_username+"' ");
			
			while(rs.next())
			{
				u_userID = rs.getInt("u_userID");
			}
			if(u_userID != 0)
			{
				System.out.println("Username Already Exists");
				fail = true;
			}
			else
			{
				statement.executeUpdate("insert into users(u_username, u_fullname, u_age, u_country, u_admin, u_password) values ('"+u_username+"','"+u_fullname+"',"+u_age+",'"+u_country+"',"+u_admin+",'"+u_password+"')");
			}
		}
		catch(SQLException e)
		{
			System.err.println(e.getMessage());
		}
		if(fail == false)
		{
			System.out.println("User added");
		}
	}
	
	public static void main(String[] args)
	{
		Scanner input = new Scanner(System.in);
		int exit=0;
		connect_to_database();
		System.out.print("username: ");
		String username=input.nextLine();
		System.out.print("password");
		String password=input.nextLine();
		try
		{
			statement = connection.createStatement();
			ResultSet rs = statement.executeQuery("select u_userID, u_admin from users where u_username='"+username+"' and password='"+password"'");
			int rs_u_userID=0;
			//verify the user
			while(rs.next())
			{
				rs_u_userID=rs.getInt("u_userID");
				rs_u_admin=rs.getInt("u_admin");
			}
			if(rs_userID==0)
			{
				System.out.println("Invalid username or password");
				exit=1;
			}
			else
			{
				if(rs_u_admin==1)
				{
					System.out.println("Log in as Administrator? (Y/N)");
					String admin_login=input.nextLine();
					if(admin_login=='Y' || admin_login=='y')
					{
						//log in as admin
					}
					else
					{
						//log in as user
					}
				}
			}
		}
		catch(SQLException e)
		{
			System.err.println(e.getMessage());
		}
		while(exit==0)
		{

		}
		//closing the connection
		disconnect_from_database();
	}
}
