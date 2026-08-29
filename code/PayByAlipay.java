package playhub;

public class PayByAlipay implements TopUpMethod {

	private static final PayByAlipay INSTANCE = new PayByAlipay();
	private static final String NAME = "Alipay";

	private PayByAlipay() {
	}

	public static PayByAlipay getInstance() {
		return INSTANCE;
	}

	@Override
	public void topup(double amount, Wallet walletObj) {
		if (amount <= 0) {
			throw new IllegalArgumentException("Amount must be positive");
		}
		if (walletObj == null) {
			throw new IllegalArgumentException("Wallet cannot be null");
		}
		walletObj.addFunds(amount, NAME);
	}

	@Override
	public String getName() {
		return NAME;
	}

}
