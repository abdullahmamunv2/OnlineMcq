package common;

import java.io.Serializable;

public class sendInfo implements Serializable{
	//public static final long serialVersionUID = 1L;
	public String sendFrom;
	public String sendTo;
	public String type;
	public String msg;
	public byte b[];
	public sendInfo(){
		sendFrom=null;
		sendTo=null;
		type=null;
		msg=null;
		byte b[]=null;
		
	}
	public void setFrom(String sendForm){
		this.sendFrom=sendForm;
	} 
	public void setTo(String sendTo){
		this.sendTo=sendTo;
	}
	
	public void setType(String type){
		this.type=type;
	}
	public void setMsg(String msg){
		this.msg=msg;
	}
	public void setB(byte b[]){
		this.b=new byte[2048];
		this.b=b;
	}
	public String getFrom(){
		return sendFrom;
	} 
	public String getTo(){
		return sendTo;
	}
	
	public String getType(){
		return type;
	}
	public String getMsg(){
		return msg;
	}
	public byte[] getB(){
		return b;
	}
}