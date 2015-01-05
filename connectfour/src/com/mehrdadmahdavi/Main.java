package com.mehrdadmahdavi;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {

	public static int xIndex = 0;
	public static int yIndex = 0;
	public static int h = 0;
	public static double resultValue = Double.NEGATIVE_INFINITY;
	public static double bestMove = -1;
	static List<Integer> skippedCols = new ArrayList<Integer>();

	public static void main(String[] args) throws FileNotFoundException {
		PrintStream out = new PrintStream(new FileOutputStream(args[1]));
		System.setOut(out);
		Board board = new Board(args[0]);

		// board.printBoard();
		char[][] state = board.getBoardMatrix();

		// System.out.println(eval(state));
		// System.out.println("!!");

		alfaBeta(true, 0, state, Double.NEGATIVE_INFINITY,
				Double.POSITIVE_INFINITY);
		System.out.println("first move: " + (int) bestMove);
	}

	public static double alfaBeta(boolean isMax, int depth, char[][] state,
			double alpha, double beta) {
		if (depth == 4) {
			h = eval(state);
			System.out.println("; h=" + h);
			// printState(state);
			return h;
		}
		double value;
		if (isMax) {
			value = alpha;
		} else {
			value = beta;
		}
		// double value = Double.POSITIVE_INFINITY;
		int undo = 0;
		for (int i = 1; i <= 7; i++) {
			List<Integer> tmpList = updateSkippedCol(state);
			if (skippedCols.contains(i - 1))
				continue;
			if (isMax) {
				undo = play(i, state, 'a');
				for (int j = 0; j < depth; j++) {
					System.out.print("|-");
				}
				if (!finihsed(state)) {
					System.out.println("A" + (depth + 1) + ": " + i);
				} else {
					System.out.print("A" + (depth + 1) + ": " + i);
				}
			} else {
				undo = play(i, state, 'b');
				for (int j = 0; j < depth; j++) {
					System.out.print("|-");
				}
				if (depth + 1 == 4) {
					System.out.print("B" + (depth + 1) + ": " + i);
				} else {
					if (!finihsed(state)) {
						System.out.println("B" + (depth + 1) + ": " + i);
					} else {
						System.out.print("B" + (depth + 1) + ": " + i);
					}
				}
			}

			if (isMax) {
				if (!finihsed(state)) {
					value = Math.max(value,
							alfaBeta(!isMax, depth + 1, state, alpha, beta));
				} else {
					h = eval(state);
					System.out.println("; h=" + h);
					value = Math.max(value, h);
				}
				if (depth == 0) {
					if (value > resultValue) {
						resultValue = value;
						bestMove = i;
					}
				}
			} else {
				if (!finihsed(state)) {
					value = Math.min(value,
							alfaBeta(!isMax, depth + 1, state, alpha, beta));
				} else {
					h = eval(state);
					System.out.println("; h=" + h);
					value = Math.min(value, h);
				}
				if (depth == 0) {
					if (value > resultValue) {
						resultValue = value;
						bestMove = i;
					}
				}
			}
			state[undo][i - 1] = 'e';
			if (!isMax) {
				beta = Math.min(beta, value);
				if (value <= alpha) {

					int counter = 0;
					for (int j = i + 1; j <= 7; j++) {
						updateSkippedCol(state);
						if (!skippedCols.contains(j - 1)) {
							counter++;
						}
					}
					if (counter != 0) {
						for (int j = 0; j < depth; j++) {
							System.out.print("|-");
						}
						System.out.print("B" + (depth + 1) + ": " + "pruning ");
						for (int j = i + 1; j <= 7; j++) {
							updateSkippedCol(state);
							if (!skippedCols.contains(j - 1)) {
								if (counter == 1) {
									System.out.print(j + "; ");
								} else {
									System.out.print(j + ", ");
								}
								counter--;
							}

						}
						System.out.println("alpha=" + (int) alpha + ", "
								+ "beta=" + (int) beta);
					}
					return value;
				}
				// beta = Math.min(beta, value);
			} else {
				alpha = Math.max(alpha, value);
				if (value >= beta) {
					int counter = 0;
					for (int j = i + 1; j <= 7; j++) {
						updateSkippedCol(state);
						if (!skippedCols.contains(j - 1)) {
							counter++;
						}
					}
					if (counter != 0) {
						for (int j = 0; j < depth; j++) {
							System.out.print("|-");
						}
						System.out.print("A" + (depth + 1) + ": " + "pruning ");
						for (int j = i + 1; j <= 7; j++) {
							updateSkippedCol(state);
							if (!skippedCols.contains(j - 1)) {
								if (counter == 1) {
									System.out.print(j + "; ");
								} else {
									System.out.print(j + ", ");
								}
								counter--;
							}

						}
						System.out.println("alpha=" + (int) alpha + ", "
								+ "beta=" + (int) beta);
					}
					return value;
				}
			}
		}
		return value;
	}

	private static boolean finihsed(char[][] state) {

		int h = eval(state);
		updateSkippedCol(state);
		if (skippedCols.size() == 7) {
			return true;
		}
		if (h == 1000 || h == -1000) {
			return true;
		}
		return false;
	}

	private static List<Integer> updateSkippedCol(char[][] state) {
		skippedCols.clear();
		boolean skippedFlag;
		int j = 0;
		int l = 0;
		for (l = 0; l < 7; l++) {
			skippedFlag = false;
			for (j = 0; j < 6; j++) {
				if (state[j][l] == 'e') {
					skippedFlag = true;
				}
			}
			if (!skippedFlag) {
				skippedCols.add(l);
			}
		}
		return skippedCols;
	}

	private static int play(int i, char[][] state, char player) {

		for (int j = 5; j >= 0; j--) {
			if (state[j][i - 1] == 'e') {
				xIndex = j;
				yIndex = i - 1;
				state[xIndex][yIndex] = player;
				break;
			}
		}
		// printState(state);
		return xIndex;
	}

	private static int eval(char[][] state) {

		if (unterminated(state, 'a') == 1000) {
			return 1000;
		} else if (unterminated(state, 'b') == -1000) {
			return -1000;
		}
		return unterminated(state, 'a') - unterminated(state, 'b');
	}

	public static boolean isInBoard(int i, int j) {
		if (i < 0 || i >= 6) {
			return false;
		}
		if (j < 0 || j >= 7) {
			return false;
		}
		return true;
	}

	public static int unterminated(char[][] state, char c) {

		int x = 0;
		int y = 0;

		for (int i = 0; i < 6; i++) {
			for (int j = 0; j < 7; j++) {
				int v0 = -1;
				int v1 = 1;
				if (isInBoard(i - v0, j - v1) && state[i - v0][j - v1] == c) {
					continue;
				}
				if (state[i][j] != c) {
					continue;
				}
				if (isInBoard(i, j) && state[i][j] == c
						&& isInBoard(i + v0, j + v1)
						&& state[i + v0][j + v1] == c
						&& isInBoard(i + 2 * v0, j + 2 * v1)
						&& state[i + 2 * v0][j + 2 * v1] == c
						&& isInBoard(i + 3 * v0, j + 3 * v1)
						&& state[i + 3 * v0][j + 3 * v1] == c) {
					if (c == 'a') {
						return 1000;
					} else if (c == 'b') {
						return -1000;
					}
				}
				if (isInBoard(i, j) && state[i][j] == c
						&& isInBoard(i + v0, j + v1)
						&& state[i + v0][j + v1] == c
						&& isInBoard(i + 2 * v0, j + 2 * v1)
						&& state[i + 2 * v0][j + 2 * v1] == c
						&& isInBoard(i + 3 * v0, j + 3 * v1)
						&& state[i + 3 * v0][j + 3 * v1] == 'e') {
					y++;
				}
				if (isInBoard(i, j) && state[i][j] == c
						&& isInBoard(i + v0, j + v1)
						&& state[i + v0][j + v1] == c
						&& isInBoard(i + 2 * v0, j + 2 * v1)
						&& state[i + 2 * v0][j + 2 * v1] == 'e') {
					x++;
				}
			}
		}
		for (int i = 0; i < 6; i++) {
			for (int j = 0; j < 7; j++) {
				int v0 = -1;
				int v1 = -1;
				if (isInBoard(i - v0, j - v1) && state[i - v0][j - v1] == c) {
					continue;
				}
				if (state[i][j] != c) {
					continue;
				}
				if (isInBoard(i, j) && state[i][j] == c
						&& isInBoard(i + v0, j + v1)
						&& state[i + v0][j + v1] == c
						&& isInBoard(i + 2 * v0, j + 2 * v1)
						&& state[i + 2 * v0][j + 2 * v1] == c
						&& isInBoard(i + 3 * v0, j + 3 * v1)
						&& state[i + 3 * v0][j + 3 * v1] == c) {
					if (c == 'a') {
						return 1000;
					} else if (c == 'b') {
						return -1000;
					}
				}
				if (isInBoard(i, j) && state[i][j] == c
						&& isInBoard(i + v0, j + v1)
						&& state[i + v0][j + v1] == c
						&& isInBoard(i + 2 * v0, j + 2 * v1)
						&& state[i + 2 * v0][j + 2 * v1] == c
						&& isInBoard(i + 3 * v0, j + 3 * v1)
						&& state[i + 3 * v0][j + 3 * v1] == 'e') {
					y++;
				}
				if (isInBoard(i, j) && state[i][j] == c
						&& isInBoard(i + v0, j + v1)
						&& state[i + v0][j + v1] == c
						&& isInBoard(i + 2 * v0, j + 2 * v1)
						&& state[i + 2 * v0][j + 2 * v1] == 'e') {
					x++;
				}
			}
		}
		for (int i = 0; i < 6; i++) {
			for (int j = 0; j < 7; j++) {
				int v0 = 1;
				int v1 = 1;
				if (isInBoard(i - v0, j - v1) && state[i - v0][j - v1] == c) {
					continue;
				}
				if (state[i][j] != c) {
					continue;
				}
				if (isInBoard(i, j) && state[i][j] == c
						&& isInBoard(i + v0, j + v1)
						&& state[i + v0][j + v1] == c
						&& isInBoard(i + 2 * v0, j + 2 * v1)
						&& state[i + 2 * v0][j + 2 * v1] == c
						&& isInBoard(i + 3 * v0, j + 3 * v1)
						&& state[i + 3 * v0][j + 3 * v1] == c) {
					if (c == 'a') {
						return 1000;
					} else if (c == 'b') {
						return -1000;
					}
				}
				if (isInBoard(i, j) && state[i][j] == c
						&& isInBoard(i + v0, j + v1)
						&& state[i + v0][j + v1] == c
						&& isInBoard(i + 2 * v0, j + 2 * v1)
						&& state[i + 2 * v0][j + 2 * v1] == c
						&& isInBoard(i + 3 * v0, j + 3 * v1)
						&& state[i + 3 * v0][j + 3 * v1] == 'e') {
					y++;
				}
				if (isInBoard(i, j) && state[i][j] == c
						&& isInBoard(i + v0, j + v1)
						&& state[i + v0][j + v1] == c
						&& isInBoard(i + 2 * v0, j + 2 * v1)
						&& state[i + 2 * v0][j + 2 * v1] == 'e') {
					x++;
				}
			}
		}
		for (int i = 0; i < 6; i++) {
			for (int j = 0; j < 7; j++) {
				int v0 = 1;
				int v1 = 0;
				if (isInBoard(i - v0, j - v1) && state[i - v0][j - v1] == c) {
					continue;
				}
				if (state[i][j] != c) {
					continue;
				}
				if (isInBoard(i, j) && state[i][j] == c
						&& isInBoard(i + v0, j + v1)
						&& state[i + v0][j + v1] == c
						&& isInBoard(i + 2 * v0, j + 2 * v1)
						&& state[i + 2 * v0][j + 2 * v1] == c
						&& isInBoard(i + 3 * v0, j + 3 * v1)
						&& state[i + 3 * v0][j + 3 * v1] == c) {
					if (c == 'a') {
						return 1000;
					} else if (c == 'b') {
						return -1000;
					}
				}
				if (isInBoard(i, j) && state[i][j] == c
						&& isInBoard(i + v0, j + v1)
						&& state[i + v0][j + v1] == c
						&& isInBoard(i + 2 * v0, j + 2 * v1)
						&& state[i + 2 * v0][j + 2 * v1] == c
						&& isInBoard(i + 3 * v0, j + 3 * v1)
						&& state[i + 3 * v0][j + 3 * v1] == 'e') {
					y++;
				}
				if (isInBoard(i, j) && state[i][j] == c
						&& isInBoard(i + v0, j + v1)
						&& state[i + v0][j + v1] == c
						&& isInBoard(i + 2 * v0, j + 2 * v1)
						&& state[i + 2 * v0][j + 2 * v1] == 'e') {
					x++;
				}
			}
		}
		for (int i = 0; i < 6; i++) {
			for (int j = 0; j < 7; j++) {
				int v0 = -1;
				int v1 = 0;
				if (isInBoard(i - v0, j - v1) && state[i - v0][j - v1] == c) {
					continue;
				}
				if (state[i][j] != c) {
					continue;
				}
				if (isInBoard(i, j) && state[i][j] == c
						&& isInBoard(i + v0, j + v1)
						&& state[i + v0][j + v1] == c
						&& isInBoard(i + 2 * v0, j + 2 * v1)
						&& state[i + 2 * v0][j + 2 * v1] == c
						&& isInBoard(i + 3 * v0, j + 3 * v1)
						&& state[i + 3 * v0][j + 3 * v1] == c) {
					if (c == 'a') {
						return 1000;
					} else if (c == 'b') {
						return -1000;
					}
				}
				if (isInBoard(i, j) && state[i][j] == c
						&& isInBoard(i + v0, j + v1)
						&& state[i + v0][j + v1] == c
						&& isInBoard(i + 2 * v0, j + 2 * v1)
						&& state[i + 2 * v0][j + 2 * v1] == c
						&& isInBoard(i + 3 * v0, j + 3 * v1)
						&& state[i + 3 * v0][j + 3 * v1] == 'e') {
					y++;
				}
				if (isInBoard(i, j) && state[i][j] == c
						&& isInBoard(i + v0, j + v1)
						&& state[i + v0][j + v1] == c
						&& isInBoard(i + 2 * v0, j + 2 * v1)
						&& state[i + 2 * v0][j + 2 * v1] == 'e') {
					x++;
				}
			}
		}
		for (int i = 0; i < 6; i++) {
			for (int j = 0; j < 7; j++) {
				int v0 = 0;
				int v1 = 1;
				if (isInBoard(i - v0, j - v1) && state[i - v0][j - v1] == c) {
					continue;
				}
				if (state[i][j] != c) {
					continue;
				}
				if (isInBoard(i, j) && state[i][j] == c
						&& isInBoard(i + v0, j + v1)
						&& state[i + v0][j + v1] == c
						&& isInBoard(i + 2 * v0, j + 2 * v1)
						&& state[i + 2 * v0][j + 2 * v1] == c
						&& isInBoard(i + 3 * v0, j + 3 * v1)
						&& state[i + 3 * v0][j + 3 * v1] == c) {
					if (c == 'a') {
						return 1000;
					} else if (c == 'b') {
						return -1000;
					}
				}
				if (isInBoard(i, j) && state[i][j] == c
						&& isInBoard(i + v0, j + v1)
						&& state[i + v0][j + v1] == c
						&& isInBoard(i + 2 * v0, j + 2 * v1)
						&& state[i + 2 * v0][j + 2 * v1] == c
						&& isInBoard(i + 3 * v0, j + 3 * v1)
						&& state[i + 3 * v0][j + 3 * v1] == 'e') {
					y++;
				}
				if (isInBoard(i, j) && state[i][j] == c
						&& isInBoard(i + v0, j + v1)
						&& state[i + v0][j + v1] == c
						&& isInBoard(i + 2 * v0, j + 2 * v1)
						&& state[i + 2 * v0][j + 2 * v1] == 'e') {
					x++;
				}
			}
		}
		for (int i = 0; i < 6; i++) {
			for (int j = 0; j < 7; j++) {
				int v0 = 0;
				int v1 = -1;
				if (isInBoard(i - v0, j - v1) && state[i - v0][j - v1] == c) {
					continue;
				}
				if (state[i][j] != c) {
					continue;
				}
				if (isInBoard(i, j) && state[i][j] == c
						&& isInBoard(i + v0, j + v1)
						&& state[i + v0][j + v1] == c
						&& isInBoard(i + 2 * v0, j + 2 * v1)
						&& state[i + 2 * v0][j + 2 * v1] == c
						&& isInBoard(i + 3 * v0, j + 3 * v1)
						&& state[i + 3 * v0][j + 3 * v1] == c) {
					if (c == 'a') {
						return 1000;
					} else if (c == 'b') {
						return -1000;
					}
				}
				if (isInBoard(i, j) && state[i][j] == c
						&& isInBoard(i + v0, j + v1)
						&& state[i + v0][j + v1] == c
						&& isInBoard(i + 2 * v0, j + 2 * v1)
						&& state[i + 2 * v0][j + 2 * v1] == c
						&& isInBoard(i + 3 * v0, j + 3 * v1)
						&& state[i + 3 * v0][j + 3 * v1] == 'e') {
					y++;
				}
				if (isInBoard(i, j) && state[i][j] == c
						&& isInBoard(i + v0, j + v1)
						&& state[i + v0][j + v1] == c
						&& isInBoard(i + 2 * v0, j + 2 * v1)
						&& state[i + 2 * v0][j + 2 * v1] == 'e') {
					x++;
				}
			}
		}
		for (int i = 0; i < 6; i++) {
			for (int j = 0; j < 7; j++) {
				int v0 = 1;
				int v1 = -1;
				if (isInBoard(i - v0, j - v1) && state[i - v0][j - v1] == c) {
					continue;
				}
				if (state[i][j] != c) {
					continue;
				}
				if (isInBoard(i, j) && state[i][j] == c
						&& isInBoard(i + v0, j + v1)
						&& state[i + v0][j + v1] == c
						&& isInBoard(i + 2 * v0, j + 2 * v1)
						&& state[i + 2 * v0][j + 2 * v1] == c
						&& isInBoard(i + 3 * v0, j + 3 * v1)
						&& state[i + 3 * v0][j + 3 * v1] == c) {
					if (c == 'a') {
						return 1000;
					} else if (c == 'b') {
						return -1000;
					}
				}
				if (isInBoard(i, j) && state[i][j] == c
						&& isInBoard(i + v0, j + v1)
						&& state[i + v0][j + v1] == c
						&& isInBoard(i + 2 * v0, j + 2 * v1)
						&& state[i + 2 * v0][j + 2 * v1] == c
						&& isInBoard(i + 3 * v0, j + 3 * v1)
						&& state[i + 3 * v0][j + 3 * v1] == 'e') {
					y++;
				}
				if (isInBoard(i, j) && state[i][j] == c
						&& isInBoard(i + v0, j + v1)
						&& state[i + v0][j + v1] == c
						&& isInBoard(i + 2 * v0, j + 2 * v1)
						&& state[i + 2 * v0][j + 2 * v1] == 'e') {
					x++;
				}
			}
		}
		return x + 5 * y;
	}

	private static int eval1(char[][] state) {

		int AlicWon = 0;
		boolean boardIsFull = true;

		int xA = 0;
		int xB = 0;
		int yA = 0;
		int yB = 0;

		// printState(state);

		for (int i = 0; i < 6; i++) {
			for (int j = 0; j < 7; j++) {
				if (state[i][j] != 'e') {
					boardIsFull = false;
				}
			}
		}

		// check board for diagonal unterminated lines
		for (int slice = 0; slice < 6 + 7 - 1; ++slice) {

			List<Character> diagonalList = new ArrayList<Character>();
			int z1 = slice < 7 ? 0 : slice - 7 + 1;
			int z2 = slice < 6 ? 0 : slice - 6 + 1;
			for (int j1 = slice - z2; j1 >= z1; --j1) {
				diagonalList.add(state[j1][slice - j1]);
			}

			char[] diagonalArray = new char[diagonalList.size()];
			for (int i = 0; i < diagonalList.size(); i++) {
				diagonalArray[i] = diagonalList.get(i);
			}

			StringBuilder builder = new StringBuilder();
			for (char s : diagonalArray) {
				builder.append(s);
			}
			String diagonalString = builder.toString();

			if (diagonalString.contains("aaaa")) {
				return 1000;
			} else if (diagonalString.contains("bbbb")) {
				return -1000;
			}

			if (countSubstring("aae", diagonalString) == 2
					|| countSubstring("eaa", diagonalString) == 2) {
				if (countSubstring("aaae", diagonalString) == 2
						|| countSubstring("eaaa", diagonalString) == 2) {
					yA++;
					yA++;
				} else if (countSubstring("aaae", diagonalString) == 1
						|| countSubstring("eaaa", diagonalString) == 1) {
					xA++;
					yA++;
				} else {
					xA++;
					xA++;
				}
				if (countSubstring("eaae", diagonalString) == 1) {
					xA++;
				}
				if (countSubstring("eaaae", diagonalString) == 1) {
					yA++;
				}
			} else if (countSubstring("aae", diagonalString) == 1
					|| countSubstring("eaa", diagonalString) == 1) {
				if (countSubstring("aaae", diagonalString) == 1
						|| countSubstring("eaaa", diagonalString) == 1) {
					if (countSubstring("aaeaaa", diagonalString) == 1
							|| countSubstring("aaaeaa", diagonalString) == 1) {
						xA++;
					}
					yA++;
				} else {
					xA++;
				}
				if (countSubstring("eaae", diagonalString) == 1) {
					xA++;
					if (countSubstring("eaaeaae", diagonalString) == 1) {
						xA++;
					}
				}
				if (countSubstring("eaaae", diagonalString) == 1) {
					yA++;
				}
			}

			if (countSubstring("bbe", diagonalString) == 2
					|| countSubstring("ebb", diagonalString) == 2) {
				if (countSubstring("bbbe", diagonalString) == 2
						|| countSubstring("ebbb", diagonalString) == 2) {
					yB++;
					yB++;
				} else if (countSubstring("bbbe", diagonalString) == 1
						|| countSubstring("ebbb", diagonalString) == 1) {
					xB++;
					yB++;
				} else {
					xB++;
					xB++;
				}
				if (countSubstring("ebbe", diagonalString) == 1) {
					xB++;
					if (countSubstring("ebbebbe", diagonalString) == 1) {
						xB++;
					}
				}
				if (countSubstring("ebbbe", diagonalString) == 1) {
					yB++;
				}
			} else if (countSubstring("bbe", diagonalString) == 1
					|| countSubstring("ebb", diagonalString) == 1) {
				if (countSubstring("bbbe", diagonalString) == 1
						|| countSubstring("ebbb", diagonalString) == 1) {
					if (countSubstring("bbebbb", diagonalString) == 1
							|| countSubstring("bbbebb", diagonalString) == 1) {
						xB++;
					}
					yB++;
				} else {
					xB++;
				}
				if (countSubstring("ebbe", diagonalString) == 1) {
					xB++;
				}
				if (countSubstring("ebbbe", diagonalString) == 1) {
					yB++;
				}
			}

		}

		// check board for reverse-diagonal unterminated lines
		for (int slice = 0; slice < 6 + 7 - 1; ++slice) {

			List<Character> diagonalList = new ArrayList<Character>();
			int z1 = slice < 7 ? 0 : slice - 7 + 1;
			int z2 = slice < 6 ? 0 : slice - 6 + 1;
			for (int j1 = slice - z2; j1 >= z1; --j1) {
				diagonalList.add(state[6 - j1 - 1][slice - j1]);
			}

			char[] diagonalArray = new char[diagonalList.size()];
			for (int i = 0; i < diagonalList.size(); i++) {
				diagonalArray[i] = diagonalList.get(i);
			}

			StringBuilder builder = new StringBuilder();
			for (char s : diagonalArray) {
				builder.append(s);
			}
			String diagonalString = builder.toString();

			if (diagonalString.contains("aaaa")) {
				return 1000;
			} else if (diagonalString.contains("bbbb")) {
				return -1000;
			}
			if (countSubstring("aae", diagonalString) == 2
					|| countSubstring("eaa", diagonalString) == 2) {
				if (countSubstring("aaae", diagonalString) == 2
						|| countSubstring("eaaa", diagonalString) == 2) {
					yA++;
					yA++;
				} else if (countSubstring("aaae", diagonalString) == 1
						|| countSubstring("eaaa", diagonalString) == 1) {
					xA++;
					yA++;
				} else {
					xA++;
					xA++;
				}
				if (countSubstring("eaae", diagonalString) == 1) {
					xA++;
				}
				if (countSubstring("eaaae", diagonalString) == 1) {
					yA++;
				}
			} else if (countSubstring("aae", diagonalString) == 1
					|| countSubstring("eaa", diagonalString) == 1) {
				if (countSubstring("aaae", diagonalString) == 1
						|| countSubstring("eaaa", diagonalString) == 1) {
					if (countSubstring("aaeaaa", diagonalString) == 1
							|| countSubstring("aaaeaa", diagonalString) == 1) {
						xA++;
					}
					yA++;
				} else {
					xA++;
				}
				if (countSubstring("eaae", diagonalString) == 1) {
					xA++;
					if (countSubstring("eaaeaae", diagonalString) == 1) {
						xA++;
					}
				}
				if (countSubstring("eaaae", diagonalString) == 1) {
					yA++;
				}
			}

			if (countSubstring("bbe", diagonalString) == 2
					|| countSubstring("ebb", diagonalString) == 2) {
				if (countSubstring("bbbe", diagonalString) == 2
						|| countSubstring("ebbb", diagonalString) == 2) {
					yB++;
					yB++;
				} else if (countSubstring("bbbe", diagonalString) == 1
						|| countSubstring("ebbb", diagonalString) == 1) {
					xB++;
					yB++;
				} else {
					xB++;
					xB++;
				}
				if (countSubstring("ebbe", diagonalString) == 1) {
					xB++;
					if (countSubstring("ebbebbe", diagonalString) == 1) {
						xB++;
					}
				}
				if (countSubstring("ebbbe", diagonalString) == 1) {
					yB++;
				}
			} else if (countSubstring("bbe", diagonalString) == 1
					|| countSubstring("ebb", diagonalString) == 1) {
				if (countSubstring("bbbe", diagonalString) == 1
						|| countSubstring("ebbb", diagonalString) == 1) {
					if (countSubstring("bbebbb", diagonalString) == 1
							|| countSubstring("bbbebb", diagonalString) == 1) {
						xB++;
					}
					yB++;
				} else {
					xB++;
				}
				if (countSubstring("ebbe", diagonalString) == 1) {
					xB++;
				}
				if (countSubstring("ebbbe", diagonalString) == 1) {
					yB++;
				}
			}
		}

		// check board for horizontal unterminated lines
		int j = 0;
		while (j <= 5) {

			char[] rowArray = state[j];
			StringBuilder builder = new StringBuilder();
			for (char s : rowArray) {
				builder.append(s);
			}
			String rowString = builder.toString();
			if (rowString.contains("aaaa")) {
				return 1000;
			} else if (rowString.contains("bbbb")) {
				return -1000;
			}
			if (countSubstring("aae", rowString) == 2
					|| countSubstring("eaa", rowString) == 2) {
				if (countSubstring("aaae", rowString) == 2
						|| countSubstring("eaaa", rowString) == 2) {
					yA++;
					yA++;
				} else if (countSubstring("aaae", rowString) == 1
						|| countSubstring("eaaa", rowString) == 1) {
					xA++;
					yA++;
				} else {
					xA++;
					xA++;
				}
				if (countSubstring("eaae", rowString) == 1) {
					xA++;
				}
				if (countSubstring("eaaae", rowString) == 1) {
					yA++;
				}
			} else if (countSubstring("aae", rowString) == 1
					|| countSubstring("eaa", rowString) == 1) {
				if (countSubstring("aaae", rowString) == 1
						|| countSubstring("eaaa", rowString) == 1) {
					if (countSubstring("aaeaaa", rowString) == 1
							|| countSubstring("aaaeaa", rowString) == 1) {
						xA++;
					}
					yA++;
				} else {
					xA++;
				}
				if (countSubstring("eaae", rowString) == 1) {
					xA++;
					if (countSubstring("eaaeaae", rowString) == 1) {
						xA++;
					}
				}
				if (countSubstring("eaaae", rowString) == 1) {
					yA++;
				}
			}

			if (countSubstring("bbe", rowString) == 2
					|| countSubstring("ebb", rowString) == 2) {
				if (countSubstring("bbbe", rowString) == 2
						|| countSubstring("ebbb", rowString) == 2) {
					yB++;
					yB++;
				} else if (countSubstring("bbbe", rowString) == 1
						|| countSubstring("ebbb", rowString) == 1) {
					xB++;
					yB++;
				} else {
					xB++;
					xB++;
				}
				if (countSubstring("ebbe", rowString) == 1) {
					xB++;
					if (countSubstring("ebbebbe", rowString) == 1) {
						xB++;
					}
				}
				if (countSubstring("ebbbe", rowString) == 1) {
					yB++;
				}
			} else if (countSubstring("bbe", rowString) == 1
					|| countSubstring("ebb", rowString) == 1) {
				if (countSubstring("bbbe", rowString) == 1
						|| countSubstring("ebbb", rowString) == 1) {
					if (countSubstring("bbebbb", rowString) == 1
							|| countSubstring("bbbebb", rowString) == 1) {
						xB++;
					}
					yB++;
				} else {
					xB++;
				}
				if (countSubstring("ebbe", rowString) == 1) {
					xB++;
				}
				if (countSubstring("ebbbe", rowString) == 1) {
					yB++;
				}
			}
			j++;
		}

		// check board for vertical unterminated lines
		int k = 0;
		while (k <= 6) {

			char[] columnArray = new char[6];
			for (int i = 0; i < state.length; i++) {
				columnArray[i] = state[i][k];
			}

			StringBuilder builder = new StringBuilder();
			for (char s : columnArray) {
				builder.append(s);
			}
			String columnString = builder.toString();
			if (columnString.contains("aaaa")) {
				return 1000;
			} else if (columnString.contains("bbbb")) {
				return -1000;
			}
			if (countSubstring("aae", columnString) == 2
					|| countSubstring("eaa", columnString) == 2) {
				if (countSubstring("aaae", columnString) == 2
						|| countSubstring("eaaa", columnString) == 2) {
					yA++;
					yA++;
				} else if (countSubstring("aaae", columnString) == 1
						|| countSubstring("eaaa", columnString) == 1) {
					xA++;
					yA++;
				} else {
					xA++;
					xA++;
				}
				if (countSubstring("eaae", columnString) == 1) {
					xA++;
				}
				if (countSubstring("eaaae", columnString) == 1) {
					yA++;
				}
			} else if (countSubstring("aae", columnString) == 1
					|| countSubstring("eaa", columnString) == 1) {
				if (countSubstring("aaae", columnString) == 1
						|| countSubstring("eaaa", columnString) == 1) {
					if (countSubstring("aaeaaa", columnString) == 1
							|| countSubstring("aaaeaa", columnString) == 1) {
						xA++;
					}
					yA++;
				} else {
					xA++;
				}
				if (countSubstring("eaae", columnString) == 1) {
					xA++;
					if (countSubstring("eaaeaae", columnString) == 1) {
						xA++;
					}
				}
				if (countSubstring("eaaae", columnString) == 1) {
					yA++;
				}
			}

			if (countSubstring("bbe", columnString) == 2
					|| countSubstring("ebb", columnString) == 2) {
				if (countSubstring("bbbe", columnString) == 2
						|| countSubstring("ebbb", columnString) == 2) {
					yB++;
					yB++;
				} else if (countSubstring("bbbe", columnString) == 1
						|| countSubstring("ebbb", columnString) == 1) {
					xB++;
					yB++;
				} else {
					xB++;
					xB++;
				}
				if (countSubstring("ebbe", columnString) == 1) {
					xB++;
					if (countSubstring("ebbebbe", columnString) == 1) {
						xB++;
					}
				}
				if (countSubstring("ebbbe", columnString) == 1) {
					yB++;
				}
			} else if (countSubstring("bbe", columnString) == 1
					|| countSubstring("ebb", columnString) == 1) {
				if (countSubstring("bbbe", columnString) == 1
						|| countSubstring("ebbb", columnString) == 1) {
					if (countSubstring("bbebbb", columnString) == 1
							|| countSubstring("bbbebb", columnString) == 1) {
						xB++;
					}
					yB++;
				} else {
					xB++;
				}
				if (countSubstring("ebbe", columnString) == 1) {
					xB++;
				}
				if (countSubstring("ebbbe", columnString) == 1) {
					yB++;
				}
			}
			k++;
			// System.out.println(xA);
			// System.out.println(xB);
			// System.out.println(yA);
			// System.out.println(yB);
		}
		if (boardIsFull) {
			return 0;
		} else {
			return (xA - xB) + 5 * (yA - yB);
		}

	}

	public static void printState(char[][] state) {
		System.out.println(Arrays.deepToString(state).replaceAll("],", "]\r\n")
				.replaceAll("]]", "]").replace("[[", " ["));
		System.out.println("\n");
	}

	public static int countSubstring(String subStr, String str) {
		return (str.length() - str.replace(subStr, "").length())
				/ subStr.length();
	}

}
