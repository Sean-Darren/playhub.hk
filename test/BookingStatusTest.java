package playhub;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

class BookingStatusTest {

	@Test
	@DisplayName("All expected enum values exist")
	void testAllExpectedEnumValuesExist() {
		assertNotNull(BookingStatus.valueOf("CANCELLED"));
		assertNotNull(BookingStatus.valueOf("PENDING"));
		assertNotNull(BookingStatus.valueOf("CONFIRMED"));
		assertNotNull(BookingStatus.valueOf("HAS_SESSION"));
	}

	@Test
	@DisplayName("values() returns exactly 4 values")
	void testValuesReturnsExactlyFourValues() {
		BookingStatus[] values = BookingStatus.values();
		assertEquals(4, values.length);
	}

	@Test
	@DisplayName("ordinal values are correct")
	void testOrdinalValuesAreCorrect() {
		assertEquals(0, BookingStatus.CANCELLED.ordinal());
		assertEquals(1, BookingStatus.PENDING.ordinal());
		assertEquals(2, BookingStatus.CONFIRMED.ordinal());
		assertEquals(3, BookingStatus.HAS_SESSION.ordinal());
	}
}
