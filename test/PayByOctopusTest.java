package playhub;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

class PayByOctopusTest {

	private PayByOctopus octopus;
	private Wallet wallet;

	@BeforeEach
	void setUp() {
		octopus = PayByOctopus.getInstance();
		wallet = new Wallet("HKD");
	}

	@Test
	@DisplayName("getInstance returns singleton instance")
	void testGetInstanceReturnsSingleton() {
		PayByOctopus instance1 = PayByOctopus.getInstance();
		PayByOctopus instance2 = PayByOctopus.getInstance();
		assertSame(instance1, instance2);
	}

	@Test
	@DisplayName("getName returns Octopus")
	void testGetNameReturnsOctopus() {
		assertEquals("Octopus", octopus.getName());
	}

	@Test
	@DisplayName("topup adds funds to wallet")
	void testTopupAddsFundsToWallet() {
		octopus.topup(100.0, wallet);
		assertEquals(100.0, wallet.getBalance());
	}

	@Test
	@DisplayName("topup adds transaction with Octopus method")
	void testTopupAddsTransactionWithOctopusMethod() {
		octopus.topup(100.0, wallet);
		assertEquals(1, wallet.getTransactionHistory().size());
		assertTrue(wallet.getTransactionHistory().get(0).contains("Octopus"));
	}

	@Test
	@DisplayName("topup throws for negative amount")
	void testTopupThrowsForNegativeAmount() {
		assertThrows(IllegalArgumentException.class, () ->
			octopus.topup(-10.0, wallet));
	}

	@Test
	@DisplayName("topup throws for zero amount")
	void testTopupThrowsForZeroAmount() {
		assertThrows(IllegalArgumentException.class, () ->
			octopus.topup(0.0, wallet));
	}

	@Test
	@DisplayName("topup throws for null wallet")
	void testTopupThrowsForNullWallet() {
		assertThrows(IllegalArgumentException.class, () ->
			octopus.topup(100.0, null));
	}

	@Test
	@DisplayName("Multiple topups accumulate")
	void testMultipleTopupsAccumulate() {
		octopus.topup(50.0, wallet);
		octopus.topup(50.0, wallet);
		assertEquals(100.0, wallet.getBalance());
	}
}
