package test;

import interactionTesting.Constraint;
import interactionTesting.CoveringArray;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;

import presentation.ConsolePresenter;
import base.SimulatedAnnealing;
import base.utils.Helper;

public class AnnealingParams {

	static String[] neiGen = { "st1", "st2", "st3", "rt1", "rt2", "rt3", "ar" };
	static int index = neiGen.length - 1;

	static String[] sizemysql = { "12", "15", "20", "24", "30", "40", "60" };
	static int[] lbmysql = { 10, 11, 13, 15, 20, 25, 30, 20, 21, 22, 24, 26, 28, 30 };
	static int[] ubmysql = { 73, 81, 91, 96, 103, 112, 120, 203, 230, 260, 282, 308, 342, 384 };

	static String[] sizeapache = { "13", "17", "22", "26", "33", "44", "65" };
	static int[] lbapache = { 10, 11, 13, 15, 20, 25, 30, 20, 21, 22, 23, 24, 26, 28 };
	static int[] ubapache = { 48, 56, 65, 69, 77, 84, 98, 114, 141, 165, 171, 188, 214, 247 };
	static int optionSpace = 7;

	public static void main(String[] args) throws Exception {
		TestApache();
	}

	public static void TestMysql() throws Exception {
		long startTime, finishTime;
		double[] coolingRates = { 0.0001, 0.00001 };
		double[] tempratures = { 2, 5, 10 };
		HashMap<String, Constraint> tempConstraintMap;
		System.out.println("Option,Strenght,N,Pair,Second,CoolR,Heat");
		for (int t = 2; t < 4; t++)
			for (int op = 0; op < optionSpace; op++)
				for (int run = 1; run <= 5; run++)
					for (double c : coolingRates)
						for (double d : tempratures) {
							CoveringArray coveringArray;
							startTime = System.currentTimeMillis();
							SimulatedAnnealing sa = new SimulatedAnnealing(neiGen[index], d, c, 0.0001);
							Helper.setT(t);
							Helper.parseInputFile("input/mysql/" + sizemysql[op] + "o.txt");
							Helper.setPresentationLayer(new ConsolePresenter());
							tempConstraintMap = Helper.copyConstraintMap(Helper.getConfigSpaceModel().getConstraintMap());
							Constraint constraint = Helper.getConfigSpaceModel().getConstraintMap().get("sys");
							Helper.getConfigSpaceModel().getConstraintMap().clear();
							Helper.getConfigSpaceModel().getConstraintMap().put("sys", constraint);
							coveringArray = sa.anneal(lbmysql[op + (t - 2) * optionSpace], Helper.getInitialSoln());
							Helper.getConfigSpaceModel().setConstraints(tempConstraintMap);
							coveringArray = sa.search(lbmysql[op + (t - 2) * optionSpace], ubmysql[op + (t - 2) * optionSpace], coveringArray);
							finishTime = System.currentTimeMillis();
							FileOutputStream fos = new FileOutputStream("output - mysql/" + sizemysql[op] + "." + t + "t.out.txt");
							OutputStreamWriter out = new OutputStreamWriter(fos, "UTF-8");
							out.write(Helper.verbose(coveringArray, startTime, finishTime));
							out.write(coveringArray.toString());
							out.flush();
							out.close();
							System.out.print(Helper.display(coveringArray, startTime, finishTime) + "," + c + "," + d + "\n");
							Helper.clear();
						}
	}

