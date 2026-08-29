package playhub;

import java.util.ArrayList;
import java.util.List;

public class CourtOwner extends User {

	private final List<Court> ownedCourts = new ArrayList<>();

	public CourtOwner(String name, String phoneNum, String password) {
		super(name, phoneNum, password);
	}

	public void registerCourt(Court court) {
		if (court == null) {
			throw new IllegalArgumentException("Court cannot be null");
		}
		ownedCourts.add(court);
	}

	public void manageCourtAvailability(Court court, boolean available) {
		if (court == null) {
			throw new IllegalArgumentException("Court cannot be null");
		}
		court.setAvailable(available);
	}

	public void receivePaymentToWallet(double amount) {
		getWallet().addFunds(amount, "Court Revenue");
	}

	public List<Court> getOwnedCourts() {
		return new ArrayList<>(ownedCourts);
	}

}
