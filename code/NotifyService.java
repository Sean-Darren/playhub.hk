package playhub;

import java.util.*;

public class NotifyService {

	private final Map<String, List<String>> userNotifications = new HashMap<>();

	public void notify(String message, String userId) {
		if (message == null || userId == null) {
			throw new IllegalArgumentException("Message and userId cannot be null");
		}
		userNotifications.computeIfAbsent(userId, k -> new ArrayList<>()).add(message);
	}

	public void notifyAll(String bookingId, String message) {
		if (bookingId == null || message == null) {
			throw new IllegalArgumentException("bookingId and message cannot be null");
		}
		// Notify all users associated with this booking
		userNotifications.computeIfAbsent(bookingId, k -> new ArrayList<>()).add(message);
	}

	public void notifyAllBooking(String bookingId, String message) {
		notifyAll(bookingId, message);
	}

	public List<String> getNotifications(String userId) {
		return new ArrayList<>(userNotifications.getOrDefault(userId, new ArrayList<>()));
	}

	public void clearNotifications(String userId) {
		userNotifications.remove(userId);
	}

	public Map<String, List<String>> getUserNotifications() {
		return new HashMap<>(userNotifications);
	}

}
