package chattingprogram;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class ChatClient {
	String nickName;
	Socket socket;
	ObjectInputStream ois;
	ObjectOutputStream oos;
	Scanner sc = new Scanner(System.in);
	
	public static void main(String[] args) {
		boolean s = true;
		ChatClient cc = new ChatClient();
		Scanner sc = cc.sc;
		cc.connectToServer();
		ObjectInputStream ois = cc.ois;
		ObjectOutputStream oos = cc.oos;
		System.out.println("서버 접속 성공");
		
		while(s) {
			System.out.println("1. 접속");
			System.out.println("2. 새 닉네임 생성");
			int num = sc.nextInt();
			sc.nextLine();
			switch(num) {
			case 1:
				// 기존 닉네임으로 접속, 사용하던 닉네임과 비밀번호를 전송하여 받은 결과에 따라 로그인 여부 결정
				while(true) {
					System.out.print("닉네임: ");
					String nickName = sc.nextLine();
					System.out.print("비밀번호: ");
					String password = sc.nextLine();
					int result = cc.logIn(nickName, password);
					if(result == 1) {
						s = false;
						cc.nickName = nickName;
						break;
					} else {
						continue;
					}
				}
				break;
			case 2:
				// 새로운 닉네임 생성, 데이터베이스에서 이미 사용중인 닉네임인지 확인하여 생성여부 결정, 생성 완료시 기존 닉네임 접속 메뉴로 이행
				cc.createNickName();
				break;
			}
		}
		
		cc.receive();
		
		System.out.println("채팅방에 입장하였습니다.");
		System.out.println("채팅을 종료하시려면 'q'를 입력해주세요.");
		// 메세지 입력 및 전달
		while(true) {
			String message = sc.nextLine();
			if(message.equals("q")) break;
			try {
				oos.writeObject(new Message(cc.nickName, message));
				oos.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		cc.logOut();
	}
	
	
	
	
	public void connectToServer() {
		try {
			socket = new Socket("192.168.219.49", 50001);
			ois = new ObjectInputStream(socket.getInputStream());
			oos = new ObjectOutputStream(socket.getOutputStream());
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void createNickName() {
		String nickName;
		try {
			while(true) {
				System.out.print("사용할 닉네임: ");
				nickName = sc.nextLine();
				oos.writeObject(new Message(nickName, "새 계정 생성", "닉네임 중복 확인")); oos.flush();
				Message msg = (Message)ois.readObject();
				if(msg.message.equals("사용 가능한 닉네임")) {
					System.out.println("사용할 수 있는 닉네임입니다. 사용할 비밀번호를 입력해주세요.");
					String password = sc.nextLine();
					oos.writeObject(new Message(nickName, password, "닉네임 생성"));
					System.out.println("회원 가입 성공했습니다.");
					break;
				} else {
					System.out.println("이미 사용중인 닉네임입니다. 다른 닉네임을 입력해주세요.");
					continue;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public int logIn(String nickName, String password) {
		try {
			oos.writeObject(new Message(nickName, password, "로그인"));
			oos.flush();
			Message msg = (Message)ois.readObject();
			if(msg.message.equals("로그인 성공")) {
				System.out.println("로그인 성공");
				return 1;
			} else {
				System.out.println("닉네임 또는 비밀번호를 다시 확인해주세요");
				return 0;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	public void receive() {
		Thread thread = new Thread(() -> {
			try {
				while(true) {
					Message msg = (Message)ois.readObject();
					
					if(msg.command.equals("귓속말")) {
						System.out.println(msg.command + ") " + msg);
					} else {
						System.out.println(msg);
					}
				}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
		thread.setDaemon(true);
		thread.start();
	}
	
	public void logOut() {
		try {
			sc.close();
			oos.flush();
			oos.close();
			ois.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
