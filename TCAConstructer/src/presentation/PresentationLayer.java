package presentation;

public interface PresentationLayer {

	public abstract void report_annealing_stads(String str);

	public abstract void report_neighbouring_stads(String str);

	public abstract void report_final_miss(int str);

	public abstract void report_iteration_count(long str);

	public abstract void report_CA_size(int str);

	public abstract void report_missing_pairs(int str);

	public abstract void report_missing_tuples(int str);

	public abstract void report_binarySearch_step(int str);
}
