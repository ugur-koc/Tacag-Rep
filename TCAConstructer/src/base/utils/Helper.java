package base.utils;

import interactionTesting.Config;
import interactionTesting.ConfigSpaceModel;
import interactionTesting.Constraint;
import interactionTesting.CoveringArray;
import interactionTesting.Option;
import interactionTesting.PairList;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.paukov.combinatorics.CombinatoricsVector;
import org.paukov.combinatorics.Generator;
import org.paukov.combinatorics.Iterator;
import org.paukov.combinatorics.combination.simple.SimpleCombinationGenerator;

import presentation.PresentationLayer;

import com.google.common.collect.Sets;

public class Helper {

	private static int strength;
	private static int iter = 0;
	private static int tupleCount = 0, validTupleCount = 0, pairCount = 0, validPairCount = 0, pairCountTC = 0, validPairCountTC = 0;
	private static int initialMiss, finalMiss;
	public static ConfigSpaceModel model = new ConfigSpaceModel();
	public static HashMap<String, Integer> missingPairMap = new HashMap<String, Integer>();
	public static CoveringArray initialSoln = null;
	public static PairList allPairList = new PairList();
	public static List<Config> transitionList = new ArrayList<Config>();
	public static Random random = new Random();

	public static PresentationLayer presentationLayer;
	private static int totalIter = 0;
	private static int bsStep = 0;

	public static PresentationLayer getPresenter() {
		return presentationLayer;
	}

	public static void setPresentationLayer(PresentationLayer presentationLayer) {
		Helper.presentationLayer = presentationLayer;
	}

	public static void parseInputFile(String string) throws Exception {
		Scanner scanner = new Scanner(new FileInputStream(string), "UTF-8");
		Helper.parseInputFile(scanner);
		scanner.close();
	}

	public static void parseInputFile(Scanner scanner) throws Exception {
		String currentLine;
		List<String> tempConstraintList = new ArrayList<String>();

		List<String[]> initialSolnRows = new ArrayList<String[]>();
		currentLine = scanner.nextLine();
		while (scanner.hasNext()) {
			if (currentLine.equalsIgnoreCase(""))
				currentLine = scanner.nextLine();
			if (currentLine.equalsIgnoreCase("Test Cases")) {
				currentLine = scanner.nextLine();
				while (scanner.hasNext() && currentLine.equalsIgnoreCase(""))
					currentLine = scanner.nextLine();
				if (!(currentLine.equalsIgnoreCase("Initial Soln") || currentLine.equalsIgnoreCase("Options") || currentLine.equalsIgnoreCase("") || currentLine
						.equalsIgnoreCase("Constraints"))) {
					String[] split = currentLine.split(",");
					for (String tc : split)
						model.getTestCaseList().add(tc);
				}
				if (scanner.hasNext())
					currentLine = scanner.nextLine();
			}
			if (currentLine.equalsIgnoreCase("Options")) {
				int rowNumber = 0;
				do {
					currentLine = scanner.nextLine();
					while (scanner.hasNext() && currentLine.equalsIgnoreCase(""))
						currentLine = scanner.nextLine();
					if (!(currentLine.equalsIgnoreCase("Initial Soln") || currentLine.equalsIgnoreCase("Test Cases") || currentLine
							.equalsIgnoreCase("Constraints"))) {
						String[] option = currentLine.split(":");
						String[] settings = option[1].split(",");
						model.getOptionList().add(new Option(option[0], settings, rowNumber++));
					}
				} while (scanner.hasNext()
						&& !(currentLine.equalsIgnoreCase("Initial Soln") || currentLine.equalsIgnoreCase("Test Cases") || currentLine
								.equalsIgnoreCase("Constraints")));
			}
			if (currentLine.equalsIgnoreCase("Initial Soln")) {

				do {
					currentLine = scanner.nextLine();
					while (scanner.hasNext() && currentLine.equalsIgnoreCase(""))
						currentLine = scanner.nextLine();
					if (!(currentLine.equalsIgnoreCase("Options") || currentLine.equalsIgnoreCase("Test Cases") || currentLine.equalsIgnoreCase("Constraints"))) {
						initialSolnRows.add(currentLine.split(","));
					}
				} while (scanner.hasNext()
						&& !(currentLine.equalsIgnoreCase("Options") || currentLine.equalsIgnoreCase("Test Cases") || currentLine
								.equalsIgnoreCase("Constraints")));
			}
			if (currentLine.equalsIgnoreCase("Constraints")) {
				do {
					currentLine = scanner.nextLine();
					while (scanner.hasNext() && currentLine.equalsIgnoreCase(""))
						currentLine = scanner.nextLine();
					if (!(currentLine.equalsIgnoreCase("Initial Soln") || currentLine.equalsIgnoreCase("Options") || currentLine.equalsIgnoreCase("Test Cases")))
						tempConstraintList.add(currentLine);
				} while (scanner.hasNext()
						&& !(currentLine.equalsIgnoreCase("Initial Soln") || currentLine.equalsIgnoreCase("Options") || currentLine
								.equalsIgnoreCase("Test Cases")));
			}
		}
		String sysWide = "";
		if (tempConstraintList.size() > 0) {
			String[] firstConst = tempConstraintList.get(0).split(":");
			if (firstConst[0].equalsIgnoreCase("sys"))
				sysWide = "(" + firstConst[1] + ") & ";
			model.getConstraintMap().put(firstConst[0], new Constraint(firstConst[1]));
			for (int i = 1; i < tempConstraintList.size(); i++) {
				String[] split = tempConstraintList.get(i).split(":");
				model.getConstraintMap().put(split[0], new Constraint(sysWide + "(" + split[1] + ")"));
			}
		} else
			model.getConstraintMap().put("sys", null);

		for (Entry<String, Constraint> entry : model.getConstraintMap().entrySet())
			model.getTestCaseList().remove(entry.getKey());

		if (initialSolnRows.size() > 0) {
			initialSoln = new CoveringArray(initialSolnRows.size(), model.getOptionList().size(), 0);
			for (int i = 0; i < initialSolnRows.size(); i++)
				getInitialSoln().addRow(i, initialSolnRows.get(i), Helper.buildAssociatedTestList(initialSolnRows.get(i)));
		}
	}

