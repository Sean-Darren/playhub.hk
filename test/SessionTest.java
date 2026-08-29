package playhub;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SessionTest {

	private Session session;
	private Booking booking;
	private Community community;

	@BeforeEach
	void setUp() {
		LocalDateTime futureDateTime = LocalDateTime.now().plusDays(1);
		booking = new Booking(futureDateTime, "user1", "court1");
		community = new Community("Test Community", "Basketball", "Test description", new Date());
		session = new Session(booking, community, 10, 50.0);
	}

	@Test
	@DisplayName("Constructor initializes session correctly")
	void testConstructorInitializesSession() {
		assertNotNull(session.getSessionId());
		assertEquals(SessionStatus.PENDING, session.getStatus());
		assertEquals(10, session.getCapacity());
		assertEquals(50.0, session.getFeePerPerson());
		assertEquals(booking, session.getBooking());
		assertEquals(community, session.getCommunity());
	}

	@Test
	@DisplayName("addParticipant adds user to session")
	void testAddParticipantAddsUser() {
		User user = new User("John", "12345678", "password");
		session.addParticipant(user);
		List<User> participants = session.getParticipantList();
		assertEquals(1, participants.size());
		assertTrue(participants.contains(user));
	}

	@Test
	@DisplayName("addParticipant throws when session is full")
	void testAddParticipantThrowsWhenFull() {
		for (int i = 0; i < 10; i++) {
			session.addParticipant(new User("User" + i, "phone" + i, "pass" + i));
		}
		User extraUser = new User("Extra", "99999999", "pass");
		assertThrows(IllegalStateException.class, () -> session.addParticipant(extraUser));
	}

	@Test
	@DisplayName("addParticipant throws when user already in session")
	void testAddParticipantThrowsWhenUserAlreadyInSession() {
		User user = new User("John", "12345678", "password");
		session.addParticipant(user);
		assertThrows(IllegalStateException.class, () -> session.addParticipant(user));
	}

	@Test
	@DisplayName("removeParticipant removes user from session")
	void testRemoveParticipantRemovesUser() {
		User user = new User("John", "12345678", "password");
		session.addParticipant(user);
		session.removeParticipant(user);
		assertFalse(session.getParticipantList().contains(user));
		assertEquals(0, session.getParticipantCount());
	}

	@Test
	@DisplayName("calculateTotalFee returns correct total")
	void testCalculateTotalFeeReturnsCorrectTotal() {
		session.addParticipant(new User("User1", "1", "p"));
		session.addParticipant(new User("User2", "2", "p"));
		session.addParticipant(new User("User3", "3", "p"));
		assertEquals(150.0, session.calculateTotalFee());
	}

	@Test
	@DisplayName("calculateTotalFee returns 0 with no participants")
	void testCalculateTotalFeeReturnsZeroWithNoParticipants() {
		assertEquals(0.0, session.calculateTotalFee());
	}

	@Test
	@DisplayName("isFull returns false when not at capacity")
	void testIsFullReturnsFalseWhenNotAtCapacity() {
		assertFalse(session.isFull());
	}

	@Test
	@DisplayName("isFull returns true when at capacity")
	void testIsFullReturnsTrueWhenAtCapacity() {
		for (int i = 0; i < 10; i++) {
			session.addParticipant(new User("User" + i, "phone" + i, "pass"));
		}
		assertTrue(session.isFull());
	}

	@Test
	@DisplayName("getSessionDateTime returns booking date time")
	void testGetSessionDateTimeReturnsBookingDateTime() {
		assertEquals(booking.getBookingDateTime(), session.getSessionDateTime());
	}

	@Test
	@DisplayName("getParticipantList returns a copy")
	void testGetParticipantListReturnsCopy() {
		User user = new User("John", "12345678", "password");
		session.addParticipant(user);
		List<User> copy = session.getParticipantList();
		copy.clear();
		assertEquals(1, session.getParticipantList().size());
	}

	@Test
	@DisplayName("Setters update fields correctly")
	void testSettersUpdateFields() {
		session.setTitle("New Title");
		session.setCapacity(20);
		session.setFeePerPerson(100.0);
		session.setStatus(SessionStatus.CONFIRMED);
		assertEquals("New Title", session.getTitle());
		assertEquals(20, session.getCapacity());
		assertEquals(100.0, session.getFeePerPerson());
		assertEquals(SessionStatus.CONFIRMED, session.getStatus());
	}

	@Test
	@DisplayName("Session with zero capacity")
	void testSessionWithZeroCapacity() {
		Session zeroSession = new Session(booking, community, 0, 50.0);
		assertTrue(zeroSession.isFull());
	}

	@Test
	@DisplayName("getParticipantCount returns correct count")
	void testGetParticipantCountReturnsCorrectCount() {
		assertEquals(0, session.getParticipantCount());
		session.addParticipant(new User("User1", "1", "p"));
		assertEquals(1, session.getParticipantCount());
		session.addParticipant(new User("User2", "2", "p"));
		assertEquals(2, session.getParticipantCount());
	}
}
