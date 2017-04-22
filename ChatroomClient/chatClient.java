/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pkg3401project;

import java.net.*;
import java.io.*;
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
	
	public void run(){
		try{
			try{
				input = new Scanner(socket.getInputStream());
				out = new PrintWriter(socket.getOutputStream());
				out.flush();
				CheckStream();
			}
			finally{
				socket.close();
			}
		}
		catch(Exception e){
			System.out.println(e);
		}
	
}//end run
	
	public void Disconnect() throws IOException{
		out.println(chatClientGUI.UserName + " has disconnected.");
		out.flush();
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
			}
			else{
				chatClientGUI.TA_Conversation.append(msg + "\n");
                                if(!msg.contains(chatClientGUI.UserName))System.out.println(msg);
			}
		}
	}
	
	public void Send(String s){
		out.println(chatClientGUI.UserName + ": " + s);
		out.flush();
		chatClientGUI.TF_Message.setText("");
	}

}