	public static boolean isConfigValid(String[] config, String constraint_id) {
		Constraint constraint = model.getConstraintMap().get(constraint_id);
		if (constraint == null)
			return true;

		int match = 0, index = 0;
		ArrayList<String> referedOptions = new ArrayList<String>();
		for (Integer integer : constraint.getReferedOptionIdexes())
			referedOptions.add(config[integer]);
		while (match != referedOptions.size() && index < constraint.getValidCombList().size()) {
			match = 0;
			List<String> list = constraint.getValidCombList().get(index);
			for (int i = 0; i < list.size(); i++)
				if (referedOptions.get(i).equalsIgnoreCase(list.get(i)))
					match++;
			index++;
		}
		if (match == referedOptions.size())
			return true;
		else
			return false;
	}

	public static HashMap<Integer, String> offerSetting(String[] tupleElements, String[] config, String constraint_id) {

		HashMap<Integer, String> tempMap = new HashMap<Integer, String>();
		Constraint constraint = model.getConstraintMap().get(constraint_id);
		if (constraint == null)
			return tempMap;

		int maxMatch = constraint.getReferedOptionIdexes().length;
		HashMap<Integer, String> resultMap = new HashMap<Integer, String>();
		for (List<String> list : constraint.getValidCombList()) {
			int index = 0;
			for (Integer integer : constraint.getReferedOptionIdexes()) {
				if (!config[integer].equalsIgnoreCase(list.get(index)))
					tempMap.put(integer, list.get(index));
				index++;
			}
			if (maxMatch >= tempMap.size()) {
				int strenght = tupleElements.length / 2;
				boolean fine = true;
				for (int i = 0; i < strenght; i++) {
					int option = Integer.parseInt(tupleElements[i]);
					if (tempMap.containsKey(option) && !tempMap.get(option).equalsIgnoreCase(tupleElements[strenght + i]))
						fine = false;
				}
				if (fine) {
					maxMatch = tempMap.size();
					resultMap = tempMap;
				}
			}
		}

		return resultMap;
	}

