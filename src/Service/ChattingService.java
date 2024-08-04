package Service;

import java.sql.ResultSet;

import ClientDao.Client;
import ClientDao.ClientDao;

public class ChattingService {
	
	public int searchClient(String nickName) {
		return new ClientDao().searchClient(nickName);
	}

	public int searchClientInfo(String nickName, String password) {
		return new ClientDao().searchClient(nickName, password);
	}

	
	public int searchClientInfo(String nickName) {
		return new ClientDao().searchClient(nickName);
	}

	public int insertClient(Client c) {
		int result = 0;
		result = new ClientDao().insertClient(c);
		return result;
	}
	
	

}
