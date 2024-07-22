package chattingprogram;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

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
			ois = new ObjectInputStream(socket.getInputStream());
			oos = new ObjectOutputStream(socket.getOutputStream());
			
			// 클라이언트의 채팅네임 정보 저장
			Message msg = (Message) ois.readObject();
			nickName = msg.nickName;
			// 채팅룸 입장 직전 기존 채팅룸 유저에게 유저 입장 알림
			chatServer.sendToAll(msg);
			// 채팅룸 입장 및 메시지 수신 시작
			receive();
		} catch(IOException e) {
			System.out.println("ChatSocket 생성 중 입출력 객체 생성 실패");
		} catch(ClassNotFoundException e) {}
	} 
	
	public void receive() {
		// 채팅룸 입장
		chatServer.addChatSocket(this);
		//메시지 수신 시작
		chatServer.threadPool.execute(() -> {
			try {
				while(true) {
					Message msg = (Message)ois.readObject();
					if(this.nickName.equals(msg.nickName)) {
						chatServer.sendToAll(msg);
					} else {
						this.send(msg);
					}
				}
			}catch(IOException e) {
				// 채팅 중 예외 발생 시 채팅소켓 객체 제거
				close();
			}catch(ClassNotFoundException e) {
				close();
			}
		});
	}
	
	public void send(Message msg) {
		try {
			oos.writeObject(msg);
			oos.flush();
		} catch (IOException e) {
			// System.out.println(msg.nickName + "님의 메시지를 "+ this.nickName + "님에게 보내는 데 실패했습니다.");
		}
	}
	// 채팅 소켓 종료
	public void close() {
		try {
			chatServer.sendToAll(new Message(nickName, "님이 퇴장하셨습니다."));
			ois.close();
			oos.flush();
			oos.close();
			chatServer.removeChatSocket(this);
		} catch(IOException e) {}
	}
}