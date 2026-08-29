package playhub;

import java.util.*;

public class UserService {

	private final Map<String, User> userMap = new HashMap<>();

	public User createUser(String name, String phoneNum, String password) {
		if (name == null || name.isEmpty()) {
			throw new IllegalArgumentException("Name cannot be null or empty");
		}
		if (phoneNum == null || phoneNum.isEmpty()) {
			throw new IllegalArgumentException("Phone number cannot be null or empty");
		}
		if (password == null || password.isEmpty()) {
			throw new IllegalArgumentException("Password cannot be null or empty");
		}
		User user = new User(name, phoneNum, password);
		userMap.put(user.getUserId(), user);
		return user;
	}

	public void registerUser(User user) {
		if (user == null) {
			throw new IllegalArgumentException("User cannot be null");
		}
		userMap.put(user.getUserId(), user);
	}

	public User getUser(String userId) {
		return userMap.get(userId);
	}

	public User findUserByName(String name) {
		for (User user : userMap.values()) {
			if (user.getName().equals(name)) {
				return user;
			}
		}
		return null;
	}

	public Map<String, User> getUserMap() {
		return new HashMap<>(userMap);
	}

	public int getUserCount() {
		return userMap.size();
	}

}
