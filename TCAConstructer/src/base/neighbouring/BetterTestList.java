package base.neighbouring;

import interactionTesting.Config;
import interactionTesting.CoveringArray;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import base.utils.Helper;

public class BetterTestList implements NeighbouringMethod {

	public void generateNeigbour(CoveringArray coveringArray) {
		Helper.getTransitionList().clear();
		List<Integer> shuffledIndexes = new ArrayList<Integer>();
		for (int i = 0; i < coveringArray.getRowCount(); i++)
			shuffledIndexes.add(i);
		Collections.shuffle(shuffledIndexes);
		String[] row = new String[coveringArray.getColumnCount()];
		ArrayList<String> shuffledTupleList = Helper.getMissingTupleList();
		Collections.shuffle(shuffledTupleList);
		for (String selectedTuple : shuffledTupleList) {
			String[] tupleElements = selectedTuple.split(",");
			for (Integer rowIndex : shuffledIndexes) {
				for (int i = 0; i < row.length; i++)
					row[i] = coveringArray.getConfigArr()[rowIndex].getRow()[i];
				for (int j = 0; j < coveringArray.getStrength(); j++)
					row[Integer.parseInt(tupleElements[j])] = tupleElements[coveringArray.getStrength() + j];
				if (Helper.isConfigValid(row, "sys")) {
					ArrayList<String> testListToAssociate = Helper.buildAssociatedTestList(row);
					if (testListToAssociate.size() >= coveringArray.getConfigArr()[rowIndex].getAssociatedTestList().size()) {
						Helper.getTransitionList().add(new Config(rowIndex, row, testListToAssociate));
						return;
					}
				}
			}
		}
		String selectedTuple = shuffledTupleList.get(0);
		String[] tupleElements = selectedTuple.split(",");
		for (Integer randomRow : shuffledIndexes) {
			for (int i = 0; i < row.length; i++)
				row[i] = coveringArray.getConfigArr()[randomRow].getRow()[i];
			for (int i = 0; i < tupleElements.length / 2; i++)
				row[Integer.parseInt(tupleElements[i])] = tupleElements[tupleElements.length / 2 + i];
			String randomTC = Helper.selectRandomTC(selectedTuple);
			if (!Helper.isConfigValid(row, randomTC))
				row = Helper.alterRow(selectedTuple, row, randomTC);

			if (Helper.isConfigValid(row, "sys")) {
				Helper.getTransitionList().add(new Config(randomRow, row, Helper.buildAssociatedTestList(row)));
				return;
			}
		}
	}
}