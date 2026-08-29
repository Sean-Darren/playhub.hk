package playhub;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.LocalDateTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class BookingServiceTest {

	private BookingService service;
	private LocalDateTime futureDateTime;
	private Court court;

	@BeforeEach
	void setUp() {
		service = new BookingService();
		futureDateTime = LocalDateTime.now().plusDays(1).withNano(0);
		court = new Court("Main Court", "Central", 150.0, "Basketball");
		service.registerCourt(court);
	}

	@Test
	@DisplayName("createBooking creates booking successfully")
	void testCreateBookingCreatesSuccessfully() {
		Booking booking = service.createBooking("user1", court.getCourtId(), futureDateTime);
		assertNotNull(booking);
		assertEquals("user1", booking.getUserId());
		assertEquals(court.getCourtId(), booking.getCourtId());
		assertEquals(futureDateTime, booking.getBookingDateTime());
		assertEquals(BookingStatus.PENDING, booking.getBookingStatus());
		assertEquals(1, court.getBookingList().size());
	}

	@Test
	@DisplayName("createBooking throws for unknown court")
	void testCreateBookingThrowsForUnknownCourt() {
		assertThrows(IllegalArgumentException.class, () ->
			service.createBooking("user1", "missing-court", futureDateTime));
	}

	@Test
	@DisplayName("createBooking throws when slot is already taken")
	void testCreateBookingThrowsWhenSlotTaken() {
		service.createBooking("user1", court.getCourtId(), futureDateTime);
		assertThrows(IllegalStateException.class, () ->
			service.createBooking("user2", court.getCourtId(), futureDateTime));
	}

	@Test
	@DisplayName("createBooking throws for null userId")
	void testCreateBookingThrowsForNullUserId() {
		assertThrows(IllegalArgumentException.class, () ->
			service.createBooking(null, court.getCourtId(), futureDateTime));
	}

	@Test
	@DisplayName("createBooking throws for null courtId")
	void testCreateBookingThrowsForNullCourtId() {
		assertThrows(IllegalArgumentException.class, () ->
			service.createBooking("user1", null, futureDateTime));
	}

	@Test
	@DisplayName("createBooking throws for null dateTime")
	void testCreateBookingThrowsForNullDateTime() {
		assertThrows(IllegalArgumentException.class, () ->
			service.createBooking("user1", court.getCourtId(), null));
	}

	@Test
	@DisplayName("createBooking throws for past dateTime")
	void testCreateBookingThrowsForPastDateTime() {
		LocalDateTime past = LocalDateTime.now().minusDays(1);
		assertThrows(IllegalArgumentException.class, () ->
			service.createBooking("user1", court.getCourtId(), past));
	}

	@Test
	@DisplayName("cancelBooking cancels existing booking")
	void testCancelBookingCancelsExisting() {
		Booking booking = service.createBooking("user1", court.getCourtId(), futureDateTime);
		service.cancelBooking(booking.getBookingId());
		assertEquals(BookingStatus.CANCELLED, booking.getBookingStatus());
		assertTrue(court.checkAvailability(futureDateTime));
	}

	@Test
	@DisplayName("cancelBooking throws for nonexistent booking")
	void testCancelBookingThrowsForNonexistent() {
		assertThrows(IllegalArgumentException.class, () ->
			service.cancelBooking("nonexistent"));
	}

	@Test
	@DisplayName("requestBookingApproval returns true when a matching court is free")
	void testRequestBookingApprovalReturnsTrueForFuture() {
		assertTrue(service.requestBookingApproval(futureDateTime, "Basketball"));
	}

	@Test
	@DisplayName("requestBookingApproval returns false when no courts are registered")
	void testRequestBookingApprovalReturnsFalseWhenNoCourts() {
		BookingService empty = new BookingService();
		assertFalse(empty.requestBookingApproval(futureDateTime, "Basketball"));
	}

	@Test
	@DisplayName("requestBookingApproval returns false for past date")
	void testRequestBookingApprovalReturnsFalseForPast() {
		LocalDateTime past = LocalDateTime.now().minusDays(1);
		assertFalse(service.requestBookingApproval(past, "Basketball"));
	}

	@Test
	@DisplayName("requestBookingApproval returns false for null dateTime")
	void testRequestBookingApprovalReturnsFalseForNullDateTime() {
		assertFalse(service.requestBookingApproval(null, "Basketball"));
	}

	@Test
	@DisplayName("requestBookingApproval returns false for null courtType")
	void testRequestBookingApprovalReturnsFalseForNullCourtType() {
		assertFalse(service.requestBookingApproval(futureDateTime, null));
	}

	@Test
	@DisplayName("requestBookingApproval returns false for empty courtType")
	void testRequestBookingApprovalReturnsFalseForEmptyCourtType() {
		assertFalse(service.requestBookingApproval(futureDateTime, ""));
	}

	@Test
	@DisplayName("getBooking returns existing booking")
	void testGetBookingReturnsExisting() {
		Booking booking = service.createBooking("user1", court.getCourtId(), futureDateTime);
		Booking retrieved = service.getBooking(booking.getBookingId());
		assertSame(booking, retrieved);
	}

	@Test
	@DisplayName("getBooking returns null for nonexistent booking")
	void testGetBookingReturnsNullForNonexistent() {
		assertNull(service.getBooking("nonexistent"));
	}

	@Test
	@DisplayName("registerCourt adds court to map")
	void testRegisterCourtAddsToMap() {
		assertTrue(service.getCourtMap().containsKey(court.getCourtId()));
	}

	@Test
	@DisplayName("registerCourt throws for null court")
	void testRegisterCourtThrowsForNull() {
		assertThrows(IllegalArgumentException.class, () -> service.registerCourt(null));
	}

	@Test
	@DisplayName("Multiple bookings tracked correctly")
	void testMultipleBookingsTrackedCorrectly() {
		Court court2 = new Court("Court 2", "Loc 2", 100.0, "Tennis");
		Court court3 = new Court("Court 3", "Loc 3", 100.0, "Football");
		service.registerCourt(court2);
		service.registerCourt(court3);
		service.createBooking("user1", court.getCourtId(), LocalDateTime.now().plusDays(1).withNano(0));
		service.createBooking("user2", court2.getCourtId(), LocalDateTime.now().plusDays(2).withNano(0));
		service.createBooking("user3", court3.getCourtId(), LocalDateTime.now().plusDays(3).withNano(0));
		assertEquals(3, service.getBookingMap().size());
	}

	@Test
	@DisplayName("getBookingMap returns a copy")
	void testGetBookingMapReturnsCopy() {
		service.createBooking("user1", court.getCourtId(), futureDateTime);
		Map<String, Booking> copy = service.getBookingMap();
		copy.clear();
		assertEquals(1, service.getBookingMap().size());
	}

	@Test
	@DisplayName("getCourtMap returns a copy")
	void testGetCourtMapReturnsCopy() {
		Map<String, Court> copy = service.getCourtMap();
		copy.clear();
		assertEquals(1, service.getCourtMap().size());
	}
}
