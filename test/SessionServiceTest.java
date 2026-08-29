package playhub;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class SessionServiceTest {

	private SessionService service;
	private Community community;
	private Booking booking;

	@BeforeEach
	void setUp() {
		service = new SessionService();
		community = new Community("Hoops", "Basketball", "Desc", new Date());
		service.registerCommunity(community);
		booking = new Booking(LocalDateTime.now().plusDays(1));
	}

	@Test
	@DisplayName("createSession creates session successfully")
	void testCreateSessionCreatesSuccessfully() {
		Session session = service.createSession(booking, community.getCommunityId(), 10, 50.0);
		assertNotNull(session);
		assertEquals(10, session.getCapacity());
		assertEquals(50.0, session.getFeePerPerson());
		assertEquals(BookingStatus.HAS_SESSION, booking.getBookingStatus());
	}

	@Test
	@DisplayName("createSession adds session to community")
	void testCreateSessionAddsSessionToCommunity() {
		service.createSession(booking, community.getCommunityId(), 10, 50.0);
		assertEquals(1, community.getSessionList().size());
	}

	@Test
	@DisplayName("createSession throws for null booking")
	void testCreateSessionThrowsForNullBooking() {
		assertThrows(IllegalArgumentException.class, () ->
			service.createSession(null, community.getCommunityId(), 10, 50.0));
	}

	@Test
	@DisplayName("createSession throws for nonexistent community")
	void testCreateSessionThrowsForNonexistentCommunity() {
		assertThrows(IllegalArgumentException.class, () ->
			service.createSession(booking, "nonexistent", 10, 50.0));
	}

	@Test
	@DisplayName("createSession throws for zero capacity")
	void testCreateSessionThrowsForZeroCapacity() {
		assertThrows(IllegalArgumentException.class, () ->
			service.createSession(booking, community.getCommunityId(), 0, 50.0));
	}

	@Test
	@DisplayName("createSession throws for negative capacity")
	void testCreateSessionThrowsForNegativeCapacity() {
		assertThrows(IllegalArgumentException.class, () ->
			service.createSession(booking, community.getCommunityId(), -5, 50.0));
	}

	@Test
	@DisplayName("createSession throws for negative fee")
	void testCreateSessionThrowsForNegativeFee() {
		assertThrows(IllegalArgumentException.class, () ->
			service.createSession(booking, community.getCommunityId(), 10, -10.0));
	}

	@Test
	@DisplayName("createSession allows zero fee")
	void testCreateSessionAllowsZeroFee() {
		Session session = service.createSession(booking, community.getCommunityId(), 10, 0.0);
		assertEquals(0.0, session.getFeePerPerson());
	}

	@Test
	@DisplayName("checkSessionIsFull returns false for empty session")
	void testCheckSessionIsFullReturnsFalseForEmpty() {
		Session session = service.createSession(booking, community.getCommunityId(), 10, 50.0);
		assertFalse(service.checkSessionIsFull(session.getSessionId()));
	}

	@Test
	@DisplayName("checkSessionIsFull returns true for full session")
	void testCheckSessionIsFullReturnsTrueForFull() {
		Session session = service.createSession(booking, community.getCommunityId(), 2, 50.0);
		session.addParticipant(new User("User1", "1", "p"));
		session.addParticipant(new User("User2", "2", "p"));
		assertTrue(service.checkSessionIsFull(session.getSessionId()));
	}

	@Test
	@DisplayName("checkSessionIsFull throws for nonexistent session")
	void testCheckSessionIsFullThrowsForNonexistent() {
		assertThrows(IllegalArgumentException.class, () ->
			service.checkSessionIsFull("nonexistent"));
	}

	@Test
	@DisplayName("addMemberToSession succeeds for non-full session")
	void testAddMemberToSessionSucceedsForNonFull() {
		Session session = service.createSession(booking, community.getCommunityId(), 10, 50.0);
		User user = new User("Pat", "11111111", "pw");
		service.registerUser(user);
		assertDoesNotThrow(() -> service.addMemberToSession(user.getUserId(), session.getSessionId()));
		assertEquals(1, session.getParticipantCount());
	}

	@Test
	@DisplayName("addMemberToSession throws when session is full")
	void testAddMemberToSessionThrowsWhenFull() {
		Session session = service.createSession(booking, community.getCommunityId(), 1, 50.0);
		User first = new User("One", "11111111", "pw");
		User second = new User("Two", "22222222", "pw");
		service.registerUser(first);
		service.registerUser(second);
		service.addMemberToSession(first.getUserId(), session.getSessionId());
		assertThrows(IllegalStateException.class, () ->
			service.addMemberToSession(second.getUserId(), session.getSessionId()));
	}

	@Test
	@DisplayName("sessionIsCancelAllowed returns false for confirmed session with participants")
	void testSessionIsCancelAllowedReturnsFalseForConfirmedWithParticipants() {
		Session session = service.createSession(booking, community.getCommunityId(), 10, 50.0);
		session.setStatus(SessionStatus.CONFIRMED);
		session.addParticipant(new User("Pat", "11111111", "pw"));
		assertFalse(service.sessionIsCancelAllowed(session.getSessionId()));
	}

	@Test
	@DisplayName("addMemberToSession throws for nonexistent session")
	void testAddMemberToSessionThrowsForNonexistent() {
		assertThrows(IllegalArgumentException.class, () ->
			service.addMemberToSession("user1", "nonexistent"));
	}

	@Test
	@DisplayName("sessionIsCancelAllowed returns true for pending session")
	void testSessionIsCancelAllowedReturnsTrueForPending() {
		Session session = service.createSession(booking, community.getCommunityId(), 10, 50.0);
		assertTrue(service.sessionIsCancelAllowed(session.getSessionId()));
	}

	@Test
	@DisplayName("sessionIsCancelAllowed returns true for empty confirmed session")
	void testSessionIsCancelAllowedReturnsTrueForEmptyConfirmed() {
		Session session = service.createSession(booking, community.getCommunityId(), 10, 50.0);
		session.setStatus(SessionStatus.CONFIRMED);
		assertTrue(service.sessionIsCancelAllowed(session.getSessionId()));
	}

	@Test
	@DisplayName("sessionIsCancelAllowed throws for nonexistent session")
	void testSessionIsCancelAllowedThrowsForNonexistent() {
		assertThrows(IllegalArgumentException.class, () ->
			service.sessionIsCancelAllowed("nonexistent"));
	}

	@Test
	@DisplayName("memberWithdrawFromSession removes user")
	void testMemberWithdrawFromSessionRemovesUser() {
		Session session = service.createSession(booking, community.getCommunityId(), 10, 50.0);
		User user = new User("TestUser", "12345678", "password");
		session.addParticipant(user);
		service.registerSession(session);
		service.memberWithdrawFromSession(session.getSessionId(), user.getUserId());
		assertEquals(0, session.getParticipantCount());
	}

	@Test
	@DisplayName("memberWithdrawFromSession throws for nonexistent session")
	void testMemberWithdrawFromSessionThrowsForNonexistent() {
		assertThrows(IllegalArgumentException.class, () ->
			service.memberWithdrawFromSession("nonexistent", "user1"));
	}

	@Test
	@DisplayName("memberWithdrawFromSession throws for user not in session")
	void testMemberWithdrawFromSessionThrowsForUserNotInSession() {
		Session session = service.createSession(booking, community.getCommunityId(), 10, 50.0);
		service.registerSession(session);
		assertThrows(IllegalArgumentException.class, () ->
			service.memberWithdrawFromSession(session.getSessionId(), "nonexistent"));
	}

	@Test
	@DisplayName("getSession returns existing session")
	void testGetSessionReturnsExistingSession() {
		Session session = service.createSession(booking, community.getCommunityId(), 10, 50.0);
		Session retrieved = service.getSession(session.getSessionId());
		assertSame(session, retrieved);
	}

	@Test
	@DisplayName("getSession returns null for nonexistent session")
	void testGetSessionReturnsNullForNonexistent() {
		assertNull(service.getSession("nonexistent"));
	}

	@Test
	@DisplayName("Multiple sessions tracked correctly")
	void testMultipleSessionsTrackedCorrectly() {
		Booking b1 = new Booking(LocalDateTime.now().plusDays(1));
		Booking b2 = new Booking(LocalDateTime.now().plusDays(2));
		service.createSession(b1, community.getCommunityId(), 10, 50.0);
		service.createSession(b2, community.getCommunityId(), 5, 30.0);
		assertEquals(2, service.getSessionMap().size());
	}

	@Test
	@DisplayName("getSessionMap returns a copy")
	void testGetSessionMapReturnsCopy() {
		service.createSession(booking, community.getCommunityId(), 10, 50.0);
		Map<String, Session> copy = service.getSessionMap();
		copy.clear();
		assertEquals(1, service.getSessionMap().size());
	}
}
