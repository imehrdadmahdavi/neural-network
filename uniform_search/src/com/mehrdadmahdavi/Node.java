package com.mehrdadmahdavi;

import java.util.Comparator;

public class Node implements Comparable<Node> {

	public String name;
	public int pathCost = 0;
	public String parent = "noParent";

	public Node(String name) {
		setName(name);
	}

	public int getPathCost() {
		return pathCost;
	}

	public void setPathCost(int pathCost) {
		this.pathCost = pathCost;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	public void setParent(String parent) {
		this.parent = name;
	}

	public String getPrent() {
		return this.parent;
	}

	@Override
	public int compareTo(Node node) {
		return (this.pathCost - node.pathCost);
	}

	public static Comparator<Node> pathComparator = new Comparator<Node>() {

		@Override
		public int compare(Node n1, Node n2) {
		return (int) (n1.getPathCost() - n2.getPathCost());
		}
	};
	
	public static Comparator<Node> nameComparator = new Comparator<Node>() {
		 
        @Override
        public int compare(Node n1, Node n2) {
            return n1.getName().compareTo(n2.getName());
        }
    };

	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(this.getName());
		return builder.toString();
	}
}
