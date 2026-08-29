package playhub;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

class PayByAlipayTest {

	private PayByAlipay alipay;
	private Wallet wallet;

	@BeforeEach
	void setUp() {
		alipay = PayByAlipay.getInstance();
		wallet = new Wallet("HKD");
	}

	@Test
	@DisplayName("getInstance returns singleton instance")
	void testGetInstanceReturnsSingleton() {
		PayByAlipay instance1 = PayByAlipay.getInstance();
		PayByAlipay instance2 = PayByAlipay.getInstance();
		assertSame(instance1, instance2);
	}

	@Test
	@DisplayName("getName returns Alipay")
	void testGetNameReturnsAlipay() {
		assertEquals("Alipay", alipay.getName());
	}

	@Test
	@DisplayName("topup adds funds to wallet")
	void testTopupAddsFundsToWallet() {
		alipay.topup(100.0, wallet);
		assertEquals(100.0, wallet.getBalance());
	}

	@Test
	@DisplayName("topup adds transaction with Alipay method")
	void testTopupAddsTransactionWithAlipayMethod() {
		alipay.topup(100.0, wallet);
		assertEquals(1, wallet.getTransactionHistory().size());
		assertTrue(wallet.getTransactionHistory().get(0).contains("Alipay"));
	}

	@Test
	@DisplayName("topup throws for negative amount")
	void testTopupThrowsForNegativeAmount() {
		assertThrows(IllegalArgumentException.class, () ->
			alipay.topup(-10.0, wallet));
	}

	@Test
	@DisplayName("topup throws for zero amount")
	void testTopupThrowsForZeroAmount() {
		assertThrows(IllegalArgumentException.class, () ->
			alipay.topup(0.0, wallet));
	}

	@Test
	@DisplayName("topup throws for null wallet")
	void testTopupThrowsForNullWallet() {
		assertThrows(IllegalArgumentException.class, () ->
			alipay.topup(100.0, null));
	}

	@Test
	@DisplayName("Multiple topups accumulate")
	void testMultipleTopupsAccumulate() {
		alipay.topup(50.0, wallet);
		alipay.topup(30.0, wallet);
		alipay.topup(20.0, wallet);
		assertEquals(100.0, wallet.getBalance());
		assertEquals(3, wallet.getTransactionHistory().size());
	}
}
