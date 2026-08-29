package playhub;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

	private User user;

	@BeforeEach
	void setUp() {
		user = new User("John", "12345678", "securePass123");
	}

	@Test
	@DisplayName("Constructor initializes all fields")
	void testConstructorInitializesAllFields() {
		assertNotNull(user.getUserId());
		assertEquals("John", user.getName());
		assertEquals("12345678", user.getPhoneNum());
		assertNotNull(user.getWallet());
	}

	@Test
	@DisplayName("register updates user fields")
	void testRegisterUpdatesUserFields() {
		user.register("Jane", "87654321", "newPass");
		assertEquals("Jane", user.getName());
		assertEquals("87654321", user.getPhoneNum());
		assertEquals("newPass", user.getPassword());
	}

	@Test
	@DisplayName("login succeeds with valid credentials")
	void testLoginSucceedsWithValidCredentials() {
		assertDoesNotThrow(() -> user.login("John", "securePass123"));
	}

	@Test
	@DisplayName("login fails with wrong name")
	void testLoginFailsWithWrongName() {
		assertThrows(IllegalArgumentException.class, () -> user.login("Wrong", "securePass123"));
	}

	@Test
	@DisplayName("login fails with wrong password")
	void testLoginFailsWithWrongPassword() {
		assertThrows(IllegalArgumentException.class, () -> user.login("John", "wrongPass"));
	}

	@Test
	@DisplayName("joinCommunity adds community to user's list")
	void testJoinCommunityAddsCommunity() {
		Community community = new Community("Hoops", "Basketball", "Desc", new java.util.Date());
		user.joinCommunity(community);
		List<Community> communities = user.getUserCommunityList();
		assertEquals(1, communities.size());
		assertTrue(communities.contains(community));
	}

	@Test
	@DisplayName("joinCommunity does not add duplicates")
	void testJoinCommunityDoesNotAddDuplicates() {
		Community community = new Community("Hoops", "Basketball", "Desc", new java.util.Date());
		user.joinCommunity(community);
		user.joinCommunity(community);
		assertEquals(1, user.getUserCommunityList().size());
	}

	@Test
	@DisplayName("leaveCommunity removes community from user's list")
	void testLeaveCommunityRemovesCommunity() {
		Community community = new Community("Hoops", "Basketball", "Desc", new java.util.Date());
		user.joinCommunity(community);
		user.leaveCommunity(community);
		assertTrue(user.getUserCommunityList().isEmpty());
	}

	@Test
	@DisplayName("joinCommunity throws for null community")
	void testJoinCommunityThrowsForNull() {
		assertThrows(IllegalArgumentException.class, () -> user.joinCommunity(null));
	}

	@Test
	@DisplayName("joinSession adds user to session")
	void testJoinSessionAddsUser() {
		Community community = new Community("Hoops", "Basketball", "Desc", new java.util.Date());
		Booking booking = new Booking(java.time.LocalDateTime.now().plusDays(1));
		Session session = new Session(booking, community, 10, 50.0);
		user.joinSession(session);
		assertTrue(session.getParticipantList().contains(user));
	}

	@Test
	@DisplayName("joinSession fails when session is full")
	void testJoinSessionFailsWhenSessionFull() {
		Community community = new Community("Hoops", "Basketball", "Desc", new java.util.Date());
		Booking booking = new Booking(java.time.LocalDateTime.now().plusDays(1));
		Session session = new Session(booking, community, 1, 50.0);
		User otherUser = new User("Other", "88888888", "pass");
		session.addParticipant(otherUser);
		assertThrows(IllegalStateException.class, () -> user.joinSession(session));
	}

	@Test
	@DisplayName("topUpWallet increases wallet balance")
	void testTopUpWalletIncreasesBalance() {
		user.topUpWallet(100.0, "Alipay");
		assertEquals(100.0, user.getWalletBalance());
	}

	@Test
	@DisplayName("getWalletBalance returns current balance")
	void testGetWalletBalanceReturnsCurrentBalance() {
		assertEquals(0.0, user.getWalletBalance());
		user.topUpWallet(50.0, "Alipay");
		assertEquals(50.0, user.getWalletBalance());
	}

	@Test
	@DisplayName("addNotificationMessage adds to notification list")
	void testAddNotificationMessageAddsToList() {
		user.addNotificationMessage("Test message");
		assertEquals("Test message", user.getNotificationMessage());
	}

	@Test
	@DisplayName("getNotificationMessage returns empty string when no notifications")
	void testGetNotificationMessageReturnsEmptyWhenNone() {
		assertEquals("", user.getNotificationMessage());
	}

	@Test
	@DisplayName("Multiple notifications are tracked")
	void testMultipleNotificationsTracked() {
		user.addNotificationMessage("Msg 1");
		user.addNotificationMessage("Msg 2");
		user.addNotificationMessage("Msg 3");
		List<String> notifications = user.getNotificationList();
		assertEquals(3, notifications.size());
		assertEquals("Msg 3", user.getNotificationMessage());
	}

	@Test
	@DisplayName("update method adds notification message")
	void testUpdateAddsNotificationMessage() {
		Community community = new Community("Hoops", "Basketball", "Desc", new java.util.Date());
		user.update(community, "Observer notification");
		assertEquals("Observer notification", user.getNotificationMessage());
	}

	@Test
	@DisplayName("getUser returns self")
	void testGetUserReturnsSelf() {
		assertSame(user, user.getUser());
	}

	@Test
	@DisplayName("Setters update fields correctly")
	void testSettersUpdateFields() {
		user.setName("New Name");
		user.setPhoneNum("99999999");
		user.setPassword("newPass");
		assertEquals("New Name", user.getName());
		assertEquals("99999999", user.getPhoneNum());
		assertEquals("newPass", user.getPassword());
	}
}
