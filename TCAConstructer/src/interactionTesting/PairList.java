package interactionTesting;

import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

@SuppressWarnings("serial")
public class PairList extends HashMap<String, HashMap<String, AtomicInteger>> {

	public void addPair(String tuple, String tc, int weight) {
		if (this.containsKey(tuple))
			this.get(tuple).put(tc, new AtomicInteger(weight));
		else {
			HashMap<String, AtomicInteger> hashMap = new HashMap<String, AtomicInteger>();
			hashMap.put(tc, new AtomicInteger(weight));
			this.put(tuple, hashMap);
		}
	}

	public int getWeight(String tuple, String tc) {
		return get(tuple).get(tc).intValue();
	}

	public PairList() {
		super();
	}

	public void incrementWeight(String tuple, String tc) {
		this.get(tuple).get(tc).incrementAndGet();
	}

	public int decrementAndGetWeight(String tuple, String tc) {
		return this.get(tuple).get(tc).decrementAndGet();
	}

	public boolean isNotUniqueInCA(String tuple, String tc) {
		if (this.get(tuple).get(tc).intValue() > 1)
			return true;
		else
			return false;
	}

	public Set<java.util.Map.Entry<String, HashMap<String, AtomicInteger>>> getTupleSet() {
		return this.entrySet();
	}

	public int getWeight(String key) {
		int weight = 0;
		for (java.util.Map.Entry<String, AtomicInteger> entry : get(key).entrySet())
			weight += entry.getValue().intValue();
		return weight;
	}

	public int incrementAndGetWeight(String tuple, String tc) {
		return this.get(tuple).get(tc).incrementAndGet();
	}

}
