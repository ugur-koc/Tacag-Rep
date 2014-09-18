package test;

import interactionTesting.CoveringArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import base.Initializer;
import base.SimulatedAnnealing;
import base.utils.Helper;

public class ScheduledTCPersentages {
	static int[] strenght = { 2, 3 };
	static int[][] runCounts = { { 5, 5, 5, 5, 5, 5, 5 }, { 5, 5, 5, 5, 4, 3, 2 } };
	static double coolR = 0.001;
	static double temprature = 1;

	public static void main(String[] args) throws Exception {
		String header = "id,sut,opt,strength,tupleN,validTupleN,pairN,validPairN\n";
		Helper.writeFile("output/stads.txt", header, false);

		List<SoftwareUnderTest> suts = new ArrayList<SoftwareUnderTest>();
		SoftwareUnderTest apacheSUT = new SoftwareUnderTest("apache", new String[] { "17" });
		SoftwareUnderTest mysqlSUT = new SoftwareUnderTest("mysql", new String[] { "12" });
		suts.add(apacheSUT);
		suts.add(mysqlSUT);

		ExperimentDesign design = new ExperimentDesign();

		for (SoftwareUnderTest sut : suts) {
			String[] optArr = sut.getOpts();
			int t = Integer.parseInt(optArr[0]);
			Helper.setT(t);
			int[] params = design.getMap().get(sut.getName()).get(optArr[0] + "-" + t);
			int lb = params[0];
			int ub = params[1];
			Helper.parseInputFile("input/" + sut.getName() + "/" + sut.getOpts()[0] + "o.txt");
			long initStart = System.currentTimeMillis();
			int lbinit;
			int ubinit;
			lbinit = lb;
			ubinit = ub;
			CoveringArray initSoln = Initializer.getInitSoln("hammingDistance", lbinit, ubinit, Helper.getInitialSoln(), new SimulatedAnnealing("alterRow",
					temprature, coolR, 0.0001));
			long initFinish = System.currentTimeMillis();
			Helper.clear();
			Helper.parseInputFile("input/" + sut.getName() + "/" + sut.getOpts()[0] + "o.txt");
			Helper.setInitialSoln(initSoln);
			long annealStart = System.currentTimeMillis();
			SimulatedAnnealing sa = new SimulatedAnnealing("alterRow", temprature, coolR, 0.0001);
			CoveringArray coveringArray = sa.search(lb, ub, Helper.getInitialSoln());
			long annealFinish = System.currentTimeMillis();
			Helper.writeFile("output/" + sut.getName() + "/" + sut.getOpts()[0] + "." + t + "t." + "hammingDistance" + "." + "alterRow" + ".out.txt",
					coveringArray.toString() + "Initial Soln:\n" + Helper.getInitialSoln().toString(), false);
			String verbose = sut.getName() + "," + sut.getOpts()[0] + "," + t + "," + temprature + "," + coolR + "," + "hammingDistance" + ","
					+ Helper.getInitialSoln().getRowCount() + "," + coveringArray.getRowCount() + "," + Helper.getInitialMiss() + "," + coveringArray.getMiss()
					+ "," + "alterRow" + "," + Helper.getTotalAnnealIter() + "," + Helper.getBsStep() + "," + lb + "," + ub + ","
					+ TimeUnit.MILLISECONDS.toSeconds(Math.abs(initFinish - initStart)) + ","
					+ TimeUnit.MILLISECONDS.toSeconds(Math.abs(annealFinish - annealStart)) + ","
					+ TimeUnit.MILLISECONDS.toSeconds(Math.abs(annealFinish - annealStart) + Math.abs(initFinish - initStart)) + "\n";
			Helper.writeFile("output/stads.txt", verbose, true);
			System.out.print(verbose);
			Helper.clear();

		}
	}
}