	public static boolean isTupleValid(int[] indexes, String[] tuple, String constraint_id) {
		Constraint constraint = model.getConstraintMap().get(constraint_id);
		if (constraint == null)
			return true;

		int match = 0, index = 0;
		ArrayList<Integer> referedOptionsOfTuple = new ArrayList<Integer>();
		for (int i = 0; i < tuple.length; i++)
			if (constraint.hasOption(indexes[i]))
				referedOptionsOfTuple.add(i);

		while (match != referedOptionsOfTuple.size() && index < constraint.getValidCombList().size()) {
			match = 0;
			List<String> validComb = constraint.getValidCombList().get(index);
			for (int i = 0; i < referedOptionsOfTuple.size(); i++) {
				int binarySearch = Arrays.binarySearch(constraint.getReferedOptionIdexes(), indexes[referedOptionsOfTuple.get(i)]);
				if (tuple[referedOptionsOfTuple.get(i)].equalsIgnoreCase(validComb.get(binarySearch)))
					match++;
			}
			index++;
		}
		if (match == referedOptionsOfTuple.size())
			return true;
		else
			return false;
	}

	public static int findIndex(String str) {
		for (int i = 0; i < model.getOptionList().size(); i++)
			if (model.getOptionList().get(i).getName().equalsIgnoreCase(str))
				return i;
		return -1;
	}

	public static String formatTime(long startTime, long finishTime) {
		long timeDiff = Math.abs(finishTime - startTime);
		long hours = TimeUnit.MILLISECONDS.toHours(timeDiff);
		long minutes = TimeUnit.MILLISECONDS.toMinutes(timeDiff) - TimeUnit.HOURS.toMinutes(hours);
		long seconds = TimeUnit.MILLISECONDS.toSeconds(timeDiff) - TimeUnit.HOURS.toSeconds(hours) - TimeUnit.MINUTES.toSeconds(minutes);
		long millis = TimeUnit.MILLISECONDS.toMillis(timeDiff) - TimeUnit.HOURS.toMillis(hours) - TimeUnit.MINUTES.toMillis(minutes)
				- TimeUnit.SECONDS.toMillis(seconds);
		return (hours > 9 ? (hours + "") : ("0" + hours)) + ":" + (minutes > 9 ? (minutes + "") : ("0" + minutes)) + ":"
				+ (seconds > 9 ? (seconds + "") : ("0" + seconds)) + "." + millis;
	}

	public static String verbose(CoveringArray coveringArray, long startTime, long finishTime) {
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss:SSS");
		String string = "Start      :" + dateFormat.format(new Date(startTime));
		string += "\nFinish     :" + dateFormat.format(new Date(finishTime));
		string += "\nTime       :" + Helper.formatTime(startTime, finishTime);
		string += "\nIter number:" + iter;
		string += "\nAll Pairs  :" + Helper.getValidPairCountManual();
		string += "\nInit. miss :" + initialMiss;
		string += "\nFinal miss :" + coveringArray.getMiss();
		string += "\nCA size    :" + coveringArray.getRowCount() + "x" + coveringArray.getColumnCount();
		string += "\nCA         : \n";

		return string;
	}

	public static String display(CoveringArray coveringArray, long startTime, long finishTime) {
		String string = coveringArray.getColumnCount() + "," + coveringArray.getStrength() + "," + coveringArray.getRowCount();
		string += "," + Helper.getValidPairCountManual() + "," + TimeUnit.MILLISECONDS.toSeconds(Math.abs(finishTime - startTime));
		return string;
	}

	public static int hammingDistance(Config[] configArr, String[] cr1) {
		int distance = 0;
		for (Config config : configArr)
			if (config != null)
				for (int i = 0; i < cr1.length; i++)
					if (!config.getRow()[i].equalsIgnoreCase(cr1[i]))
						distance++;
		return distance;
	}

