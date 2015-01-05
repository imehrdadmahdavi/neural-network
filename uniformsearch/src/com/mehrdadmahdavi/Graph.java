package com.mehrdadmahdavi;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Stack;

public class Graph {

	public int[][] adjacencyMatrix;
	ArrayList<Node> nodes = new ArrayList<>();
	Node rootNode;
	Node goalNode;

	public Node getRootNode() {
		return rootNode;
	}

	public Node getGoalNode() {
		return goalNode;
	}

	// This method creates a list of graph's nodes.
	// 'nodes' then is used in the rest of this class as the main reference
	// to graph's nodes.
	public void creatNodes(String[] nodeNames) {
		for (int i = 0; i < nodeNames.length; i++) {
			nodes.add(new Node(nodeNames[i]));
		}
	}

	public void printGraph() {
		System.out.println(" " + nodes + "\n");
	}

	public void makeConnection(int[][] adjacencyMatrix) {
		this.adjacencyMatrix = adjacencyMatrix;
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	// Breadth-first search-first search function
	public void bfs(int rootIndex, int goalIndex) {

		int size = nodes.size();

		Node rootNode = nodes.get(rootIndex);
		Node goalNode = nodes.get(goalIndex);

		// Using queue for both frontier and explored.
		Queue<Node> frontier = new LinkedList<Node>();
		Queue<Node> explored = new LinkedList<Node>();

		ArrayList<String> list = new ArrayList<>();

		// Used to sort adjacency nodes alphabetically.
		ArrayList<Node> prePushList = new ArrayList<>();

		// Used for storing parent names.
		Map<String, String> map = new HashMap<String, String>();

		// Check if there is only one node in graph.
		if (rootNode.getName() == goalNode.getName()) {
			System.out.println("The goal is the same as root!");
			return;
		} else {
			frontier.add(rootNode);
		}

		// Main while loop
		while (!frontier.isEmpty()) {
			Node n = (Node) frontier.remove();
			explored.add(n);
			map.put(n.name, n.parent);
			if (n.getName() == goalNode.getName()) {
			} else {
				int index = nodes.indexOf(n);
				int j = 0;
				boolean flag = true;
				while (flag) {
					while (j < size) {
						if (adjacencyMatrix[index][j] != 0
								&& !explored.contains(nodes.get(j))
								&& !frontier.contains(nodes.get(j))) {
							Node n2 = nodes.get(j);
							n2.parent = n.getName();
							// frontier.add(n2);
							prePushList.add(n2);
						}
						j++;
					}
					flag = false;
				}
				Collections.sort(prePushList, Node.nameComparator);
				for (Node node : prePushList) {
					frontier.add(node);
				}
				prePushList.clear();
			}
		}
		
		System.out.println("\n");
		System.out
				.println("I reached the GOAL! The BFS path was written in 'breadth-first.result.txt'!");
		list.add(goalNode.getName());
		String tmp = map.get(goalNode.getName());
		list.add(tmp);

		// Print the solution (path).
		while (tmp != "noParent") {
			tmp = map.get(tmp);
			list.add(tmp);
		}

		writePathInFile("breadth-first.result.txt", "", false);
		for (int i = list.size() - 2; i >= 0; i--) {
			// System.out.print(list.get(i));
			writePathInFile("breadth-first.result.txt", list.get(i), true);

			if (i == 0) {
				continue;
			}
			// System.out.print(" ---> ");
			writePathInFile("breadth-first.result.txt", " ---> ", true);
		}
		System.out.print("\n\n");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	// Depth-first search function
	public void dfs(int rootIndex, int goalIndex) {

		int size = nodes.size();
		Node rootNode = nodes.get(rootIndex);
		Node goalNode = nodes.get(goalIndex);

		// Using stack for dfs.
		Stack<Node> stack = new Stack<>();

		// Used to sort adjacency nodes alphabetically.
		ArrayList<Node> prePushList = new ArrayList<>();

		ArrayList<Node> explored = new ArrayList<>();
		ArrayList<String> list = new ArrayList<>();
		Map<String, String> map = new HashMap<String, String>();

		if (rootNode.getName() == goalNode.getName()) {
			System.out.println("The goal is the same as root!");
			return;
		} else {
			Node root = rootNode;
			stack.push(root);
			explored.add(root);
		}
		while (!stack.isEmpty()) {
			Node n = stack.peek();
			boolean flag = true;
			if (n.getName() == goalNode.getName()) {
				System.out
						.println("I reached the GOAL! The DFS path was written in 'depth-first.result.txt'!");
				list.add(goalNode.getName());
				String tmp = map.get(goalNode.getName());
				list.add(tmp);
				while (tmp != rootNode.getName()) {
					tmp = map.get(tmp);
					list.add(tmp);
				}
				writePathInFile("depth-first.result.txt", "", false);
				for (int i = list.size() - 1; i >= 0; i--) {
					// System.out.print(list.get(i));
					writePathInFile("depth-first.result.txt", list.get(i), true);
					if (i == 0) {
						continue;
					}
					// System.out.print(" ---> ");
					writePathInFile("depth-first.result.txt", " ---> ", true);
				}
			} else {
				int index = nodes.indexOf(n);
				int j = 0;
				while (j < size) {
					if (adjacencyMatrix[index][j] != 0
							&& !explored.contains(nodes.get(j))) {
						Node n2 = nodes.get(j);
						n2.parent = n.getName();
						// stack.push(n2);
						prePushList.add(n2);

						map.put(n2.name, n2.parent);
						// break;
					}
					j++;
				}
				Collections.sort(prePushList, Node.nameComparator);
				if (!prePushList.isEmpty()) {
					stack.push(prePushList.get(0));
					explored.add(prePushList.get(0));
					flag = false;
				}
				prePushList.clear();
			}
			if (flag) {
				stack.pop();
			}
		}
		System.out.print("\n\n");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	// Uniform-cost search-first search function
	public void ucs(int rootIndex, int goalIndex) {

		int size = nodes.size();
		Node rootNode = nodes.get(rootIndex);
		Node goalNode = nodes.get(goalIndex);

		// Using Priority Queue for uniform cost search
		Queue<Node> frontier = new PriorityQueue<>(20, Node.pathComparator);
		Queue<Node> explored = new LinkedList<Node>();
		ArrayList<String> list = new ArrayList<>();
		Map<String, String> map = new HashMap<String, String>();

		if (rootNode.getName() == goalNode.getName()) {
			System.out.println("The goal is the same as root!");
			return;
		} else {
			frontier.add(rootNode);
		}
		while (!frontier.isEmpty()) {

			Node n = (Node) frontier.remove();
			explored.add(n);
			map.put(n.name, n.parent);
			if (n.getName() == goalNode.getName()) {
				System.out
						.println("I reached the GOAL! The UCS path was written in 'uniform-cost.result.txt'!");
			} else {
				int index = nodes.indexOf(n);
				int j = 0;
				boolean flag = true;
				while (flag) {
					while (j < size) {
						if (adjacencyMatrix[index][j] != 0
								&& !explored.contains(nodes.get(j))) {
							Node n2 = nodes.get(j);
							n2.parent = n.getName();
							int currentCost = adjacencyMatrix[index][j]
									+ n.getPathCost();
							if (frontier.contains(n2)
									&& n2.getPathCost() > currentCost) {
								n2.setPathCost(currentCost);
							} else {
								n2.setPathCost(currentCost);
								frontier.add(n2);
							}
						}
						j++;
					}
					flag = false;
				}
			}
		}

		list.add(goalNode.getName());
		String tmp = map.get(goalNode.getName());
		list.add(tmp);
		while (tmp != "noParent") {
			tmp = map.get(tmp);
			list.add(tmp);
		}

		writePathInFile("uniform-cost.result.txt", "", false);
		for (int i = list.size() - 2; i >= 0; i--) {
			// System.out.print(list.get(i));
			writePathInFile("uniform-cost.result.txt", list.get(i), true);
			if (i == 0) {
				continue;
			}
			// System.out.print(" ---> ");
			writePathInFile("uniform-cost.result.txt", " ---> ", true);
		}
		// System.out.print("\n\n\n");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	// Write results in a file
	public void writePathInFile(String address, String content, boolean flag) {

		try (PrintWriter out = new PrintWriter(new BufferedWriter(
				new FileWriter(address, flag)))) {
			out.print(content);
		} catch (IOException e) {
		}
	}
}
