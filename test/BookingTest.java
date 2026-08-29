package playhub;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BookingTest {

	private Booking booking;
	private LocalDateTime futureDateTime;

	@BeforeEach
	void setUp() {
		futureDateTime = LocalDateTime.now().plusDays(1);
		booking = new Booking(futureDateTime);
	}

	@Test
	@DisplayName("Constructor initializes with PENDING status")
	void testConstructorInitializesWithPendingStatus() {
		assertNotNull(booking.getBookingId());
		assertEquals(BookingStatus.PENDING, booking.getBookingStatus());
		assertEquals(futureDateTime, booking.getBookingDateTime());
	}

	@Test
	@DisplayName("getBookingId returns non-null unique ID")
	void testGetBookingIdReturnsNonNullUniqueId() {
		Booking anotherBooking = new Booking(LocalDateTime.now().plusDays(2));
		assertNotEquals(booking.getBookingId(), anotherBooking.getBookingId());
	}

	@Test
	@DisplayName("Constructor with userId and courtId sets fields correctly")
	void testConstructorWithUserIdAndCourtId() {
		Booking fullBooking = new Booking(futureDateTime, "user123", "court456");
		assertEquals("user123", fullBooking.getUserId());
		assertEquals("court456", fullBooking.getCourtId());
		assertEquals(BookingStatus.PENDING, fullBooking.getBookingStatus());
	}

	@Test
	@DisplayName("setBookingStatus changes status correctly")
	void testSetBookingStatusChangesStatus() {
		booking.setBookingStatus(BookingStatus.CONFIRMED);
		assertEquals(BookingStatus.CONFIRMED, booking.getBookingStatus());

		booking.setBookingStatus(BookingStatus.HAS_SESSION);
		assertEquals(BookingStatus.HAS_SESSION, booking.getBookingStatus());

		booking.setBookingStatus(BookingStatus.CANCELLED);
		assertEquals(BookingStatus.CANCELLED, booking.getBookingStatus());
	}

	@Test
	@DisplayName("setUserId and getUserId work correctly")
	void testSetUserIdAndGetUserId() {
		booking.setUserId("newUser");
		assertEquals("newUser", booking.getUserId());
	}

	@Test
	@DisplayName("setCourtId and getCourtId work correctly")
	void testSetCourtIdAndGetCourtId() {
		booking.setCourtId("newCourt");
		assertEquals("newCourt", booking.getCourtId());
	}

	@Test
	@DisplayName("Multiple bookings have different IDs")
	void testMultipleBookingsHaveDifferentIds() {
		Booking b1 = new Booking(LocalDateTime.now().plusHours(1));
		Booking b2 = new Booking(LocalDateTime.now().plusHours(2));
		Booking b3 = new Booking(LocalDateTime.now().plusHours(3));
		assertNotEquals(b1.getBookingId(), b2.getBookingId());
		assertNotEquals(b2.getBookingId(), b3.getBookingId());
		assertNotEquals(b1.getBookingId(), b3.getBookingId());
	}

	@Test
	@DisplayName("getBookingDateTime returns the set value")
	void testGetBookingDateTimeReturnsSetValue() {
		LocalDateTime newTime = LocalDateTime.now().plusDays(5);
		booking = new Booking(newTime);
		assertEquals(newTime, booking.getBookingDateTime());
	}
}
