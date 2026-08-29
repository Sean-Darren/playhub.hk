package playhub;

import java.util.*;

public class SessionService {

	private final Map<String, Session> sessionMap = new HashMap<>();
	private final Map<String, Community> communityMap = new HashMap<>();
	private final Map<String, User> userMap = new HashMap<>();

	public void addMemberToSession(String userId, String sessionId) {
		Session session = sessionMap.get(sessionId);
		if (session == null) {
			throw new IllegalArgumentException("Session not found: " + sessionId);
		}
		if (session.isFull()) {
			throw new IllegalStateException("Session is full");
		}
		User user = resolveUser(userId, session);
		if (user == null) {
			throw new IllegalArgumentException("User not found: " + userId);
		}
		session.addParticipant(user);
	}

	private User resolveUser(String userId, Session session) {
		User user = userMap.get(userId);
		if (user != null) {
			return user;
		}
		if (session.getCommunity() != null) {
			for (User member : session.getCommunity().getCommunityMember()) {
				if (member.getUserId().equals(userId)) {
					return member;
				}
			}
		}
		return null;
	}

	public boolean checkSessionIsFull(String sessionId) {
		Session session = sessionMap.get(sessionId);
		if (session == null) {
			throw new IllegalArgumentException("Session not found: " + sessionId);
		}
		return session.isFull();
	}

	public boolean sessionIsCancelAllowed(String sessionId) {
		Session session = sessionMap.get(sessionId);
		if (session == null) {
			throw new IllegalArgumentException("Session not found: " + sessionId);
		}
		// Allow cancellation if session is not yet confirmed or has no participants
		return session.getStatus() != SessionStatus.CONFIRMED || session.getParticipantCount() == 0;
	}

	public Session createSession(Booking booking, String communityId, int capacity, double fee) {
		return createSession(booking, communityId, capacity, fee, null);
	}

	public Session createSession(Booking booking, String communityId, int capacity, double fee, String title) {
		if (booking == null) {
			throw new IllegalArgumentException("Booking cannot be null");
		}
		if (capacity <= 0) {
			throw new IllegalArgumentException("Capacity must be positive");
		}
		if (fee < 0) {
			throw new IllegalArgumentException("Fee cannot be negative");
		}
		Community community = communityMap.get(communityId);
		if (community == null) {
			throw new IllegalArgumentException("Community not found: " + communityId);
		}
		Session session = new Session(booking, community, capacity, fee);
		session.setTitle(title == null || title.isEmpty() ? community.getName() + " Session" : title);
		sessionMap.put(session.getSessionId(), session);
		community.addSession(session);
		booking.setBookingStatus(BookingStatus.HAS_SESSION);
		return session;
	}

	public void memberWithdrawFromSession(String sessionId, String userId) {
		Session session = sessionMap.get(sessionId);
		if (session == null) {
			throw new IllegalArgumentException("Session not found: " + sessionId);
		}
		// Find and remove user from session
		List<User> participants = session.getParticipantList();
		for (User user : participants) {
			if (user.getUserId().equals(userId)) {
				session.removeParticipant(user);
				return;
			}
		}
		throw new IllegalArgumentException("User not in session: " + userId);
	}

	public void registerSession(Session session) {
		sessionMap.put(session.getSessionId(), session);
	}

	public void registerCommunity(Community community) {
		communityMap.put(community.getCommunityId(), community);
	}

	public void registerUser(User user) {
		if (user == null) {
			throw new IllegalArgumentException("User cannot be null");
		}
		userMap.put(user.getUserId(), user);
	}

	public Session getSession(String sessionId) {
		return sessionMap.get(sessionId);
	}

	public Map<String, Session> getSessionMap() {
		return new HashMap<>(sessionMap);
	}

}
