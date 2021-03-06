package pkg3401project;

/**
 *
 * @author Jocelyn
 */
import java.awt.event.WindowEvent;
import java.io.FileOutputStream;
import javax.swing.*;
import java.io.PrintWriter;
import java.net.*;
import java.sql.Timestamp;
import java.util.Scanner;

public class chatClientGUI extends JFrame {
    //Globals

    private static ChatClient ChatClient;
    public static String UserName = "Anonymous";

    public static String UserList = "";
    //for log
    public static PrintWriter writer;//log
    public static final int TIMEOUT = 10000;

    public static JFrame MainWindow = new JFrame();
    private static JButton B_Connect = new JButton("Connect");
    private static JButton B_Disconnect = new JButton("Bye");
    private static JButton B_Send = new JButton("Send");
    private static JLabel L_Message = new JLabel("Message: ");
    public static JTextField TF_Message = new JTextField(20);
    private static JLabel L_Conversation = new JLabel("Conversation");
    public static JTextArea TA_Conversation = new JTextArea();
    private static JScrollPane SP_Conversation = new JScrollPane();
    private static JLabel L_Online = new JLabel("Online");
    public static JList JL_Online = new JList();
    private static JScrollPane SP_Online = new JScrollPane();
    private static JLabel L_LoggedInAs = new JLabel("Logged in as");
    private static JLabel L_LoggedInAsBox = new JLabel();

    //login window    
    public static JFrame LogInWindow = new JFrame();
    public static JTextField TF_UserNameBox = new JTextField(20);
    private static JButton B_Enter = new JButton("Enter");
    private static JLabel L_EnterUserName = new JLabel("Enter username: ");
    private static JPanel P_Login = new JPanel();

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        System.out.print("Type in a 1 to chat from GUI or 2 to enter command line chat: ");
        String choice = input.next();

