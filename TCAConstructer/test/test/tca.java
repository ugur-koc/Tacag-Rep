package test;

public class tca {

	private int n; // number of rows
	private int v; // number of binary options
	private int p; // number of test cases

	private int rowWordCnt;

	private int[] rowWordIdx;
	private int[] rowBitIdx;
	private long[] bitMasks;

	private long[][][] coveringSetInfo;

	public tca(int n, int v, int p) {
		this.n = n;
		this.v = v;
		this.p = p;
	}

	public void init() {

		// compute the number of words (assumed to be 64 bits) required
		// to represent the rows
		rowWordCnt = (int) Math.ceil(n / (double) 64);

		// compute the row word idx and bit index
		rowWordIdx = new int[n];
		rowBitIdx = new int[n];
		rowWordIdx[0] = 0;
		rowBitIdx[0] = 0;
		for (int row = 1; row < n; row++) {
			rowWordIdx[row] = (int) (row / 64);
			rowBitIdx[row] = row % 64;
		}

		// compute the bit masks
		bitMasks = new long[64];
		long mask;
		for (int bit = 0; bit < 64; bit++) {
			mask = 1;
			bitMasks[bit] = (mask << (64 - bit - 1));
		}

		coveringSetInfo = new long[p][(2 * v)][rowWordCnt];
		for (int testIdx = 0; testIdx < p; testIdx++) {
			for (int settingIdx = 0; settingIdx < (2 * v); settingIdx++) {
				for (int wordIdx = 0; wordIdx < rowWordCnt; wordIdx++) {
					coveringSetInfo[testIdx][settingIdx][wordIdx] = (long) 0;
				}
			}
		}
	}

	public void set(int testIdx, int settingIdx, int rowIdx) {
		coveringSetInfo[testIdx][settingIdx][rowWordIdx[rowIdx]] |= bitMasks[rowBitIdx[rowIdx]];
	}

	public void unset(int testIdx, int settingIdx, int rowIdx) {
		coveringSetInfo[testIdx][settingIdx][rowWordIdx[rowIdx]] &= (~bitMasks[rowBitIdx[rowIdx]]);
	}

	public boolean isSet(int testIdx, int settingIdx, int rowIdx) {
		if ((coveringSetInfo[testIdx][settingIdx][rowWordIdx[rowIdx]] & bitMasks[rowBitIdx[rowIdx]]) != 0)
			return true;
		return false;
	}

	public boolean isCovered(int testIdx, int setting1, int setting2, int excludeRowIdx) {

		int excludeRowWordIdx = rowWordIdx[excludeRowIdx];
		long excludeRowBitMask = ~bitMasks[rowBitIdx[excludeRowIdx]];

		for (int wordIdx = 0; wordIdx < rowWordCnt; wordIdx++) {
			if (wordIdx != excludeRowWordIdx) {
				if ((coveringSetInfo[testIdx][setting1][wordIdx] & coveringSetInfo[testIdx][setting2][wordIdx]) != 0)
					return true;
			} else {
				if ((excludeRowBitMask & coveringSetInfo[testIdx][setting1][wordIdx] & coveringSetInfo[testIdx][setting2][wordIdx]) != 0)
					return true;
			}

		}

		return false;
	}

	public void prettyPrint(int testIdx, int settingIdx) {
		System.out.print(testIdx + ": {");
		for (int rowIdx = 0; rowIdx < n; rowIdx++) {
			if (isSet(testIdx, settingIdx, rowIdx)) {
				System.out.print(rowIdx + ", ");
			}
		}
		System.out.println("}");
	}

	public static void main(String[] args) {
		tca tca = new tca(100, 100, 100);
		tca.init();

		tca.set(0, 0, 65);
		tca.set(0, 0, 3);
		tca.set(0, 0, 99);

		tca.set(0, 2, 65);
		tca.set(0, 2, 4);
		tca.set(0, 2, 78);

		tca.prettyPrint(0, 0);
		tca.prettyPrint(0, 2);

		for (long i = 0; i < 1000000; i++) {
			tca.isCovered(0, 0, 2, 3);
		}

		if (tca.isCovered(0, 0, 2, 3)) {
			System.out.println("Covered...");
		} else {
			System.out.println("NOT Covered...");

		}

	}

}