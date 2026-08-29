package playhub;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CommunityHost extends User {

	private final List<Community> ownedCommunity = new ArrayList<>();
	private final List<Booking> bookedCourts = new ArrayList<>();
	private BookingService bookingService;
	private SessionService sessionService;

	public CommunityHost(String name, String phoneNum, String password) {
		super(name, phoneNum, password);
	}

	public void setBookingService(BookingService bookingService) {
		this.bookingService = bookingService;
	}

	public void setSessionService(SessionService sessionService) {
		this.sessionService = sessionService;
	}

	public void createCommunity(String name, String sportType, String description) {
		Community community = new Community(name, sportType, description, new java.util.Date());
		ownedCommunity.add(community);
		community.addMember(this);
		if (sessionService != null) {
			sessionService.registerCommunity(community);
		}
	}

	public void createBooking(String communityId, LocalDateTime dateTime, String courtId) {
		if (bookingService == null) {
			throw new IllegalStateException("BookingService is required");
		}
		Booking booking = bookingService.createBooking(getUserId(), courtId, dateTime);
		bookedCourts.add(booking);
	}

	public void rescheduleBooking(String communityId, LocalDateTime dateTime, String courtId) {
		if (dateTime == null || courtId == null) {
			throw new IllegalArgumentException("dateTime and courtId cannot be null");
		}
		if (!dateTime.isAfter(LocalDateTime.now())) {
			throw new IllegalArgumentException("Cannot reschedule to the past");
		}
		for (Booking booking : bookedCourts) {
			if (courtId.equals(booking.getCourtId())
					&& booking.getBookingStatus() != BookingStatus.CANCELLED) {
				if (bookingService != null) {
					Court court = bookingService.getCourtMap().get(courtId);
					if (court != null
							&& !booking.getBookingDateTime().equals(dateTime)
							&& !court.checkAvailability(dateTime)) {
						throw new IllegalStateException("Court is not available at the requested time");
					}
				}
				booking.setBookingDateTime(dateTime);
				return;
			}
		}
		throw new IllegalArgumentException("No booking found for court: " + courtId);
	}

	public void cancelBooking(String bookingId) {
		for (Booking booking : bookedCourts) {
			if (booking.getBookingId().equals(bookingId)) {
				if (bookingService != null) {
					bookingService.cancelBooking(bookingId);
				} else {
					booking.setBookingStatus(BookingStatus.CANCELLED);
				}
				return;
			}
		}
		throw new IllegalArgumentException("Booking not found: " + bookingId);
	}

	public Session createSession(Booking booking, int capacity, double fee, String communityId) {
		return createSession(booking, capacity, fee, communityId, null);
	}

	public Session createSession(Booking booking, int capacity, double fee, String communityId, String title) {
		Community community = findOwnedCommunity(communityId);
		if (community == null) {
			throw new IllegalArgumentException("Community not found: " + communityId);
		}
		if (sessionService != null) {
			sessionService.registerCommunity(community);
			return sessionService.createSession(booking, communityId, capacity, fee, title);
		}
		if (booking == null) {
			throw new IllegalArgumentException("Booking cannot be null");
		}
		if (capacity <= 0) {
			throw new IllegalArgumentException("Capacity must be positive");
		}
		if (fee < 0) {
			throw new IllegalArgumentException("Fee cannot be negative");
		}
		Session session = new Session(booking, community, capacity, fee);
		session.setTitle(title == null || title.isEmpty() ? community.getName() + " Session" : title);
		community.addSession(session);
		booking.setBookingStatus(BookingStatus.HAS_SESSION);
		return session;
	}

	public void cancelSession(String sessionId) {
		for (Community community : ownedCommunity) {
			for (Session session : community.getSessionList()) {
				if (session.getSessionId().equals(sessionId)) {
					session.setStatus(SessionStatus.CANCELLED);
					return;
				}
			}
		}
		throw new IllegalArgumentException("Session not found: " + sessionId);
	}

	public void sendNotification(Community community, String message) {
		community.notifyAllMembers(message);
	}

	public List<Community> getOwnedCommunity() {
		return new ArrayList<>(ownedCommunity);
	}

	public List<Booking> getBookedCourts() {
		return new ArrayList<>(bookedCourts);
	}

	private Community findOwnedCommunity(String communityId) {
		for (Community c : ownedCommunity) {
			if (c.getCommunityId().equals(communityId)) {
				return c;
			}
		}
		return null;
	}

}
