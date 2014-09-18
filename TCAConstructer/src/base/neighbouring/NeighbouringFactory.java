package base.neighbouring;



public class NeighbouringFactory {

	public static NeighbouringMethod newInstance(String string) {

		if (string.equalsIgnoreCase("AVO"))
			return new AlterRow();
		else if (string.equalsIgnoreCase("CUP"))
			return new HasNewlyCovered();
		else if (string.equalsIgnoreCase("CRI"))
			return new ChangeARandomIndex();
		else if (string.equalsIgnoreCase("SMT"))
			return new BetterTestList();
		return new InjectTuple();
	}
}
