import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.io.*;
import java.util.*;
import java.net.*;
import java.sql.*;
public class Server extends JFrame implements ActionListener{
	
	private ObjectInputStream ois = null;
	private ObjectOutputStream oos = null;
	private ServerSocket ss = null;
	
	private String msg="";
	
	private Statement stmt = null;
	private ResultSet rs = null;
	private Connection con = null;
	
	private JLabel trainnolbl = new JLabel("Train No.");
	private JTextField trainnotxt = new JTextField(4);
	private JPanel trainnopnl = new JPanel();
	private JLabel trainnamelbl = new JLabel("Train Name:");
	private JTextField trainnametxt = new JTextField(20);
	private JPanel trainnamepnl = new JPanel();
	private JLabel traintimelbl = new JLabel("Train Time:");
	private JTextField traintimetxt = new JTextField(10);
	private JPanel traintimepnl = new JPanel();
	
	private JButton defaultbtn = new JButton("Default Time");
	private JButton searchbtn = new JButton("Search Train");
	private JButton updatebtn = new JButton("Update");
	private JPanel btnpnl = new JPanel();
	
	private Vector allports = new Vector();
	private Vector allips = new Vector();
	
	
	private JPanel allpnl = new JPanel();
	public Server()
	{
		super("Railway Server");
		trainnopnl.add(trainnolbl);
		trainnopnl.add(trainnotxt);
		trainnamepnl.add(trainnamelbl);
		trainnamepnl.add(trainnametxt);
		traintimepnl.add(traintimelbl);
		traintimepnl.add(traintimetxt);
		btnpnl.add(searchbtn);
		btnpnl.add(updatebtn);
		btnpnl.add(defaultbtn);
	
		allpnl.setLayout(new GridLayout(4,2));
		allpnl.add(trainnopnl);
		allpnl.add(trainnamepnl);
		allpnl.add(traintimepnl);
		allpnl.add(btnpnl);
		this.getContentPane().add(allpnl);
		this.addWindowListener(new WindowAdapter()
				{
					public void windowClosing()
					{
						System.exit(0);
					}
			});
		
		this.setSize(400,400);
		this.setVisible(true);
		updatebtn.addActionListener(this);
		searchbtn.addActionListener(this);
		defaultbtn.addActionListener(this);
		try
		{
		ss = new ServerSocket(20000);
		}
		catch(IOException ie)
		{}
		serveraccept sra = new serveraccept();
		sra.start();
	}
	
	public class serveraccept extends Thread
	{
		private Socket s=null;
		public void run()
		{
			while(true)
			{
				try
				{
					s = ss.accept();
					ois = new ObjectInputStream(s.getInputStream());
					TrainInfo tif = (TrainInfo)ois.readObject();
					allports.addElement(new Integer(tif.getPortno()));
					allips.addElement(s.getInetAddress());
				
					Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
				    con = DriverManager.getConnection("jdbc:odbc:Train","","");
				    stmt = con.createStatement();
				    
				    String qry = "select * from Train";
				    
				    rs = stmt.executeQuery(qry);
				    
				    while(rs.next())
				    {
				    	msg += "***Train No.->" + rs.getString(1) + "  Train Name:->" + rs.getString(2) + "  Train Time:->" + rs.getString(3) + "***" ;
				    }
				    
		
				    	//InetAddress conip = (InetAddress)allips.elementAt(i);
				    	//int pt = (Integer)allports.elementAt(i);
				    	//Socket serve = new Socket(conip,pt);
				    	oos = new ObjectOutputStream(s.getOutputStream());
				    	oos.writeObject(new String(msg));
				    	
				    
				} // end try
				catch(IOException ie)
				{
					System.out.println("IO Exception in ServerAccept: " + ie);
				}
				catch(ClassNotFoundException cnfe)
				{
					System.out.println("ClassNotFound Exception in ServerAccept: " + cnfe);
				}
				catch(SQLException sqle)
				{
					System.out.println("IO Exception in ServerAccept: " + sqle);
				}
			}// end while
		}// end run
	} // end thread
	
	public void actionPerformed(ActionEvent ae)
	{
		if(ae.getSource() == searchbtn)
		{
			String qry = "select * from Train where TrainNumber=" + trainnotxt.getText();
			try
			{
				rs = stmt.executeQuery(qry);
				if (! rs.next())
					JOptionPane.showMessageDialog(null,"No Such Train Exists","Failed",JOptionPane.ERROR_MESSAGE);
				else
				{
					
					trainnotxt.setText(rs.getString(1));
					trainnametxt.setText(rs.getString(2));
					traintimetxt.setText(rs.getString(3));
				}	
			}
			catch(SQLException sqle)
			{
				System.out.println("SQL Exception in ActionEvent:" + sqle);
			}
				
		}
		
		if(ae.getSource() == updatebtn)
		{
			String qry = "update Train set updatetrain='" + traintimetxt.getText() + "' where TrainNumber=" + Integer.parseInt(trainnotxt.getText());
			
			
			try
			{
				 stmt.executeUpdate(qry);
			}
			catch(SQLException sqle)
			{
				System.out.println("SQL exception in update:" + sqle);
			}
			String qrey = "select * from Train";
		    
			try
			{
		    rs = stmt.executeQuery(qrey);
		    
		    msg ="";
		    while(rs.next())
		    {
		    	msg += "***Train No.->" + rs.getString(1) + "  Train Name:->" + rs.getString(2) + "  Train Time:->" + rs.getString(4) + "***" ;
		    }
			}
			catch(SQLException sqle)
			{
				System.out.println("sql exception in forming msg:" + sqle);
			}
		    
			for(int i=0;i<allips.size();i++)
			{
				try
				{
			    	InetAddress conip = (InetAddress)allips.elementAt(i);
			    	int pt = (Integer)allports.elementAt(i);
			    	Socket serve = new Socket(conip,pt);
			    	oos = new ObjectOutputStream(serve.getOutputStream());
			    	oos.writeObject(new String(msg));
				}
				catch(IOException ie)
				{
					System.out.println("IO exception in sending msg to all clients: " + ie);
				}
			    
			}
		}// end if
		
		if(ae.getSource() == defaultbtn)
		{
			 String qry = "select * from Train";
			    try
			    {
			    	rs = stmt.executeQuery(qry);
			    	msg="";
			    	while(rs.next())
			    	{
			    	msg += "***Train No.->" + rs.getString(1) + "  Train Name:->" + rs.getString(2) + "  Train Time:->" + rs.getString(3) + "***" ;
			    	}
			    }
			    catch(SQLException sqle)
				{
					System.out.println("sql exception in forming msg:" + sqle);
				}
			    
			    for(int i=0;i<allips.size();i++)
				{
					try
					{
				    	InetAddress conip = (InetAddress)allips.elementAt(i);
				    	int pt = (Integer)allports.elementAt(i);
				    	Socket serve = new Socket(conip,pt);
				    	oos = new ObjectOutputStream(serve.getOutputStream());
				    	oos.writeObject(new String(msg));
					}
					catch(IOException ie)
					{
						System.out.println("IO exception in sending msg to all clients: " + ie);
					}
				}
			
		}
		
	}// end actionperformed
	
	public static void main(String []args)
	{
		new Server();
	}
}
