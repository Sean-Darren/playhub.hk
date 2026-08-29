package playhub;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

class SessionStatusTest {

	@Test
	@DisplayName("All expected enum values exist")
	void testAllExpectedEnumValuesExist() {
		assertNotNull(SessionStatus.valueOf("CANCELLED"));
		assertNotNull(SessionStatus.valueOf("PENDING"));
		assertNotNull(SessionStatus.valueOf("CONFIRMED"));
	}

	@Test
	@DisplayName("values() returns exactly 3 values")
	void testValuesReturnsExactlyThreeValues() {
		SessionStatus[] values = SessionStatus.values();
		assertEquals(3, values.length);
	}

	@Test
	@DisplayName("ordinal values are correct")
	void testOrdinalValuesAreCorrect() {
		assertEquals(0, SessionStatus.CANCELLED.ordinal());
		assertEquals(1, SessionStatus.PENDING.ordinal());
		assertEquals(2, SessionStatus.CONFIRMED.ordinal());
	}
}
