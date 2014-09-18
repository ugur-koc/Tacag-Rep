package interactionTesting;

import java.util.LinkedHashSet;
import java.util.Set;

public class Option {

	public String name;
	private int settingCount;
	private String[] settings;
	private int index;

	public int getColumnNumber() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public Option(String name, String[] settings, int rowNumber) {
		this.name = name;
		this.settings = settings;
		this.index = rowNumber;
		settingCount = settings.length;
	}

	public Set<String> getSettingSet() {
		Set<String> ss = new LinkedHashSet<String>();
		for (String string : settings)
			ss.add(string);
		return ss;
	}

	public int getSettingCount() {
		return settingCount;
	}

	public String[] getAllSettings() {
		return settings;
	}

	public String getSetting(int i) {
		return settings[i];
	}

	public void setSettingCount(int settingCount) {
		this.settingCount = settingCount;
	}

	public void setSettings(String[] settings) {
		this.settings = settings;
	}

	public String getName() {
		return name;
	}

}
