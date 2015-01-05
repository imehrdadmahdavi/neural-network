public class Literal {

	public int guestNumber;
	public int tableNumber;
	public boolean isNegated;

	public Literal(int guestNumber, int tableNumber, boolean isNegated) {
		this.guestNumber = guestNumber;
		this.tableNumber = tableNumber;
		this.isNegated = isNegated;
	}

	public int getGuestNumber() {
		return guestNumber;
	}

	public void setGuestNumber(int guestNumber) {
		this.guestNumber = guestNumber;
	}

	public int getTableNumber() {
		return tableNumber;
	}

	public void setTableNumber(int tableNumber) {
		this.tableNumber = tableNumber;
	}

	public boolean isNegated() {
		return isNegated;
	}

	public void setNegated(boolean isNegated) {
		this.isNegated = isNegated;
	}
}
