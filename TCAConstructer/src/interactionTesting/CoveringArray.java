package interactionTesting;

import java.util.ArrayList;
import java.util.List;

public class CoveringArray {

	int strength;
	int optionCount;
	int miss;
	Config[] configArr;

	public int getStrength() {
		return strength;
	}

	public void setConfig(Config config) {
		configArr[config.getRowIndex()] = new Config(config.getRowIndex(), config.getRow(), config.associatedTestList);
	}

	public CoveringArray(int row, int column, int t) {
		configArr = new Config[row];
		optionCount = column;
		this.strength = t;
	}

	public int getRowCount() {
		return configArr.length;
	}

	public void addRow(int i, String[] config,ArrayList<String> arrayList) {
		configArr[i] = new Config(i, config, arrayList);
	}

	public int getColumnCount() {
		return optionCount;
	}

	public String toString() {
		String str = "";
		for (Config config : configArr)
			str += config.toString() + "\n";
		return str;
	}

	public String[] getRow(int i) {
		return configArr[i].getRow();
	}

	public String[] getColumn(int j) {
		String[] strings = new String[getRowCount()];
		for (int i = 0; i < getRowCount(); i++)
			strings[i] = configArr[i].getRow()[j];
		return strings;
	}

	public void setArray(String[][] array2) {
		for (int i = 0; i < array2.length; i++) {
			for (int j = 0; j < array2[i].length; j++)
				this.configArr[i].getRow()[j] = array2[i][j];
		}
	}

	public int coverCount(String tuple) {
		int weight = 0;
		String[] split = tuple.split(",");
		for (int i = 0; i < getRowCount(); i++) {
			String key = split[strength - 1] + "," + configArr[i].getRow()[Integer.parseInt(split[0])];
			for (int j = strength - 2; j >= 0; j--)
				key = split[j] + "," + key + "," + configArr[i].getRow()[Integer.parseInt(split[strength - j - 1])];
			if (key.equals(tuple))
				weight++;
		}
		return weight;
	}

	public List<Integer> getContainingRows(String tuple) {
		List<Integer> rows = new ArrayList<Integer>();
		String[] split = tuple.split(",");
		for (int i = 0; i < getRowCount(); i++) {
			String key = split[strength - 1] + "," + configArr[i].getRow()[Integer.parseInt(split[0])];
			for (int j = strength - 2; j >= 0; j--)
				key = split[j] + "," + key + "," + configArr[i].getRow()[Integer.parseInt(split[strength - j - 1])];
			if (key.equals(tuple))
				rows.add(i);
		}
		return rows;
	}

	public Config[] getConfigArr() {
		return configArr;
	}

	public void setConfigArr(Config[] configArr) {
		this.configArr = configArr;
	}

	public int getMiss() {
		return miss;
	}

	public void setMiss(int miss) {
		this.miss = miss;
	}

	public int coverCount(int[] key, List<String> list) {
		int weight = 0;
		for (int i = 0; i < getRowCount(); i++) {
			int match = 0;
			for (int j = 0; j < key.length; j++)
				if (!configArr[i].getRow()[key[j]].equalsIgnoreCase(list.get(key[j])))
					break;
				else
					match++;
			if (key.length == match)
				weight++;
		}
		return weight;
	}
}
