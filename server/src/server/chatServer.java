package server;

/**
 *
 * @author ReadUnpingco
 * @author JocelynLouie
 * @author KatrinaBoudreau
 */
import java.io.*;
import java.net.*;
import java.sql.Timestamp;
import java.util.*;
import javax.swing.JOptionPane;

public class chatServer {
	
	//Arrays are difficult to resize as needed, use ArrayLists instead for
	//maintaining connections, and current users
	public static ArrayList<Socket> ConnectionArray = new ArrayList<Socket>();
	public static ArrayList<String> CurrentUsers = new ArrayList<String>();
        
        //log
        public static PrintWriter writer;
	
	public static void main(String[] args) throws IOException{
                
		try{
			//instantiate new sever socket
			final int port = 8080;
			ServerSocket server = new ServerSocket(port);
			//server listening...
			System.out.println("Waiting for clients...");
                        
			//start of log file
                        writer = new PrintWriter(new FileOutputStream("controller_log.txt",true));
                        writer.printf("%-23s", new Timestamp(System.currentTimeMillis()));
                        writer.println(":svr: Controller session instantiated");
                        writer.flush();
			
                            while(true){
                                    //add a connection, save it in ArrayList
                                    Socket socket = server.accept();
                                    ConnectionArray.add(socket);
                                    System.out.println("Client connected from: " + socket.getLocalAddress().getHostName());
                                    
                                    //log new client
                                    writer.printf("%-23s", new Timestamp(System.currentTimeMillis()));
                                    writer.println(":svr: Remote client (" + socket.getLocalAddress().getHostName() + ") connected");
                                    writer.flush();

                                    //pre-timestamp username
                                    writer.printf("%-23s", new Timestamp(System.currentTimeMillis()));

                                    //save individual connection with a thread
                                    //This is so we can multiple clients
                                    chatServerReturn chat = new chatServerReturn(socket);
                                    
                                    //add to CurrentUsers list, to see everyone connected
                                    AddUserName(socket, chat);
                                    
                                    Thread thread = new Thread(chat);
                                    thread.start();
                            }
                
		}catch(Exception e){
			System.out.print(e);
		}
                
                //log session end & close
                writer.printf("%-23s", new Timestamp(System.currentTimeMillis()));
                writer.println(":svr: Chat Session Closed");
                writer.flush();
                writer.close();
	}
	
	public static void AddUserName(Socket socket, chatServerReturn chat) throws IOException{
		//accept user input for creating a new user
		Scanner input = new Scanner(socket.getInputStream());
		String userName = input.nextLine(); 
		CurrentUsers.add(userName);
                chat.setUsername(userName);
                
                //log new client username
                writer.println(":svr: Remote client (" + socket.getLocalAddress().getHostName() + ") created username " + userName + "");
                writer.flush();
		
		//echo CurrentUsers to all users
		for(int i = 0; i < chatServer.ConnectionArray.size(); i++){
			Socket temp = (Socket) chatServer.ConnectionArray.get(i);
			PrintWriter out = new PrintWriter(temp.getOutputStream());
                        out.println(userName + " joined the chatroom.");
                        out.flush();
                        
			//JListener command 
			out.println("#?!" + CurrentUsers);
			out.flush();
		}
	}
}
