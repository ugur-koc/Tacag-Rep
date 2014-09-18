package base.neighbouring;

import interactionTesting.Config;
import interactionTesting.CoveringArray;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import base.utils.Helper;

public class ChangeARandomIndex implements NeighbouringMethod {
	Random random = new Random();

	public void generateNeigbour(CoveringArray coveringArray) {
		Helper.getTransitionList().clear();
		List<Integer> shuffledIndexes = new ArrayList<Integer>();
		for (int i = 0; i < coveringArray.getRowCount(); i++)
			shuffledIndexes.add(i);
		Collections.shuffle(shuffledIndexes);
		String[] row = new String[coveringArray.getColumnCount()];

		for (Integer randomRow : shuffledIndexes) {
			for (int i = 0; i < row.length; i++)
				row[i] = coveringArray.getConfigArr()[randomRow].getRow()[i];

			int columnIndex = Helper.random.nextInt(Helper.getConfigSpaceModel().getOptionList().size());
			row[columnIndex] = Helper.getConfigSpaceModel().getOptionList().get(columnIndex)
					.getSetting(random.nextInt(Helper.getConfigSpaceModel().getOptionList().get(columnIndex).getSettingCount()));
			if (Helper.isConfigValid(row, "sys")) {
				Helper.getTransitionList().add(new Config(randomRow, row, Helper.buildAssociatedTestList(row)));
				return;
			}
		}
	}
}