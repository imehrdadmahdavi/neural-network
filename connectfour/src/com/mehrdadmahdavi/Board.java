package com.mehrdadmahdavi;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Board {

	List<String> boardList = new ArrayList<String>();
	char[][] boardMartix = new char[6][7];
	int i = 0;
	int j = 0;

	public Board(String inputAddress) {
//		try (BufferedReader br = new BufferedReader(
//				new FileReader(inputAddress))) {
//
//			String sCurrentLine;
//
//			while ((sCurrentLine = br.readLine()) != null) {
//				if (!sCurrentLine.isEmpty()) {
//					boardList.add(sCurrentLine);
//				}
//			}
//
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		
		BufferedReader br = null;
		try {
			 
			String sCurrentLine;
 
			br = new BufferedReader(new FileReader(inputAddress));
 
			while ((sCurrentLine = br.readLine()) != null) {
					boardList.add(sCurrentLine);
			}
 
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		

		for (String string : boardList) {
			for (j = 0; j < string.length(); j++) {
				boardMartix[5 - j][i] = string.charAt(j);
			}
			while (j < 6) {
				boardMartix[5 - j][i] = 'e';
				j++;
			}
			i++;
		}
	}

	public void printBoard() {
		System.out.println(Arrays.deepToString(boardMartix)
				.replaceAll("],", "]\r\n").replaceAll("]]", "]")
				.replace("[[", " ["));
		System.out.println("\n");
	}

	public char[][] getBoardMatrix() {
		return this.boardMartix;

	}
}
