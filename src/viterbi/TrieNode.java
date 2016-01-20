package viterbi;

import java.util.HashMap;

public class TrieNode {

	boolean wordend;
	public HashMap<Character, TrieNode> children = null;
	
	public TrieNode() {
		wordend = false;
		children = new HashMap<Character, TrieNode>();
	}
}
