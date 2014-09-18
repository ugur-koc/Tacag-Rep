package test;

public class SoftwareUnderTest {

	String name;
	String[] opts;

	public SoftwareUnderTest(String name, String[] options) {
		super();
		this.name = name;
		this.opts = options;
	}

	public String getName() {
		return name;
	}

	public String[] getOpts() {
		return opts;
	}
}