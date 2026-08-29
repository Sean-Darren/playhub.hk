package playhub;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

class PayByCreditCardTest {

	private PayByCreditCard creditCard;
	private Wallet wallet;

	@BeforeEach
	void setUp() {
		creditCard = PayByCreditCard.getInstance();
		wallet = new Wallet("HKD");
	}

	@Test
	@DisplayName("getInstance returns singleton instance")
	void testGetInstanceReturnsSingleton() {
		PayByCreditCard instance1 = PayByCreditCard.getInstance();
		PayByCreditCard instance2 = PayByCreditCard.getInstance();
		assertSame(instance1, instance2);
	}

	@Test
	@DisplayName("getName returns Credit Card")
	void testGetNameReturnsCreditCard() {
		assertEquals("Credit Card", creditCard.getName());
	}

	@Test
	@DisplayName("topup adds funds to wallet")
	void testTopupAddsFundsToWallet() {
		creditCard.topup(100.0, wallet);
		assertEquals(100.0, wallet.getBalance());
	}

	@Test
	@DisplayName("topup adds transaction with Credit Card method")
	void testTopupAddsTransactionWithCreditCardMethod() {
		creditCard.topup(100.0, wallet);
		assertEquals(1, wallet.getTransactionHistory().size());
		assertTrue(wallet.getTransactionHistory().get(0).contains("Credit Card"));
	}

	@Test
	@DisplayName("topup throws for negative amount")
	void testTopupThrowsForNegativeAmount() {
		assertThrows(IllegalArgumentException.class, () ->
			creditCard.topup(-10.0, wallet));
	}

	@Test
	@DisplayName("topup throws for zero amount")
	void testTopupThrowsForZeroAmount() {
		assertThrows(IllegalArgumentException.class, () ->
			creditCard.topup(0.0, wallet));
	}

	@Test
	@DisplayName("topup throws for null wallet")
	void testTopupThrowsForNullWallet() {
		assertThrows(IllegalArgumentException.class, () ->
			creditCard.topup(100.0, null));
	}

	@Test
	@DisplayName("Multiple topups accumulate")
	void testMultipleTopupsAccumulate() {
		creditCard.topup(200.0, wallet);
		creditCard.topup(150.0, wallet);
		assertEquals(350.0, wallet.getBalance());
	}
}
