package playhub;

public interface TopUpMethod {

	void topup(double amount, Wallet walletObj);

	String getName();

}
