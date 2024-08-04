package ClientDao;

import java.io.Serializable;

public class Client implements Serializable {
	String nickName;
	String password;
	
	public Client(String nickName, String password) {
		this.nickName = nickName;
		this.password = password;
	}

}
