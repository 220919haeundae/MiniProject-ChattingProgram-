package chattingprogram;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import ClientDao.Client;

public class ChatSocket {
	String nickName;
	ChatServer chatServer;
	Socket socket;
	ObjectInputStream ois;
	ObjectOutputStream oos;
	
	public ChatSocket(ChatServer chatServer, Socket socket) {
		this.chatServer = chatServer;
		this.socket = socket;
		try {
			oos = new ObjectOutputStream(socket.getOutputStream());
			ois = new ObjectInputStream(socket.getInputStream());
			while(true) {
				Message msg = (Message)ois.readObject();
				
				
				//로그인 시 처리부분
				if(msg.command.equals("로그인")) {
					int result = chatServer.searchClientInfo(msg.nickName, msg.message, this);
					if(result == 1) {
						oos.writeObject(new Message("", "로그인 성공", "")); oos.flush();
						break;
					} else {
						oos.writeObject(new Message("", "로그인 실패", "")); oos.flush();
						continue;
					}
					
				// 새 계정 생성 요청 시 처리부분	
				} else if(msg.command.equals("닉네임 중복 확인")) {
					int result = chatServer.searchClientInfo(msg.nickName);
					if(result == 0) {
						oos.writeObject(new Message("", "사용 가능한 닉네임", "")); oos.flush();
						Message password = (Message)ois.readObject();
						chatServer.insertClient(new Client(password.nickName, password.message));
					} else {
						oos.writeObject(new Message("", "사용 중인 닉네임", ""));
						continue;
					}
				}
			}
			
			
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}
		
		receive();
	}
	
	

	
	public void send(Message msg) {
		try {
			oos.writeObject(msg);
			oos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void receive() {
		chatServer.threadPool.execute(() -> {
			try {
				while(true) {
					Message msg = (Message)ois.readObject();
					
					if(msg.nickName.equals(this.nickName)) {
						chatServer.sendToAll(msg);
					} else {
						send(msg);
					}
				}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
				
			
			
		});
	}
}