	public static void pairEnumeration(CoveringArray coveringArray) throws Exception {
		CombinatoricsVector<Option> initialVector = new CombinatoricsVector<Option>(model.getOptionList());
		Generator<Option> gen = new SimpleCombinationGenerator<Option>(initialVector, strength);
		org.paukov.combinatorics.Iterator<CombinatoricsVector<Option>> itr = gen.createIterator();
		Set<Entry<String, Constraint>> constraintSet = model.getConstraintMap().entrySet();
		if (model.getConstraintMap().size() == 1) {
			ArrayList<String> arrayList = new ArrayList<String>();
			for (int i = 0; i < coveringArray.getRowCount(); i++)
				coveringArray.getConfigArr()[i].setAssociatedTestList(arrayList);
		} else
			for (int i = 0; i < coveringArray.getRowCount(); i++)
				coveringArray.getConfigArr()[i].setAssociatedTestList(new ArrayList<String>());

		while (itr.hasNext()) {
			List<Option> vector = ((CombinatoricsVector<interactionTesting.Option>) itr.next()).getVector();
			List<Set<String>> settings = new ArrayList<Set<String>>();
			int[] columns = new int[strength];
			for (int i = 0; i < strength; i++) {
				columns[i] = vector.get(i).getColumnNumber();
				settings.add(vector.get(i).getSettingSet());
			}
			Set<List<String>> cartesianProduct = Sets.cartesianProduct(settings);
			tupleCount += cartesianProduct.size();
			for (List<String> tuple : cartesianProduct) {
				String[] tupleArr = new String[tuple.size()];
				tupleArr = tuple.toArray(tupleArr);

				if (Helper.isTupleValid(columns, tupleArr, "sys")) {
					validTupleCount++;
					String tupleStr = columns[strength - 1] + "," + tupleArr[0];
					for (int i = 1; i < tupleArr.length; i++)
						tupleStr = columns[strength - i - 1] + "," + tupleStr + "," + tupleArr[i];

					List<Integer> containingRows = coveringArray.getContainingRows(tupleStr);
					Helper.getMissingPairMap().put(tupleStr, 0);
					for (Entry<String, Constraint> constraint : constraintSet) {
						pairCount++;
						pairCountTC += model.testCaseMap.get(constraint.getKey() + "-" + Helper.getConfigSpaceModel().getNAME());
						if (Helper.isTupleValid(columns, tupleArr, constraint.getKey())) {
							validPairCount++;
							validPairCountTC += model.testCaseMap.get(constraint.getKey() + "-" + Helper.getConfigSpaceModel().getNAME());
							int weight = 0;
							for (Integer integer : containingRows)
								if (Helper.isConfigValid(coveringArray.getRow(integer), constraint.getKey())) {
									weight++;
									if (!coveringArray.getConfigArr()[integer].getAssociatedTestList().contains(constraint.getKey()))
										coveringArray.getConfigArr()[integer].getAssociatedTestList().add(constraint.getKey());
								}
							Helper.getAllPairList().addPair(tupleStr, constraint.getKey(), weight);
							if (weight == 0)
								Helper.getMissingPairMap().put(tupleStr, Helper.getMissingPairMap().get(tupleStr).intValue() + 1);
						}
					}
				}
			}
		}
	}

	public static ArrayList<String> buildAssociatedTestList(String[] row) {
		ArrayList<String> testListToAssociate = new ArrayList<String>();
		Set<Entry<String, Constraint>> entrySet = model.getConstraintMap().entrySet();
		for (Entry<String, Constraint> entry : entrySet)
			if (Helper.isConfigValid(row, entry.getKey()))
				testListToAssociate.add(entry.getKey());
		return testListToAssociate;
	}

	public static boolean preservesExistingTestList(ArrayList<String> oldTestList, ArrayList<String> newTestList) {
		for (String string : oldTestList)
			if (!newTestList.contains(string))
				return false;
		return true;
	}

	public static boolean hasNewlyCovered(String tuple, ArrayList<String> testListToAssociate) {
		for (String string : testListToAssociate)
			if (allPairList.getWeight(tuple, string) == 0)
				return true;
		return false;
	}

	public static String[] alterRow(String selectedTuple, String[] row, String randomTC) {
		String[] tupleElements = selectedTuple.split(",");
		HashMap<Integer, String> offeredSetting = Helper.offerSetting(tupleElements, row, randomTC);
		Set<Entry<Integer, String>> entrySet = offeredSetting.entrySet();
		for (Entry<Integer, String> entry : entrySet)
			row[entry.getKey()] = entry.getValue();
		return row;
	}

	public static String getTestCase(String name) {
		int i;
		for (i = 0; !Helper.getConfigSpaceModel().getTestCaseList().get(i).equalsIgnoreCase(name); i++)
			;
		return getConfigSpaceModel().getTestCaseList().get(i);
	}

	public static void incrementIter() {
		iter++;
	}

	public static void setIter(int iter) {
		Helper.iter = iter;
	}

	public static int getInitialMiss() {
		return initialMiss;
	}

	public static void setInitialMiss(int size) {
		initialMiss = size;
	}

	public static List<String> toList(String[] strings) {
		List<String> list = new ArrayList<String>();
		for (String string : strings)
			list.add(string);
		return list;
	}

