import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {

	public static int n = 0;
	public static int m = 0;
	static int kh = 0;

	public static void main(String[] args) throws FileNotFoundException {

		 PrintStream out = new PrintStream(new FileOutputStream(args[1]));
		 System.setOut(out);

		int[][] inputMartix = readInputFile(args[0]);
		ArrayList<ArrayList<Literal>> kb = new ArrayList<ArrayList<Literal>>();
		//printMartix(inputMartix);
		kb = makeKB(inputMartix);
		if (plResolution(kb)) {
			System.out.println("1");
		} else {
			System.out.println("0");
		}
	}

	// READ INPUT
	// ---------------------------------------------------------
	private static int[][] readInputFile(String inputAddress) {

		List<String> inputList = new ArrayList<String>();
		int[][] inputMartix = null;

		BufferedReader br = null;
		try {

			String sCurrentLine;

			br = new BufferedReader(new FileReader(inputAddress));

			String firstLine = br.readLine();
			String[] nm = firstLine.split(" ");
			n = Integer.parseInt(nm[0]);
			m = Integer.parseInt(nm[1]);
			inputMartix = new int[m][m];

			while ((sCurrentLine = br.readLine()) != null) {
				inputList.add(sCurrentLine);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)
					br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		int i = 0;
		for (String string : inputList) {
			String[] s = string.split(" ");
			for (int j = 0; j < s.length; j++) {
				inputMartix[i][j] = Integer.parseInt(s[j]);
			}
			i++;
		}
		return inputMartix;
	}

	// PRINT MARIX
	// ---------------------------------------------------
	public static void printMartix(int[][] inputMartix) {
		System.out.println(Arrays.deepToString(inputMartix)
				.replaceAll("],", "]\r\n").replaceAll("]]", "]")
				.replace("[[", " [").replace("0", " 0").replace("1", " 1")
				.replace("- 1", "-1"));
		System.out.println("\n");
	}

	// PL-ROSEULTION
	// --------------------------------------------------------------------
	public static boolean plResolution(ArrayList<ArrayList<Literal>> kb) {

		ArrayList<ArrayList<Literal>> clauses = kb;

		while (true) {
//			System.out.println("----------");
//			printKB(clauses);
//			System.out.println("----------");
//			System.out.println(clauses.size());
			ArrayList<ArrayList<Literal>> newClauses = new ArrayList<ArrayList<Literal>>();
			for (int i = 0; i < clauses.size(); i++) {
				for (int j = i + 1; j < clauses.size(); j++) {
					ArrayList<ArrayList<Literal>> resolvents = new ArrayList<ArrayList<Literal>>();
					// for each pair (ci, cj) of clauses do plReolve()
					resolvents = plResolve(clauses.get(i), clauses.get(j),
							clauses);
					ArrayList<ArrayList<Literal>> optResolvents = new ArrayList<ArrayList<Literal>>();
					for (ArrayList<Literal> myClause : resolvents)
						optResolvents.add(revise_clause(myClause));
					resolvents = optResolvents;
					if (containsEmptyClause(resolvents)) {
						return false;
					}
					newClauses.addAll(resolvents);
				}
			}

			// System.out.println("new");
			// printKB(newClauses);

			// ArrayList<ArrayList<Literal>> newClauses2 = new
			// ArrayList<ArrayList<Literal>>();
			ArrayList<ArrayList<Literal>> newClauses3 = new ArrayList<ArrayList<Literal>>();
			// newClauses3.addAll(newClauses);
			for (ArrayList<Literal> clause_in_new : newClauses) {
				if ((!check_membership(clause_in_new, clauses))
						&& (!check_membership(clause_in_new, newClauses3))) {
					newClauses3.add(clause_in_new);
					// newClauses2.add(clause_in_new);
					// newClauses3.remove(clause_in_new);
					// for (int i = 0; i < newClauses3.size(); i++) {
					// if (my_check_equality(newClauses3.get(i), clause_in_new))
					// {
					// newClauses3.remove(i);
					// // break;
					// i--;
					// }
					// }
				}
			}

			if (newClauses3.size() == 0) {
				return true;
			} else {
				clauses.addAll(newClauses3);
			}
		}
	}

	private static ArrayList<Literal> revise_clause(ArrayList<Literal> myClause) {
		ArrayList<Literal> answer = new ArrayList<Literal>();
		for (int i = 0; i < myClause.size(); i++) {
			boolean flag = true;
			for (int j = 0; j < i; j++)
				if (myClause.get(i).getGuestNumber() == myClause.get(j)
						.getGuestNumber()
						&& myClause.get(i).getTableNumber() == myClause.get(j)
								.getTableNumber()
						&& myClause.get(i).isNegated() == myClause.get(j)
								.isNegated())
					flag = false;
			if (flag)
				answer.add(myClause.get(i));
		}
		return answer;
	}

	private static boolean check_membership(ArrayList<Literal> clause_in_new,
			ArrayList<ArrayList<Literal>> clauses) {
		for (ArrayList<Literal> myClause : clauses)
			if (my_check_equality(myClause, clause_in_new))
				return true;
		return false;
	}

	private static boolean my_check_equality(ArrayList<Literal> myClause,
			ArrayList<Literal> clause_in_new) {
		if (clause_in_new.size() != myClause.size())
			return false;
		for (int i = 0; i < clause_in_new.size(); i++)
			if (clause_in_new.get(i).getGuestNumber() != myClause.get(i)
					.getGuestNumber()
					|| clause_in_new.get(i).getTableNumber() != myClause.get(i)
							.getTableNumber()
					|| clause_in_new.get(i).isNegated() != myClause.get(i)
							.isNegated())
				return false;
		// TODO Auto-generated method stub
		return true;
	}

	// containsEmptyClause()
	// -------------------------------------------------
	private static boolean containsEmptyClause(
			ArrayList<ArrayList<Literal>> resolvents) {

		for (ArrayList<Literal> clause : resolvents) {
			if (clause.size() == 0) {
				return true;
			}
		}
		return false;
	}

	// PL-RESOLVE
	// -----------------------------------------------------
	private static ArrayList<ArrayList<Literal>> plResolve(
			ArrayList<Literal> clause_i, ArrayList<Literal> clause_j,
			ArrayList<ArrayList<Literal>> clauses) {
		ArrayList<ArrayList<Literal>> resolvents = new ArrayList<ArrayList<Literal>>();
		for (Literal literal_i : clause_i) {
			for (Literal literal_j : clause_j) {
				if (literal_i.getGuestNumber() == literal_j.getGuestNumber()
						&& literal_i.getTableNumber() == literal_j
								.getTableNumber()
						&& literal_i.isNegated() != literal_j.isNegated()) {
					// create new clause from ci and cj by removing
					// two complement literals and merge the rest
					// literals toghether

					boolean flag = false;
					ArrayList<Literal> newClause = new ArrayList<Literal>();
					// if (clause_i.size() == 1 && clause_j.size() == 1) {
					// newClause.add(literal_i);
					// newClause.add(literal_j);
					// } else {
					ArrayList<Literal> clause_i_without_literal_i = new ArrayList<Literal>();
					clause_i_without_literal_i.addAll(clause_i);
					clause_i_without_literal_i.remove(literal_i);
					ArrayList<Literal> clause_j_without_literal_j = new ArrayList<Literal>();
					clause_j_without_literal_j.addAll(clause_j);
					clause_j_without_literal_j.remove(literal_j);
					newClause.addAll(clause_i_without_literal_i);
					newClause.addAll(clause_j_without_literal_j);

					// if newClause has two complement literals inside it,
					// ignore it, otherwise add it to resolvents
					if (newClause.size() != 1) {
						for (Literal literal_new1 : newClause) {
							for (Literal literal_new2 : newClause) {
								if (literal_new1.getGuestNumber() == literal_new2
										.getGuestNumber()
										&& literal_new1.getTableNumber() == literal_new2
												.getTableNumber()
										&& literal_new1.isNegated() != literal_new2
												.isNegated()) {
									flag = true;
								}
							}
						}
					}

					// if newClause has two same literals inside it,
					// ignore it, otherwise add it to resolvents

					// if (newClause.size() != 1) {
					// for (Literal literal_new1 : newClause) {
					// for (Literal literal_new2 : newClause) {
					// if (literal_new1.getGuestNumber() == literal_new2
					// .getGuestNumber()
					// && literal_new1.getTableNumber() == literal_new2
					// .getTableNumber()
					// && literal_new1.isNegated() == literal_new2
					// .isNegated()) {
					// flag = true;
					// }
					// }
					// }
					// }

					// }

					// for (ArrayList<Literal> clause : clauses) {
					// if (clause.size() == newClause.size()) {
					// for (int i = 0; i < clause.size(); i++) {
					// if (clause.get(i).getGuestNumber() == newClause
					// .get(i).getGuestNumber()
					// && clause.get(i).getTableNumber() == newClause
					// .get(i).getTableNumber()
					// && clause.get(i).isNegated() == newClause
					// .get(i).isNegated()) {
					// flag = true;
					// }
					// }
					// }
					// }

					if (!flag) {
						resolvents.add(newClause);
					}
				}
			}
		}
		return resolvents;
	}

	// MAKE KB
	// -----------------------------------------------------------------------
	public static ArrayList<ArrayList<Literal>> makeKB(int[][] inputMartix) {
		ArrayList<ArrayList<Literal>> kb = new ArrayList<ArrayList<Literal>>();
		for (int i = 0; i < inputMartix.length; i++) {
			for (int j = i + 1; j < inputMartix.length; j++) {
				// if they are friends
				if (inputMartix[i][j] == 1) {
					int k = 1;
					while (k <= n) {
						ArrayList<Literal> c1 = new ArrayList<Literal>();
						ArrayList<Literal> c2 = new ArrayList<Literal>();
						c1.add(new Literal((i + 1), k, true));
						c1.add(new Literal((j + 1), k, false));
						c2.add(new Literal((j + 1), k, true));
						c2.add(new Literal((i + 1), k, false));
						kb.add(c1);
						kb.add(c2);
						k++;
					}
					// if they are enemy
				} else if (inputMartix[i][j] == -1) {
					int l = 1;
					while (l <= n) {
						ArrayList<Literal> c3 = new ArrayList<Literal>();
						c3.add(new Literal((i + 1), l, true));
						c3.add(new Literal((j + 1), l, true));
						kb.add(c3);
						l++;
					}
				}
			}
		}

		// force to seat in at LEAST one table
		for (int i = 1; i <= m; i++) {
			ArrayList<Literal> c4 = new ArrayList<Literal>();
			for (int j = 1; j <= n; j++) {
				c4.add(new Literal(i, j, false));
			}
			kb.add(c4);
		}

		// force to seat in ONLY one table
		for (int i = 1; i <= m; i++) {
			for (int j = 1; j <= n - 1; j++) {
				for (int k = j + 1; k <= n; k++) {
					ArrayList<Literal> c5 = new ArrayList<Literal>();
					c5.add(new Literal(i, j, true));
					c5.add(new Literal(i, k, true));
					kb.add(c5);
				}
			}
		}

		// print KB
		// printKB(kb);

		return kb;
	}

	private static void printKB(ArrayList<ArrayList<Literal>> kb) {
		for (int i = 0; i < kb.size(); i++) {
			System.out.print("clause " + (i + 1) + ": ");
			for (int j = 0; j < kb.get(i).size(); j++) {
				if (kb.get(i).get(j).isNegated()) {
					System.out.print("~");
				}
				System.out.print("x");
				System.out.print(kb.get(i).get(j).getGuestNumber() + ""
						+ kb.get(i).get(j).getTableNumber());
				if (j != kb.get(i).size() - 1) {
					System.out.print(" V ");
				} else {
					System.out.println();
				}
			}
		}

	}

}
