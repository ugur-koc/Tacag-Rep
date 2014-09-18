package base;

import interactionTesting.Constraint;
import interactionTesting.CoveringArray;

import java.util.HashMap;

import base.utils.Helper;

public class Initializer {

	public static CoveringArray getInitSoln(String initStrategy, int lb, int ub, CoveringArray coveringArray, SimulatedAnnealing sa) throws Exception {
		CoveringArray CA = null;
		int N = (lb + ub) / 2;
		if (initStrategy.equalsIgnoreCase("RIS")) {
			CA = new CoveringArray(N, Helper.getConfigSpaceModel().getOptionList().size(), Helper.getStrength());

			int oldN = 0;
			if (coveringArray != null)
				oldN = coveringArray.getRowCount();

			if (oldN > CA.getRowCount())
				oldN = CA.getRowCount();
			for (int i = 0; i < oldN; i++)
				CA.addRow(i, coveringArray.getRow(i), Helper.buildAssociatedTestList(coveringArray.getRow(i)));

			for (int i = oldN; i < N; i++) {
				String[] row = new String[Helper.getConfigSpaceModel().getOptionList().size()];
				do {
					for (int j = 0; j < Helper.getConfigSpaceModel().getOptionList().size(); j++)
						row[j] = Helper.getConfigSpaceModel().getOptionList().get(j)
								.getSetting(Helper.random.nextInt(Helper.getConfigSpaceModel().getOptionList().get(j).getSettingCount()));
				} while (!Helper.isConfigValid(row, "sys"));
				CA.addRow(i, row, Helper.buildAssociatedTestList(row));
			}
		} else if (initStrategy.equalsIgnoreCase("HIS")) {
			CA = new CoveringArray(N, Helper.getConfigSpaceModel().getOptionList().size(), Helper.getStrength());
			int oldN = 0;
			if (coveringArray != null)
				oldN = coveringArray.getRowCount();

			if (oldN > CA.getRowCount())
				oldN = CA.getRowCount();

			for (int i = 0; i < oldN; i++)
				CA.addRow(i, coveringArray.getRow(i), Helper.buildAssociatedTestList(coveringArray.getRow(i)));

			for (int i = oldN; i < N; i++) {
				String[] newRow;
				do {
					newRow = Helper.generateNewRow(CA);
				} while (!Helper.isConfigValid(newRow, "sys"));
				CA.addRow(i, newRow, Helper.buildAssociatedTestList(newRow));
			}
		} else if (initStrategy.equalsIgnoreCase("TIS")) {
			HashMap<String, Constraint> tempConstraintMap = Helper.copyConstraintMap(Helper.getConfigSpaceModel().getConstraintMap());
			Constraint constraint = Helper.getConfigSpaceModel().getConstraintMap().get("sys");
			Helper.getConfigSpaceModel().getConstraintMap().clear();
			Helper.getConfigSpaceModel().getConstraintMap().put("sys", constraint);
			CA = new CoveringArray(lb, Helper.getConfigSpaceModel().getOptionList().size(), Helper.getStrength());
			for (int i = 0; i < lb; i++) {
				String[] newRow;
				do {
					newRow = Helper.generateNewRow(CA);
				} while (!Helper.isConfigValid(newRow, "sys"));
				CA.addRow(i, newRow, Helper.buildAssociatedTestList(newRow));
			}
			CA = sa.anneal(lb, CA);
			CoveringArray coveringArray2 = new CoveringArray(N, Helper.getConfigSpaceModel().getOptionList().size(), Helper.getStrength());
			for (int i = 0; i < lb; i++)
				coveringArray2.addRow(i, CA.getRow(i), Helper.buildAssociatedTestList(CA.getRow(i)));
			for (int i = lb; i < N; i++) {
				String[] newRow;
				do {
					newRow = Helper.generateNewRow(coveringArray2);
				} while (!Helper.isConfigValid(newRow, "sys"));
				coveringArray2.addRow(i, newRow, Helper.buildAssociatedTestList(newRow));
			}
			CA = coveringArray2;
			Helper.getConfigSpaceModel().setConstraints(tempConstraintMap);
		} else if (initStrategy.equalsIgnoreCase("TCIS")) {
			HashMap<String, Constraint> tempConstraintMap = Helper.copyConstraintMap(Helper.getConfigSpaceModel().getConstraintMap());
			Helper.setStrength(Helper.getStrength() - 1);
			Constraint constraint = Helper.getConfigSpaceModel().getConstraintMap().get("sys");
			Helper.getConfigSpaceModel().getConstraintMap().clear();
			Helper.getConfigSpaceModel().getConstraintMap().put("sys", constraint);
			CA = new CoveringArray(lb, Helper.getConfigSpaceModel().getOptionList().size(), Helper.getStrength());
			for (int i = 0; i < CA.getRowCount(); i++) {
				String[] newRow;
				do {
					newRow = Helper.generateNewRow(CA);
				} while (!Helper.isConfigValid(newRow, "sys"));
				CA.addRow(i, newRow, Helper.buildAssociatedTestList(newRow));
			}
			CA = sa.anneal(lb, CA);
			Helper.getConfigSpaceModel().setConstraints(tempConstraintMap);
			CA = sa.search(lb, ub, CA);
			Helper.setStrength(Helper.getStrength() + 1);
			Helper.getAllPairList().clear();
			Helper.getMissingPairMap().clear();
		}
		return CA;
	}
}
