package interactionTesting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ConfigSpaceModel {

	public List<Option> optionList = new ArrayList<Option>();
	public ArrayList<String> testCaseList = new ArrayList<String>();
	public HashMap<String, Constraint> constraintMap = new HashMap<String, Constraint>();
	public HashMap<String, Integer> testCaseMap = new HashMap<String, Integer>();
	public String name;

	public List<Option> getOptionList() {
		return optionList;
	}

	public void setOptions(List<Option> options) {
		this.optionList = options;
	}

	public ArrayList<String> getTestCaseList() {
		return testCaseList;
	}

	public void setTestCases(ArrayList<String> testCases) {
		this.testCaseList = testCases;
	}

	public ConfigSpaceModel() {
		testCaseMap.put("t1-mysql", 86);
		testCaseMap.put("t2-mysql", 60);
		testCaseMap.put("t3-mysql", 33);
		testCaseMap.put("t4-mysql", 28);
		testCaseMap.put("t5-mysql", 22);
		testCaseMap.put("t6-mysql", 18);
		testCaseMap.put("t7-mysql", 17);
		testCaseMap.put("t8-mysql", 17);
		testCaseMap.put("t9-mysql", 16);
		testCaseMap.put("t10-mysql", 6);
		testCaseMap.put("t11-mysql", 4);
		testCaseMap.put("t12-mysql", 4);
		testCaseMap.put("t13-mysql", 4);
		testCaseMap.put("t14-mysql", 4);
		testCaseMap.put("t15-mysql", 2);
		testCaseMap.put("t16-mysql", 2);
		testCaseMap.put("t17-mysql", 1);
		testCaseMap.put("t18-mysql", 1);
		testCaseMap.put("t19-mysql", 1);
		testCaseMap.put("t20-mysql", 1);
		testCaseMap.put("t21-mysql", 1);
		testCaseMap.put("t22-mysql", 1);
		testCaseMap.put("t23-mysql", 1);
		testCaseMap.put("t24-mysql", 1);
		testCaseMap.put("t25-mysql", 1);
		testCaseMap.put("t26-mysql", 1);
		testCaseMap.put("t27-mysql", 1);
		testCaseMap.put("t28-mysql", 1);
		testCaseMap.put("t29-mysql", 1);
		testCaseMap.put("t30-mysql", 1);
		testCaseMap.put("sys-mysql", 1);

		testCaseMap.put("t1-apache", 172);
		testCaseMap.put("t2-apache", 74);
		testCaseMap.put("t3-apache", 26);
		testCaseMap.put("t4-apache", 22);
		testCaseMap.put("t5-apache", 21);
		testCaseMap.put("t6-apache", 16);
		testCaseMap.put("t7-apache", 11);
		testCaseMap.put("t8-apache", 8);
		testCaseMap.put("t9-apache", 7);
		testCaseMap.put("t10-apache", 5);
		testCaseMap.put("t11-apache", 4);
		testCaseMap.put("t12-apache", 3);
		testCaseMap.put("t13-apache", 2);
		testCaseMap.put("t14-apache", 2);
		testCaseMap.put("t15-apache", 2);
		testCaseMap.put("t16-apache", 2);
		testCaseMap.put("t17-apache", 1);
		testCaseMap.put("sys-apache", 1);
	}

	public HashMap<String, Constraint> getConstraintMap() {
		return constraintMap;
	}

	public void setConstraints(HashMap<String, Constraint> constraints) {
		this.constraintMap = constraints;
	}

	public String getNAME() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
