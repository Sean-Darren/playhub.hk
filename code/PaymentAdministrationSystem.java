package playhub;

import java.util.*;

public class PaymentAdministrationSystem {

	private static PaymentAdministrationSystem instance;
	private final List<Map<String, Object>> paymentHistory = new ArrayList<>();

	private PaymentAdministrationSystem() {
	}

	public static synchronized PaymentAdministrationSystem getInstance() {
		if (instance == null) {
			instance = new PaymentAdministrationSystem();
		}
		return instance;
	}

	/**
	 * Records a payment in history without moving wallet funds.
	 * Use {@link #processPayment(double, User, User, String)} to transfer balances.
	 */
	public boolean processPayment(double amount, String payerId, String payeeId, String method) {
		validatePayment(amount, payerId, payeeId);
		recordPayment(amount, payerId, payeeId, method);
		return true;
	}

	public boolean processPayment(double amount, User payer, User payee, String method) {
		if (payer == null || payee == null) {
			throw new IllegalArgumentException("Payer and payee cannot be null");
		}
		validatePayment(amount, payer.getUserId(), payee.getUserId());
		payer.getWallet().deductFunds(amount, method);
		payee.getWallet().addFunds(amount, method);
		recordPayment(amount, payer.getUserId(), payee.getUserId(), method);
		return true;
	}

	private void validatePayment(double amount, String payerId, String payeeId) {
		if (amount <= 0) {
			throw new IllegalArgumentException("Amount must be positive");
		}
		if (payerId == null || payerId.isEmpty()) {
			throw new IllegalArgumentException("Payer ID cannot be null or empty");
		}
		if (payeeId == null || payeeId.isEmpty()) {
			throw new IllegalArgumentException("Payee ID cannot be null or empty");
		}
		if (payerId.equals(payeeId)) {
			throw new IllegalArgumentException("Payer and payee cannot be the same");
		}
	}

	private void recordPayment(double amount, String payerId, String payeeId, String method) {
		Map<String, Object> payment = new HashMap<>();
		payment.put("amount", amount);
		payment.put("payerId", payerId);
		payment.put("payeeId", payeeId);
		payment.put("method", method);
		payment.put("timestamp", System.currentTimeMillis());
		paymentHistory.add(payment);
	}

	public List<Map<String, Object>> getPaymentHistory() {
		return new ArrayList<>(paymentHistory);
	}

	public static synchronized void resetInstance() {
		instance = null;
	}

}
