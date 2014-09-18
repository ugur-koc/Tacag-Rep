package test;

import interactionTesting.CoveringArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import base.Initializer;
import base.SimulatedAnnealing;
import base.utils.Helper;

public class TcacStrategies {
	static int[] strenght = { 2, 3 };
	static int[][] runCounts = { { 5, 5, 5, 5, 5, 5, 5 }, { 5, 5, 5, 5, 4, 3, 2 } };
	static double coolR = 0.001;
	static double temprature = 1;

	public static void main(String[] args) throws Exception {
		String header = "id,sut,opt,strength,tupleN,validTupleN,pairN,validPairN\n";
		Helper.writeFile("output/stads.txt", header, false);
		String[] initialization = { "randomInit", "hammingDistance", "tMinusOneCA", "traditionalCA" };

		String[] neighbouring = { "alterRow", "hasNewlyCovered", "injectTuple", "changeARandomIndex" };// "betterTestList"

		List<SoftwareUnderTest> suts = new ArrayList<SoftwareUnderTest>();
		SoftwareUnderTest apacheSUT = new SoftwareUnderTest("apache", new String[] { "13", "17", "22", "26", "33", "44", "65" });
		SoftwareUnderTest mysqlSUT = new SoftwareUnderTest("mysql", new String[] { "12", "15", "20", "24", "30", "40", "60" });
		suts.add(apacheSUT);
		suts.add(mysqlSUT);

		int[][] lbapache = { { 10, 11, 13, 15, 20, 25, 30 }, { 20, 21, 22, 23, 24, 26, 28 } };
		int[][] ubapache = { { 48, 56, 65, 69, 77, 84, 98 }, { 114, 141, 165, 171, 188, 214, 247 } };
		int[][] lbmysql = { { 10, 11, 13, 15, 20, 25, 30 }, { 20, 21, 22, 24, 26, 28, 30 } };
		int[][] ubmysql = { { 73, 81, 91, 96, 103, 112, 120 }, { 203, 230, 260, 282, 308, 342, 384 } };
		ExperimentDesign design = new ExperimentDesign();
		design.addSut("apache", strenght, lbapache, ubapache, runCounts, apacheSUT, new HashMap<String, int[]>());
		design.addSut("mysql", strenght, lbmysql, ubmysql, runCounts, mysqlSUT, new HashMap<String, int[]>());

		for (int t = 3; t < 4; t++) {
			Helper.setT(t);
			for (SoftwareUnderTest sut : suts) {
				String[] optArr = sut.getOpts();
				for (int index = 0; index < optArr.length; index++) {
					int[] params = design.getMap().get(sut.getName()).get(optArr[index] + "-" + t);
					int lb = params[0];
					int ub = params[1];
					for (int run = 0; run < params[2]; run++) {
						for (String init : initialization) {
							Helper.parseInputFile("input/" + sut.getName() + "/" + sut.getOpts()[index] + "o.txt");
							long initStart = System.currentTimeMillis();
							int lbinit;
							int ubinit;
							if (init.equalsIgnoreCase("tMinusOneCA")) {
								lbinit = design.getMap().get(sut.getName()).get(optArr[index] + "-" + (t - 1))[0];
								ubinit = design.getMap().get(sut.getName()).get(optArr[index] + "-" + (t - 1))[1];
							} else {
								lbinit = lb;
								ubinit = ub;
							}
							CoveringArray initSoln = Initializer.getInitSoln(init, lbinit, ubinit, Helper.getInitialSoln(), new SimulatedAnnealing("alterRow",
									temprature, coolR, 0.0001));
							long initFinish = System.currentTimeMillis();
							for (String neigh : neighbouring) {
								Helper.clear();
								Helper.parseInputFile("input/" + sut.getName() + "/" + sut.getOpts()[index] + "o.txt");
								Helper.setInitialSoln(initSoln);
								long annealStart = System.currentTimeMillis();
								SimulatedAnnealing sa = new SimulatedAnnealing(neigh, temprature, coolR, 0.0001);
								CoveringArray coveringArray = sa.search(lb, ub, Helper.getInitialSoln());
								long annealFinish = System.currentTimeMillis();
								Helper.writeFile("output/" + sut.getName() + "/" + sut.getOpts()[index] + "." + t + "t." + init + "." + neigh + "." + run
										+ ".out.txt", coveringArray.toString() + "Initial Soln:\n" + Helper.getInitialSoln().toString(), false);
								String verbose = run + "," + sut.getName() + "," + sut.getOpts()[index] + "," + t + "," + temprature + "," + coolR + "," + init
										+ "," + Helper.getInitialSoln().getRowCount() + "," + coveringArray.getRowCount() + "," + Helper.getInitialMiss() + ","
										+ coveringArray.getMiss() + "," + neigh + "," + Helper.getTotalAnnealIter() + "," + Helper.getBsStep() + "," + lb + ","
										+ ub + "," + TimeUnit.MILLISECONDS.toSeconds(Math.abs(initFinish - initStart)) + ","
										+ TimeUnit.MILLISECONDS.toSeconds(Math.abs(annealFinish - annealStart)) + ","
										+ TimeUnit.MILLISECONDS.toSeconds(Math.abs(annealFinish - annealStart) + Math.abs(initFinish - initStart)) + "\n";
								Helper.writeFile("output/stads.txt", verbose, true);
								System.out.print(verbose);
								Helper.clear();

							}
						}
					}
				}
			}
		}
	}
}