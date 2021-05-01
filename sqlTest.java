import javax.swing.*;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
//import java.lang.*;
import java.awt.event.*;
import java.sql.*;
import java.io.File;
import java.io.IOException;
import java.io.FileWriter;
//import java.util.Properties;
//End of Imports

public class sqlTest
{
    final static String version= "0.1.6"; //version no.
    //final static String user=System.getProperty("user.name").toLowerCase(); //seems to cause problems
    
    public static void login() //creates login window and takes user name and pw, passes through JDBC
    {
        try {

            JFrame login=new JFrame("Login to DB");
            final JTextField tf=new JTextField();   //Username field
            final JTextField host=new JTextField();   //Username field
            final JTextField port=new JTextField();   //Username field
            final JPasswordField tf1=new JPasswordField();   //password field 
            final JButton b=new JButton("Login");//creating instance of JButton  
		    final JButton reset=new JButton("Reset");
            final ImageIcon ic = new ImageIcon("sql2.png");
            
            login.setSize(400,500);//400 width and 500 height  
		    login.setLayout(null);//using no layout managers  
		    login.setVisible(true);//making the frame visible  
            // login.pack(); minises tab 
            tf.setBounds(150,180, 150,20); 
		    tf1.setBounds(150,210, 150,20);  
            host.setBounds(150,150,90,20);
            port.setBounds(250,150,50,20);
		    b.setBounds(75,250,100,40);//x axis, y axis, width, height  
		    reset.setBounds(200,250,100, 40);//x axis, y axis, width, height  

            port.setEnabled(false); host.setEnabled(false); //disables these fields for now

            //port.setText("3306"); //causes rendering issues??
            //tf.setText(user); //takes account name and makes it lower case | Seems to have an issue when generating

            JLabel t1,t2,t3,icon,v,t4;
            t1=new JLabel();
            t2=new JLabel();
            t3=new JLabel();    
            icon=new JLabel(ic);  
            v = new JLabel();   
            t4= new JLabel();
            t1.setText("Username: ");
            t2.setText("Password: ");
            t3.setText("");
            v.setText("Version : "+version);
            t4.setText("Host + Port:");
           

            t1.setBounds(50,180,150,20);
            t2.setBounds(50,210,150,20);
            t3.setBounds(110,225,150,20);
            icon.setBounds(110,0,150,150);
            v.setBounds(140,400,150,20);
            t4.setBounds(50,150,150,20);


            login.add(b);login.add(reset);login.add(tf);login.add(tf1);login.add(t1);login.add(t2);login.add(t3);login.add(icon);login.add(v);
            login.add(host);login.add(port);login.add(t4);
		    login.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);   


            b.addActionListener(new ActionListener(){ // waits for button click takes U&PW passes it into Connection
                public void actionPerformed(ActionEvent e)
                {  
                    String h = ("jdbc:mysql://"+host.getText()+":"+port.getText()+"/schema");
                    String u = tf.getText();
					String p = tf1.getText();
					b.setText("Checking...");  
                   
                   // System.out.println(h);

                    if(u.isEmpty() || p.isEmpty())
                    {
                        JOptionPane.showMessageDialog(new JFrame(),"Please enter a Username & Password","Error",JOptionPane.ERROR_MESSAGE);
                        b.setText("Login");
                    }
                   /* else if(host.getText().isEmpty()|| port.getText().isEmpty()){
                        JOptionPane.showMessageDialog(new JFrame(),"Please enter a Hostname & Port","Error",JOptionPane.ERROR_MESSAGE);
                        b.setText("Login");
                    } */ //commented out for now, not yet needed 

                    else{
                    
                        try 
                        {
                            Class.forName("com.mysql.cj.jdbc.Driver");  
                            Connection con=DriverManager.getConnection(h,u,p);  
                            t3.setText("Success");
                            b.setText("Login");

                            query(con);
                            login.dispose(); 
                        } 
                        
                        catch (Exception l) 
                        {
                            b.setText("Login");
                            JOptionPane.showMessageDialog(new JFrame(),l,"Error",JOptionPane.ERROR_MESSAGE);
                            fillFile(l.toString(),"error");
                        }


                } // end of else
            }

            });

            reset.addActionListener( new ActionListener() // waits for reset button click clears text
            {  
                public void actionPerformed(ActionEvent r)
                {  
                    tf.setText("");
                    tf1.setText("");
                }
            }); 
        }
        
