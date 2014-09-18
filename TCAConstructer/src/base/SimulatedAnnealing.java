package base;

import interactionTesting.Config;
import interactionTesting.CoveringArray;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.paukov.combinatorics.CombinatoricsVector;
import org.paukov.combinatorics.Iterator;

import base.neighbouring.NeighbouringFactory;
import base.neighbouring.NeighbouringMethod;
import base.utils.Helper;

public class SimulatedAnnealing {

	protected double temprature, coolingRate, stoppingCriterion;
	protected Random random = new Random();
	NeighbouringMethod neighbouringMethod;

	public SimulatedAnnealing(String neighbouring, double temprature, double coolingRate, double stoppingCriterion) {
		super();
		this.temprature = temprature;
		this.coolingRate = coolingRate;
		this.stoppingCriterion = stoppingCriterion;
		this.neighbouringMethod = NeighbouringFactory.newInstance(neighbouring);
	}

	public CoveringArray anneal(int N, CoveringArray CA) throws Exception {
		Helper.pairEnumeration(CA);
		CA.setMiss(Helper.getMissScore());
		Helper.setInitialMiss(CA.getMiss());
		return annealCore(CA);
	}

	public CoveringArray search(int lowerBound, int upperBound, CoveringArray CA) throws Exception {
		CoveringArray coveringArray = Helper.copyCA(CA);
		int N = (lowerBound + upperBound) / 2;
		Helper.setBsStep(0);
		while (upperBound >= lowerBound) {
			CA = Initializer.getInitSoln("HIS", N, N, CA, null);
			Helper.addToTotalAnnealIter(Helper.getIter());
			Helper.incrementbsStep();
			Helper.getAllPairList().clear();
			Helper.getMissingPairMap().clear();
			Helper.pairEnumeration(CA);
			CA.setMiss(Helper.getMissScore());
			if (Helper.getBsStep() == 1)
				Helper.setInitialMiss(CA.getMiss());
			CA = annealCore(CA);
			if (CA.getMiss() == 0) {
				upperBound = N - 1;
				coveringArray = Helper.copyCA(CA);
			} else {
				if (CA.getMiss() < coveringArray.getMiss())
					coveringArray = Helper.copyCA(CA);
				lowerBound = N + 1;
			}
			N = (lowerBound + upperBound) / 2;
		}
		return coveringArray;
	}

	public CoveringArray annealCore(CoveringArray M) {
		CoveringArray result = Helper.copyCA(M);
		double temp = temprature;
		Helper.setIter(0);
		while (result.getMiss() != 0 && stoppingCriterion < temp) {
			Helper.incrementIter();
			neighbouringMethod.generateNeigbour(M);
			int delta = computeDelta(M);
			if (delta < 0 || random.nextDouble() <= Math.exp(-delta / temp)) {
				accept(M);
				if (Helper.getMissScore() < result.getMiss())
					result = Helper.copyCA(M);
			} else
				temp = temp - coolingRate * temp;
		}
		return result;
	}

	private int computeDelta(CoveringArray m) {
		int missCount = 0;
		int t = m.getStrength();
		List<Integer> columnlist = new ArrayList<Integer>();
		int size = Helper.getConfigSpaceModel().getOptionList().size();
		for (int i = 0; i < size; i++)
			columnlist.add(i);
		for (Config config : Helper.getTransitionList()) {
			List<String> newConfig = Helper.toList(config.getRow());
			List<String> oldConfig = Helper.toList(m.getConfigArr()[config.getRowIndex()].getRow());

			Iterator<CombinatoricsVector<String>> newItr = Helper.getIterator(newConfig, t);
			Iterator<CombinatoricsVector<String>> oldItr = Helper.getIterator(oldConfig, t);
			Iterator<CombinatoricsVector<Integer>> columnIterator = Helper.getIterator(columnlist, t);

			while (newItr.hasNext()) {
				List<Integer> vector = columnIterator.next().getVector();

				String newTuple = Helper.toStr(newConfig, vector, newItr.next().getVector());
				String oldTuple = Helper.toStr(oldConfig, vector, oldItr.next().getVector());

				for (String tc : m.getConfigArr()[config.getRowIndex()].getAssociatedTestList())
					if (Helper.getAllPairList().getWeight(oldTuple, tc) == 1
							&& (!config.getAssociatedTestList().contains(tc) || !oldTuple.equalsIgnoreCase(newTuple)))
						missCount++;
				for (String tc : config.getAssociatedTestList())
					if (Helper.getAllPairList().getWeight(newTuple, tc) == 0)
						missCount--;
			}
		}
		return missCount;
	}

	private void accept(CoveringArray m) {
		int t = m.getStrength();
		List<Integer> columnlist = new ArrayList<Integer>();
		int size = Helper.getConfigSpaceModel().getOptionList().size();
		for (int i = 0; i < size; i++)
			columnlist.add(i);
		for (Config config : Helper.getTransitionList()) {
			List<String> newConfigSettings = Helper.toList(config.getRow());
			int index = config.getRowIndex();
			List<String> oldConfig = Helper.toList(m.getConfigArr()[index].getRow());

			Iterator<CombinatoricsVector<String>> newItr = Helper.getIterator(newConfigSettings, t);
			Iterator<CombinatoricsVector<String>> oldItr = Helper.getIterator(oldConfig, t);
			Iterator<CombinatoricsVector<Integer>> columnIterator = Helper.getIterator(columnlist, t);

			while (newItr.hasNext()) {
				List<Integer> vector = columnIterator.next().getVector();

				String newTuple = Helper.toStr(newConfigSettings, vector, newItr.next().getVector());
				String oldTuple = Helper.toStr(oldConfig, vector, oldItr.next().getVector());

				for (String tc : m.getConfigArr()[config.getRowIndex()].getAssociatedTestList())
					if (Helper.getAllPairList().decrementAndGetWeight(oldTuple, tc) == 0)
						Helper.updateMissingPairMap(oldTuple, 1);

				for (String tc : config.getAssociatedTestList())
					if (Helper.getAllPairList().incrementAndGetWeight(newTuple, tc) == 1)
						Helper.updateMissingPairMap(newTuple, -1);
			}

			m.setConfig(config);
			m.setMiss(Helper.getMissScore());
		}
	}
}