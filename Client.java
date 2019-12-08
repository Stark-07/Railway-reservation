import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.io.*;
import java.util.*;
import java.net.*;
public class Client extends JFrame {

	private JLabel trainmsg = new JLabel("");
	private JPanel trainmsgpnl = new JPanel();
	private Socket s = null;
	private int portno = 20000;
	private String msg = "";
	private ObjectInputStream ois = null;
	private ObjectOutputStream oos = null;
	private ServerSocket ss = null;
	private int p=20001;
	public Client()
	{
		super("Railway Client");
		//trainmsgpnl.setLayout(new GridLayout(1,1));
		//trainmsgpnl.add(trainmsg);
		this.getContentPane().add(trainmsg);
		this.addWindowListener(new WindowAdapter()
		{
			public void windowClosing()
			{
				System.exit(0);
			}
		});
		this.setSize(400,200);
		this.setVisible(true);
		
		try
		{
		ss = new ServerSocket(++p);
		}
		catch(IOException ie)
		{
			for(;;)
			{
			p++;
				try
				{
				ss = new ServerSocket(p);
				break;
				}
				catch(IOException ioe)
				{}
			}
		}
		TrainInfo tif = new TrainInfo();
		tif.SetPortno(p);
			try
			{
				s = new Socket("127.0.0.1",portno);
				oos = new ObjectOutputStream(s.getOutputStream());
				oos.writeObject(tif);
				ois = new ObjectInputStream(s.getInputStream());
				try{
				msg = (String)ois.readObject();
				}
				catch(ClassNotFoundException cnfe)
				{
					System.out.println("class not found Wrapper:" + cnfe);
				}
				ReadMsg rmg = new ReadMsg();
				rmg.start();
				Roll rl = new Roll();
				rl.start();
				
			}
			catch(IOException ie)
			{}			
	}
	
	public class ReadMsg extends Thread
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
					msg = (String)ois.readObject().toString();
				}
				catch(IOException ie)
				{}
				catch(ClassNotFoundException cnfe)
				{}
			}
		}
	}
	
	public class Roll extends Thread
	{
		public void run()
		{
			while(true)
			{
				try{
				Thread.sleep(250);
				}
				catch(InterruptedException iet)
				{}
				msg = msg.substring(1) + msg.charAt(0);
				trainmsg.setText(msg);	
			}
			
		}
	}
	
	public static void main(String []args)
	{
		new Client();
	}
}