	public static String toStr(List<String> config, List<Integer> vector, List<String> strings) {
		String result = vector.get(strength - 1) + "," + strings.get(0);
		for (int i = 1; i < strings.size(); i++)
			result = vector.get(strength - i - 1) + "," + result + "," + strings.get(i);
		return result;
	}

	public static int getValidPairCountManual() {
		int count = 0;
		Set<Entry<String, HashMap<String, AtomicInteger>>> entrySet = Helper.getAllPairList().entrySet();
		for (Entry<String, HashMap<String, AtomicInteger>> entry : entrySet)
			count += entry.getValue().size();
		return count;
	}

	public static <E> Iterator<CombinatoricsVector<E>> getIterator(List<E> config, int t) {
		CombinatoricsVector<E> vector = new CombinatoricsVector<E>(config);
		Generator<E> newgen = new SimpleCombinationGenerator<E>(vector, t);
		org.paukov.combinatorics.Iterator<CombinatoricsVector<E>> itr = newgen.createIterator();
		return itr;
	}

	public static void setT(int t2) {
		strength = t2;
	}

	public static CoveringArray getInitialSoln() {
		return initialSoln;
	}

	public static void setInitialSoln(CoveringArray coveringArray) {
		initialSoln = coveringArray;
	}

	public static PairList getAllPairList() {
		return allPairList;
	}

	public static int getFinalMiss() {
		return finalMiss;
	}

	public static void setFinalMiss(int finalMiss) {
		Helper.finalMiss = finalMiss;
	}

	public static HashMap<String, Integer> getMissingPairMap() {
		return missingPairMap;
	}

	public void setMissingPairs(HashMap<String, Integer> missingPairMap) {
		Helper.missingPairMap = missingPairMap;
	}

	public static ConfigSpaceModel getConfigSpaceModel() {
		return model;
	}

	public static List<Config> getTransitionList() {
		return transitionList;
	}

	public static HashMap<String, Constraint> copyConstraintMap(HashMap<String, Constraint> constraintMap) {
		HashMap<String, Constraint> result = new HashMap<String, Constraint>();
		for (Entry<String, Constraint> entry : constraintMap.entrySet())
			result.put(entry.getKey(), entry.getValue());
		return result;
	}

	public static void clear() {
		iter = 0;
		bsStep = 0;
		totalIter = 0;
		tupleCount = 0;
		validTupleCount = 0;
		pairCount = 0;
		validPairCount = 0;
		pairCountTC = 0;
		validPairCountTC = 0;
		Helper.getConfigSpaceModel().getConstraintMap().clear();
		Helper.getConfigSpaceModel().getTestCaseList().clear();
		Helper.getConfigSpaceModel().getOptionList().clear();
		Helper.getAllPairList().clear();
		Helper.getMissingPairMap().clear();
		Helper.setInitialSoln(null);
	}

	public static boolean isFineToRemove(String key) {
		for (Entry<String, AtomicInteger> entry : getAllPairList().get(key).entrySet())
			if (entry.getValue().intValue() <= 1)
				return false;
		return true;
	}

	public static String[] generateNewRow(CoveringArray coveringArray) {
		String[] cr1 = new String[Helper.getConfigSpaceModel().getOptionList().size()];
		for (int i = 0; i < Helper.getConfigSpaceModel().getOptionList().size(); i++)
			cr1[i] = Helper.getConfigSpaceModel().getOptionList().get(i)
					.getSetting(random.nextInt(Helper.getConfigSpaceModel().getOptionList().get(i).getSettingCount()));
		if (Helper.getMissScore() != 0) {
			String[] tupleElements = Helper.selectRandomMissingTuple().split(",");
			for (int j = 0; j < coveringArray.getStrength(); j++)
				cr1[Integer.parseInt(tupleElements[j])] = tupleElements[coveringArray.getStrength() + j];
			return cr1;
		}

		String[] cr2 = new String[Helper.getConfigSpaceModel().getOptionList().size()];
		for (int i = 0; i < Helper.getConfigSpaceModel().getOptionList().size(); i++)
			cr2[i] = Helper.getConfigSpaceModel().getOptionList().get(i)
					.getSetting(random.nextInt(Helper.getConfigSpaceModel().getOptionList().get(i).getSettingCount()));

		int distance1 = Helper.hammingDistance(coveringArray.getConfigArr(), cr1);
		int distance2 = Helper.hammingDistance(coveringArray.getConfigArr(), cr2);
		if (distance1 > distance2)
			return cr1;
		else
			return cr2;
	}

