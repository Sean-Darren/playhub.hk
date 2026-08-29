package playhub;

public class PayByCreditCard implements TopUpMethod {

	private static final PayByCreditCard INSTANCE = new PayByCreditCard();
	private static final String NAME = "Credit Card";

	private PayByCreditCard() {
	}

	public static PayByCreditCard getInstance() {
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
