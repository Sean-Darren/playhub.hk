package playhub;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class NotifyServiceTest {

	private NotifyService service;

	@BeforeEach
	void setUp() {
		service = new NotifyService();
	}

	@Test
	@DisplayName("notify adds message for user")
	void testNotifyAddsMessageForUser() {
		service.notify("Test message", "user1");
		List<String> notifications = service.getNotifications("user1");
		assertEquals(1, notifications.size());
		assertEquals("Test message", notifications.get(0));
	}

	@Test
	@DisplayName("notify accumulates messages for same user")
	void testNotifyAccumulatesMessages() {
		service.notify("Message 1", "user1");
		service.notify("Message 2", "user1");
		service.notify("Message 3", "user1");
		List<String> notifications = service.getNotifications("user1");
		assertEquals(3, notifications.size());
	}

	@Test
	@DisplayName("notify throws for null message")
	void testNotifyThrowsForNullMessage() {
		assertThrows(IllegalArgumentException.class, () ->
			service.notify(null, "user1"));
	}

	@Test
	@DisplayName("notify throws for null userId")
	void testNotifyThrowsForNullUserId() {
		assertThrows(IllegalArgumentException.class, () ->
			service.notify("Test", null));
	}

	@Test
	@DisplayName("notifyAll adds message for booking")
	void testNotifyAllAddsMessageForBooking() {
		service.notifyAll("booking1", "Booking update");
		List<String> notifications = service.getNotifications("booking1");
		assertEquals(1, notifications.size());
		assertEquals("Booking update", notifications.get(0));
	}

	@Test
	@DisplayName("notifyAll accumulates messages")
	void testNotifyAllAccumulatesMessages() {
		service.notifyAll("booking1", "Update 1");
		service.notifyAll("booking1", "Update 2");
		List<String> notifications = service.getNotifications("booking1");
		assertEquals(2, notifications.size());
	}

	@Test
	@DisplayName("notifyAll throws for null bookingId")
	void testNotifyAllThrowsForNullBookingId() {
		assertThrows(IllegalArgumentException.class, () ->
			service.notifyAll(null, "Test"));
	}

	@Test
	@DisplayName("notifyAll throws for null message")
	void testNotifyAllThrowsForNullMessage() {
		assertThrows(IllegalArgumentException.class, () ->
			service.notifyAll("booking1", null));
	}

	@Test
	@DisplayName("getNotifications returns empty list for unknown user")
	void testGetNotificationsReturnsEmptyForUnknownUser() {
		List<String> notifications = service.getNotifications("unknown");
		assertTrue(notifications.isEmpty());
	}

	@Test
	@DisplayName("getNotifications returns a copy")
	void testGetNotificationsReturnsCopy() {
		service.notify("Test", "user1");
		List<String> copy = service.getNotifications("user1");
		copy.clear();
		assertEquals(1, service.getNotifications("user1").size());
	}

	@Test
	@DisplayName("clearNotifications removes all notifications for user")
	void testClearNotificationsRemovesAllForUser() {
		service.notify("Msg 1", "user1");
		service.notify("Msg 2", "user1");
		service.clearNotifications("user1");
		assertTrue(service.getNotifications("user1").isEmpty());
	}

	@Test
	@DisplayName("clearNotifications only affects specified user")
	void testClearNotificationsOnlyAffectsSpecifiedUser() {
		service.notify("Msg", "user1");
		service.notify("Msg", "user2");
		service.clearNotifications("user1");
		assertTrue(service.getNotifications("user1").isEmpty());
		assertEquals(1, service.getNotifications("user2").size());
	}

	@Test
	@DisplayName("getUserNotifications returns all notifications")
	void testGetUserNotificationsReturnsAll() {
		service.notify("Msg 1", "user1");
		service.notify("Msg 2", "user2");
		Map<String, List<String>> all = service.getUserNotifications();
		assertEquals(2, all.size());
	}

	@Test
	@DisplayName("getUserNotifications returns a copy")
	void testGetUserNotificationsReturnsCopy() {
		service.notify("Msg", "user1");
		Map<String, List<String>> copy = service.getUserNotifications();
		copy.clear();
		assertEquals(1, service.getUserNotifications().size());
	}

	@Test
	@DisplayName("Empty service has no notifications")
	void testEmptyServiceHasNoNotifications() {
		assertTrue(service.getUserNotifications().isEmpty());
	}
}