	public static int selectRow(CoveringArray coveringArray) {
		int row = 0, minSize = model.getConstraintMap().size();
		for (int i = 0; i < coveringArray.getRowCount(); i++)
			if (coveringArray.getConfigArr()[i].getAssociatedTestList().size() < minSize) {
				minSize = coveringArray.getConfigArr()[i].getAssociatedTestList().size();
				row = i;
			}
		return row;
	}

	public static Set<String> toSet(ArrayList<String> list) {
		Set<String> set = new HashSet<String>();
		for (String string : list)
			set.add(string);
		return set;
	}

	public static void updateState() {

	}

	public static int getMissScore() {
		int score = 0;
		for (Entry<String, Integer> entry : missingPairMap.entrySet())
			score += entry.getValue().intValue();
		return score;
	}

	public static void updateMissingPairMap(String oldTuple, int i) {
		Helper.getMissingPairMap().put(oldTuple, Helper.getMissingPairMap().get(oldTuple).intValue() + i);
	}

	public static int getIter() {
		return iter;
	}

	public static ArrayList<String> getMissingTupleList() {
		ArrayList<String> list = new ArrayList<String>();
		for (Entry<String, Integer> entry : missingPairMap.entrySet())
			if (entry.getValue().intValue() != 0)
				list.add(entry.getKey());
		return list;
	}

	public static String selectRandomMissingTuple() {
		Random random = new Random();
		ArrayList<String> list = new ArrayList<String>();
		for (Entry<String, Integer> entry : missingPairMap.entrySet())
			if (entry.getValue().intValue() != 0)
				list.add(entry.getKey());
		return list.get(random.nextInt(list.size()));
	}

	public static String selectRandomTC(String tuple) {
		Random random = new Random();
		ArrayList<String> list = new ArrayList<String>();
		HashMap<String, AtomicInteger> hashMap = allPairList.get(tuple);
		Set<Entry<String, AtomicInteger>> entrySet = hashMap.entrySet();
		for (Entry<String, AtomicInteger> entry : entrySet)
			if (entry.getValue().intValue() == 0)
				list.add(entry.getKey());
		return list.get(random.nextInt(list.size()));
	}

	public static String selectBestTuple() {
		String tuple = "";
		int max = 0;
		for (Entry<String, Integer> entry : missingPairMap.entrySet())
			if (entry.getValue().intValue() > max) {
				max = entry.getValue().intValue();
				tuple = entry.getKey();
			}
		return tuple;
	}

	public static CoveringArray copyCA(CoveringArray CA) {
		CoveringArray coveringArray = new CoveringArray(CA.getRowCount(), CA.getColumnCount(), CA.getStrength());
		for (int j = 0; j < CA.getConfigArr().length; j++) {
			ArrayList<String> tcList = new ArrayList<String>();
			ArrayList<String> associatedTestList = CA.getConfigArr()[j].getAssociatedTestList();
			for (String string : associatedTestList)
				tcList.add(string);
			coveringArray.addRow(j, CA.getConfigArr()[j].getRow(), tcList);
		}
		coveringArray.setMiss(CA.getMiss());
		return coveringArray;
	}

	public static void writeFile(String path, String str, boolean append) throws IOException {
		OutputStreamWriter stadFile1 = new OutputStreamWriter(new FileOutputStream(path, append), "UTF-8");
		stadFile1.write(str);
		stadFile1.flush();
		stadFile1.close();
	}

	public static int getStrength() {
		return strength;
	}

	public static void setStrength(int strenght) {
		Helper.strength = strenght;

	}

	public static void addToTotalAnnealIter(int iter) {
		totalIter += iter;
	}

	public static void incrementbsStep() {
		bsStep++;
	}

	public static int getTotalAnnealIter() {
		return totalIter;
	}

	public static int getBsStep() {
		return bsStep;
	}

	public static void setBsStep(int i) {
		Helper.bsStep = i;

	}

	public static int getTupleCount() {
		return tupleCount;
	}

	public static int getValidTupleCount() {
		return validTupleCount;
	}

	public static int getValidPairCount() {
		return validPairCount;
	}

	public static int getPairCount() {
		return pairCount;
	}
	public static int getValidPairCountTC() {
		return validPairCountTC;
	}

	public static int getPairCountTC() {
		return pairCountTC;
	}

}