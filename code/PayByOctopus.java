package playhub;

public class PayByOctopus implements TopUpMethod {

	private static final PayByOctopus INSTANCE = new PayByOctopus();
	private static final String NAME = "Octopus";

	private PayByOctopus() {
	}

	public static PayByOctopus getInstance() {
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
