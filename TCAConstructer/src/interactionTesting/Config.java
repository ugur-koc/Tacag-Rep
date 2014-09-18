package interactionTesting;

import java.util.ArrayList;

public class Config {

	int rowIndex;
	String[] row;
	ArrayList<String> associatedTestList = new ArrayList<String>();

	public Config(int index, String[] row, ArrayList<String> associatedTestList) {
		rowIndex = index;
		this.row = row;
		this.associatedTestList = associatedTestList;
	}

	public int getRowIndex() {
		return rowIndex;
	}

	public void setRowIndex(int rowIndex) {
		this.rowIndex = rowIndex;
	}

	public String[] getRow() {
		return row;
	}

	public void setRow(String[] row) {
		this.row = row;
	}

	public ArrayList<String> getAssociatedTestList() {
		return associatedTestList;
	}

	public void setAssociatedTestList(ArrayList<String> associatedTestList) {
		this.associatedTestList = associatedTestList;
	}

	public String toString() {
		String settings = row[0];
		for (int i = 1; i < row.length; i++)
			settings = settings + "," + row[i];
		String tcList = "{";
		for (String tc : associatedTestList)
			if (!tc.equalsIgnoreCase("sys"))
				tcList += tc + ",";
		if (tcList.charAt(tcList.length() - 1) == ',')
			tcList = tcList.substring(0, tcList.length() - 1);
		return settings + "," + tcList + "}";
	}
}