	public static void TestApache() throws Exception {
		long startTime, finishTime;
		double[] coolingRates = { 0.0001, 0.00001 };
		double[] tempratures = { 2, 5, 10 };
		HashMap<String, Constraint> tempConstraintMap;
		System.out.println("Option,Strenght,N,Pair,Second,CoolR,Heat");
		for (int t = 2; t < 4; t++)
			for (int op = 0; op < optionSpace; op++)
				for (int run = 1; run <= 5; run++)
					for (double c : coolingRates)
						for (double d : tempratures) {
							CoveringArray coveringArray;
							startTime = System.currentTimeMillis();
							SimulatedAnnealing sa = new SimulatedAnnealing(neiGen[index], d, c, 0.0001);
							Helper.setT(t);
							Helper.parseInputFile("input/apache/" + sizeapache[op] + "o.txt");
							Helper.setPresentationLayer(new ConsolePresenter());
							tempConstraintMap = Helper.copyConstraintMap(Helper.getConfigSpaceModel().getConstraintMap());
							Constraint constraint = Helper.getConfigSpaceModel().getConstraintMap().get("sys");
							Helper.getConfigSpaceModel().getConstraintMap().clear();
							Helper.getConfigSpaceModel().getConstraintMap().put("sys", constraint);
							coveringArray = sa.anneal(lbapache[op + (t - 2) * optionSpace], Helper.getInitialSoln());
							Helper.getConfigSpaceModel().setConstraints(tempConstraintMap);
							coveringArray = sa.search(lbapache[op + (t - 2) * optionSpace], ubapache[op + (t - 2) * optionSpace], coveringArray);
							finishTime = System.currentTimeMillis();
							FileOutputStream fos = new FileOutputStream("output/" + sizeapache[op] + "." + t + "t." + run + ".out.txt");
							OutputStreamWriter out = new OutputStreamWriter(fos, "UTF-8");
							out.write(Helper.verbose(coveringArray, startTime, finishTime));
							out.write(coveringArray.toString());
							out.flush();
							out.close();
							System.out.print(Helper.display(coveringArray, startTime, finishTime) + "," + c + "," + d + "\n");
							Helper.clear();
						}
	}

	public static void TestBinary() throws Exception {
		long startTime, finishTime;
		int t = 2;
		HashMap<String, Constraint> tempConstraintMap;

		CoveringArray coveringArray;
		startTime = System.currentTimeMillis();
		SimulatedAnnealing sa = new SimulatedAnnealing(neiGen[index], 1, 0.001, 0.0001);
		Helper.setT(t);
		Helper.parseInputFile("input/mysql/12o.txt");
		Helper.setPresentationLayer(new ConsolePresenter());
		tempConstraintMap = Helper.copyConstraintMap(Helper.getConfigSpaceModel().getConstraintMap());
		Constraint constraint = Helper.getConfigSpaceModel().getConstraintMap().get("sys");
		Helper.getConfigSpaceModel().getConstraintMap().clear();
		Helper.getConfigSpaceModel().getConstraintMap().put("sys", constraint);
		coveringArray = sa.anneal(20, Helper.getInitialSoln());
		Helper.getConfigSpaceModel().setConstraints(tempConstraintMap);
		coveringArray = sa.search(20, 70, coveringArray);
		finishTime = System.currentTimeMillis();
		FileOutputStream fos = new FileOutputStream("output/temp.out.txt");
		OutputStreamWriter out = new OutputStreamWriter(fos, "UTF-8");
		out.write(Helper.verbose(coveringArray, startTime, finishTime));
		out.write(coveringArray.toString());
		out.flush();
		out.close();
		System.out.print(1 + "\t" + Helper.display(coveringArray, startTime, finishTime));
		Helper.clear();
	}

	public static void TestOne() throws Exception {
		long startTime, finishTime;
		int t = 2;

		startTime = System.currentTimeMillis();
		SimulatedAnnealing sa = new SimulatedAnnealing(neiGen[index], 1, 0.001, 0.0001);
		Helper.setPresentationLayer(new ConsolePresenter());
		Helper.setT(t);
		Helper.parseInputFile("input/mysql_space_model_12.txt");
		CoveringArray coveringArray = sa.anneal(45, Helper.getInitialSoln());
		finishTime = System.currentTimeMillis();
		FileOutputStream fos = new FileOutputStream("output-" + neiGen[2] + "/mysql_space_model_12." + t + "t.out.txt");
		OutputStreamWriter out = new OutputStreamWriter(fos, "UTF-8");
		out.write(Helper.verbose(coveringArray, startTime, finishTime));
		out.write(coveringArray.toString());
		out.flush();
		out.close();
		System.out.print(1 + "\t" + Helper.display(coveringArray, startTime, finishTime));
		Helper.clear();
	}
}
