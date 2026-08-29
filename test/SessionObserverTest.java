package playhub;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

class SessionObserverTest {

	@Test
	@DisplayName("SessionObserver is abstract")
	void testSessionObserverIsAbstract() {
		// Verify that we cannot instantiate SessionObserver directly
		assertThrows(java.lang.InstantiationException.class, () -> {
			// This will fail at runtime due to abstract class
			SessionObserver.class.getDeclaredConstructor().newInstance();
		});
	}

	@Test
	@DisplayName("Can create concrete subclass")
	void testCanCreateConcreteSubclass() {
		TestObserver observer = new TestObserver();
		assertNotNull(observer);
	}

	@Test
	@DisplayName("update method can be implemented")
	void testUpdateMethodCanBeImplemented() {
		TestObserver observer = new TestObserver();
		Community community = new Community("Test", "Basketball", "Desc", new java.util.Date());
		observer.update(community, "Test message");
		assertEquals("Test message", observer.getLastMessage());
		assertNotNull(observer.getLastObserved());
	}

	@Test
	@DisplayName("update can be called multiple times")
	void testUpdateCanBeCalledMultipleTimes() {
		TestObserver observer = new TestObserver();
		Community community = new Community("Test", "Basketball", "Desc", new java.util.Date());
		observer.update(community, "Msg 1");
		observer.update(community, "Msg 2");
		observer.update(community, "Msg 3");
		assertEquals(3, observer.getUpdateCount());
		assertEquals("Msg 3", observer.getLastMessage());
	}

	// Helper class for testing
	static class TestObserver extends SessionObserver {
		private int updateCount = 0;
		private String lastMessage = "";
		private Observable lastObserved = null;

		@Override
		public void update(Observable observed, String message) {
			updateCount++;
			lastMessage = message;
			lastObserved = observed;
		}

		public int getUpdateCount() { return updateCount; }
		public String getLastMessage() { return lastMessage; }
		public Observable getLastObserved() { return lastObserved; }
	}
}
