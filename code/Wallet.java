package playhub;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Wallet {

	private String walletId;
	private double balance;
	private String currency;
	private final List<String> transactionList = new ArrayList<>();

	public Wallet(String currency) {
		this.walletId = UUID.randomUUID().toString();
		this.balance = 0.0;
		this.currency = currency;
	}

	public void addFunds(double amount, String method) {
		if (amount <= 0) {
			throw new IllegalArgumentException("Amount must be positive");
		}
		this.balance += amount;
		String transaction = "Credit: " + amount + " " + currency + " via " + method;
		transactionList.add(transaction);
	}

	public void deductFunds(double amount, String method) {
		if (amount <= 0) {
			throw new IllegalArgumentException("Amount must be positive");
		}
		if (amount > balance) {
			throw new IllegalArgumentException("Insufficient balance");
		}
		this.balance -= amount;
		String transaction = "Debit: " + amount + " " + currency + " via " + method;
		transactionList.add(transaction);
	}

	public List<String> getTransactionHistory() {
		return new ArrayList<>(transactionList);
	}

	public double getBalance() {
		return balance;
	}

	public String getWalletId() {
		return walletId;
	}

	public String getCurrency() {
		return currency;
	}

}
