package playhub;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class User extends SessionObserver {

	private String userId;
	private String name;
	private String phoneNum;
	private String password;
	private final List<Community> userCommunityList = new ArrayList<>();
	private Wallet walletObj;
	private final List<String> notificationList = new ArrayList<>();

	public User(String name, String phoneNum, String password) {
		this.userId = UUID.randomUUID().toString();
		this.name = name;
		this.phoneNum = phoneNum;
		this.password = password;
		this.walletObj = new Wallet("HKD");
	}

	public void register(String name, String phoneNumber, String password) {
		if (name == null || name.isEmpty()) {
			throw new IllegalArgumentException("Name cannot be null or empty");
		}
		if (phoneNumber == null || phoneNumber.isEmpty()) {
			throw new IllegalArgumentException("Phone number cannot be null or empty");
		}
		if (password == null || password.isEmpty()) {
			throw new IllegalArgumentException("Password cannot be null or empty");
		}
		this.name = name;
		this.phoneNum = phoneNumber;
		this.password = password;
	}

	public void login(String name, String password) {
		if (!this.name.equals(name) || !this.password.equals(password)) {
			throw new IllegalArgumentException("Invalid credentials");
		}
	}

	public void joinCommunity(Community community) {
		if (community == null) {
			throw new IllegalArgumentException("Community cannot be null");
		}
		if (!userCommunityList.contains(community)) {
			userCommunityList.add(community);
		}
	}

	public void leaveCommunity(Community community) {
		userCommunityList.remove(community);
	}

	public void joinSession(Session session) {
		if (session.isFull()) {
			throw new IllegalStateException("Session is full");
		}
		session.addParticipant(this);
	}

	public void topUpWallet(double amount, String method) {
		walletObj.addFunds(amount, method);
	}

	public double getWalletBalance() {
		return walletObj.getBalance();
	}

	public Wallet getWallet() {
		return walletObj;
	}

	@Override
	public void update(Observable observed, String message) {
		addNotificationMessage(message);
	}

	public User getUser() {
		return this;
	}

	public void addNotificationMessage(String message) {
		notificationList.add(message);
	}

	public String getNotificationMessage() {
		if (notificationList.isEmpty()) {
			return "";
		}
		return notificationList.get(notificationList.size() - 1);
	}

	public List<String> getNotificationList() {
		return new ArrayList<>(notificationList);
	}

	public String getUserId() {
		return userId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhoneNum() {
		return phoneNum;
	}

	public void setPhoneNum(String phoneNum) {
		this.phoneNum = phoneNum;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public List<Community> getUserCommunityList() {
		return new ArrayList<>(userCommunityList);
	}

}
