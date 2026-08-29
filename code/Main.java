package playhub;

import java.time.LocalDateTime;

public class Main {

    public static void main(String[] args) {

        PaymentAdministrationSystem.resetInstance();
        System.out.println("=== PlayHub Simulation ===\n");

        //1. Setup: Court Owner registers a court
        System.out.println("--- [1] Court Owner Setup ---");
        CourtOwner courtOwner = new CourtOwner("Alice (Court Owner)", "98001111", "pass123");
        Court court = new Court("Central Badminton Court", "Kowloon Bay", 150.0, "Badminton");
        courtOwner.registerCourt(court);
        System.out.println("Court registered: " + court.getName() + " @ " + court.getLocation());
        System.out.println("Hourly rate: HKD " + court.getHourlyRate());

        //2. Community Host creates a community
        System.out.println("\n--- [2] Community Host Setup ---");
        CommunityHost host = new CommunityHost("Bob (Host)", "98002222", "pass456");
        host.createCommunity("Badminton Lovers HK", "Badminton", "Casual badminton sessions every weekend");
        Community community = host.getOwnedCommunity().get(0);
        System.out.println("Community created: " + community.getName());
        System.out.println("Sport type: " + community.getSportType());
        System.out.println("Host auto-joined as a member.");

        //3. Regular users register and top up wallets
        System.out.println("\n--- [3] User Registration & Wallet Top-Up ---");
        User alice = new User("Alice", "98003333", "pw1");
        User charlie = new User("Charlie", "98004444", "pw2");

        TopUp topUp1 = new TopUp("Alipay", 500.0);
        topUp1.processTopUp(alice.getWallet());

        TopUp topUp2 = new TopUp("Octopus", 300.0);
        topUp2.processTopUp(charlie.getWallet());

        System.out.println(alice.getName() + " wallet balance: HKD " + alice.getWalletBalance());
        System.out.println(charlie.getName() + " wallet balance: HKD " + charlie.getWalletBalance());

        //4. Users join the community (Observer pattern)
        System.out.println("\n--- [4] Users Join Community ---");
        community.addMember(alice);
        community.addMember(charlie);
        System.out.println("Members in community: " + community.getMemberCount());
        community.getCommunityMember().forEach(m -> System.out.println("  - " + m.getName()));

        // ── 5. Host books a court ────────────────────────────────────────────
        System.out.println("\n--- [5] Host Books a Court ---");
        BookingService bookingService = new BookingService();
        bookingService.registerCourt(court);
        host.setBookingService(bookingService);
        SessionService sessionService = new SessionService();
        host.setSessionService(sessionService);

        LocalDateTime sessionTime = LocalDateTime.now().plusDays(3).withHour(10).withMinute(0).withSecond(0).withNano(0);
        boolean approved = bookingService.requestBookingApproval(sessionTime, "Badminton");
        System.out.println("Booking approval: " + (approved ? "Approved" : "Rejected"));

        host.createBooking(community.getCommunityId(), sessionTime, court.getCourtId());
        Booking booking = host.getBookedCourts().get(0);
        System.out.println("Booking created. Status: " + booking.getBookingStatus());
        System.out.println("Booking ID: " + booking.getBookingId());
        System.out.println("Court bookings recorded: " + court.getBookingList().size());
        System.out.println("Slot still available at that time: " + court.checkAvailability(sessionTime));

        //6. Host creates a session from the booking
        System.out.println("\n--- [6] Host Creates a Session ---");
        Session session = host.createSession(booking, 6, 80.0, community.getCommunityId(),
                "Weekend Badminton Session");
        System.out.println("Session created: " + session.getTitle());
        System.out.println("Capacity: " + session.getCapacity() + ", Fee per person: HKD " + session.getFeePerPerson());
        System.out.println("Booking status updated to: " + booking.getBookingStatus());
        System.out.println("Alice notified: " + alice.getNotificationMessage());

        //7. Members join the session
        System.out.println("\n--- [7] Members Join Session ---");
        alice.joinSession(session);
        charlie.joinSession(session);
        System.out.println(alice.getName() + " joined session.");
        System.out.println(charlie.getName() + " joined session.");
        System.out.println("Current participants: " + session.getParticipantCount());
        System.out.println("Total fee collected: HKD " + session.calculateTotalFee());

        //8. Payment processing
        System.out.println("\n--- [8] Payment Processing ---");
        PaymentAdministrationSystem pas = PaymentAdministrationSystem.getInstance();
        pas.processPayment(session.getFeePerPerson(), alice, host, "Wallet");
        pas.processPayment(session.getFeePerPerson(), charlie, host, "Wallet");
        pas.processPayment(court.getHourlyRate(), host, courtOwner, "Wallet");

        System.out.println(alice.getName() + " wallet after payment: HKD " + alice.getWalletBalance());
        System.out.println(charlie.getName() + " wallet after payment: HKD " + charlie.getWalletBalance());
        System.out.println(host.getName() + " wallet (session fees minus court): HKD " + host.getWalletBalance());
        System.out.println("Court owner wallet: HKD " + courtOwner.getWalletBalance());
        System.out.println("Total payments processed: " + pas.getPaymentHistory().size());

        //9. Host sends notification to community (Observer pattern)
        System.out.println("\n--- [9] Community Notification ---");
        host.sendNotification(community, "Reminder: Session this Saturday at 10am. Bring your rackets!");
        System.out.println("Alice's latest notification: " + alice.getNotificationMessage());
        System.out.println("Charlie's latest notification: " + charlie.getNotificationMessage());

        //10. Cancel a booking
        System.out.println("\n--- [10] Cancel Booking Demo ---");
        host.createBooking(community.getCommunityId(),
                LocalDateTime.now().plusDays(7), court.getCourtId());
        Booking futureBooking = host.getBookedCourts().get(1);
        System.out.println("New booking status: " + futureBooking.getBookingStatus());
        host.cancelBooking(futureBooking.getBookingId());
        System.out.println("After cancellation: " + futureBooking.getBookingStatus());
        System.out.println("Cancelled slot is free again: "
                + court.checkAvailability(futureBooking.getBookingDateTime()));

        //11. Wallet transaction history
        System.out.println("\n--- [11] Alice's Wallet Transactions ---");
        alice.getWallet().getTransactionHistory().forEach(t -> System.out.println("  " + t));

        System.out.println("\n=== Simulation Complete ===");
    }
}
