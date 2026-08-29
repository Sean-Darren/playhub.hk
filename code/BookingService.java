package playhub;

import java.util.*;
import java.time.LocalDateTime;

public class BookingService {

	private final Map<String, Booking> bookingMap = new HashMap<>();
	private final Map<String, Court> courtMap = new HashMap<>();

	public boolean requestBookingApproval(LocalDateTime dateTime, String courtType) {
		if (dateTime == null || courtType == null || courtType.isEmpty()) {
			return false;
		}
		if (!dateTime.isAfter(LocalDateTime.now())) {
			return false;
		}
		for (Court court : courtMap.values()) {
			if (courtType.equalsIgnoreCase(court.getSportsDetail())
					&& court.checkAvailability(dateTime)) {
				return true;
			}
		}
		return false;
	}

	public Booking createBooking(String userId, String courtId, LocalDateTime dateTime) {
		if (userId == null || courtId == null || dateTime == null) {
			throw new IllegalArgumentException("userId, courtId, and dateTime cannot be null");
		}
		if (dateTime.isBefore(LocalDateTime.now())) {
			throw new IllegalArgumentException("Cannot book a court in the past");
		}
		Court court = courtMap.get(courtId);
		if (court == null) {
			throw new IllegalArgumentException("Court not found: " + courtId);
		}
		if (!court.checkAvailability(dateTime)) {
			throw new IllegalStateException("Court is not available at the requested time");
		}
		Booking booking = new Booking(dateTime, userId, courtId);
		bookingMap.put(booking.getBookingId(), booking);
		court.addBooking(booking);
		return booking;
	}

	public void cancelBooking(String bookingId) {
		Booking booking = bookingMap.get(bookingId);
		if (booking == null) {
			throw new IllegalArgumentException("Booking not found: " + bookingId);
		}
		booking.setBookingStatus(BookingStatus.CANCELLED);
	}

	public Booking getBooking(String bookingId) {
		return bookingMap.get(bookingId);
	}

	public void registerCourt(Court court) {
		if (court == null) {
			throw new IllegalArgumentException("Court cannot be null");
		}
		courtMap.put(court.getCourtId(), court);
	}

	public Map<String, Booking> getBookingMap() {
		return new HashMap<>(bookingMap);
	}

	public Map<String, Court> getCourtMap() {
		return new HashMap<>(courtMap);
	}

}
