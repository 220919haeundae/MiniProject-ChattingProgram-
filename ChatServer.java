package chattingprogram;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatServer {
	ServerSocket serverSocket;
	Map<String, ChatSocket> chattingRoom = new Hashtable<>(20);
	ExecutorService threadPool = Executors.newFixedThreadPool(20);
	Thread thread = new Thread(() -> { // 클라이언트 접속 수락 및 소켓 객체 생성하는 스레드
		while(true) {
			try {
				Socket socket = serverSocket.accept();
				ChatSocket cs = new ChatSocket(this, socket);
			}catch(IOException e) {
				break;
			}
		}
		Thread.interrupted();
	});

	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		ChatServer server = new ChatServer();
		server.startServer();


		while(true) {
			System.out.println("서버를 종료하려면 \"q\"를 입력하세요.");
			String key = scanner.nextLine();
			if(key.equals("q")) break;
		}


		scanner.close();
		server.stopServer();

	}

	public void startServer() {
		try {
			serverSocket = new ServerSocket(50001);
			System.out.println("서버 시작됨");

			thread.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	// 메시지 송신한 클라이언트 이외 클라이언트에게 송신 메시지 전달
	public void sendToAll(Message msg) {
		Collection<ChatSocket> csCollection = chattingRoom.values();
		for(ChatSocket cs : csCollection) {
			if (cs.nickName.equals(msg.nickName)) continue;
			cs.send(msg);
		}
	}

	public Message searchNickName(Message msg) {
		if(chattingRoom.containsKey(msg.nickName)) {
			return new Message(msg.nickName, "사용중인 닉네임");
		} else return new Message(msg.nickName, "사용 가능한 닉네임");
	}
	
	public void stopServer() {
		// 클라이언트 접속 요청 수락 중단
		thread.interrupt();

		// 모든 채팅소켓 실행 종료
		chattingRoom.values().stream().forEach(cs -> cs.close());

		// 서버소켓 닫음
		try {
			threadPool.shutdownNow();
			serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("서버가 종료되었습니다.");

	}
	// 클라이언트 채팅룸 입장 
	public void addChatSocket(ChatSocket socket) {
		String key = socket.nickName;
		chattingRoom.put(key, socket);
		System.out.println("입장 : " + key);
		System.out.println("현재 채팅방 인원 : " + chattingRoom.size() + "명");

	}
	// 클라이언트 채팅룸 퇴장
	public void removeChatSocket(ChatSocket socket) {
		String key = socket.nickName;
		chattingRoom.remove(key);
		System.out.println("퇴장 : " + key);
		System.out.println("현재 채팅방 인원 : " + chattingRoom.size() + "명");
	}
}