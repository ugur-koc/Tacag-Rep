package test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class TestResult {

	static Scanner scanner;

	public static void main(String[] args) throws FileNotFoundException {

		String[] sizemysql = { "12", "15", "20", "24", "30", "40", "60" };
		String[] sizeapache = { "13", "17", "22", "26", "33", "44", "65" };
		for (int t = 3; t < 4; t++)
			for (int i = 0; i < sizeapache.length-1; i++) {
				for (int j = 1; j < 6; j++) {
					scanner = new Scanner(new FileInputStream("output/output - mysql/" + sizemysql[i] + "." + t + "t." + j + ".out" + ".txt"), "UTF-8");
					scanner.nextLine();
					scanner.nextLine();
					String[] time = scanner.nextLine().split(":");
					scanner.nextLine();
					scanner.nextLine();
					scanner.nextLine();
					scanner.nextLine();
					String[] sizes = scanner.nextLine().split(":")[1].split("x");
					String n = sizes[0];
					String m = sizes[1];
					System.out.println(m + "\t" + n + "\t" + time[1] + "\t"+ time[2] + "\t"+ time[3] + "\t");
					scanner.close();
				}
				System.out.println();
			}

	}
}
