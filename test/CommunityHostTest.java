package playhub;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CommunityHostTest {

	private CommunityHost host;
	private BookingService bookingService;
	private SessionService sessionService;
	private Court court;

	@BeforeEach
	void setUp() {
		host = new CommunityHost("Host User", "12345678", "password");
		bookingService = new BookingService();
		sessionService = new SessionService();
		court = new Court("Main Court", "Central", 150.0, "Basketball");
		bookingService.registerCourt(court);
		host.setBookingService(bookingService);
		host.setSessionService(sessionService);
	}

	@Test
	@DisplayName("Constructor initializes as User with correct fields")
	void testConstructorInitializesAsUser() {
		assertEquals("Host User", host.getName());
		assertEquals("12345678", host.getPhoneNum());
		assertNotNull(host.getUserId());
	}

	@Test
	@DisplayName("createCommunity creates and adds to owned list")
	void testCreateCommunityCreatesAndAddsToList() {
		host.createCommunity("Hoops", "Basketball", "Basketball community");
		List<Community> owned = host.getOwnedCommunity();
		assertEquals(1, owned.size());
		assertEquals("Hoops", owned.get(0).getName());
	}

	@Test
	@DisplayName("createCommunity adds host as member")
	void testCreateCommunityAddsHostAsMember() {
		host.createCommunity("Hoops", "Basketball", "Basketball community");
		List<Community> owned = host.getOwnedCommunity();
		List<User> members = owned.get(0).getCommunityMember();
		assertEquals(1, members.size());
		assertTrue(members.contains(host));
	}

	@Test
	@DisplayName("createBooking creates a booking through BookingService")
	void testCreateBookingCreatesBooking() {
		LocalDateTime dateTime = LocalDateTime.now().plusDays(1).withNano(0);
		host.createBooking("comm1", dateTime, court.getCourtId());
		List<Booking> booked = host.getBookedCourts();
		assertEquals(1, booked.size());
		assertEquals(court.getCourtId(), booked.get(0).getCourtId());
		assertEquals(dateTime, booked.get(0).getBookingDateTime());
		assertFalse(court.checkAvailability(dateTime));
		assertNotNull(bookingService.getBooking(booked.get(0).getBookingId()));
	}

	@Test
	@DisplayName("createBooking throws when BookingService is not set")
	void testCreateBookingThrowsWithoutBookingService() {
		CommunityHost unwired = new CommunityHost("Host", "11111111", "pw");
		assertThrows(IllegalStateException.class, () ->
			unwired.createBooking("comm1", LocalDateTime.now().plusDays(1), court.getCourtId()));
	}

	@Test
	@DisplayName("cancelBooking cancels existing booking on court")
	void testCancelBookingCancelsBooking() {
		LocalDateTime dateTime = LocalDateTime.now().plusDays(1).withNano(0);
		host.createBooking("comm1", dateTime, court.getCourtId());
		String bookingId = host.getBookedCourts().get(0).getBookingId();
		host.cancelBooking(bookingId);
		assertEquals(BookingStatus.CANCELLED, host.getBookedCourts().get(0).getBookingStatus());
		assertTrue(court.checkAvailability(dateTime));
	}

	@Test
	@DisplayName("cancelBooking throws for nonexistent booking")
	void testCancelBookingThrowsForNonexistent() {
		assertThrows(IllegalArgumentException.class, () -> host.cancelBooking("nonexistent"));
	}

	@Test
	@DisplayName("rescheduleBooking updates booking dateTime")
	void testRescheduleBookingUpdatesDateTime() {
		LocalDateTime original = LocalDateTime.now().plusDays(1).withNano(0);
		LocalDateTime moved = LocalDateTime.now().plusDays(2).withNano(0);
		host.createBooking("comm1", original, court.getCourtId());
		host.rescheduleBooking("comm1", moved, court.getCourtId());
		assertEquals(moved, host.getBookedCourts().get(0).getBookingDateTime());
		assertTrue(court.checkAvailability(original));
		assertFalse(court.checkAvailability(moved));
	}

	@Test
	@DisplayName("rescheduleBooking throws when target slot is taken")
	void testRescheduleBookingThrowsWhenSlotTaken() {
		LocalDateTime first = LocalDateTime.now().plusDays(1).withNano(0);
		LocalDateTime second = LocalDateTime.now().plusDays(2).withNano(0);
		host.createBooking("comm1", first, court.getCourtId());
		bookingService.createBooking("other", court.getCourtId(), second);
		assertThrows(IllegalStateException.class, () ->
			host.rescheduleBooking("comm1", second, court.getCourtId()));
	}

	@Test
	@DisplayName("createSession creates session with valid inputs")
	void testCreateSessionCreatesSession() {
		host.createCommunity("Hoops", "Basketball", "Desc");
		String communityId = host.getOwnedCommunity().get(0).getCommunityId();
		Booking booking = new Booking(LocalDateTime.now().plusDays(1));
		Session session = host.createSession(booking, 10, 50.0, communityId);
		assertNotNull(session);
		assertEquals(10, session.getCapacity());
		assertEquals(50.0, session.getFeePerPerson());
		assertEquals(BookingStatus.HAS_SESSION, booking.getBookingStatus());
		assertSame(session, sessionService.getSession(session.getSessionId()));
	}

	@Test
	@DisplayName("createSession throws for nonexistent community")
	void testCreateSessionThrowsForNonexistentCommunity() {
		Booking booking = new Booking(LocalDateTime.now().plusDays(1));
		assertThrows(IllegalArgumentException.class, () ->
			host.createSession(booking, 10, 50.0, "nonexistent"));
	}

	@Test
	@DisplayName("cancelSession cancels session")
	void testCancelSessionCancelsSession() {
		host.createCommunity("Hoops", "Basketball", "Desc");
		String communityId = host.getOwnedCommunity().get(0).getCommunityId();
		Booking booking = new Booking(LocalDateTime.now().plusDays(1));
		Session session = host.createSession(booking, 10, 50.0, communityId);
		host.cancelSession(session.getSessionId());
		assertEquals(SessionStatus.CANCELLED, session.getStatus());
	}

	@Test
	@DisplayName("cancelSession throws for nonexistent session")
	void testCancelSessionThrowsForNonexistentSession() {
		assertThrows(IllegalArgumentException.class, () -> host.cancelSession("nonexistent"));
	}

	@Test
	@DisplayName("sendNotification notifies community members")
	void testSendNotificationNotifiesMembers() {
		Community community = new Community("Hoops", "Basketball", "Desc", new Date());
		TestObserver observer = new TestObserver();
		community.attach(observer);
		host.sendNotification(community, "Meeting at 5pm");
		assertEquals(1, observer.getUpdateCount());
		assertEquals("Meeting at 5pm", observer.getLastMessage());
	}

	@Test
	@DisplayName("getOwnedCommunity returns a copy")
	void testGetOwnedCommunityReturnsCopy() {
		host.createCommunity("Hoops", "Basketball", "Desc");
		List<Community> copy = host.getOwnedCommunity();
		copy.clear();
		assertEquals(1, host.getOwnedCommunity().size());
	}

	@Test
	@DisplayName("getBookedCourts returns a copy")
	void testGetBookedCourtsReturnsCopy() {
		host.createBooking("comm1", LocalDateTime.now().plusDays(1).withNano(0), court.getCourtId());
		List<Booking> copy = host.getBookedCourts();
		copy.clear();
		assertEquals(1, host.getBookedCourts().size());
	}

	@Test
	@DisplayName("Empty lists initially")
	void testEmptyListsInitially() {
		assertTrue(host.getOwnedCommunity().isEmpty());
		assertTrue(host.getBookedCourts().isEmpty());
	}

	static class TestObserver extends SessionObserver {
		private int updateCount = 0;
		private String lastMessage = "";

		@Override
		public void update(Observable observed, String message) {
			updateCount++;
			lastMessage = message;
		}

		public int getUpdateCount() { return updateCount; }
		public String getLastMessage() { return lastMessage; }
	}
}
