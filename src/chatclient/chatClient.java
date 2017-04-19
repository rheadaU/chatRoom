package chatclient;

import java.net.*;
import java.io.*;
import java.sql.Timestamp;
import java.util.Scanner;
import javax.swing.JOptionPane;

public class chatClient implements Runnable{
	Socket socket;
	Scanner input;
	Scanner send = new Scanner(System.in);
	PrintWriter out;
	
	public chatClient(Socket s){
		this.socket = s;
	}
	
        @Override
	public void run(){
		try{
			try{    
				input = new Scanner(socket.getInputStream());
				out = new PrintWriter(socket.getOutputStream());
				out.flush();
				CheckStream();
			}
			finally{
                            if(!socket.isClosed()){
				socket.close();
                            }
			}
		}
		catch(Exception e){
			System.out.println(e);
		}
	
}//end run
	
	public void Disconnect() throws IOException{
		out.println(chatClientGUI.UserName + " has disconnected.");
		out.flush();
                
                //log client disconnect
                chatClientGUI.writer.printf("%-23s", new Timestamp(System.currentTimeMillis()));
                chatClientGUI.writer.println(":sys: Disconnected from chat");
                chatClientGUI.writer.flush();
                
		socket.close();
		JOptionPane.showMessageDialog(null, "You disconnected!");
		System.exit(0);
		
	}
	
	public void CheckStream(){
		while(true){
			Recieve();
		}
	}
	
	public void Recieve(){
		if(input.hasNext()){
			String msg = input.nextLine();
			
			//special command prefix
			if(msg.contains("#?!")){
				//sending entire ArrayList
				String temp = msg.substring(3);
				temp = temp.replace("[", "");
				temp = temp.replaceAll("]","");
				
				String[] CurrentUsers = temp.split(", ");
				chatClientGUI.JL_Online.setListData(CurrentUsers);
                        }else if(msg.equals("#syscheck#")){
                            //notify mother server that we're still here
                            out.println("#cltrsp#");
                            out.flush();
                        }else{
				chatClientGUI.TA_Conversation.append(msg + "\n");
                                
                                //log client message stream
                                chatClientGUI.writer.printf("%-23s", new Timestamp(System.currentTimeMillis()));
                                chatClientGUI.writer.println(":rcv: " + msg);
                                chatClientGUI.writer.flush();
			}
		}
	}
	
	public void Send(String s){
		out.println(chatClientGUI.UserName + ": " + s);
		out.flush();
                
                //log client send message log
                chatClientGUI.writer.printf("%-23s", new Timestamp(System.currentTimeMillis()));
                chatClientGUI.writer.println(":out: " + s);
                chatClientGUI.writer.flush();
                
		chatClientGUI.TF_Message.setText("");
	}

}