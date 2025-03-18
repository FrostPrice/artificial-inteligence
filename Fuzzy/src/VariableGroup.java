import java.util.ArrayList;
import java.util.HashMap;

public class VariableGroup {
	private ArrayList<FuzzyVariable> variableList;

	public VariableGroup() {
		variableList = new ArrayList<>();
	}

	public void add(FuzzyVariable variable) {
		variableList.add(variable);
	}

	public void fuzzify(float value, HashMap<String, Float> fuzzyVariables) {
		for (FuzzyVariable variable : variableList) {
			float fuzzifiedValue = variable.fuzzify(value);
			fuzzyVariables.put(variable.getName(), fuzzifiedValue);
		}
	}
}