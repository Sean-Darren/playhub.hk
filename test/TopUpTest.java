package playhub;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

class TopUpTest {

	private Wallet wallet;

	@BeforeEach
	void setUp() {
		wallet = new Wallet("HKD");
	}

	@Test
	@DisplayName("Constructor with Alipay method")
	void testConstructorWithAlipayMethod() {
		TopUp topUp = new TopUp("Alipay", 100.0);
		assertEquals(100.0, topUp.getAmount());
		assertSame(PayByAlipay.getInstance(), topUp.getPaymentType());
		assertNotNull(topUp.getTopUpId());
	}

	@Test
	@DisplayName("Constructor with CreditCard method")
	void testConstructorWithCreditCardMethod() {
		TopUp topUp = new TopUp("CreditCard", 100.0);
		assertSame(PayByCreditCard.getInstance(), topUp.getPaymentType());
	}

	@Test
	@DisplayName("Constructor with Credit Card method (with space)")
	void testConstructorWithCreditCardWithSpaceMethod() {
		TopUp topUp = new TopUp("Credit Card", 100.0);
		assertSame(PayByCreditCard.getInstance(), topUp.getPaymentType());
	}

	@Test
	@DisplayName("Constructor with Octopus method")
	void testConstructorWithOctopusMethod() {
		TopUp topUp = new TopUp("Octopus", 100.0);
		assertSame(PayByOctopus.getInstance(), topUp.getPaymentType());
	}

	@Test
	@DisplayName("Constructor throws for unknown method")
	void testConstructorThrowsForUnknownMethod() {
		assertThrows(IllegalArgumentException.class, () ->
			new TopUp("Bitcoin", 100.0));
	}

	@Test
	@DisplayName("Constructor throws for null method")
	void testConstructorThrowsForNullMethod() {
		assertThrows(IllegalArgumentException.class, () -> new TopUp(null, 100.0));
	}

	@Test
	@DisplayName("Constructor throws for empty method")
	void testConstructorThrowsForEmptyMethod() {
		assertThrows(IllegalArgumentException.class, () -> new TopUp("", 100.0));
	}

	@Test
	@DisplayName("Constructor is case-insensitive for method")
	void testConstructorIsCaseInsensitive() {
		TopUp topUp1 = new TopUp("alipay", 100.0);
		TopUp topUp2 = new TopUp("ALIPAY", 100.0);
		assertSame(PayByAlipay.getInstance(), topUp1.getPaymentType());
		assertSame(PayByAlipay.getInstance(), topUp2.getPaymentType());
	}

	@Test
	@DisplayName("processTopUp adds funds to wallet")
	void testProcessTopUpAddsFunds() {
		TopUp topUp = new TopUp("Alipay", 100.0);
		topUp.processTopUp(wallet);
		assertEquals(100.0, wallet.getBalance());
	}

	@Test
	@DisplayName("processTopUp throws for null wallet")
	void testProcessTopUpThrowsForNullWallet() {
		TopUp topUp = new TopUp("Alipay", 100.0);
		assertThrows(IllegalArgumentException.class, () ->
			topUp.processTopUp(null));
	}

	@Test
	@DisplayName("processTopUp with CreditCard")
	void testProcessTopUpWithCreditCard() {
		TopUp topUp = new TopUp("CreditCard", 200.0);
		topUp.processTopUp(wallet);
		assertEquals(200.0, wallet.getBalance());
		assertTrue(wallet.getTransactionHistory().get(0).contains("Credit Card"));
	}

	@Test
	@DisplayName("processTopUp with Octopus")
	void testProcessTopUpWithOctopus() {
		TopUp topUp = new TopUp("Octopus", 50.0);
		topUp.processTopUp(wallet);
		assertEquals(50.0, wallet.getBalance());
		assertTrue(wallet.getTransactionHistory().get(0).contains("Octopus"));
	}

	@Test
	@DisplayName("Multiple topups via TopUp class")
	void testMultipleTopupsViaTopUpClass() {
		TopUp topUp1 = new TopUp("Alipay", 100.0);
		TopUp topUp2 = new TopUp("Octopus", 50.0);
		topUp1.processTopUp(wallet);
		topUp2.processTopUp(wallet);
		assertEquals(150.0, wallet.getBalance());
	}

	@Test
	@DisplayName("Each topUp has unique ID")
	void testEachTopUpHasUniqueId() {
		TopUp topUp1 = new TopUp("Alipay", 100.0);
		TopUp topUp2 = new TopUp("Alipay", 100.0);
		assertNotEquals(topUp1.getTopUpId(), topUp2.getTopUpId());
	}
}
