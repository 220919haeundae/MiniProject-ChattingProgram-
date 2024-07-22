package chattingprogram;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.Scanner;

public class ChatClient {
	String nickName;
	Socket socket;
	ObjectInputStream ois;
	ObjectOutputStream oos;
	Scanner scanner;

	public void connectToServer() throws IOException {
		socket = new Socket("localhost", 50001);
	}
	// 메시지 수신 기능을 별도 스레드에서 처리
	public void receive() {
		Thread thread = new Thread(() -> {
			try {
				ois = new ObjectInputStream(socket.getInputStream());
				while(true) {
					Message msg = (Message) ois.readObject();
					
					if(msg.targetUser != null) {
						
						System.out.println("(귓속말) " + msg);
						
					} else {
						
						if(msg.message.equals("님이 입장하셨습니다.") ||msg.message.equals("님이 퇴장하셨습니다.")) {
							System.out.println(msg.nickName+msg.message);
						} else {
							System.out.println(msg);
						}
						
					}
				}
			} catch (SocketException e) {
			} catch (IOException e) {
			} catch (ClassNotFoundException e) {} 
		});
		thread.start();
	}
	
	public void psrMessage() {
		System.out.print("귓속말 상대의 닉네임을 입력해주세요: ");
		String targetUser = scanner.nextLine();
		System.out.println("귓속말로 전환");
		while(true) {
			String message = scanner.nextLine();
			if(message.length() == 4 && message.equals("/전체말")) {
				System.out.println("전체말로 전환");
				break;
			}
			
			try {
				oos.writeObject(new Message(nickName, message, targetUser));
			} catch (IOException e) {
			}

		}
	}

	public void disconnectServer() throws IOException {
		ois.close();
		oos.flush();
		oos.close();
		socket.close();
	}

	public static void main(String[] args) {
		ChatClient cc = new ChatClient();
		Scanner scanner = cc.scanner = new Scanner(System.in);
		try {
			// 서버 연결
			cc.connectToServer();
			cc.oos = new ObjectOutputStream(cc.socket.getOutputStream());
			System.out.println("서버에 접속했습니다. 채팅룸에서 사용할 이름을 지어주세요.");
			System.out.print("대화명 : ");
			cc.nickName = scanner.nextLine();
			// 첫 입장, 다른 이용자들에게 닉네임과 입장 알림
			cc.oos.writeObject(new Message(cc.nickName, "님이 입장하셨습니다."));
			// 채팅방 입장 후 타 유저의 메시지 수신 시작
			cc.receive();

			//채팅방 입장 후 메시지 발송 시작
			while(true) {
				String message = scanner.nextLine();
				// 채팅 메세지로 "q"를 입력하면 클라이언트 프로그램 종료
				if(message.length() == 4 && message.equals("/귓속말")) {
					cc.psrMessage();
					continue;
				}
				if(message.equals("q")) break;
				cc.oos.writeObject(new Message(cc.nickName, message));
			}
			cc.disconnectServer();
		} catch(IOException e) {}
	}
}