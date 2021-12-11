import java.net.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.io.*;
import java.util.*;


public class Server{
	public static ArrayList<ClientHandler>clt_hdr = new ArrayList<>();
	public static Connection con;
	
	public static void main(String args[]) throws IOException, ClassNotFoundException, SQLException{
		try {	
			ServerSocket ss = new ServerSocket(6666);
	        // Class.forName("com.mysql.jdbc.Driver");
	        con = DriverManager.getConnection("jdbc:mysql://localhost:3306/SDL_LMS", "root", "@mysql2020");
	        ExecutorService pool = Executors.newFixedThreadPool(5);
	        
	        while(true) {
		        System.out.println("starting Server ... ");	        
	        	Socket client = ss.accept();
	        	System.out.println("Client is connected !!");
	        	ClientHandler obj = new ClientHandler(client);
	        	clt_hdr.add(obj);
	        	pool.execute(obj);
	        }
	        
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
}
