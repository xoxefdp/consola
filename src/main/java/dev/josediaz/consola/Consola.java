package dev.josediaz.consola;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.UIManager;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

/**
 *
 * @author josediaz
 */
public class Consola {
    
    public JFrame frame;
    public JTextPane console;
    public JTextField input;
    public JScrollPane scrollpane;
    public StyledDocument document;
    public boolean trace = false;
    
    public ArrayList<String> recent_used = new ArrayList<String>();
    public int recent_used_id = 0;
    public int recent_used_maximum = 2;
    
    public boolean loop = false;
    public int loop_times = 1;
    public int loop_times_temp = 1;
    
    public Consola() {
        
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch(Exception ex){
            ex.printStackTrace();
        }
        frame = new JFrame();
        frame.setTitle("Console");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        console = new JTextPane();
        console.setEditable(false);
        console.setFont(new Font("Courier New", Font.PLAIN, 12));
        console.setOpaque(false);
        
        document = console.getStyledDocument();
        
        input = new JTextField();
        //input.setBorder(null);
        input.setEditable(true);
        input.setFont(new Font("Courier New", Font.PLAIN, 12));
        input.setForeground(Color.WHITE);
        input.setCaretColor(Color.GREEN);
        input.setOpaque(false);
        
        input.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent event) {
		String text = input.getText();
                if(text.length() > 1){
                    recent_used.add(text);
                    recent_used_id = 0;

                    output(text);

                    scrollBottom();
                    input.selectAll();
                }
            }
        });

        input.addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent ke) {
                if (ke.getKeyCode() == KeyEvent.VK_UP) {
                    if (recent_used_id < (recent_used_maximum -1) && recent_used_id < (recent_used.size() -1) ) {
                        recent_used_id++;
                    }
                    input.setText(recent_used.get(recent_used.size() -1 - recent_used_id));
                } else if(ke.getKeyCode() == KeyEvent.VK_DOWN){
                    if (recent_used_id > 0 ) {
                        recent_used_id--;
                    }
                    input.setText(recent_used.get(recent_used.size() -1 - recent_used_id));
                }
            }
        });
        
        scrollpane = new JScrollPane(console);
        scrollpane.setBorder(null);
        scrollpane.setOpaque(false);
        scrollpane.getViewport().setOpaque(false);        
        
        frame.getContentPane().setBackground(new Color(50,50,50));
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] gs = ge.getScreenDevices();
        frame.setSize(gs[0].getDisplayMode().getWidth()*1/2, gs[0].getDisplayMode().getHeight()*1/2);
        
        frame.add(input, BorderLayout.SOUTH);
        frame.add(scrollpane, BorderLayout.CENTER);
        
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setVisible(true);
    }
    
    public void output(String s){
        final String[] commands = s.split(" ");
        
        for (int i = 0; i < loop_times; i++) {
        
            try{
                if (commands[0].equalsIgnoreCase("clear")) {
                    clear();
                } else if(commands[0].equalsIgnoreCase("popup")) {
                    String message = "";
                    for (int j = 1; j < commands.length; j++) {
                        message += commands[j];
                        if (j != commands.length -1) {
                            message += " ";
                        }
                    }
                    JOptionPane.showMessageDialog(null, message, "Message", JOptionPane.INFORMATION_MESSAGE);

                } else if(commands[0].equalsIgnoreCase("loop")) {
                    loop_times_temp = Integer.parseInt(commands[1]);
                    loop = true;
                    println("The next statement will loop " + loop_times_temp + " times!", trace, new Color(255,255,255));

                /*
                } else if(commands[0].equalsIgnoreCase("start")) {
                    String url = "";
                    for (int j = 1; j < commands.length; j++) {
                        url += commands[j];
                        if (j != commands.length -j) {
                            url += " ";
                        }
                    }
                    if( Desktop.isDesktopSupported() ){
                        Desktop desktop = Desktop.getDesktop();
                        try {
                            desktop.browse(new URI(url));
                        } catch (IOException e) {
                            println("Error IOException on Desktop supported "+Desktop.isDesktopSupported()+" -> " + e.getMessage(), trace, new Color(255,155,155));
                        } catch (URISyntaxException e) {
                            println("Error URISyntaxException -> " + e.getMessage(), trace, new Color(255,155,155));
                        }
                    } else {
                        Runtime runtime = Runtime.getRuntime();
                        try {
                            runtime.exec("xdg-open " + url);
                        } catch (IOException e) {
                            println("Error IOException -> " + e.getMessage(), trace, new Color(255,155,155));
                        }
                    }
                */
                } else if( commands[0].equalsIgnoreCase("trace") ) {
                    if( commands[1].equalsIgnoreCase("true") ) {
                        trace = true;
                        println("Tracing Enabled!", trace, new Color(155,155,255));
                    } else if( commands[1].equalsIgnoreCase("false") ){
                        trace = false;
                        println("Tracing Disabled!", trace, new Color(155,155,255));
                    }

                } else if(commands[0].equalsIgnoreCase("get")) {
                    if(commands[1].equalsIgnoreCase("ip")) {
                        new Thread(new Runnable(){
                           @Override
                           public void run() {
                               try {
                                   println("Localhost IP: "+InetAddress.getLocalHost().getHostAddress()+" *** Loopback IP: "+InetAddress.getLoopbackAddress().getHostAddress(), trace, new Color(155,155,255));
                               } catch (Exception ex) {
                                   println("Error -> " + ex.getMessage(), trace, new Color(255,155,155));
                               }
                               try {
                                   String url = "http://checkip.amazonaws.com";
                                   println("Connecting to Proxy", trace, new Color(155,255,155));
                                   try { Thread.sleep(10); } catch(Exception ex) {}
                                   BufferedReader br = new BufferedReader(
                                           new InputStreamReader(
                                                   new URL(url).openStream()));
                                   println("Retrieving Global IP Address", trace, new Color(155,255,155));
                                   String receive = br.readLine();
                                   println("Global IP: " + receive, trace, new Color(155,155,255));
                               } catch (Exception ex) {
                                   println("Error -> " + ex.getMessage(), trace, new Color(255,155,155));
                               }
                           }
                       }).start();   
                    }
                } else if(commands[0].equalsIgnoreCase("exit")) {
                    System.exit(0);
                } else {
                    println(s, trace, new Color(255,255,255));
                }
            } catch(Exception ex){
                println("Error -> " + ex.getMessage(), trace, new Color(255,155,155));
            }
        }
        
        if (loop) {
            loop_times = loop_times_temp;
            loop_times_temp = 1;
        } else {
            loop_times = 1;
            loop_times_temp = 1;
        }
    }
    
    public void scrollTop(){
        console.setCaretPosition(0);
    }
    
    public void scrollBottom(){
        console.setCaretPosition(console.getDocument().getLength());
    }
    
    public void print(String s, boolean trace){
        print(s, trace, new Color(255,255,255));
    }
    
    public void print(String s, boolean trace, Color c){
        
        Style style = console.addStyle("Style", null);
        StyleConstants.setForeground(style,c);
        
        if (trace) {
            Throwable t = new Throwable();
            StackTraceElement[] elements = t.getStackTrace();
            String caller = elements[0].getClassName();
            
            s = caller + " -> " + s;
        }
        
        try {
            document.insertString(document.getLength(), s, style);
        } catch(Exception e){
            //
        }
    }
    
    public void clear(){
        try{
            document.remove(0, document.getLength());
        } catch(Exception e){}
    }
    
    public void println(String s, boolean trace){
        println(s, trace, new Color(255,255,255));
    }
    
    public void println(String s, boolean trace, Color c){
        print(s + "\n", trace, c);
    }
}