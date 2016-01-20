package viterbi;

import java.io.IOException;
import java.util.Stack;

public class WordSeg {

	public Stack<String> seg = null;
	public int maxLen = 3;
	Dict lexicon = new Dict();

	public String[] Segment(String text) throws IOException {

		seg = new Stack<String>();
		text = text.replace("[\\pP¡®¡¯¡°¡±]", "");
		lexicon.ReadDict();

		while (text.length() > 0) {
			int len = maxLen;
			if (text.length() < maxLen) {
				len = text.length();
			}
			String word = text.substring(text.length() - len);
			while (!lexicon.Findword(word)) {
				if (word.length() == 1) {
					break;
				}
				word = word.substring(1);
			}
			seg.add(word);
			text = text.substring(0, text.length() - word.length());
		}
		int size = seg.size();
		String[] result = new String[size];
		for (int i = 0; i < size; i++) {
			result[i] = seg.pop();
		}
		return result;
	}

}
