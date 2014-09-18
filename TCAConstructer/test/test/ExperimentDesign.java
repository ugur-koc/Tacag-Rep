package test;

import java.util.HashMap;

public class ExperimentDesign {

	private HashMap<String, HashMap<String, int[]>> map = new HashMap<String, HashMap<String, int[]>>();

	public ExperimentDesign() {

	}

	public void addSut(String name, int[] strengths, int[][] lb, int[][] ub, int[][] runCount, SoftwareUnderTest sut,HashMap<String, int[]> paramMap) {

		for (int opt = 0; opt < sut.getOpts().length; opt++) {
			for (int t = 0; t < strengths.length; t++) {
				int[] params = { lb[t][opt], ub[t][opt], runCount[t][opt] };
				paramMap.put(sut.getOpts()[opt] + "-" + strengths[t], params);
			}
		}
		map.put(name, paramMap);
	}

	public HashMap<String, HashMap<String, int[]>> getMap() {
		return map;
	}

}
