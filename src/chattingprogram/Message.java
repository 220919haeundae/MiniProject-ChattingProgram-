package chattingprogram;

import java.io.Serializable;

public class Message implements Serializable {
	String nickName;
	String message;
	String command;

	
	
	public Message(String nickName, String message) {
		this.nickName = nickName;
		this.message = message;
		this.command = "메세지";
	}
	
	public Message(String nickName, String message, String command) {
		this.nickName = nickName;
		this.message = message;
		this.command = command;
	}
	
	public String toString() {
		return String.format("%s 님의 메세지: %s", nickName, message);
	}

}
