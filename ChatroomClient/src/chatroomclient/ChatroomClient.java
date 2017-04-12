/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatroomclient;

/**
 *
 * @author ReadUnpingco
 */

import java.io.*;
import java.net.*;
import java.util.*; 

public class ChatroomClient {

    /**
     * @param args the command line arguments
     */
    private static Socket clientSocket;
    private static String username;
    public static boolean isClosed = false;
    
    //Time to be implemented later
    public static final int TIME_OUT = 1000 * 60 * 30;
    
    public static void menu(PrintWriter outServer, BufferedReader inServer, BufferedReader inUser) throws Exception{
        Thread send = Thread(new SendThread(1, outServer, inUser));
        Thread recieve = Thread(new RecieveThread(2, inServer));
        
        
        send.start();
        recieve.start();
    }
    
    //Thread To Send Messages
    private static class SendThread implements Runnable{
        private int threadName;
        PrintWriter outServer;
        BufferedReader inUser;
        
        public SendThread(int i, PrintWriter out, BufferedReader in){
            threadName = i;
            outServer = out;
            inUser = in;
        }
        
        public void run(){
            try{
                long currentTime = System.currentTimeMillis();
                clientSocket.setSoTimeout(TIME_OUT);
                while(true){
                    
                    String toSend = inUser.readLine();
                    outServer.println(toSend);
                    
                    if(toSend.equals("Bye")){
                        System.out.println("LOGGING OUT CLIENT SIDE");
                    }
                    
                }
            }
        }
    }
}
