package playhub;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Court {

	private String courtId;
	private String name;
	private String location;
	private double hourlyRate;
	private final List<Booking> bookingList = new ArrayList<>();
	private String sportsDetail;
	private boolean available = true;

	public Court() {
		this.courtId = UUID.randomUUID().toString();
	}

	public Court(String name, String location, double hourlyRate, String sportsDetail) {
		this.courtId = UUID.randomUUID().toString();
		this.name = name;
		this.location = location;
		this.hourlyRate = hourlyRate;
		this.sportsDetail = sportsDetail;
	}

	public boolean checkAvailability(java.time.LocalDateTime dateTime) {
		if (!available) {
			return false;
		}
		for (Booking booking : bookingList) {
			if (booking.getBookingDateTime().equals(dateTime)
					&& booking.getBookingStatus() != BookingStatus.CANCELLED) {
				return false;
			}
		}
		return true;
	}

	public void addBooking(Booking booking) {
		this.bookingList.add(booking);
	}

	public List<Booking> getBookingList() {
		return new ArrayList<>(bookingList);
	}

	public String getCourtId() {
		return courtId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public double getHourlyRate() {
		return hourlyRate;
	}

	public void setHourlyRate(double hourlyRate) {
		this.hourlyRate = hourlyRate;
	}

	public String getSportsDetail() {
		return sportsDetail;
	}

	public void setSportsDetail(String sportsDetail) {
		this.sportsDetail = sportsDetail;
	}

	public boolean isAvailable() {
		return available;
	}

	public void setAvailable(boolean available) {
		this.available = available;
	}

}
