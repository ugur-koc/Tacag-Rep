package test;

import java.util.HashMap;

public class Bounds {

	public static HashMap<String, Integer> map = new HashMap<String, Integer>();

	public static int getBound(String string) {
		if (map.size() < 1) {
			// v=2 t=2
			map.put("10o.2v.2t", 8);
			map.put("20o.2v.2t", 10);
			map.put("30o.2v.2t", 11);
			map.put("40o.2v.2t", 11);
			map.put("50o.2v.2t", 11);
			map.put("60o.2v.2t", 12);
			map.put("70o.2v.2t", 12);
			map.put("80o.2v.2t", 13);
			map.put("90o.2v.2t", 13);
			map.put("100o.2v.2t", 13);

			// v=3 t=2
			map.put("10o.3v.2t", 19);
			map.put("20o.3v.2t", 21);
			map.put("30o.3v.2t", 23);
			map.put("40o.3v.2t", 24);
			map.put("50o.3v.2t", 26);
			map.put("60o.3v.2t", 27);
			map.put("70o.3v.2t", 27);
			map.put("80o.3v.2t", 29);
			map.put("90o.3v.2t", 30);
			map.put("100o.3v.2t", 30);

			// v=4 t=2
			map.put("10o.4v.2t", 29);
			map.put("20o.4v.2t", 37);
			map.put("30o.4v.2t", 41);
			map.put("40o.4v.2t", 44);
			map.put("50o.4v.2t", 46);
			map.put("60o.4v.2t", 49);
			map.put("70o.4v.2t", 50);
			map.put("80o.4v.2t", 50);
			map.put("90o.4v.2t", 52);
			map.put("100o.4v.2t", 52);

			// v=2 t=3
			map.put("10o.2v.3t", 18);
			map.put("20o.2v.3t", 25);
			map.put("30o.2v.3t", 28);
			map.put("40o.2v.3t", 33);
			map.put("50o.2v.3t", 36);
			map.put("60o.2v.3t", 38);
			map.put("70o.2v.3t", 39);
			map.put("80o.2v.3t", 42);
			map.put("90o.2v.3t", 44);
			map.put("100o.2v.3t", 45);

			// v=3 t=3
			map.put("10o.3v.3t", 66);
			map.put("20o.3v.3t", 92);
			map.put("30o.3v.3t", 106);
			map.put("40o.3v.3t", 118);
			map.put("50o.3v.3t", 126);
			map.put("60o.3v.3t", 134);
			map.put("70o.3v.3t", 141);
			map.put("80o.3v.3t", 147);
			map.put("90o.3v.3t", 154);
			map.put("100o.3v.3t", 159);

			// v=4 t=3
			map.put("10o.4v.3t", 155);
			map.put("20o.4v.3t", 215);
			map.put("30o.4v.3t", 255);
			map.put("40o.4v.3t", 282);
			map.put("50o.4v.3t", 306);
			map.put("60o.4v.3t", 324);
			map.put("70o.4v.3t", 338);
			map.put("80o.4v.3t", 354);
			map.put("90o.4v.3t", 368);
			map.put("100o.4v.3t", 378);

			// v=2 t=4
			map.put("10o.2v.4t", 43);
			map.put("20o.2v.4t", 65);
			map.put("30o.2v.4t", 80);
			map.put("40o.2v.4t", 90);
			map.put("50o.2v.4t", 98);
			map.put("60o.2v.4t", 104);
			map.put("70o.2v.4t", 111);
			map.put("80o.2v.4t", 115);
			map.put("90o.2v.4t", 120);
			map.put("100o.2v.4t", 125);

			// v=3 t=4
			map.put("10o.3v.4t", 228);
			map.put("20o.3v.4t", 358);
			map.put("30o.3v.4t", 446);
			map.put("40o.3v.4t", 513);
			map.put("50o.3v.4t", 559);
			map.put("60o.3v.4t", 596);
			map.put("70o.3v.4t", 634);
			map.put("80o.3v.4t", 663);
			map.put("90o.3v.4t", 690);
			map.put("100o.3v.4t", 714);

			// v=4 t=4
			map.put("10o.4v.4t", 784);
			map.put("20o.4v.4t", 1165);
			map.put("30o.4v.4t", 1448);
			map.put("40o.4v.4t", 1661);
			map.put("50o.4v.4t", 1821);
			map.put("60o.4v.4t", 1958);
			map.put("70o.4v.4t", 2072);
			map.put("80o.4v.4t", 2169);
			map.put("90o.4v.4t", 2257);
			map.put("100o.4v.4t", 2337);
		}
		return map.get(string);
	}
}
