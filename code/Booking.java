package playhub;

import java.time.LocalDateTime;
import java.util.UUID;

public class Booking {

	private String bookingId;
	private LocalDateTime bookingDateTime;
	private BookingStatus bookingStatus;
	private String userId;
	private String courtId;

	public Booking(LocalDateTime bookingDateTime) {
		this.bookingId = UUID.randomUUID().toString();
		this.bookingDateTime = bookingDateTime;
		this.bookingStatus = BookingStatus.PENDING;
	}

	public Booking(LocalDateTime bookingDateTime, String userId, String courtId) {
		this.bookingId = UUID.randomUUID().toString();
		this.bookingDateTime = bookingDateTime;
		this.bookingStatus = BookingStatus.PENDING;
		this.userId = userId;
		this.courtId = courtId;
	}

	public String getBookingId() {
		return bookingId;
	}

	public BookingStatus getBookingStatus() {
		return bookingStatus;
	}

	public LocalDateTime getBookingDateTime() {
		return bookingDateTime;
	}

	public String getUserId() {
		return userId;
	}

	public String getCourtId() {
		return courtId;
	}

	public void setBookingStatus(BookingStatus bookingStatus) {
		this.bookingStatus = bookingStatus;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public void setCourtId(String courtId) {
		this.courtId = courtId;
	}

	public void setBookingDateTime(LocalDateTime bookingDateTime) {
		this.bookingDateTime = bookingDateTime;
	}

}
