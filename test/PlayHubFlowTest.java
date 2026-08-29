package playhub;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class PlayHubFlowTest {

	@BeforeEach
	void setUp() {
		PaymentAdministrationSystem.resetInstance();
	}

	@AfterEach
	void tearDown() {
		PaymentAdministrationSystem.resetInstance();
	}

	@Test
	@DisplayName("Main path: book court, create session, join, pay, cancel frees slot")
	void testEndToEndBookingSessionAndPayment() {
		CourtOwner courtOwner = new CourtOwner("Owner", "98001111", "pass");
		Court court = new Court("Central Court", "Kowloon", 150.0, "Badminton");
		courtOwner.registerCourt(court);

		CommunityHost host = new CommunityHost("Host", "98002222", "pass");
		BookingService bookingService = new BookingService();
		SessionService sessionService = new SessionService();
		bookingService.registerCourt(court);
		host.setBookingService(bookingService);
		host.setSessionService(sessionService);
		host.createCommunity("Badminton HK", "Badminton", "Weekend games");
		Community community = host.getOwnedCommunity().get(0);

		User alice = new User("Alice", "98003333", "pw1");
		User charlie = new User("Charlie", "98004444", "pw2");
		new TopUp("Alipay", 500.0).processTopUp(alice.getWallet());
		new TopUp("Octopus", 300.0).processTopUp(charlie.getWallet());
		community.addMember(alice);
		community.addMember(charlie);

		LocalDateTime sessionTime = LocalDateTime.now().plusDays(3).withHour(10).withMinute(0).withSecond(0).withNano(0);
		assertTrue(bookingService.requestBookingApproval(sessionTime, "Badminton"));
		host.createBooking(community.getCommunityId(), sessionTime, court.getCourtId());
		Booking booking = host.getBookedCourts().get(0);
		assertFalse(court.checkAvailability(sessionTime));

		Session session = host.createSession(booking, 6, 80.0, community.getCommunityId(), "Weekend Session");
		assertEquals(BookingStatus.HAS_SESSION, booking.getBookingStatus());
		assertSame(session, sessionService.getSession(session.getSessionId()));

		alice.joinSession(session);
		charlie.joinSession(session);
		assertEquals(2, session.getParticipantCount());
		assertEquals(160.0, session.calculateTotalFee());

		PaymentAdministrationSystem pas = PaymentAdministrationSystem.getInstance();
		pas.processPayment(session.getFeePerPerson(), alice, host, "Wallet");
		pas.processPayment(session.getFeePerPerson(), charlie, host, "Wallet");
		pas.processPayment(court.getHourlyRate(), host, courtOwner, "Wallet");

		assertEquals(420.0, alice.getWalletBalance());
		assertEquals(220.0, charlie.getWalletBalance());
		assertEquals(10.0, host.getWalletBalance());
		assertEquals(150.0, courtOwner.getWalletBalance());
		assertEquals(3, pas.getPaymentHistory().size());

		LocalDateTime later = LocalDateTime.now().plusDays(7).withNano(0);
		host.createBooking(community.getCommunityId(), later, court.getCourtId());
		Booking extra = host.getBookedCourts().get(1);
		host.cancelBooking(extra.getBookingId());
		assertEquals(BookingStatus.CANCELLED, extra.getBookingStatus());
		assertTrue(court.checkAvailability(later));
	}
}
