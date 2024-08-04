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

import ClientDao.Client;
import Service.ChattingService;

public class ChatServer {
	ServerSocket serverSocket;
	Map<String, ChatSocket> chatRoom = new Hashtable<>();
	Scanner sc = new Scanner(System.in);
	ExecutorService threadPool = Executors.newFixedThreadPool(20);
	
	public static void main(String[] args) {
		ChatServer cs = new ChatServer();
		try {
			cs.serverSocket = new ServerSocket(50001);
			System.out.println("서버 시작");
			
			// 클라이언트 접속요청 별도 스레드에서 처리
			Thread thread = new Thread(() -> {
				while(true) {
					Socket socket;
					try {
						socket = cs.serverSocket.accept();
						new ChatSocket(cs, socket);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			});
			thread.setDaemon(true);
			thread.start();
			
			
			while(true) {
				System.out.println("서버를 종료하려면 'q'를 입력하세요");
				String key = cs.sc.nextLine();
				if(key.equals("q")) break;
			}
			
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			cs.sc.close();
			try {
				cs.serverSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
	}
	
	public int searchClientInfo(String nickName, String password, ChatSocket socket) {
		int result = 0;
		
		result = new ChattingService().searchClientInfo(nickName, password);
		if(result > 0) {
			if(chatRoom.containsKey(nickName)) {
				return 0;
			}
			socket.nickName = nickName;
			addChatSocket(socket);
			return result;
		}
		
		return result;
	}
	
	public int searchClientInfo(String nickName) {
		int result = 0;
		result = new ChattingService().searchClientInfo(nickName);
		
		return result;
	}
	
	
	
	public void addChatSocket(ChatSocket socket) {
		String key = socket.nickName;
		chatRoom.put(key, socket);
		sendToAll(new Message(socket.nickName, "님이 입장하셨습니다."));
	}
	
	public void sendToAll(Message msg) {
		Collection<ChatSocket> coll = chatRoom.values();
		for(ChatSocket cs : coll) {
			if(cs.nickName.equals(msg.nickName)) continue;
			cs.send(msg);
		}
	}
	
	public int insertClient(Client c) {
		return new ChattingService().insertClient(c);
	}

}
