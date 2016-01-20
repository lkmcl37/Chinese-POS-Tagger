package viterbi;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class Dict {

	TrieNode root = new TrieNode();

	public void ReadDict() throws IOException, IOException {

		File file = new File("SogouLabDic.txt");
		String encode = "UTF-8";

		BufferedReader br = null;
		br = new BufferedReader(new InputStreamReader(
				new FileInputStream(file), encode));
		String inputLine = br.readLine();

		while (inputLine != null) {
			String[] inputLineArr = inputLine.trim().split("	");

			String word = inputLineArr[0];
			TrieNode node = root;
			for (int i = 0; i < word.length(); i++) {
				if (!(node.children.containsKey(word.charAt(i)))) {
					node.children.put(word.charAt(i), new TrieNode());
				}
				node = node.children.get(word.charAt(i));
			}
			node.wordend = true;
			inputLine = br.readLine();
		}
		br.close();
	}

	public boolean Findword(String word) {
		TrieNode node = root;
		for (int i = 0; i < word.length(); i++) {
			if (!(node.children.containsKey(word.charAt(i)))) {
				return false;
			} else {
				node = node.children.get(word.charAt(i));
			}
		}return node.wordend;
	}
}
