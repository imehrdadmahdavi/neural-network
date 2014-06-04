package com.mehrdadmahdavi;

// Mehrdad Mahdavi Boroujerdi - HW1 - CSCI 561 AI

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// Instruction:
//
//
// This class reads "social-network.txt" file, store the name of nodes
// into the "graphNodes" array and also the adjacency matrix into the 
// "adjMatrix" 2D array. Then it creates a graph by using these two arrays.
// Then it calls g.bfs(), g.dfs() and g.usc() methods to find the path.
// Which accepts "aliceIndex" and "noahIndex" as parameters.
//
//--------------------------------------------------------------------------------------
// How to run the code with different test cases:
// 
//
// Replace your test case with "social-network.txt" and run the code. That's it!
//
// The code will write the results into the three files: "breadth-first.result.txt",
// "depth-first.result.txt" and "uniform-cost.result.txt" in the project root directory.
// Please note that every time that you run the code these three files will be over written
// so there should be no worries about these files.
// 
// Please Note that the name of your test case should be exactly "social-network.txt"
// since code is looking for the file with this name.

// This code supposes that the Root is "Alice" and The destination is "Noah" so
// it parses the "social-network.txt" and get the index of "Alice" and "Noah" 
// automatically.

public class Main {

	public static void main(String[] args) {

		List<String> nodeList = new ArrayList<String>();
		List<String> martixList = new ArrayList<String>();
		try (BufferedReader br = new BufferedReader(new FileReader(
				"social-network.txt"))) {

			String sCurrentLine;

			while ((sCurrentLine = br.readLine()) != null) {
				if (sCurrentLine.isEmpty()) {
					break;
				} else if (!sCurrentLine.matches("(.)*(\\d)(.)*")) {
					nodeList.add(sCurrentLine);
				} else {
					martixList.add(sCurrentLine);
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		int aliceIndex = nodeList.indexOf("Alice");
		int noahIndex = nodeList.indexOf("Noah");

		String[] graphNodes = nodeList.toArray(new String[0]);

		int[][] adjMatrix = new int[graphNodes.length][graphNodes.length];
		int i = 0;
		for (String string : martixList) {
			String[] s = string.split(" ");
			for (int j = 0; j < s.length; j++) {
				adjMatrix[i][j] = Integer.parseInt(s[j]);
			}
			i++;
		}

		System.out.println(Arrays.deepToString(adjMatrix)
				.replaceAll("],", "]\r\n").replaceAll("]]", "]")
				.replace("[[", " ["));
		System.out.println("\n");

		// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

		Graph g = new Graph();

		g.creatNodes(graphNodes);
		g.makeConnection(adjMatrix);
		g.printGraph();

		g.bfs(aliceIndex, noahIndex);
		g.dfs(aliceIndex, noahIndex);
		g.ucs(aliceIndex, noahIndex);
	}
}
