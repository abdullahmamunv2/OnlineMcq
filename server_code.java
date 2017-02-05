package server_messenger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import common.sendInfo;

public class server_code extends Thread {
	private ServerSocket server;
	private int port;
	
	public static ArrayList<String> loginName = new ArrayList<String>();
	public static ArrayList<task> objectList = new ArrayList<task>();
	public static ArrayList<sendFile>filePacket=new ArrayList<sendFile>();
	public static ArrayList<String >fileSendTo=new ArrayList<>();
	
	server_code(int port){
		this.port=port;
		start();
	}
	public void run(){
		try{
			Socket socket;
			server=new ServerSocket(port);
			while(true){
				try{
					socket=server.accept();
					System.out.printf("server connected to client %s\n",socket.getInetAddress());
					System.out.println(socket.getPort());
					ObjectOutputStream socketOut=new ObjectOutputStream(socket.getOutputStream());
					socketOut.flush();
					ObjectInputStream socketIn=new ObjectInputStream(socket.getInputStream());
					
					sendInfo socketOb=(sendInfo)socketIn.readObject();
					System.out.println("object list");
					if(socketOb.getType().equals("name")){
						task ob=new task(socketIn,socketOut,socketOb);
						objectList.add(ob);
						
					}
					else if(socketOb.getType().equals("file")){
						sendFile ob=new sendFile(socketIn,socketOut,socketOb);
						filePacket.add(ob);
					}
					
					//ob.start();
				}catch(Exception e){
					
				}
			}
			
		}catch(IOException e){
			
		}
		
	}
	
	
	public class task extends Thread {
		
		//private java.io.OutputStream out;
		//private InputStream in;
		
		private ObjectInputStream obin;
		private ObjectOutputStream obout;
		private String clientName;
		task(ObjectInputStream obin,ObjectOutputStream obout,sendInfo ob) throws IOException, ClassNotFoundException{
			this.obout=obout;
			this.obin=obin;
			this.clientName=ob.getMsg();
			System.out.println("aaaaaaaa");
			loginName.add(clientName);
			System.out.println(clientName);
			
			start();
		}
		
		void send_msg(sendInfo ob) throws IOException{
			obout.writeObject(ob);
		}
		
		public void run(){
			try {
				sendName();
				while(true){
					try {
						sendInfo obj=(sendInfo)obin.readObject();
						for(int i=0;i<loginName.size();i++){           
							if(loginName.get(i).equals(obj.getTo())){
								objectList.get(i).send_msg(obj);
								//obout.writeObject(obj);
								break;
							}
						}
						
						/*String msg=(String)obin.readObject();
						System.out.printf("input : %s\n",msg);
						if(msg.matches("%%%%[A-Za-z]*%%%%[A-Za-z]*")){    ////create window for chat
							System.out.println(msg);
							String name[]=msg.split("%%%%");
							for(int i=0;i<login_name.size();i++){
								if(login_name.get(i).equals(name[2])){
									object_list.get(i).send_msg(msg);
									break;
								}
							}
						}
						else if(msg.matches("##%%[A-Za-z]*##%%[A-Za-z]*##%%(.*)")){   ///send msg
							System.out.println(msg);
							String name[]=msg.split("##%%");
							for(int i=0;i<login_name.size();i++){
								if(login_name.get(i).equals(name[2])){
									object_list.get(i).send_msg(msg);
									System.out.println("send msg.........");
									break;
									
								}
							}
						}
						else if(msg.matches("@@[A-Za-z]*@@[A-Za-z]*")){
							String file_name=(String) obin.readObject();
							System.out.println(file_name);
							int file_size=in.read();
							System.out.println(file_size);
							FileOutputStream fileo=new FileOutputStream("F:\\"+file_name);
							byte b[]=new byte[2048];
							int read;
							while((read=in.read(b, 0, 2048))!=-1){
								
								fileo.write(b, 0, read);
								System.out.printf("write to file : %d\n",read);
								//out.flush();
							}
							fileo.close();
							System.out.println("end of file");
							
						}  */
					} catch (ClassNotFoundException e) {
						System.out.println("class not found");
						// TODO Auto-generated catch block
						//e.printStackTrace();
					}
				} 
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		public void sendName() throws IOException{
			
			for(int i=0;i<loginName.size();i++){
				sendInfo ob=new sendInfo();
				ob.setType("name");
				ob.setMsg(loginName.get(i));
				obout.writeObject(ob);
			}
		}
	}
	
	
	
	public class sendFile extends Thread{
		
		public ObjectOutputStream fileObjectOut;
		public ObjectInputStream fileObjectIn;
		public String fileName;
		sendFile(ObjectInputStream in,ObjectOutputStream out,sendInfo ob){
			this.fileObjectIn=in;
			this.fileObjectOut=out;
			fileSendTo.add(ob.getMsg());
			start();
		}
		void sendPacket(sendInfo ob) throws IOException{
			fileObjectOut.writeObject(ob);
		}
		public void run(){
			try{
				while(true){
					sendInfo obj=(sendInfo)fileObjectIn.readObject();
					for(int i=0;i<fileSendTo.size();i++){           
						if(fileSendTo.get(i).equals(obj.getTo())){
							filePacket.get(i).sendPacket(obj);
							break;
						}
					}
				}
				
			}catch(Exception e){
				System.out.println("send file exception : "+e);
			}
		}
		
	}
}
