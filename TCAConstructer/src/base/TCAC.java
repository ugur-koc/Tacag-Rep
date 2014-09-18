package base;

import interactionTesting.CoveringArray;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.concurrent.TimeUnit;

import presentation.ConsolePresenter;
import presentation.PresentationLayer;
import base.utils.Helper;

public class TCAC {
	static double coolR = 0.001;
	static double temprature = 5;
	private static PresentationLayer presenter = new ConsolePresenter();

	public static void main(String[] args) throws Exception {
		if (args.length != 6)
			throw new TCACException("Invalid usage, use with params : t IS NS inputfilepath loverbound upperbound");
		int t = Integer.parseInt(args[0]);
		String IS = args[1];
		String NS = args[2];
		String path = args[3];
		int lb = Integer.parseInt(args[4]);
		int ub = Integer.parseInt(args[5]);

		long initStart = System.currentTimeMillis();
		Helper.parseInputFile(path);
		Helper.setT(t);
		CoveringArray initSoln = Initializer.getInitSoln(IS, lb, ub, Helper.getInitialSoln(), new SimulatedAnnealing("AVO", temprature, coolR, 0.0001));
		long initFinish = System.currentTimeMillis();

		long annealStart = System.currentTimeMillis();
		Helper.clear();
		Helper.parseInputFile(path);
		Helper.setInitialSoln(initSoln);
		Helper.setT(t);
		Helper.setPresentationLayer(presenter);
		SimulatedAnnealing sa = new SimulatedAnnealing(NS, temprature, coolR, 0.0001);
		CoveringArray coveringArray = sa.search(lb, ub, Helper.getInitialSoln());
		long annealFinish = System.currentTimeMillis();

		int lastIndexOf = path.lastIndexOf("/");
		if (lastIndexOf == -1)
			lastIndexOf = 0;
		FileOutputStream fos = new FileOutputStream("output" + path.substring(lastIndexOf, path.lastIndexOf(".")) + "." + t + "t." + IS + "x" + NS + ".txt");
		OutputStreamWriter out = new OutputStreamWriter(fos, "UTF-8");
		out.write(Helper.verbose(coveringArray, annealStart, annealFinish));
		out.write(coveringArray.toString());
		out.flush();
		out.close();

		System.out.print(Helper.getConfigSpaceModel().getOptionList().size() + "," + t + "," + temprature + "," + coolR + "," + IS + ","
				+ Helper.getInitialSoln().getRowCount() + "," + coveringArray.getRowCount() + "," + Helper.getInitialMiss() + "," + coveringArray.getMiss()
				+ "," + NS + "," + Helper.getTotalAnnealIter() + "," + Helper.getBsStep() + "," + lb + "," + ub + ","
				+ TimeUnit.MILLISECONDS.toSeconds(Math.abs(initFinish - initStart)) + ","
				+ TimeUnit.MILLISECONDS.toSeconds(Math.abs(annealFinish - annealStart)) + ","
				+ TimeUnit.MILLISECONDS.toSeconds(Math.abs(annealFinish - annealStart) + Math.abs(initFinish - initStart)) + "\n");
	}

	public TCAC(PresentationLayer present) {
		presenter = present;
	}
}
