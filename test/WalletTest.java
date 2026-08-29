package playhub;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WalletTest {

	private Wallet wallet;

	@BeforeEach
	void setUp() {
		wallet = new Wallet("HKD");
	}

	@Test
	@DisplayName("Constructor initializes with zero balance")
	void testConstructorInitializesWithZeroBalance() {
		assertEquals(0.0, wallet.getBalance());
		assertEquals("HKD", wallet.getCurrency());
		assertNotNull(wallet.getWalletId());
	}

	@Test
	@DisplayName("Constructor with different currency")
	void testConstructorWithDifferentCurrency() {
		Wallet usdWallet = new Wallet("USD");
		assertEquals("USD", usdWallet.getCurrency());
	}

	@Test
	@DisplayName("addFunds increases balance")
	void testAddFundsIncreasesBalance() {
		wallet.addFunds(100.0, "Alipay");
		assertEquals(100.0, wallet.getBalance());
	}

	@Test
	@DisplayName("addFunds adds transaction to history")
	void testAddFundsAddsTransactionToHistory() {
		wallet.addFunds(100.0, "Alipay");
		List<String> history = wallet.getTransactionHistory();
		assertEquals(1, history.size());
		assertTrue(history.get(0).contains("100.0"));
		assertTrue(history.get(0).contains("Alipay"));
	}

	@Test
	@DisplayName("deductFunds decreases balance")
	void testDeductFundsDecreasesBalance() {
		wallet.addFunds(100.0, "Alipay");
		wallet.deductFunds(30.0, "Payment");
		assertEquals(70.0, wallet.getBalance());
	}

	@Test
	@DisplayName("deductFunds adds debit transaction")
	void testDeductFundsAddsDebitTransaction() {
		wallet.addFunds(100.0, "Alipay");
		wallet.deductFunds(30.0, "Payment");
		List<String> history = wallet.getTransactionHistory();
		assertEquals(2, history.size());
		assertTrue(history.get(1).contains("Debit"));
	}

	@Test
	@DisplayName("deductFunds throws when insufficient balance")
	void testDeductFundsThrowsWhenInsufficient() {
		wallet.addFunds(50.0, "Alipay");
		assertThrows(IllegalArgumentException.class, () -> wallet.deductFunds(100.0, "Payment"));
	}

	@Test
	@DisplayName("addFunds throws for negative amount")
	void testAddFundsThrowsForNegativeAmount() {
		assertThrows(IllegalArgumentException.class, () -> wallet.addFunds(-10.0, "Alipay"));
	}

	@Test
	@DisplayName("addFunds throws for zero amount")
	void testAddFundsThrowsForZeroAmount() {
		assertThrows(IllegalArgumentException.class, () -> wallet.addFunds(0.0, "Alipay"));
	}

	@Test
	@DisplayName("deductFunds throws for negative amount")
	void testDeductFundsThrowsForNegativeAmount() {
		wallet.addFunds(100.0, "Alipay");
		assertThrows(IllegalArgumentException.class, () -> wallet.deductFunds(-10.0, "Payment"));
	}

	@Test
	@DisplayName("deductFunds throws for zero amount")
	void testDeductFundsThrowsForZeroAmount() {
		wallet.addFunds(100.0, "Alipay");
		assertThrows(IllegalArgumentException.class, () -> wallet.deductFunds(0.0, "Payment"));
	}

	@Test
	@DisplayName("deductFunds throws when balance is zero")
	void testDeductFundsThrowsWhenZeroBalance() {
		assertThrows(IllegalArgumentException.class, () -> wallet.deductFunds(10.0, "Payment"));
	}

	@Test
	@DisplayName("getTransactionHistory returns a copy")
	void testGetTransactionHistoryReturnsCopy() {
		wallet.addFunds(100.0, "Alipay");
		List<String> copy = wallet.getTransactionHistory();
		copy.clear();
		assertEquals(1, wallet.getTransactionHistory().size());
	}

	@Test
	@DisplayName("Empty transaction history is empty list")
	void testEmptyTransactionHistoryIsEmpty() {
		assertTrue(wallet.getTransactionHistory().isEmpty());
	}

	@Test
	@DisplayName("Multiple transactions are tracked correctly")
	void testMultipleTransactionsTrackedCorrectly() {
		wallet.addFunds(100.0, "Alipay");
		wallet.addFunds(50.0, "Credit Card");
		wallet.deductFunds(30.0, "Payment");
		List<String> history = wallet.getTransactionHistory();
		assertEquals(3, history.size());
		assertEquals(120.0, wallet.getBalance());
	}
}
