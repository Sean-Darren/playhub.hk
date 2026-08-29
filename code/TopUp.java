package playhub;

import java.util.UUID;

public class TopUp {

	private String topUpId;
	private double amount;
	private TopUpMethod paymentType;

	public TopUp(String method, double amount) {
		if (method == null || method.isEmpty()) {
			throw new IllegalArgumentException("Payment method cannot be null or empty");
		}
		this.topUpId = UUID.randomUUID().toString();
		this.amount = amount;
		switch (method.toLowerCase()) {
			case "alipay":
				this.paymentType = PayByAlipay.getInstance();
				break;
			case "creditcard":
			case "credit card":
				this.paymentType = PayByCreditCard.getInstance();
				break;
			case "octopus":
				this.paymentType = PayByOctopus.getInstance();
				break;
			default:
				throw new IllegalArgumentException("Unknown payment method: " + method);
		}
	}

	public void processTopUp(Wallet walletObj) {
		if (walletObj == null) {
			throw new IllegalArgumentException("Wallet cannot be null");
		}
		paymentType.topup(amount, walletObj);
	}

	public String getTopUpId() {
		return topUpId;
	}

	public double getAmount() {
		return amount;
	}

	public TopUpMethod getPaymentType() {
		return paymentType;
	}

}
