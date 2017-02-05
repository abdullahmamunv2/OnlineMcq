package client_messenger;

import java.util.concurrent.*;

public class client_main {
	public static void main(String args[]){
		int port=1234;
		client_code client1=new client_code("kamrul",port);
		client_code client2=new client_code("real",port);
		//client_code client3=new client_code("masud",port);
		ExecutorService ob=Executors.newCachedThreadPool();
		ob.execute(client1);
		ob.execute(client2);
		//ob.execute(client3);
		ob.shutdown();
	}
}
