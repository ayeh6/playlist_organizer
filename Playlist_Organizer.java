import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

public class Playlist_Organizer
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
		input_language = input.nextLine();

		// try
		// {
		// 		Statement statement = connection.createStatement();
		// 		statement.setQueryTimeout(30);
		// 		ResultSet rs = statement.executeQuery("select u_userID from users where u_username like '"+input_username+"'");
		// 		while(rs.next())
		// 		{
		// 			u_userID = rs.getInt("u_userID");
		// 		}
		// 		if(u_userID != 0)
		// 		{
		// 			System.out.println("Username Already Exists");
		// 			fail = true;
		// 		}
		// 		else
		// 		{
		// 			statement.executeUpdate("insert into users(u_username, u_fullname, u_age, u_country, u_admin, u_password) values ("+u_username+",'"+u_fullname+"',"+u_age+","+u_country+",'"+u_admin+"',"+u_password+")");
		// 		}
		// 	}
		// 	catch(SQLException e)
		// 	{
		// 		System.err.println(e.getMessage());
		// 	}
		if(fail == false)
		{
			System.out.println("User added");
		}
	}

	public static void delete_user()
	{
		String input_username;
		int u_userID=0;
		Scanner input = new Scanner(System.in);

		System.out.print("Username: ");
		input_username = input.nextLine();

		try
		{
			Statement statement = connection.createStatement();
			statement.setQueryTimeout(30);
			ResultSet rs = statement.executeQuery("select u_userID from users where u_username like '"+input_username+"'");
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
				statement.executeUpdate("delete from users where u_username like '"+input_username+"'");
			}
		}
		catch(SQLException e)
		{
			System.err.println(e.getMessage());
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
			if(input_admin_str.equals("Y") || input_admin_str.equals("y"))
			{
				input_admin = 1;
			}
			else if(input_admin_str.equals("N") || input_admin_str.equals("n"))
			{
				input_admin = 0;
			}
		} while(input_admin == -1);

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
				statement.executeUpdate("insert into users(u_username, u_fullname, u_age, u_country, u_admin, u_password) values ('"+input_username+"','"+input_fullname+"',"+input_age+",'"+input_country+"',"+input_admin+",'"+input_password+"')");
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
	
	public static int user_interface()
	{
		Scanner input = new Scanner(System.in);
		int selection=0;
		System.out.println("Select an option:");
		System.out.println("1 create playlist");
		System.out.println("2 browse");
		selection=input.nextInt();
		if(selection==1)
		{
			//create playlist function
		}
		else
		{
			//browse function
		}

		int exit_int=0;
		String exit;
		do
		{
			System.out.print("exit? (Y/N): ");
			exit=input.nextLine();
			if(exit.equals("Y") || exit.equals("y"))
			{
				exit_int=1;
				return 1;
			}
			else if(exit.equals("N") || exit.equals("n"))
			{
				exit_int=1;
				return 0;
			}
			System.out.println("incorrect input");
		}while(exit_int == 0);
		
		return 0;
	}

	public static int admin_interface()
	{
		Scanner input = new Scanner(System.in);
		int selection=0;
		System.out.println("Select an option:");
		System.out.println("1 add user");
		System.out.println("2 delete user");
		System.out.println("3 add/delete from library");
		selection=input.nextInt();
		if(selection==1)
		{
			add_user();
		}
		else if(selection==2)
		{
			delete_user();
		}

		int exit_int=0;
		String exit;
		do
		{
			System.out.print("exit? (Y/N): ");
			exit=input.nextLine();
			if(exit.equals("Y") || exit.equals("y"))
			{
				exit_int=1;
				return 1;
			}
			else if(exit.equals("N") || exit.equals("n"))
			{
				exit_int=1;
				return 0;
			}
			System.out.println("incorrect input");
		}while(exit_int == 0);

		return 0;
	}

	public static void main(String[] args)
	{
		Scanner input = new Scanner(System.in);
		int exit=0,rs_u_admin=0,admin_selection=0;
		connect_to_database();
		System.out.print("username: ");
		String input_username=input.nextLine();
		System.out.print("password: ");
		String input_password=input.nextLine();
		try
		{
			Statement statement = connection.createStatement();
			statement.setQueryTimeout(30);
			ResultSet rs = statement.executeQuery("select u_userID, u_admin from users where u_username like '"+input_username+"' and u_password like '"+input_password+"'");
			int rs_u_userID=0;
			//verify the user
			while(rs.next())
			{
				rs_u_userID=rs.getInt("u_userID");
				rs_u_admin=rs.getInt("u_admin");
			}
			if(rs_u_userID==0)
			{
				System.out.println("Invalid username or password");
				exit=1;
			}
			else
			{
				if(rs_u_admin==1)
				{
					System.out.print("Log in as Administrator? (Y/N): ");
					String admin_login=input.nextLine();
					System.out.println(admin_login);
					if(admin_login.equals("Y") || admin_login.equals("y"))
					{
						//log in as admin
						admin_selection=1;
					}
					else if(admin_login == "N" || admin_login == "n")
					{
						//log in as user
						admin_selection=0;
					}
				}
				else
				{
					admin_selection=0;
				}
			}
		}
		catch(SQLException e)
		{
			System.err.println(e.getMessage());
		}
		//interfaces
		while(exit==0)
		{
			if(admin_selection==1)
			{
				exit = admin_interface();
			}
			else if(admin_selection==0)
			{
				exit = user_interface();
			}
		}
		//closing the connection
		disconnect_from_database();
	}
}
