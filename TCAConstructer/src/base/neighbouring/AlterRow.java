package base.neighbouring;

import interactionTesting.Config;
import interactionTesting.CoveringArray;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import base.utils.Helper;

public class AlterRow implements NeighbouringMethod {

	public void generateNeigbour(CoveringArray coveringArray) {
		Helper.getTransitionList().clear();
		List<Integer> shuffledIndexes = new ArrayList<Integer>();
		for (int i = 0; i < coveringArray.getRowCount(); i++)
			shuffledIndexes.add(i);
		Collections.shuffle(shuffledIndexes);
		String[] row = new String[coveringArray.getColumnCount()];

		String selectedTuple = Helper.selectRandomMissingTuple();
		String[] tupleElements = selectedTuple.split(",");
		ArrayList<String> testListToAssociate = null;
		for (Integer randomRow : shuffledIndexes) {
			for (int i = 0; i < row.length; i++)
				row[i] = coveringArray.getConfigArr()[randomRow].getRow()[i];
			for (int i = 0; i < tupleElements.length / 2; i++)
				row[Integer.parseInt(tupleElements[i])] = tupleElements[tupleElements.length / 2 + i];
			String randomTC = Helper.selectRandomTC(selectedTuple);
			if (!Helper.isConfigValid(row, randomTC))
				row = Helper.alterRow(selectedTuple, row, randomTC);

			if (Helper.isConfigValid(row, "sys")) {
				testListToAssociate = Helper.buildAssociatedTestList(row);
				Helper.getTransitionList().add(new Config(randomRow, row, testListToAssociate));
				return;
			}
		}
	}
}