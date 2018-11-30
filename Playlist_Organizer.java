import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;


public class Playlist_Organizer
{
	public static Connection connection = null;

	public static String current_username;
	public static int current_userID;

	public static void connect_to_database()
	{
		try
		{
			connection = DriverManager.getConnection("jdbc:sqlite:playlist_organizer.db");
			Statement statement = connection.createStatement();
			statement.execute("PRAGMA foreign_keys = ON");
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
		int s_songID=0,ar_artID=0,al_albID=0,g_genID=0;
		Scanner input = new Scanner(System.in);

		System.out.println("Enter details of new song");
		System.out.print("Song name: ");
		input_songname = input.nextLine();
		System.out.print("Artist name: ");
		input_artistname = input.nextLine();
		System.out.print("Album name: ");
		input_albumname = input.nextLine();
		System.out.print("Genre: ");
		input_genrename = input.nextLine();
		System.out.print("Language ");
		input_language = input.nextLine();

		try
		{
			Statement statement = connection.createStatement();
			statement.setQueryTimeout(30);
			ResultSet rs;

			// check if genre exists
			rs = statement.executeQuery("select g_genID from genres where g_name like '"+input_genrename+"'");
			while(rs.next())
			{
				g_genID = rs.getInt("g_genID");
			}
			if(g_genID==0)
			{
				System.out.println("Genre "+input_genrename+" does not exist.");
				fail = true;
			}

			// check if artist exists
			rs = statement.executeQuery("select ar_artID from artists where ar_name like '"+input_artistname+"'");
			while(rs.next())
			{
				ar_artID = rs.getInt("ar_artID");
			}
			if(ar_artID==0)
			{
				System.out.println("Artist "+input_artistname+" does not exist.");
				fail = true;
			}

			// check if album exists
			rs = statement.executeQuery("select al_albID from albums where al_name like '"+input_albumname+"' and al_artID="+ar_artID+"");
			while(rs.next())
			{
				al_albID = rs.getInt("al_albID");
			}
			if(al_albID==0)
			{
				System.out.println("Album "+input_albumname+" from "+input_artistname+" does not exist.");
				fail = true;
			}

			// check if song already exists
			rs = statement.executeQuery("select s_songID from songs where s_name like '"+input_songname+"' and s_artID="+ar_artID+" and s_albID="+al_albID+" and s_genID="+g_genID+"");
			while(rs.next())
			{
				s_songID = rs.getInt("s_songID");
			}
			if(s_songID!=0)
			{
				System.out.println("Song "+input_songname+" in "+input_albumname+" from "+input_artistname+" already exists");
				fail=true;
			}
			else
			{
				statement.executeUpdate("insert into songs (s_name,s_artID,s_albID,s_genID,s_language) values ('"+input_songname+"',"+ar_artID+","+al_albID+","+g_genID+",'"+input_language+"')");
			}
		}
		catch(SQLException e)
		{
			System.err.println(e.getMessage());
		}
		if(fail == false)
		{
			System.out.println("Song added");
		}
	}

	public static void delete_song()
	{
		boolean fail=false;
		String input_songname;
		int s_songID;
		Scanner input = new Scanner(System.in);

		System.out.print("Search for song to delete: ");
		input_songname = input.nextLine();

		try
		{
			Statement statement = connection.createStatement();
			statement.setQueryTimeout(30);
			ResultSet rs = statement.executeQuery("select s_songID,s_name,al_name,ar_name from songs,artists,albums where s_artID=ar_artID and s_albID=al_albID and s_name like '%"+input_songname+"%'");
			if(!rs.isBeforeFirst())
			{
				System.out.println("No search results");
				fail=true;
			}
			else
			{
				while(rs.next())
				{
					System.out.println(rs.getInt("s_songID")+" - '"+rs.getString("s_name")+"' in "+rs.getString("al_name")+" by "+rs.getString("ar_name"));
				}
				System.out.print("Enter songID of song to delete: ");
				s_songID = input.nextInt();
				rs = statement.executeQuery("select * from songs where s_songID="+s_songID+" and s_name like '"+input_songname+"'");
				if(!rs.isBeforeFirst())
				{
					System.out.println("Invalid songID");
					fail=true;
				}
				else if(admin_authenticate())
				{
					statement.executeUpdate("delete from songs where s_songID="+s_songID+" and s_name like '"+input_songname+"'");
				}

			}
		}
		catch(SQLException e)
		{
			System.err.println(e.getMessage());
		}
		if(fail==false)
		{
			System.out.println("Song deleted");
		}
	}

	public static void delete_album()
	{
		boolean fail = false;
		Scanner input = new Scanner(System.in);
		System.out.print("Search for album to delete: ");
		String al_name = input.nextLine();

		try
		{
			Statement statement = connection.createStatement();
			statement.setQueryTimeout(30);
			ResultSet rs = statement.executeQuery("select al_albID, al_name, ar_name from artists,albums where al_name like '%"+al_name+"%' and al_artID=ar_artID");
			if(!rs.isBeforeFirst())
			{
				System.out.println("No search results");
				fail=true;
			}
			else
			{
				while(rs.next())
				{
					System.out.println(rs.getInt("al_albID")+" - '"+rs.getString("al_name")+"' by "+rs.getString("ar_name"));
				}
				System.out.print("Enter album ID number of album to delete: ");
				int al_albID = input.nextInt();
				rs = statement.executeQuery("select * from albums where al_name like '"+al_name+"' and al_albID="+al_albID);
				if(!rs.isBeforeFirst())
				{
					System.out.println("Invalid album ID number");
					fail=true;
				}
				else if(admin_authenticate())
				{
					statement.executeUpdate("delete from albums where al_albID="+al_albID);
				}
			}
		}
		catch(SQLException e)
		{
			System.err.println(e.getMessage());
		}
		if(fail==false)
		{
			System.out.println("Album deleted");
		}
	}

	public static void delete_artist()
	{
		boolean fail = false;
		Scanner input = new Scanner(System.in);
		System.out.print("Search for artist to delete: ");
		String ar_name = input.nextLine();

		try
		{
			Statement statement = connection.createStatement();
			statement.setQueryTimeout(30);
			ResultSet rs = statement.executeQuery("select ar_artID, ar_name from artists where al_name like '%"+ar_name+"%'");
			if(!rs.isBeforeFirst())
			{
				System.out.println("No search results");
				fail=true;
			}
			else
			{
				while(rs.next())
				{
					System.out.println(rs.getInt("ar_artID")+" - '"+rs.getString("ar_name")+"'");
				}
				System.out.print("Enter ID number of artist to delete: ");
				int ar_artID = input.nextInt();
				rs = statement.executeQuery("select * from artists where ar_name like '%"+ar_name+"%' and ar_artID="+ar_artID);
				if(!rs.isBeforeFirst())
				{
					System.out.println("Invalid ID number");
					fail=true;
				}
				else if(admin_authenticate())
				{
					statement.executeUpdate("delete from artists where ar_artID="+ar_artID);
				}
			}
		}
		catch(SQLException e)
		{
			System.err.println(e.getMessage());
		}
		if(fail==false)
		{
			System.out.println("Artist deleted");
		}
	}

	public static void delete_genre()
	{
		boolean fail = false;
		Scanner input = new Scanner(System.in);
		System.out.print("Search for genre to delete: ");
		String g_name = input.nextLine();

		try
		{
			Statement statement = connection.createStatement();
			statement.setQueryTimeout(30);
			ResultSet rs = statement.executeQuery("select g_genID, g_name from genres where g_name like '%"+g_name+"%'");
			if(!rs.isBeforeFirst())
			{
				System.out.println("No search results");
				fail=true;
			}
			else
			{
				while(rs.next())
				{
					System.out.println(rs.getInt("g_genID")+" - '"+rs.getString("g_name")+"'");
				}
				System.out.print("Enter ID number of genre to delete: ");
				int g_genID = input.nextInt();
				rs = statement.executeQuery("select * from genres where g_name like '%"+g_name+"%' and g_genID="+g_genID);
				if(!rs.isBeforeFirst())
				{
					System.out.println("Invalid ID number");
					fail=true;
				}
				else if(admin_authenticate())
				{
					statement.executeUpdate("delete from genres where g_genID="+g_genID);
				}
			}
		}
		catch(SQLException e)
		{
			System.err.println(e.getMessage());
		}
		if(fail==false)
		{
			System.out.println("Genre deleted");
		}
	}

	public static void delete_user()
	{
		String input_username;
		int u_userID;
		Scanner input = new Scanner(System.in);

		System.out.print("Search for username to delete: ");
		input_username = input.nextLine();

		try
		{
			Statement statement = connection.createStatement();
			statement.setQueryTimeout(30);
			ResultSet rs = statement.executeQuery("select u_userID, u_username, u_fullname from users where u_username like '%"+input_username+"%'");
			if(!rs.isBeforeFirst())
			{
				System.out.println("No search results");
			}
			else
			{
				while(rs.next())
				{
					System.out.println(rs.getInt("u_userID")+" - "+rs.getString("u_username")+" | "+rs.getString("u_fullname"));
				}
				System.out.print("Enter ID number of user to delete: ");
				u_userID = input.nextInt();
				rs = statement.executeQuery("select * from users where u_userID="+u_userID+" and u_username like '%"+input_username+"%'");
				if(!rs.isBeforeFirst())
				{
					System.out.println("Invalid ID number");
				}
				else if(admin_authenticate())
				{
					statement.setQueryTimeout(30);
					statement.executeUpdate("delete from users where u_userID="+u_userID);
				}
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

	public static void add_genre()
	{
		boolean fail = false;
		String input_genrename;
		int g_genID = 0;
		Scanner input = new Scanner(System.in);

		System.out.print("Enter new genre name: ");
		input_genrename = input.nextLine();

		try
		{
			Statement statement = connection.createStatement();
			statement.setQueryTimeout(30);
			ResultSet rs = statement.executeQuery("SELECT g_genID FROM genres WHERE g_name LIKE '"+input_genrename+"' ");

			while(rs.next())
			{
				g_genID = rs.getInt("g_genID");
			}
			if(g_genID != 0)
			{
				System.out.println("Genre Already Exists");
				fail = true;
			}
			else
			{
				statement.executeUpdate("insert into genres(g_name) values ('"+input_genrename+"')");
			}
		}
		catch(SQLException e)
		{
			System.err.println(e.getMessage());
		}
		if(fail == false)
		{
			System.out.println("Genre added");
		}
	}

	public static void add_album()
	{
		boolean fail = false;
		String input_albumname, input_artistname, input_genrename;
		int al_albID = 0, ar_artID = 0, g_genID = 0;
		Scanner input = new Scanner(System.in);
		System.out.println("Enter details of new album");
		System.out.print("Album name: ");
		input_albumname = input.nextLine();
		System.out.print("Artist name: ");
		input_artistname = input.nextLine();
		System.out.print("Genre: ");
		input_genrename = input.nextLine();

		try
		{
			Statement statement = connection.createStatement();
			statement.setQueryTimeout(30);
			ResultSet rs;

			// check if genre exists
			rs = statement.executeQuery("select g_genID from genres where g_name like '"+input_genrename+"'");
			while(rs.next())
			{
				g_genID = rs.getInt("g_genID");
			}
			if(g_genID == 0)
			{
				System.out.println("Genre "+input_genrename+" does not exist.");
				fail = true;
			}

			// check if artist exists
			rs = statement.executeQuery("select ar_artID from artists where ar_name like '"+input_artistname+"'");
			while(rs.next())
			{
				ar_artID = rs.getInt("ar_artID");
			}
			if(ar_artID == 0)
			{
				System.out.println("Artist "+input_artistname+" does not exist.");
				fail = true;
			}

			// check if album already exists
			rs = statement.executeQuery("select al_albID from albums where al_name like '"+input_albumname+"' and al_artID="+ar_artID+"");
			while(rs.next())
			{
				al_albID = rs.getInt("al_albID");
			}
			if(al_albID != 0)
			{
				System.out.println("Album "+input_albumname+" by "+input_artistname+" in genre "+input_genrename+" already exists");
				fail = true;
			}
			else
			{
				statement.executeUpdate("insert into albums (al_name, al_artID, al_genID) values ('"+input_albumname+"',"+ar_artID+","+g_genID+")");
			}
		}
		catch(SQLException e)
		{
			System.err.println(e.getMessage());
		}
		if(fail == false)
		{
			System.out.println("Album added");
		}
	}

	public static void add_artist()
	{
		boolean fail = false;
		String input_artistname, input_genrename;
		int ar_artID = 0, g_genID = 0;
		Scanner input = new Scanner(System.in);
		System.out.println("Enter details of new artist");
		System.out.print("Artist name: ");
		input_artistname = input.nextLine();
		System.out.print("Genre: ");
		input_genrename = input.nextLine();

		try
		{
			Statement statement = connection.createStatement();
			statement.setQueryTimeout(30);
			ResultSet rs;

			// check if genre exists
			rs = statement.executeQuery("select g_genID from genres where g_name like '"+input_genrename+"'");
			while(rs.next())
			{
				g_genID = rs.getInt("g_genID");
			}
			if(g_genID == 0)
			{
				System.out.println("Genre "+input_genrename+" does not exist.");
				fail = true;
			}

			// check if artist exists
			rs = statement.executeQuery("select ar_artID from artists where ar_name like '"+input_artistname+"'");
			while(rs.next())
			{
				ar_artID = rs.getInt("ar_artID");
			}
			if(ar_artID != 0)
			{
				System.out.println("Artist Already Exists");
				fail = true;
			}
			else
			{
				statement.executeUpdate("insert into artists(ar_name, ar_genID) values ('"+input_artistname+"',"+g_genID+")");
			}
		}
		catch(SQLException e)
		{
			System.err.println(e.getMessage());
		}
		if(fail == false)
		{
			System.out.println("Artist added");
		}
	}

	public static boolean admin_authenticate()
	{
		Scanner input = new Scanner(System.in);
		String input_password;
		System.out.print("enter password: ");
		input_password = input.nextLine();
		try
		{
			Statement statement = connection.createStatement();
			statement.setQueryTimeout(30);
			ResultSet rs = statement.executeQuery("select u_userID from users where u_username like '"+current_username+"' and u_password='"+input_password+"'");
			if(!rs.isBeforeFirst())
			{
				System.out.println("invalid password");
				return false;
			}
			else
			{
				return true;
			}
		}
		catch(SQLException e)
		{
			System.err.println(e.getMessage());
		}
		return false;
	}

	public static void create_playlist()
	{	
		boolean fail = false;
		String input_playlistname;
		int p_playlistID;
		Scanner input = new Scanner(System.in);

		System.out.print("Enter name for new playlist: ");
		input_playlistname = input.nextLine();

		try
		{
				Statement statement = connection.createStatement();
				statement.setQueryTimeout(30);
				ResultSet rs;

				// check if playlist exists
				rs = statement.executeQuery("SELECT p_playlistID FROM playlists WHERE p_name LIKE '"+input_playlistname+"'");
				while(rs.next())
				{
					p_playlistID = rs.getInt("p_playlistID");
				}
				if(p_playlistID != 0)
				{
					System.out.println("Playlist "+input_playlistname+" already exists.");
					fail = true;
                }
                else
				{
					statement.executeUpdate("INSERT INTO playlists (p_name, p_userID) VALUES ('"+input_playlistname+"',"+current_userID+")");
				}
			}
			catch(SQLException e)
			{
				System.err.println(e.getMessage());
			}
		if(fail == false)
		{
			System.out.println("Playlist created");
		}
	}


	public static int user_interface()
	{
		Scanner input = new Scanner(System.in);
		int selection=0;
		System.out.println("Main Menu");
		System.out.println("1 - Create a Playlist");
		System.out.println("2 - Browse");
		selection=input.nextInt();
		if(selection==1)
		{
			create_playlist();
		}
		else
		{
			//browse function
			System.out.println("\nSelect category to search");
			System.out.println("1 - Songs");
		}
		return exit_func();
	}

	public static int admin_interface()
	{
		Scanner input = new Scanner(System.in);
		int selection=0;
		System.out.println("ADMINISTRATOR MENU");
		System.out.println("1 - add user");
		System.out.println("2 - delete user");
		System.out.println("3 - add song");
		System.out.println("4 - add album");
		System.out.println("5 - add artist");
		System.out.println("6 - add genre");
		System.out.println("7 - delete song");
		System.out.println("8 - delete album");
		System.out.println("9 - delete artist");
		System.out.println("10 - delete genre");
		System.out.println("0 - exit");
		System.out.print("Select an option: ");
		selection=input.nextInt();
		System.out.println();
		switch(selection)
		{
			case 1:
				add_user();
				break;
			case 2:
				delete_user();
				break;
			case 3:
				add_song();
				break;
			case 4:
				add_album();
				break;
			case 5:
				add_artist();
				break;
			case 6:
				add_genre();
				break;
			case 7:
				delete_song();
				break;
			case 8:
				delete_album();
				break;
			case 9:
				delete_artist();
				break;
			case 10:
				delete_genre();
				break;
			case 0:
				return 1;
		}
		return exit_func();
	}

	public static int exit_func()
	{
		Scanner input = new Scanner(System.in);
		int exit_int=0;
		String exit;
		System.out.print("exit? (Y/N): ");
		exit=input.nextLine();
		System.out.println();
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
		else
		{
			System.out.println("incorrect input");
			return exit_func();
		}
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
			ResultSet rs = statement.executeQuery("select u_userID, u_admin from users where u_username like '"+input_username+"' and u_password='"+input_password+"'");
			int rs_u_userID=0;
			//verify the user
			if(!rs.isBeforeFirst())
			{
				System.out.println("Invalid username or password");
				exit=1;
			}
			else
			{
				while(rs.next())
				{
					rs_u_userID=rs.getInt("u_userID");
					rs_u_admin=rs.getInt("u_admin");
				}
				current_userID=rs_u_userID;
				current_username=input_username;
				if(rs_u_admin==1)
				{
					System.out.print("Log in as Administrator? (Y/N): ");
					String admin_login=input.nextLine();
					System.out.println();
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
		System.out.println("Logging out...");
		disconnect_from_database();
	}
}
