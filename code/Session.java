package playhub;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Session {

	private String sessionId;
	private String title;
	private LocalDateTime dateTime;
	private int capacity;
	private double feePerPerson;
	private SessionStatus status;
	private final List<User> participantList = new ArrayList<>();
	private Booking booking;
	private Community community;

	public Session(Booking booking, Community community, int capacity, double feePerPerson) {
		this.sessionId = UUID.randomUUID().toString();
		this.booking = booking;
		this.community = community;
		this.capacity = capacity;
		this.feePerPerson = feePerPerson;
		this.status = SessionStatus.PENDING;
		this.dateTime = booking != null ? booking.getBookingDateTime() : null;
	}

	public List<User> getParticipantList() {
		return new ArrayList<>(participantList);
	}

	public void addParticipant(User user) {
		if (participantList.size() >= capacity) {
			throw new IllegalStateException("Session is full");
		}
		if (participantList.contains(user)) {
			throw new IllegalStateException("User already in session");
		}
		participantList.add(user);
	}

	public void removeParticipant(User user) {
		participantList.remove(user);
	}

	public double calculateTotalFee() {
		return feePerPerson * participantList.size();
	}

	public boolean isFull() {
		return participantList.size() >= capacity;
	}

	public LocalDateTime getSessionDateTime() {
		return dateTime;
	}

	public String getSessionId() {
		return sessionId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setDateTime(LocalDateTime dateTime) {
		this.dateTime = dateTime;
	}

	public int getCapacity() {
		return capacity;
	}

	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}

	public double getFeePerPerson() {
		return feePerPerson;
	}

	public void setFeePerPerson(double feePerPerson) {
		this.feePerPerson = feePerPerson;
	}

	public SessionStatus getStatus() {
		return status;
	}

	public void setStatus(SessionStatus status) {
		this.status = status;
	}

	public Booking getBooking() {
		return booking;
	}

	public void setBooking(Booking booking) {
		this.booking = booking;
	}

	public Community getCommunity() {
		return community;
	}

	public void setCommunity(Community community) {
		this.community = community;
	}

	public int getParticipantCount() {
		return participantList.size();
	}

}
