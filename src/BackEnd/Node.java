package BackEnd;

import BackEnd.Node;

public class Node {
	private Node childOne;
	private Node childTwo;
	private int UUID;
	private String key;
	
	public Node(String key, Node childOne, Node childTwo)
	{
		this.UUID = (int)(Math.random() * 100);
		this.key = key;
		this.childOne = childOne;
		this.childTwo = childTwo;
	}
	
	public Node[] getChildren() {
		Node[] temp = {childOne, childTwo};
		return temp;
	} 
	
	public void setKey(String key) {
		this.key = key;
	}
	
	public Node findNode(String key) {
		Node foundLeaf = null;
		if(childOne == null && childTwo == null) return null;
		if(this.key.equals(key)) return this;
		else {
			for(Node x : getChildren()) {
				if(x != null) 
				foundLeaf = x.findNode(key);
			}
		}
		return foundLeaf;
	}
	
	public void setChild(Node child) {
		if(child.key.compareTo(key) > 0) {
			childTwo = child;
			return;
		} else if(child.key.compareTo(key) <= 0) {
			childOne = child;
			return;
		}
	}
	
	public void addChild(Node child) {
		if(canBePlaced(child.key)) {
			setChild(child);
		}
		else {
			for(Node x : getChildren()) {
				if(x != null)
				x.addChild(child);
			}
		}
	}
	
	public boolean canBePlaced(String key) {
		if(childOne == null && childTwo == null) return true;
		if(childOne == null && key.compareTo(key) > 0) return true;
		if(childTwo == null && key.compareTo(key) <= 0) return true;
		return false;
	}
	
	public Node popChild() {
		Node temp = null; 
		if(childTwo != null) {
			temp = childTwo;
			childTwo = null;
		} else {
			temp = childOne;
			childOne = null;
		}
		return temp;
	}
	

		
	@Override
	public String toString() {
		return "Node UUID : " + this.UUID + ", My Key is : " + this.key;
	}
	

	
	
	
}
