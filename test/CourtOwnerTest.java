package playhub;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CourtOwnerTest {

	private CourtOwner owner;

	@BeforeEach
	void setUp() {
		owner = new CourtOwner("Court Owner", "12345678", "password");
	}

	@Test
	@DisplayName("Constructor initializes as User with correct fields")
	void testConstructorInitializesAsUser() {
		assertEquals("Court Owner", owner.getName());
		assertEquals("12345678", owner.getPhoneNum());
		assertNotNull(owner.getUserId());
	}

	@Test
	@DisplayName("registerCourt adds court to owned courts")
	void testRegisterCourtAddsCourt() {
		Court court = new Court("Main Court", "Central", 150.0, "Basketball");
		owner.registerCourt(court);
		List<Court> courts = owner.getOwnedCourts();
		assertEquals(1, courts.size());
		assertTrue(courts.contains(court));
	}

	@Test
	@DisplayName("registerCourt throws for null court")
	void testRegisterCourtThrowsForNull() {
		assertThrows(IllegalArgumentException.class, () -> owner.registerCourt(null));
	}

	@Test
	@DisplayName("manageCourtAvailability updates court availability")
	void testManageCourtAvailabilityUpdatesCourt() {
		Court court = new Court("Main Court", "Central", 150.0, "Basketball");
		owner.manageCourtAvailability(court, false);
		assertFalse(court.isAvailable());
		owner.manageCourtAvailability(court, true);
		assertTrue(court.isAvailable());
	}

	@Test
	@DisplayName("manageCourtAvailability throws for null court")
	void testManageCourtAvailabilityThrowsForNull() {
		assertThrows(IllegalArgumentException.class, () -> owner.manageCourtAvailability(null, false));
	}

	@Test
	@DisplayName("receivePaymentToWallet adds funds")
	void testReceivePaymentToWalletAddsFunds() {
		owner.receivePaymentToWallet(500.0);
		assertEquals(500.0, owner.getWalletBalance());
	}

	@Test
	@DisplayName("receivePaymentToWallet throws for non-positive amount")
	void testReceivePaymentToWalletThrowsForZero() {
		assertThrows(IllegalArgumentException.class, () -> owner.receivePaymentToWallet(0));
	}

	@Test
	@DisplayName("getOwnedCourts returns a copy")
	void testGetOwnedCourtsReturnsCopy() {
		Court court = new Court("Main Court", "Central", 150.0, "Basketball");
		owner.registerCourt(court);
		List<Court> copy = owner.getOwnedCourts();
		copy.clear();
		assertEquals(1, owner.getOwnedCourts().size());
	}

	@Test
	@DisplayName("Empty courts list initially")
	void testEmptyCourtsListInitially() {
		assertTrue(owner.getOwnedCourts().isEmpty());
	}

	@Test
	@DisplayName("Multiple courts can be registered")
	void testMultipleCourtsCanBeRegistered() {
		Court court1 = new Court("Court 1", "Location 1", 100.0, "Basketball");
		Court court2 = new Court("Court 2", "Location 2", 200.0, "Football");
		Court court3 = new Court("Court 3", "Location 3", 150.0, "Tennis");
		owner.registerCourt(court1);
		owner.registerCourt(court2);
		owner.registerCourt(court3);
		assertEquals(3, owner.getOwnedCourts().size());
	}
}
