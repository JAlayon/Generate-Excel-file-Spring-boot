package fg.binlist;

public class BinModel {

	private String scheme;
	private String type;
	private Bank bank;

	private BinModel() {
	}

	public String getScheme() {
		return scheme;
	}

	public void setScheme(String scheme) {
		this.scheme = scheme;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getBank() {
		return bank.getName();
	}

	public void setBank(Bank bank) {
		this.bank = bank;
	}
	
	

	@Override
	public String toString() {
		return "BinModel [scheme=" + scheme + ", type=" + type + ", bank=" + bank.getName() + "]";
	}



	class Bank {
		private String name;
		
		public Bank() {}
		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

	}

}
