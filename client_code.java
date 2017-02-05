package client_messenger;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import common.sendInfo;

public class client_code extends JFrame implements Runnable {
	private Socket msgSocket;
	
	private Socket fileSocket;
	private String clientName;
	private int port;
	private JList list;
	private final DefaultListModel model;
	private JButton chat_button;
	
	private static ArrayList<String> loginName = new ArrayList<String>();
	private static ArrayList<chatWindow> objectList = new ArrayList<chatWindow>();
	
	
	//private InputStream in;
	//private OutputStream out;
	private sendInfo ob;
	private ObjectInputStream obin;
	private ObjectOutputStream obout;
	client_code(String clientName,int port){
		super(clientName);
		this.clientName=clientName;
		this.port=port;
		setLayout(new BorderLayout());
		
		JPanel online=new JPanel(new BorderLayout());
		JPanel button=new JPanel(new BorderLayout());
		//button.setBackground(Color.GRAY);
		//online.setBackground(Color.GRAY);
		model=new DefaultListModel();
		//model.setSize(380);
		list=new JList(model);
		//list.setFixedCellHeight(20);
		//list.setFixedCellWidth(20);
		online.add(list,BorderLayout.WEST);
		online.setBorder(BorderFactory.createLineBorder(Color.blue,2));
		
		JLabel chat=new JLabel("Online:");
		//chat.setBackground(Color.BLUE);
		
		chat_button=new JButton("chat->");
		chat_button.addActionListener(new handler());
		button.add(chat_button,BorderLayout.EAST);
		button.add(chat,BorderLayout.WEST);
		
		add(online,BorderLayout.WEST);
		add(button,BorderLayout.NORTH);
		setSize(250, 500);
		//setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}
	private class handler implements ActionListener{
		
		public void actionPerformed(ActionEvent e) {
			System.out.println(list.getSelectedValue());
			chatWindow ob=new chatWindow((String)list.getSelectedValue());
			objectList.add(ob);
			loginName.add((String)list.getSelectedValue());
			try {
				sendInfo ob1=new sendInfo();
				ob1.setFrom(clientName);
				ob1.setTo((String)list.getSelectedValue());
				ob1.setType("window");
				//obout.writeObject("%%%%"+clientName+"%%%%"+(String)list.getSelectedValue());   ////create window for chat
				obout.writeObject(ob1);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}	
	} 
	public void  run() {
		try{
			connection();
			stream();
			task();
		}catch(Exception e){
			
		}
		
		
	}
	public void connection() throws UnknownHostException, IOException{
		msgSocket=new Socket("localhost",port);
		System.out.printf("client connected to file server %S\n",msgSocket.getInetAddress().getHostName());
		fileSocket=new Socket("localhost",port);
		System.out.printf("client connected to %S\n",msgSocket.getInetAddress().getHostName());
		//System.out.printf("client connected to fileserver %S\n",msgSocket.getInetAddress().getHostName());
	}
	public void stream() throws IOException{
		
		//out =socket.getOutputStream();
		//out.flush();
		
		obout=new ObjectOutputStream(msgSocket.getOutputStream());
		obout.flush();
		//obout.writeObject(obj);
		obin=new ObjectInputStream(msgSocket.getInputStream());
		ob=new sendInfo();
		ob.setType("name");
		ob.setMsg(clientName);
		System.out.printf("%s %s\n",ob.getMsg() ,ob.getType());
		obout.writeObject(ob);
		System.out.println("client1 got stream");
	}
	public void task() throws ClassNotFoundException, IOException{
		while(true){
			
			sendInfo ob=(sendInfo)obin.readObject();
			if(ob.getType().equals("name") && (!ob.getMsg().equals(clientName))){           ////on line name
				System.out.println("add "+ob.getMsg());
				model.addElement(ob.getMsg());
			}
			else if(ob.getType().equals("window")){      //// create chat window for client
				chatWindow wd=new chatWindow(ob.getFrom());
				objectList.add(wd);
				loginName.add(ob.getFrom());
			}
			
			else if(ob.getType().equals("messege")){
				String client=ob.getFrom();
				for(int i=0;i<loginName.size();i++){
					if(loginName.get(i).equals(client)){
						objectList.get(i).write_msg(ob.getMsg(), client);
						break;
					}
				}
			}
			//else if(ob.get)
			
			
			/*String msg=(String) obin.readObject();
			
			if(msg.matches("#####[A-Za-z]*")){
				String name[]=msg.split("#####");
				if(!name[1].equals(client_name)){
					model.addElement(name[1]);
				}
				
			}
			else if(msg.matches("%%%%[A-Za-z]*%%%%[A-Za-z]*")){
				String name[]=msg.split("%%%%");
				chat_window ob=new chat_window(name[1]);
				object_list.add(ob);
				login_name.add(name[1]);
			}
			else if(msg.matches("##%%[A-Za-z]*##%%[A-Za-z]*##%%[A-Za-z]*")){
				String name[]=msg.split("##%%");
				for(int i=0;i<login_name.size();i++){
					if(name[1].equals(login_name.get(i))){
						object_list.get(i).write_msg(name[3],name[1]);
					}
				}
			} */
			
			
			
		}
	}
	
	public class chatWindow extends JFrame {
		
		private String chat_with;
		private JTextField field;
		private JTextArea area;
		private JScrollPane pane;
		
		private JButton add;
		private JButton save;
		
		chatWindow(String chat_with){
			super("chat with "+chat_with);
			this.chat_with=chat_with;
			setLayout(new BorderLayout());
			
			add=new JButton("add file");
			save=new JButton("save as..");
			
			field=new JTextField(30);
			field.addActionListener(new handler());
			
			add.addActionListener(new handler());
			save.addActionListener(new handler());
			
			
			
			JPanel input_msg=new JPanel(new BorderLayout());
			JPanel show_msg=new JPanel(new BorderLayout());
			JPanel butto=new JPanel(new BorderLayout());
			area=new JTextArea();
			
			area.setForeground(Color.blue);
			area.setEditable(false);
			pane=new JScrollPane(area);
			
			input_msg.add(field);
			show_msg.add(pane);
			//show_msg.setBorder(BorderFactory.createLineBorder(Color.GREEN, 2));
			//input_msg.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
			
			butto.add(add,BorderLayout.WEST);
			butto.add(save,BorderLayout.EAST);
			
			//pane.add(area);
			//pane.createHorizontalScrollBar();
			add(show_msg,BorderLayout.CENTER);
			add(input_msg,BorderLayout.SOUTH);
			add(butto,BorderLayout.NORTH);
			//add(add,BorderLayout)
			setSize(300, 400);
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			setVisible(true);
			setLocation(300,120);
		}
		
		public class handler implements ActionListener{

			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				
				if(e.getSource()==field){
					String msg="";
					msg=e.getActionCommand();
					if(!msg.equals("")){
						area.append(clientName.toUpperCase()+ ">>\n "+"     "+ msg+"\n");
						try {
							send_data( msg );
							
						} catch (IOException e1) {
							e1.printStackTrace();
						}
						field.setText("");
					}
				}
				else if(e.getSource()==add){
					
					
					JFileChooser fc=new JFileChooser();
					int ret=fc.showOpenDialog(fc);
					File file=fc.getSelectedFile();
					/*try {
						obout.writeObject("@@"+clientName+"@@"+chat_with);
						obout.writeObject(file.getName());   ///send file name
						//out.write((int) file.length());    /// send file size
						//obout.writeObject(String.valueOf(file.length()+1));
						System.out.println(file.length());
						System.out.println("check");
						InputStream fin=new FileInputStream(file);
						BufferedInputStream brin=new BufferedInputStream(fin);
						byte b[]=new byte[2048];
						int read;
						while((read=brin.read(b, 0, 2048))!=-1){
							
							//out.write(b, 0, read);
							System.out.printf("read from file : %d\n",read);
							//out.flush();
						}
						//brin.close();
						fin.close();
						//brin.read(b);
						//out.write(b);
						
						
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} */
				}
				else if(e.getSource()==save){
					System.out.println("adsfads");
					JFileChooser fc=new JFileChooser();
					int ret=fc.showOpenDialog(fc);
				}
				
			}
			
		}
		
		public void send_data(String msg) throws IOException{
			try{
				System.out.println("send msg1");
				sendInfo ob=new sendInfo();
				ob.setType("messege");
				ob.setTo(chat_with);
				ob.setFrom(clientName);
				ob.setMsg(msg);
				obout.writeObject(ob);
				//obout.writeObject("##%%"+clientName+"##%%"+chat_with+"##%%"+msg);
				System.out.println("send msg2");
			}catch(Exception e){
				
			}
		}
		
		void write_msg(String msg,String who){
			msg=who.toUpperCase()+" >>\n "+msg+"\n";
			area.append(msg);
		}

	}
	
	public class sendFile extends Thread{
		
		private File file;
		sendFile(File f){
			this.file=f;
		}
		public void run(){
			
		}
	}
}
