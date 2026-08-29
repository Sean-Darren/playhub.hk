package playhub;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CommunityTest {

	private Community community;
	private Date createdDate;

	@BeforeEach
	void setUp() {
		createdDate = new Date();
		community = new Community("Hoops Community", "Basketball", "A community for basketball lovers", createdDate);
	}

	@Test
	@DisplayName("Constructor initializes all fields")
	void testConstructorInitializesAllFields() {
		assertNotNull(community.getCommunityId());
		assertEquals("Hoops Community", community.getName());
		assertEquals("Basketball", community.getSportType());
		assertEquals("A community for basketball lovers", community.getDescription());
		assertEquals(createdDate, community.getCreatedDate());
	}

	@Test
	@DisplayName("addMember adds user and calls joinCommunity")
	void testAddMemberAddsUser() {
		User user = new User("John", "12345678", "password");
		community.addMember(user);
		List<User> members = community.getCommunityMember();
		assertEquals(1, members.size());
		assertTrue(members.contains(user));
	}

	@Test
	@DisplayName("addMember does not add duplicate")
	void testAddMemberDoesNotAddDuplicate() {
		User user = new User("John", "12345678", "password");
		community.addMember(user);
		community.addMember(user);
		assertEquals(1, community.getMemberCount());
	}

	@Test
	@DisplayName("removeMember removes user from community")
	void testRemoveMemberRemovesUser() {
		User user = new User("John", "12345678", "password");
		community.addMember(user);
		community.removeMember(user);
		assertEquals(0, community.getMemberCount());
		assertTrue(user.getUserCommunityList().isEmpty());
	}

	@Test
	@DisplayName("removeMember detaches user so they do not receive the leave notice")
	void testRemoveMemberDoesNotNotifyRemovedUser() {
		User user = new User("John", "12345678", "password");
		community.addMember(user);
		user.getNotificationList();
		int before = user.getNotificationList().size();
		community.removeMember(user);
		assertEquals(before, user.getNotificationList().size());
	}

	@Test
	@DisplayName("getCommunityMember returns a copy")
	void testGetCommunityMemberReturnsCopy() {
		User user = new User("John", "12345678", "password");
		community.addMember(user);
		List<User> copy = community.getCommunityMember();
		copy.clear();
		assertEquals(1, community.getCommunityMember().size());
	}

	@Test
	@DisplayName("addSession adds session to community")
	void testAddSessionAddsSession() {
		Booking booking = new Booking(java.time.LocalDateTime.now().plusDays(1));
		Session session = new Session(booking, community, 10, 50.0);
		community.addSession(session);
		assertEquals(1, community.getSessionList().size());
		assertTrue(community.getSessionList().contains(session));
	}

	@Test
	@DisplayName("getSessionList returns a copy")
	void testGetSessionListReturnsCopy() {
		Booking booking = new Booking(java.time.LocalDateTime.now().plusDays(1));
		Session session = new Session(booking, community, 10, 50.0);
		community.addSession(session);
		List<Session> copy = community.getSessionList();
		copy.clear();
		assertEquals(1, community.getSessionList().size());
	}

	@Test
	@DisplayName("findCommunity returns self when IDs match")
	void testFindCommunityReturnsSelfWhenIdsMatch() {
		String id = community.getCommunityId();
		Community found = community.findCommunity(id);
		assertNotNull(found);
		assertEquals(id, found.getCommunityId());
	}

	@Test
	@DisplayName("findCommunity returns null when IDs don't match")
	void testFindCommunityReturnsNullWhenIdsDontMatch() {
		Community found = community.findCommunity("nonexistent-id");
		assertNull(found);
	}

	@Test
	@DisplayName("notifyAllMembers triggers observer notifications")
	void testNotifyAllMembersTriggersNotifications() {
		TestObserver observer = new TestObserver();
		community.attach(observer);
		community.notifyAllMembers("Test message");
		assertEquals(1, observer.getUpdateCount());
		assertEquals("Test message", observer.getLastMessage());
	}

	@Test
	@DisplayName("Setters update fields correctly")
	void testSettersUpdateFields() {
		community.setName("New Name");
		community.setSportType("Football");
		community.setDescription("New description");
		assertEquals("New Name", community.getName());
		assertEquals("Football", community.getSportType());
		assertEquals("New description", community.getDescription());
	}

	@Test
	@DisplayName("Empty community has zero members and sessions")
	void testEmptyCommunityHasZeroMembersAndSessions() {
		assertEquals(0, community.getMemberCount());
		assertEquals(0, community.getSessionList().size());
	}

	// Helper observer for testing
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
