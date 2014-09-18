package interactionTesting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import base.utils.Helper;

import com.google.common.collect.Sets;

public class Constraint {

	Integer[] referedOptionIdexes;
	List<List<String>> validCombList = new ArrayList<List<String>>();

	public Constraint(String constraint) {
		String[] infixElements = toInfixElements(constraint);
		String[] postfixElements = toPostFix(infixElements);
		fillValidCombList(postfixElements);
	}

	public String toString() {
		String result = "Refered Options and Valid Combo list\n";
		for (Integer integer : referedOptionIdexes)
			result = result + "\t" + integer;
		result = result + "\n";
		for (List<String> list : validCombList) {
			for (String string : list)
				result = result + "\t" + string;
			result = result + "\n";
		}
		return result;
	}

	private void fillValidCombList(String[] postfixElements) {
		List<Set<String>> sets = new ArrayList<Set<String>>();
		for (int i : referedOptionIdexes)
			sets.add(Helper.getConfigSpaceModel().getOptionList().get(i).getSettingSet());

		Set<List<String>> cartesianProduct = Sets.cartesianProduct(sets);
		for (List<String> list : cartesianProduct)
			if (isItAllright(list, postfixElements))
				validCombList.add(list);

	}

	public Integer[] getReferedOptionIdexes() {
		return referedOptionIdexes;
	}

	public List<List<String>> getValidCombList() {
		return validCombList;
	}

	public boolean hasOption(int optionIndex) {
		int index = Arrays.binarySearch(referedOptionIdexes, optionIndex);
		if (index < 0)
			return false;
		else
			return true;
	}

	private boolean isItAllright(List<String> list, String[] postfixElements) {
		Stack<Boolean> stack = new Stack<Boolean>();
		for (String string : postfixElements) {
			if (string.contains("=")) {
				String[] couple = string.split("=");
				stack.push(couple[1].equalsIgnoreCase(list.get(Arrays.binarySearch(referedOptionIdexes, Integer.parseInt(couple[0])))));
			} else if (string.equals("!")) {
				Boolean operand = stack.pop();
				stack.push(!operand);
			} else {
				Boolean operand1 = stack.pop();
				Boolean operand2 = stack.pop();
				if (string.equalsIgnoreCase("&"))
					stack.push(operand1 & operand2);
				else if (string.equalsIgnoreCase("|"))
					stack.push(operand1 | operand2);
				else if (string.equalsIgnoreCase(">"))
					stack.push(!operand2 | operand1);
			}
		}
		return stack.pop();
	}

	private int priority(String operator) {
		if (operator.equals("!"))
			return 4;
		if (operator.equals("&"))
			return 3;
		if (operator.equals("|"))
			return 2;
		if (operator.equals(">"))
			return 1;
		return 0;
	}

	private String[] toInfixElements(String constraint) {
		List<String> list = new ArrayList<String>();
		ArrayList<Integer> referedOptions = new ArrayList<Integer>();
		String[] pieces = constraint.split(" ");
		for (String piece : pieces) {
			if (piece.length() == 1)
				list.add(piece);
			else {
				String current = "";
				char[] charArray = piece.toCharArray();
				for (char ch : charArray) {
					if (ch == '(' || ch == ')' || ch == '!') {
						if (current.length() > 0) {
							list.add(current);
							current = "";
						}
						list.add("" + ch);
					} else if (Character.isLetter(ch) || ch == '=' || Character.isDigit(ch)) {
						if (ch == '=') {
							int findIndex = Helper.findIndex(current);
							if (!referedOptions.contains(findIndex))
								referedOptions.add(findIndex);
							current = "" + findIndex;
						}
						current += "" + ch;
					}
				}
				if (current.length() > 0) {
					list.add(current);
					current = "";
				}
			}
		}
		referedOptionIdexes = new Integer[referedOptions.size()];
		referedOptionIdexes = referedOptions.toArray(referedOptionIdexes);
		Arrays.sort(referedOptionIdexes);

		String[] strings = new String[list.size()];
		strings = list.toArray(strings);
		return strings;
	}

	private String[] toPostFix(String[] infixElements) {
		List<String> result = new ArrayList<String>();
		Stack<String> stack = new Stack<String>();
		for (String string : infixElements) {
			if (string.length() > 1)
				result.add(string);
			else if (string.equalsIgnoreCase("("))
				stack.push(string);
			else if (string.equalsIgnoreCase(")")) {
				while (!stack.peek().equalsIgnoreCase("("))
					result.add(stack.pop());
				stack.pop();
			} else {
				if (stack.isEmpty())
					stack.push(string);
				else {
					while (!stack.isEmpty() && priority(string) < priority(stack.peek()) && !stack.peek().equalsIgnoreCase("("))
						result.add(stack.pop());
					stack.push(string);
				}
			}
		}
		while (!stack.isEmpty()) {
			String pop = stack.pop();
			if (!pop.equalsIgnoreCase("("))
				result.add(pop);
		}
		String[] resultArr = new String[result.size()];
		resultArr = result.toArray(resultArr);
		return resultArr;
	}
}