package chatproject;

import java.io.*;
import java.net.*;
import java.sql.Timestamp;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

public class chatServerReturn implements Runnable{
	Socket socket;
	private Scanner input;
	private PrintWriter out;
	String msg = "";
        /*for connection checking*/
        boolean broken = false;
        Timer timer;
        long delay;
        long intervalPeriod;
        TimerTask task; 
	
	//Construction
	public chatServerReturn(Socket s){
		this.socket = s;
                
                this.task = new TimerTask(){
                    @Override
                    public void run(){
                        try{
                            CheckConnection();
                        }catch(Exception e){
                            System.out.println(e);
                        }
                        
                    }
                };
                
                this.timer = new Timer();
                this.delay = 0;
                this.intervalPeriod = 10 * 1000;
	}
	
	public void CheckConnection() throws IOException{
            
            if(isConnected()){
                //System.out.println(chatServer.ConnectionArray.size());
                out.println("#syscheck#");
                out.flush();

                broken = true;
            }else{
            //Make sure socket is closed
                if(!socket.isClosed()){
                    socket.close();
                }
            //if a socket is not connected,
            //we need to remove it from the ArrayList
                for(int i = 0; i < chatServer.ConnectionArray.size(); i++){
                        if(chatServer.ConnectionArray.get(i) == socket){

                                Socket temp = (Socket) chatServer.ConnectionArray.get(i);
                                chatServer.ConnectionArray.remove(i);

                                System.out.println(temp.getLocalAddress().getHostName() + " disconnected!");
                                chatServer.writer.printf("%-23s", new Timestamp(System.currentTimeMillis()));
                                chatServer.writer.println(":svr: Client " + temp.getLocalAddress().getHostName() + " disconnected" );
                                chatServer.writer.flush();
                        }
                }
            }
	}
	
        public boolean isConnected(){
            return socket != null
                && socket.isBound()
                && !socket.isClosed()
                && socket.isConnected()
                && !socket.isInputShutdown()
                && !socket.isOutputShutdown()
                && !broken;
        }
        
        @Override
	public void run(){
		try{
			try{
                        //writer = new PrintWriter(new FileOutputStream("controllerlog.txt",true));
				input = new Scanner(socket.getInputStream());
				out = new PrintWriter(socket.getOutputStream());
                                
                                //start connection check
                                this.timer.scheduleAtFixedRate(task, delay, intervalPeriod);
	
				while(true){
					if(!input.hasNext()){
						return;
					}
					
					msg = input.nextLine();
                                        
                                        /* User Connection Check */
                                        if(msg.equalsIgnoreCase("#cltrsp#")){
                                            broken = false;
                                            //System.out.println("Pong");
                                        }else{
					
                                            System.out.println("Client: " + msg);

                                            //log chat
                                            //chatServer.writer.print(new Timestamp(chatServer.date.getTime()));
                                            chatServer.writer.printf("%-23s", new Timestamp(System.currentTimeMillis()));
                                            chatServer.writer.println(":cht: " + msg);
                                            chatServer.writer.flush();

                                            for(int i = 1; i <= chatServer.ConnectionArray.size(); i++){
                                                    Socket temp = (Socket) chatServer.ConnectionArray.get(i-1);
                                                    PrintWriter tempOut = new PrintWriter(temp.getOutputStream());
                                                    tempOut.println(msg);
                                                    tempOut.flush();
                                                    System.out.println("Sent to: " + temp.getLocalAddress().getHostName());

                                            }//close for loop
                                        }
                                        
				}//close while loop
			}//close inner try
			finally{
                            if(!socket.isClosed()){
				socket.close();
                            }
			}
		}//close outer try
		catch(Exception e){
			System.out.print(e);
		}
	}
}

