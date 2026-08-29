package playhub;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ObservableTest {

	private TestObservable observable;
	private TestObserver observer1;
	private TestObserver observer2;

	@BeforeEach
	void setUp() {
		observable = new TestObservable();
		observer1 = new TestObserver();
		observer2 = new TestObserver();
	}

	@Test
	@DisplayName("attach adds observer")
	void testAttachAddsObserver() {
		observable.attach(observer1);
		assertEquals(1, observable.getObservers().size());
		assertTrue(observable.getObservers().contains(observer1));
	}

	@Test
	@DisplayName("attach does not add duplicate observer")
	void testAttachDoesNotAddDuplicate() {
		observable.attach(observer1);
		observable.attach(observer1);
		assertEquals(1, observable.getObservers().size());
	}

	@Test
	@DisplayName("attach adds multiple observers")
	void testAttachAddsMultipleObservers() {
		observable.attach(observer1);
		observable.attach(observer2);
		assertEquals(2, observable.getObservers().size());
	}

	@Test
	@DisplayName("detach removes observer")
	void testDetachRemovesObserver() {
		observable.attach(observer1);
		observable.detach(observer1);
		assertEquals(0, observable.getObservers().size());
	}

	@Test
	@DisplayName("detach non-existent observer does nothing")
	void testDetachNonExistentDoesNothing() {
		observable.attach(observer1);
		observable.detach(observer2);
		assertEquals(1, observable.getObservers().size());
	}

	@Test
	@DisplayName("notify sends message to all observers")
	void testNotifySendsMessageToAll() {
		observable.attach(observer1);
		observable.attach(observer2);
		observable.notify("Test message");
		assertEquals(1, observer1.getUpdateCount());
		assertEquals(1, observer2.getUpdateCount());
		assertEquals("Test message", observer1.getLastMessage());
		assertEquals("Test message", observer2.getLastMessage());
	}

	@Test
	@DisplayName("notify does nothing with no observers")
	void testNotifyDoesNothingWithNoObservers() {
		assertDoesNotThrow(() -> observable.notify("Test message"));
	}

	@Test
	@DisplayName("notify only sends to attached observers")
	void testNotifyOnlySendsToAttached() {
		observable.attach(observer1);
		observable.notify("Test message");
		assertEquals(1, observer1.getUpdateCount());
		assertEquals(0, observer2.getUpdateCount());
	}

	@Test
	@DisplayName("detached observer no longer receives notifications")
	void testDetachedObserverNoLongerReceives() {
		observable.attach(observer1);
		observable.attach(observer2);
		observable.notify("Message 1");
		observable.detach(observer1);
		observable.notify("Message 2");
		assertEquals(1, observer1.getUpdateCount());
		assertEquals(2, observer2.getUpdateCount());
	}

	@Test
	@DisplayName("getObservers returns a copy")
	void testGetObserversReturnsCopy() {
		observable.attach(observer1);
		List<SessionObserver> copy = observable.getObservers();
		copy.clear();
		assertEquals(1, observable.getObservers().size());
	}

	@Test
	@DisplayName("Initially has no observers")
	void testInitiallyHasNoObservers() {
		assertTrue(observable.getObservers().isEmpty());
	}

	// Helper classes for testing
	static class TestObservable extends Observable {
		// Inherits attach, detach, notify from Observable
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
