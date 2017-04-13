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
//        Thread send = Thread(new SendThread(1, outServer, inUser));
//        Thread recieve = Thread(new ReceiveThread(2, inServer));
        
        SendThread send = new SendThread(1, outServer, inUser);
        ReceiveThread receive = new ReceiveThread(2, inServer);
        
        
//        send.start();
//        receive.start();
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
                        
                        //close socket
                        closeSocket(clientSocket, username);
                        isClosed = true;
                        System.exit(1);
                    }
                    
                }
            }catch(Exception e){
                System.err.println("Exception: " + e.getMessage());
            }
        }
    }
    
    //Thread to Receive Messages
    private static class ReceiveThread implements Runnable{
        private int threadName;
        BufferedReader inServer;
        
        public ReceiveThread(int i, BufferedReader in){
            threadName = i;
            inServer = in;
        }
        
        public void run(){
            try{
                String toRead;
                while((toRead = inServer.readLine()) != null){
                    System.out.println("FROM SERVER >> " + toRead);
                }
            }catch(Exception e){
                System.err.println("EXCEPTION: " + e.getMessage());
                
                try{
                    closeSocket(clientSocket, username);
                }catch(Exception f){
                    System.err.println("EXCEPTION: " + f.getMessage());
                }
                
                System.exit(1);
            }
        }
    }
    
    public static void main(String argv[]) throws Exception{
        try{
            String ipAddr = argv[0];
            int port = Integer.parseInt(argv[1]);
            
            //client socket
            Socket newSocket = new Socket(ipAddr, port);
            clientSocket = newSocket; //edit global
            
            //I/O
            BufferedReader inUser = new BufferedReader(new InputStreamReader(System.in));
            PrintWriter outServer = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()), true);
            BufferedReader inServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            
            String newUsername = greeting(outServer, inServer, inUser);
            username = newUsername; //edit global
            
            if(username != null){
                menu(outServer, inServer, inUser);
                
                //CLOSESOCKET
                closeSocket(clientSocket, username);
            }
        }catch(Exception e){
            System.err.println("Sorry, but your credentials did not match the server! Please try again.");
        }
    }
    
    public static void closeSocket(Socket socket, String user) throws Exception{
        socket.close();
    }
    
    //socket opening greeting
    public static String greeting(PrintWriter outServer, BufferedReader inServer, BufferedReader inUser) throws Exception{
        //USERNAME & PASSWORD
        outServer.println("Client process connected.");
        
        String success = "";
        String user = "";
        int fails = 0;
        
        System.out.println("FROM SERVER >> " + inServer.readLine() + " ");
        
        //if initial prompt for username?
        
        while(success != null && !success.contains("WELCOME!") && fails < 3){
            //USERNAME
            username = inUser.readLine();
            outServer.println(username);
            
            //PW
            System.out.println("FROM SERVER >> " + inServer.readLine() + " ");
            outServer.println(inUser.readLine());
            
            //Authentication
            success = inServer.readLine();
            System.out.println("FROM SERVER >> " + success + " ");
            
            fails++;
        }
        
        //return username if successful, else null
        if(fails < 3){
            return username;
        }else{
            return null;
        }
    }
}