        //GUI Option
        if (choice.equals("1")) {
            BuildMainWindow();
            Initialize();
        } //IDE option
        else if (choice.equals("2")) {
            //using command line to chat

            System.out.print("Please set username now: ");
            UserName = input.next();
            input.nextLine();

            Connect();
            chatLoop();

        } else {
            System.out.println("You can't chat with us :p");
        }

    }

    public static void chatLoop() {
        //need to figure out how to know when to recieve a message from the server
        Scanner input = new Scanner(System.in);
        System.out.println("type \"help\" for commands");
        while (true) {
            try {
                String s = input.next();
                if (s.equalsIgnoreCase("bye")) {
                    System.out.println("Logging out...");
                    ChatClient.Disconnect("Bye");
                    break;
                } else if (s.equalsIgnoreCase("help")) {
                    help();
                } else if (s.equalsIgnoreCase("send")) {
                    ChatClient.Send(input.nextLine().substring(1));
                } else if (s.equalsIgnoreCase("o_list")) {
                    System.out.println(UserList);
                } else {
                    System.out.println("unrecognized command: " + s);
                    if (input.hasNext()) {
                        input.nextLine();
                    }
                }
                try {
                    ChatClient.Recieve();

                } catch (IndexOutOfBoundsException e) {

                }
            } catch (Exception e) {
                System.exit(0);
            }
        }
        System.out.println("bye");
    }

    private static void help() {
        System.out.println("send \t\t sends message to server");
        System.out.println("o_list \t\t displays who is online");
        System.out.println("help \t\t displays this message");
        System.out.println("bye \t\t leave chat");
    }

    public static void Connect() {
        try {
            final int port = 8080;
            final String host = "127.0.0.1";
            Socket socket = new Socket(host, port);

            System.out.println("You connected to :" + host);
            ChatClient = new ChatClient(socket);

            //send name to add to "Online" list
            //System.out.println("UserName:" + UserName);
            PrintWriter out = new PrintWriter(socket.getOutputStream());
            out.println(UserName);
            out.flush();

            //start of log file
            writer = new PrintWriter(new FileOutputStream("./logs/" + UserName + "_log.txt", true));
            writer.printf("%-23s", new Timestamp(System.currentTimeMillis()));
            //writer.printf("%1$23s", new Timestamp(date.getTime()));
            writer.println(":sys: Connected to chat");
            writer.flush();

            Thread thread = new Thread(ChatClient);
            thread.start();
        }//end try 
        catch (Exception e) {
            System.out.println(e);
            JOptionPane.showMessageDialog(null, "Server not responding.");
            System.exit(0);
        }

    }//end Connect

    public static void Initialize() {
        B_Send.setEnabled(false);
        B_Disconnect.setEnabled(false);
        B_Connect.setEnabled(true);
    }

    public static void BuildLoginWindow() {
        LogInWindow.setTitle("Whats your name?");
        LogInWindow.setSize(400, 100);
        LogInWindow.setLocation(250, 200);
        LogInWindow.setResizable(false);
        P_Login = new JPanel();
        P_Login.add(L_EnterUserName);
        P_Login.add(TF_UserNameBox);
        P_Login.add(B_Enter);
        LogInWindow.add(P_Login);

        Login_Action();
        LogInWindow.setVisible(true);
    }//end BuildLoginWindow

    public static void BuildMainWindow() {
        MainWindow.setTitle(UserName + "'s chat box");
        MainWindow.setSize(450, 500);
        MainWindow.setLocation(220, 180);
        MainWindow.setResizable(false);
        ConfigureMainWindow();
        MainWindow_Action();
        MainWindow.setVisible(true);

    }//end BuildMainWindow

    public static void ConfigureMainWindow() {
        MainWindow.setBackground(new java.awt.Color(255, 255, 255));
        MainWindow.setSize(500, 320);
        MainWindow.getContentPane().setLayout(null);

        B_Send.setBackground(new java.awt.Color(0, 0, 255));
        B_Send.setForeground(new java.awt.Color(255, 255, 255));
        B_Send.setOpaque(true);
        B_Send.setBorderPainted(false);
        MainWindow.getContentPane().add(B_Send);
        B_Send.setBounds(250, 40, 81, 25);

        B_Disconnect.setBackground(new java.awt.Color(0, 0, 255));
        B_Disconnect.setForeground(new java.awt.Color(255, 255, 255));
        B_Disconnect.setOpaque(true);
        B_Disconnect.setBorderPainted(false);
        MainWindow.getContentPane().add(B_Disconnect);
        B_Disconnect.setBounds(10, 40, 110, 25);

        B_Connect.setBackground(new java.awt.Color(0, 0, 255));
        B_Connect.setForeground(new java.awt.Color(255, 255, 255));
        B_Connect.setOpaque(true);
        B_Connect.setBorderPainted(false);
        B_Connect.setToolTipText("Connect to server");
        MainWindow.getContentPane().add(B_Connect);
        B_Connect.setBounds(130, 40, 110, 25);

        
        MainWindow.getContentPane().add(L_Message);
        L_Message.setBounds(10, 10, 60, 20);

        TF_Message.setForeground(new java.awt.Color(0, 0, 255));
        TF_Message.requestFocus();
        TF_Message.setEditable(false);
        MainWindow.getContentPane().add(TF_Message);
        TF_Message.setBounds(70, 4, 260, 30);

        L_Conversation.setHorizontalAlignment(SwingConstants.CENTER);
        MainWindow.getContentPane().add(L_Conversation);
        L_Conversation.setBounds(100, 70, 140, 16);

        TA_Conversation.setColumns(20);
        TA_Conversation.setFont(new java.awt.Font("Tahoma", 0, 12));
        TA_Conversation.setForeground(new java.awt.Color(0, 0, 255));
        TA_Conversation.setLineWrap(true);
        TA_Conversation.setRows(5);
        TA_Conversation.setEditable(false);

        SP_Conversation.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        SP_Conversation.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        SP_Conversation.setViewportView(TA_Conversation);
        MainWindow.getContentPane().add(SP_Conversation);
        SP_Conversation.setBounds(10, 90, 330, 180);

        L_Online.setHorizontalAlignment(SwingConstants.CENTER);
        MainWindow.getContentPane().add(L_Online);
        L_Online.setBounds(350, 70, 130, 16);

        String[] TestNames = {"Please Connect"};
        JL_Online.setForeground(new java.awt.Color(0, 0, 255));
        JL_Online.setListData(TestNames);

        SP_Online.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        SP_Online.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        SP_Online.setViewportView(JL_Online);
        MainWindow.getContentPane().add(SP_Online);
        SP_Online.setBounds(350, 90, 130, 180);

        L_LoggedInAs.setFont(new java.awt.Font("Tahoma", 0, 12));
        MainWindow.getContentPane().add(L_LoggedInAs);
        L_LoggedInAs.setBounds(348, 0, 140, 15);

        L_LoggedInAsBox.setHorizontalAlignment(SwingConstants.CENTER);
        L_LoggedInAsBox.setFont(new java.awt.Font("Tahoma", 0, 12));
        L_LoggedInAsBox.setForeground(new java.awt.Color(255, 0, 0));
        L_LoggedInAsBox.setBorder(BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        MainWindow.getContentPane().add(L_LoggedInAsBox);
        L_LoggedInAsBox.setBounds(340, 17, 150, 20);
    }//end

    public static void Login_Action() {
        B_Enter.addActionListener(
                new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Action_B_Enter();
            }
        }
        );
    }//end Login_Action

    public static void Action_B_Enter() {
        if (!TF_UserNameBox.getText().equals("")) {
            UserName = TF_UserNameBox.getText().trim();
            L_LoggedInAsBox.setText(UserName);
            MainWindow.setTitle(UserName + "'s chat box");
            LogInWindow.setVisible(false);
            B_Send.setEnabled(true);
            TF_Message.setEditable(true);
            B_Disconnect.setEnabled(true);
            B_Connect.setEnabled(false);
            Connect();
        } else {
            JOptionPane.showMessageDialog(null, "Please enter a name.");
        }
    }//end Action_B_Enter

    public static void MainWindow_Action() {
        B_Send.addActionListener(
                new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Action_B_Send();
            }
        }
        );

        B_Disconnect.addActionListener(
                new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Action_B_Disconnect();
            }
        }
        );
        B_Connect.addActionListener(
                new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BuildLoginWindow();
            }
        }
        );

        //When X on frame is pressed, program will disconnect client & stop running
        MainWindow.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                if (JOptionPane.showConfirmDialog(MainWindow,
                        "Are you sure you want to leave the chat?", "Exiting Chat Room",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
                    Action_B_Disconnect();
                }
            }
        });

    }//end

    public static void Action_B_Send() {
        //would be nice to add a key listener here
        //for rapid chatting
        if (!TF_Message.getText().equals("")) {
            ChatClient.Send(TF_Message.getText());
            TF_Message.requestFocus();
        }
    }

    public static void Action_B_Disconnect() {
        try {
            ChatClient.Disconnect("gui");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
