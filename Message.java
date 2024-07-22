package chattingprogram;

import java.io.Serializable;

public class Message implements Serializable {
	String nickName;
	String message;
	
	public Message(String nickName, String message) {
		this.nickName = nickName;
		this.message = message;
	}
	
	public String toString() {
		return nickName + "님 : " + message;
	}
}