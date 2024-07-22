package chattingprogram;

import java.io.Serializable;

public class Message implements Serializable {
	String nickName;
	String message;
	String targetUser;

	public Message(String nickName, String message) {
		this.nickName = nickName;
		this.message = message;
	}
	
	public Message(String nickName, String message, String targetUser) {
		this(nickName, message);
		this.targetUser = targetUser;
	}

	public String toString() {
		return nickName + "님 : " + message;
	}
}