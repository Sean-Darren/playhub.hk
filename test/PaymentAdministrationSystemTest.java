package playhub;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class PaymentAdministrationSystemTest {

	@BeforeEach
	void setUp() {
		PaymentAdministrationSystem.resetInstance();
	}

	@AfterEach
	void tearDown() {
		PaymentAdministrationSystem.resetInstance();
	}

	@Test
	@DisplayName("getInstance returns singleton instance")
	void testGetInstanceReturnsSingleton() {
		PaymentAdministrationSystem instance1 = PaymentAdministrationSystem.getInstance();
		PaymentAdministrationSystem instance2 = PaymentAdministrationSystem.getInstance();
		assertSame(instance1, instance2);
	}

	@Test
	@DisplayName("getInstance creates instance on first call")
	void testGetInstanceCreatesInstanceOnFirstCall() {
		PaymentAdministrationSystem.resetInstance();
		assertNotNull(PaymentAdministrationSystem.getInstance());
	}

	@Test
	@DisplayName("processPayment succeeds with valid inputs")
	void testProcessPaymentSucceedsWithValidInputs() {
		PaymentAdministrationSystem system = PaymentAdministrationSystem.getInstance();
		assertTrue(system.processPayment(100.0, "payer1", "payee1", "Alipay"));
	}

	@Test
	@DisplayName("processPayment throws for negative amount")
	void testProcessPaymentThrowsForNegativeAmount() {
		PaymentAdministrationSystem system = PaymentAdministrationSystem.getInstance();
		assertThrows(IllegalArgumentException.class, () ->
			system.processPayment(-10.0, "payer1", "payee1", "Alipay"));
	}

	@Test
	@DisplayName("processPayment throws for zero amount")
	void testProcessPaymentThrowsForZeroAmount() {
		PaymentAdministrationSystem system = PaymentAdministrationSystem.getInstance();
		assertThrows(IllegalArgumentException.class, () ->
			system.processPayment(0.0, "payer1", "payee1", "Alipay"));
	}

	@Test
	@DisplayName("processPayment throws for null payerId")
	void testProcessPaymentThrowsForNullPayerId() {
		PaymentAdministrationSystem system = PaymentAdministrationSystem.getInstance();
		assertThrows(IllegalArgumentException.class, () ->
			system.processPayment(100.0, null, "payee1", "Alipay"));
	}

	@Test
	@DisplayName("processPayment throws for empty payerId")
	void testProcessPaymentThrowsForEmptyPayerId() {
		PaymentAdministrationSystem system = PaymentAdministrationSystem.getInstance();
		assertThrows(IllegalArgumentException.class, () ->
			system.processPayment(100.0, "", "payee1", "Alipay"));
	}

	@Test
	@DisplayName("processPayment throws for null payeeId")
	void testProcessPaymentThrowsForNullPayeeId() {
		PaymentAdministrationSystem system = PaymentAdministrationSystem.getInstance();
		assertThrows(IllegalArgumentException.class, () ->
			system.processPayment(100.0, "payer1", null, "Alipay"));
	}

	@Test
	@DisplayName("processPayment throws for empty payeeId")
	void testProcessPaymentThrowsForEmptyPayeeId() {
		PaymentAdministrationSystem system = PaymentAdministrationSystem.getInstance();
		assertThrows(IllegalArgumentException.class, () ->
			system.processPayment(100.0, "payer1", "", "Alipay"));
	}

	@Test
	@DisplayName("processPayment throws when payer equals payee")
	void testProcessPaymentThrowsWhenPayerEqualsPayee() {
		PaymentAdministrationSystem system = PaymentAdministrationSystem.getInstance();
		assertThrows(IllegalArgumentException.class, () ->
			system.processPayment(100.0, "sameUser", "sameUser", "Alipay"));
	}

	@Test
	@DisplayName("processPayment records payment in history")
	void testProcessPaymentRecordsPaymentInHistory() {
		PaymentAdministrationSystem system = PaymentAdministrationSystem.getInstance();
		system.processPayment(100.0, "payer1", "payee1", "Alipay");
		List<Map<String, Object>> history = system.getPaymentHistory();
		assertEquals(1, history.size());
		assertEquals(100.0, history.get(0).get("amount"));
		assertEquals("payer1", history.get(0).get("payerId"));
		assertEquals("payee1", history.get(0).get("payeeId"));
		assertEquals("Alipay", history.get(0).get("method"));
	}

	@Test
	@DisplayName("Multiple payments are tracked in history")
	void testMultiplePaymentsTrackedInHistory() {
		PaymentAdministrationSystem system = PaymentAdministrationSystem.getInstance();
		system.processPayment(100.0, "payer1", "payee1", "Alipay");
		system.processPayment(200.0, "payer2", "payee2", "CreditCard");
		system.processPayment(50.0, "payer3", "payee3", "Octopus");
		List<Map<String, Object>> history = system.getPaymentHistory();
		assertEquals(3, history.size());
	}

	@Test
	@DisplayName("getPaymentHistory returns a copy")
	void testGetPaymentHistoryReturnsCopy() {
		PaymentAdministrationSystem system = PaymentAdministrationSystem.getInstance();
		system.processPayment(100.0, "payer1", "payee1", "Alipay");
		List<Map<String, Object>> copy = system.getPaymentHistory();
		copy.clear();
		assertEquals(1, system.getPaymentHistory().size());
	}

	@Test
	@DisplayName("Payment history includes timestamp")
	void testPaymentHistoryIncludesTimestamp() {
		PaymentAdministrationSystem system = PaymentAdministrationSystem.getInstance();
		long before = System.currentTimeMillis();
		system.processPayment(100.0, "payer1", "payee1", "Alipay");
		long after = System.currentTimeMillis();
		long timestamp = (Long) system.getPaymentHistory().get(0).get("timestamp");
		assertTrue(timestamp >= before && timestamp <= after);
	}

	@Test
	@DisplayName("resetInstance allows new singleton creation")
	void testResetInstanceAllowsNewSingleton() {
		PaymentAdministrationSystem instance1 = PaymentAdministrationSystem.getInstance();
		PaymentAdministrationSystem.resetInstance();
		PaymentAdministrationSystem instance2 = PaymentAdministrationSystem.getInstance();
		assertNotSame(instance1, instance2);
	}

	@Test
	@DisplayName("Empty payment history initially")
	void testEmptyPaymentHistoryInitially() {
		PaymentAdministrationSystem system = PaymentAdministrationSystem.getInstance();
		assertTrue(system.getPaymentHistory().isEmpty());
	}

	@Test
	@DisplayName("ID processPayment does not move wallet funds")
	void testIdProcessPaymentDoesNotMoveWalletFunds() {
		PaymentAdministrationSystem system = PaymentAdministrationSystem.getInstance();
		User payer = new User("Payer", "11111111", "pw");
		User payee = new User("Payee", "22222222", "pw");
		payer.topUpWallet(200.0, "Alipay");
		system.processPayment(50.0, payer.getUserId(), payee.getUserId(), "Alipay");
		assertEquals(200.0, payer.getWalletBalance());
		assertEquals(0.0, payee.getWalletBalance());
		assertEquals(1, system.getPaymentHistory().size());
	}

	@Test
	@DisplayName("User processPayment transfers wallet funds")
	void testUserProcessPaymentTransfersFunds() {
		PaymentAdministrationSystem system = PaymentAdministrationSystem.getInstance();
		User payer = new User("Payer", "11111111", "pw");
		User payee = new User("Payee", "22222222", "pw");
		payer.topUpWallet(200.0, "Alipay");
		assertTrue(system.processPayment(80.0, payer, payee, "Wallet"));
		assertEquals(120.0, payer.getWalletBalance());
		assertEquals(80.0, payee.getWalletBalance());
		assertEquals(1, system.getPaymentHistory().size());
	}

	@Test
	@DisplayName("User processPayment throws on insufficient funds without crediting payee")
	void testUserProcessPaymentInsufficientFunds() {
		PaymentAdministrationSystem system = PaymentAdministrationSystem.getInstance();
		User payer = new User("Payer", "11111111", "pw");
		User payee = new User("Payee", "22222222", "pw");
		payer.topUpWallet(10.0, "Alipay");
		assertThrows(IllegalArgumentException.class, () ->
			system.processPayment(50.0, payer, payee, "Wallet"));
		assertEquals(10.0, payer.getWalletBalance());
		assertEquals(0.0, payee.getWalletBalance());
		assertTrue(system.getPaymentHistory().isEmpty());
	}

	@Test
	@DisplayName("User processPayment throws for null payer or payee")
	void testUserProcessPaymentThrowsForNullUsers() {
		PaymentAdministrationSystem system = PaymentAdministrationSystem.getInstance();
		User user = new User("User", "11111111", "pw");
		assertThrows(IllegalArgumentException.class, () ->
			system.processPayment(10.0, null, user, "Wallet"));
		assertThrows(IllegalArgumentException.class, () ->
			system.processPayment(10.0, user, null, "Wallet"));
	}
}