        catch (Exception r) 
        {
            String error="Error!";
			JOptionPane.showMessageDialog(new JFrame(),error,"OOPSIE WOOPSIE",JOptionPane.ERROR_MESSAGE);
        }

    }

    public static void query(Connection con)
    { // takes a connection of Type Connection and creates a new window where a query can be entered

        JFrame query=new JFrame("Welcome!");
        final JTextField tf=new JTextField();  
        final JButton b=new JButton("Run Query");//creating instance of JButton  
        final JButton r1=new JButton("Reset");
        final JButton close=new JButton("Log Out");
        tf.setBounds(150,150,250,50);
        b.setBounds(100,250,100,40);//x axis, y axis, width, height  
        r1.setBounds(225,250,100,40);//x axis, y axis, width, height  
        close.setBounds(350,250,100,40);//x axis, y axis, width, height  

        
        JLabel t1;JLabel res,v;
        t1=new JLabel();
        res= new JLabel();
        v=new JLabel();

        t1.setText("Query: ");
        tf.setText("Select * from person;");
        res.setText("");
        v.setText("Version : "+version);

        t1.setBounds(75,165,50,20);
        res.setBounds(225,300,300,100);
        v.setBounds(225,400,150,20);


        query.add(b);query.add(r1);query.add(tf);query.add(t1);query.add(close);query.add(v);
        query.add(res);
        query.setSize(600,500);//600 width and 500 height  
        query.setLayout(null);//using no layout managers  
        query.setVisible(true);//making the frame visible  
        query.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
         
        if(getTables(con).equals(null)){ //get table names.. Will be used down the line.
            JOptionPane.showMessageDialog(new JFrame(),"GetTables was null","FUCK",JOptionPane.INFORMATION_MESSAGE);
        }
        else{
            try
            {
                String tables="";
                ResultSet rs=getTables(con);
                while(rs.next())  
                    tables=(tables+rs.getString(1));
                fillFile(tables,"Tables");
            }
            catch(SQLException e){

            }
        }

        b.addActionListener( new ActionListener() // waits for button press. creates statement, runs query and prints result.
        {  
            public void actionPerformed(ActionEvent q)
            {  
                try
                {
                    String u=tf.getText();
                    String type[]=u.split(" ",2); 
                    //gets first string "Select","Insert","Update" as these all need different types. I can code this in, by spliting the string, but I'll see how I feel
                    //System.out.println(type[0]);  //prints 'select' 

                    Statement stmt=con.createStatement();  
                    ResultSet rs=stmt.executeQuery(u); 
                    res.setText("Data Exported");
                                            
                    String res1="Result: ";

                    while(rs.next())  
                           res1=(res1+"\nID: "+rs.getInt(1)+" Name: "+rs.getString(2)+" Age: "+rs.getString(3)+"\n");

                    fillFile(res1,"results"); 
                    //passes res string into fillFile
                    //java.util.concurrent.TimeUnit.SECONDS.sleep(15);
                    //res.setText("");          
                }

                catch(Exception s)
                {
                    JOptionPane.showMessageDialog(new JFrame(),s,"Error",JOptionPane.ERROR_MESSAGE);
                    s.printStackTrace();
                    res.setText("");
                }
            }
        }); 


        r1.addActionListener( new ActionListener()
        {  
            public void actionPerformed(ActionEvent r)
            {  
                tf.setText("");
                res.setText("");
            }
        }); 

        close.addActionListener( new ActionListener()
        {  
            public void actionPerformed(ActionEvent c)
            {  
                try {
                    con.close();
                    query.dispose();
                    JOptionPane.showMessageDialog(new JFrame(),"Logged Out Successfully","Goodbye",JOptionPane.INFORMATION_MESSAGE);
                    login();
                } 
                catch (Exception y) {
                }

            }
        }); 
    }

    public static String fillFile(String res,String name)//creates file and adds res text string. writes it to the file. takes name and appends to file name
    { 
        try{

            long Date=(System.currentTimeMillis());  //gets epoch time (prevents duplicate file names)
            File in = new File(name+"_"+Date+".txt"); // creates file in format of YYYY-MM-DD
            
            if(in.createNewFile())
            {
                System.out.println("File Created "+in.getName());
                FileWriter writer = new FileWriter(in.getName());

                try {
                    writer.write(res);
                    writer.close(); // closes file
                    return in.getName();
                } 
                catch (IOException e) {
                    JOptionPane.showMessageDialog(new JFrame(),e,"Error",JOptionPane.ERROR_MESSAGE); 
                    e.printStackTrace();
                }
            }
            else
            {
                System.out.println("Oops, File exsits.");
            } 
        }
        catch(IOException f){
            JOptionPane.showMessageDialog(new JFrame(),f,"Error",JOptionPane.ERROR_MESSAGE);
            f.printStackTrace();
        }
        return null;
    }

    public static void queryTest(){
        try{
            //Class.forName("com.mysql.cj.jdbc.Driver");   //class not found???
            Connection con=DriverManager.getConnection("jdbc:mysql://localhost:3306/schema","noel","noel");  
            query(con);
        }
        catch(Exception t){
            JOptionPane.showMessageDialog(new JFrame(),t,"Error",JOptionPane.ERROR_MESSAGE);
        }
    }

    public static ResultSet getTables(Connection con){ //gets name of tables, will be used down the line
        try{
            Statement stmt=con.createStatement();  
            ResultSet rs=stmt.executeQuery("Show Tables;"); 
            return rs;
        }
        catch(SQLException s){
            fillFile(s.toString(),"error");
            return null;
        }
        
    }

    public static void main(String args[])
    {
        try 
        {
            //queryTest();
            login();
            System.getProperties().list(System.out);
                    
            //System.getProperty("user.name");
        } 

        catch (Exception e)
        {
            String error="Error!";
            JOptionPane.showMessageDialog(new JFrame(),error,"OOPSIE WOOPSIE",JOptionPane.ERROR_MESSAGE);
        }
    }
}

