package test;

import interactionTesting.CoveringArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import base.Initializer;
import base.SimulatedAnnealing;
import base.utils.Helper;

public class TuplePairCounts {
	static int[] strenght = { 2, 3 };
	static int[][] runCounts = { { 1, 1, 1, 1, 1, 1, 1 }, { 1, 1, 1, 1, 1, 1, 1 } };
	static double coolR = 0.001;
	static double temprature = 1;
	static List<SoftwareUnderTest> suts = new ArrayList<SoftwareUnderTest>();
	static ExperimentDesign design = new ExperimentDesign();

	public static void main(String[] args) throws Exception {
		String header = "sut,opt,strength,#tuple,#validTuple,#pair,#validPair,#validPairManuel,#pairTC,#validPairTC,\n";
		Helper.writeFile("output/stads.txt", header, false);

		SoftwareUnderTest apacheSUT = new SoftwareUnderTest("apache", new String[] { "13", "17", "22", "26", "33", "44", "65" });
		SoftwareUnderTest mysqlSUT = new SoftwareUnderTest("mysql", new String[] { "12", "15", "20", "24", "30", "40", "60" });
		suts.add(apacheSUT);
		suts.add(mysqlSUT);

		int[][] lbapache = { { 10, 11, 13, 15, 20, 25, 30 }, { 20, 21, 22, 23, 24, 26, 28 } };
		int[][] ubapache = { { 48, 56, 65, 69, 77, 84, 98 }, { 114, 141, 165, 171, 188, 214, 247 } };
		int[][] lbmysql = { { 10, 11, 13, 15, 20, 25, 30 }, { 20, 21, 22, 24, 26, 28, 30 } };
		int[][] ubmysql = { { 73, 81, 91, 96, 103, 112, 120 }, { 203, 230, 260, 282, 308, 342, 384 } };
		design.addSut("apache", strenght, lbapache, ubapache, runCounts, apacheSUT, new HashMap<String, int[]>());
		design.addSut("mysql", strenght, lbmysql, ubmysql, runCounts, mysqlSUT, new HashMap<String, int[]>());
		for (int t = 2; t < 4; t++) {
			Helper.setT(t);
			for (SoftwareUnderTest sut : suts) {
				Helper.getConfigSpaceModel().setName(sut.getName());
				for (int index = 0; index < sut.getOpts().length; index++) {
					int[] params = design.getMap().get(sut.getName()).get(sut.getOpts()[index] + "-" + t);
					int lb = params[0];
					int ub = params[1];
					Helper.parseInputFile("input/" + sut.getName() + "/" + sut.getOpts()[index] + "o.txt");
					CoveringArray initSoln = Initializer.getInitSoln("HIS", lb, ub, Helper.getInitialSoln(), new SimulatedAnnealing("alterRow", temprature,
							coolR, 0.0001));
					Helper.setInitialSoln(initSoln);
					Helper.pairEnumeration(initSoln);
					String verbose = sut.getName() + "," + sut.getOpts()[index] + "," + t + "," + Helper.getTupleCount() + "," + Helper.getValidTupleCount()
							+ "," + Helper.getPairCount() + "," + Helper.getValidPairCount() + "," + Helper.getValidPairCountManual() +"," + Helper.getPairCountTC() + "," + Helper.getValidPairCountTC() +"\n";
					Helper.writeFile("output/stads.txt", verbose, true);
					System.out.print(verbose);
					Helper.clear();

				}
			}
		}
	}
}
