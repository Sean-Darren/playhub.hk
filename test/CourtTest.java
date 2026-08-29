package playhub;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CourtTest {

	private Court court;

	@BeforeEach
	void setUp() {
		court = new Court("Central Court", "Central, HK", 150.0, "Basketball");
	}

	@Test
	@DisplayName("Default constructor generates court ID")
	void testDefaultConstructorGeneratesCourtId() {
		Court defaultCourt = new Court();
		assertNotNull(defaultCourt.getCourtId());
	}

	@Test
	@DisplayName("Parameterized constructor sets all fields")
	void testParameterizedConstructorSetsAllFields() {
		assertEquals("Central Court", court.getName());
		assertEquals("Central, HK", court.getLocation());
		assertEquals(150.0, court.getHourlyRate());
		assertEquals("Basketball", court.getSportsDetail());
		assertNotNull(court.getCourtId());
	}

	@Test
	@DisplayName("checkAvailability returns true for new court")
	void testCheckAvailabilityReturnsTrueForNewCourt() {
		assertTrue(court.checkAvailability(LocalDateTime.now().plusDays(1)));
	}

	@Test
	@DisplayName("checkAvailability returns false when court is marked unavailable")
	void testCheckAvailabilityReturnsFalseWhenUnavailable() {
		court.setAvailable(false);
		assertFalse(court.checkAvailability(LocalDateTime.now().plusDays(1)));
	}

	@Test
	@DisplayName("checkAvailability returns false for booked slot")
	void testCheckAvailabilityReturnsFalseForBookedSlot() {
		LocalDateTime dateTime = LocalDateTime.now().plusDays(1);
		Booking booking = new Booking(dateTime, "user1", court.getCourtId());
		court.addBooking(booking);
		assertFalse(court.checkAvailability(dateTime));
	}

	@Test
	@DisplayName("checkAvailability returns true for different time than booked")
	void testCheckAvailabilityReturnsTrueForDifferentTime() {
		LocalDateTime bookedTime = LocalDateTime.now().plusDays(1);
		Booking booking = new Booking(bookedTime, "user1", court.getCourtId());
		court.addBooking(booking);
		assertTrue(court.checkAvailability(LocalDateTime.now().plusDays(2)));
	}

	@Test
	@DisplayName("checkAvailability returns true for cancelled booking")
	void testCheckAvailabilityReturnsTrueForCancelledBooking() {
		LocalDateTime dateTime = LocalDateTime.now().plusDays(1);
		Booking booking = new Booking(dateTime, "user1", court.getCourtId());
		booking.setBookingStatus(BookingStatus.CANCELLED);
		court.addBooking(booking);
		assertTrue(court.checkAvailability(dateTime));
	}

	@Test
	@DisplayName("addBooking adds booking to list")
	void testAddBookingAddsBookingToList() {
		Booking booking = new Booking(LocalDateTime.now().plusDays(1), "user1", court.getCourtId());
		court.addBooking(booking);
		List<Booking> bookings = court.getBookingList();
		assertEquals(1, bookings.size());
		assertEquals(booking, bookings.get(0));
	}

	@Test
	@DisplayName("getBookingList returns a copy")
	void testGetBookingListReturnsCopy() {
		Booking booking = new Booking(LocalDateTime.now().plusDays(1), "user1", court.getCourtId());
		court.addBooking(booking);
		List<Booking> copy = court.getBookingList();
		copy.clear();
		assertEquals(1, court.getBookingList().size());
	}

	@Test
	@DisplayName("Setters update fields correctly")
	void testSettersUpdateFields() {
		court.setName("New Name");
		court.setLocation("New Location");
		court.setHourlyRate(200.0);
		court.setSportsDetail("Football");
		assertEquals("New Name", court.getName());
		assertEquals("New Location", court.getLocation());
		assertEquals(200.0, court.getHourlyRate());
		assertEquals("Football", court.getSportsDetail());
	}

	@Test
	@DisplayName("isAvailable and setAvailable work correctly")
	void testIsAvailableAndSetAvailable() {
		assertTrue(court.isAvailable());
		court.setAvailable(false);
		assertFalse(court.isAvailable());
		court.setAvailable(true);
		assertTrue(court.isAvailable());
	}
}
