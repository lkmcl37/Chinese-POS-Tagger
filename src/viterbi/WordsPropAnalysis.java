package viterbi;

import java.io.FileReader;
import java.io.FileWriter;
import java.util.*;
import java.io.BufferedReader;
import java.io.IOException;

public class WordsPropAnalysis {
	
	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws IOException {
		//Training
		System.out
				.println("=====================Start======================\n");
		System.out.println("Running......");
		BufferedReader reader = null;
		String analysisContent = "";
		try {
			//Read training corpus
			reader = new BufferedReader(new FileReader("train.txt"));
			String line = "";
			while ((line = reader.readLine()) != null)
				analysisContent += line;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
				}
			}
		}

		String[] text;
		String reg1 = "(/[a-z]*\\s{0,})|(][a-z]*\\s{1,})";
		text = analysisContent.split(reg1); //Get tags

		String[] temp;
		String reg2 = "[0-9|-]*/|\\s{1,}[^a-z]*|][a-z]";
		temp = analysisContent.split(reg2);
		String[] temp1;
		temp1 = new String[temp.length - 1];//Remove temp[0]
		for (int i = 0; i < temp.length - 1; i++) {
			temp1[i] = temp[i + 1];
		}

		String[] temp2;
		temp2 = new String[temp1.length - 1];
		for (int i = 0; i < temp1.length - 1; i++) {
			temp2[i] = temp1[i] + ',' + temp1[i + 1];
		}

		String[] wordPos;
		wordPos = new String[text.length];
		for (int i = 0; i < text.length; i++) {
			wordPos[i] = text[i] + ',' + temp1[i];
		}

		Hashtable singleProp = new Hashtable(); //part-of-speech tag and
												//frequency
		for (String s1 : temp1) {
			if (singleProp.containsKey(s1)) {
				singleProp.put(s1, singleProp.get(s1).hashCode() + 1);
			} else {
				singleProp.put(s1, 1);
			}
		}

		int sp = singleProp.size();
		Hashtable doubleProp = new Hashtable();
		for (String s2 : temp2) {
			if (doubleProp.containsKey(s2))
				doubleProp.put(s2, doubleProp.get(s2).hashCode() + 1);
			else
				doubleProp.put(s2, 1);
		}

		Hashtable hash3 = new Hashtable(); //word, part-of-speech tag,
											//frequency
		for (String s3 : wordPos) {
			if (hash3.containsKey(s3))
				hash3.put(s3, hash3.get(s3).hashCode() + 1);
			else
				hash3.put(s3, 1);
		}

		String[] table_pos;
		table_pos = new String[sp];
		Enumeration key = singleProp.keys();
		for (int i = 0; i < sp; i++) {
			String str = (String) key.nextElement();
			table_pos[i] = str;
		}

		//Calculating state transition probability
		double[][] status; // the probability of j transit to i
		status = new double[sp][sp];
		for (int i = 0; i < sp; i++) {
			for (int j = 0; j < sp; j++)
				status[i][j] = 0;
		}

		for (int i = 0; i < sp; i++) {
			for (int j = 0; j < sp; j++) {
				String wd = table_pos[j];
				String str = wd + ',' + table_pos[i];
				if (doubleProp.containsKey(str))
					status[i][j] = Math.log(((double) doubleProp.get(str)
							.hashCode() / (double) singleProp.get(wd)
							.hashCode()) * 100000000);
				else
					status[i][j] = Math.log((1 / ((double) singleProp.get(wd)
							.hashCode() * 1000)) * 100000000);
			}
		}

		//Calculating emission probability
		String sentence = "";
		try {
			BufferedReader str = new BufferedReader(new FileReader("Input.txt"));
			String line;
			while ((line = str.readLine()) != null)
				sentence += line;
		} catch (IOException e) {
			e.printStackTrace();
		}

		String[] test;
		WordSeg segm = new WordSeg();
		test = segm.Segment(sentence);
		int sw = 0; //Total word count
		sw = test.length;

		double[][] observe;
		observe = new double[sw][sp];
		for (int i = 0; i < sw; i++) {
			for (int j = 0; j < sp; j++)
				observe[i][j] = 0;
		}

		for (int i = 0; i < sw; i++) {
			for (int j = 0; j < sp; j++) {
				String wd = test[i];
				String ws = table_pos[j];
				String str = wd + ',' + ws;
				if (hash3.containsKey(str))
					observe[i][j] = Math.log(((double) hash3.get(str)
							.hashCode() / (double) singleProp.get(ws)
							.hashCode()) * 100000000);
				else
					observe[i][j] = Math.log((1 / ((double) singleProp.get(ws)
							.hashCode() * 1000)) * 100000000);
			}
		}

		//Viterbi algorithm
		double[][] path; //The highest probability of a word
		path = new double[sw][sp];
		for (int i = 0; i < sw; i++) {
			for (int j = 0; j < sp; j++)
				path[i][j] = 0.0;
		}

		int[][] backpointer;
		backpointer = new int[sw][sp];
		for (int i = 0; i < sw; i++) {
			for (int j = 0; j < sp; j++) {
				backpointer[i][j] = 0;
			}
		}

		for (int s = 0; s < sp; s++) {
			path[0][s] = Math.log(((double) singleProp.get(table_pos[s])
					.hashCode() / (double) temp1.length) * 100000000)
					+ observe[0][s];
		}

		for (int i = 1; i < sw; i++) {
			for (int j = 0; j < sp; j++) {
				double maxp = path[i - 1][0] + status[j][0] + observe[i][j];
				int index = 0;
				for (int k = 1; k < sp; k++) {
					path[i][j] = path[i - 1][k] + status[j][k] + observe[i][j];
					if (path[i][j] > maxp) {
						index = k;
						maxp = path[i][j];
					}
				}
				backpointer[i][j] = index;
				path[i][j] = maxp;
			}
		}

		int maxindex = 0;
		double max = path[sw - 1][0]; //find the result
		for (int i = 1; i < sp; i++) {
			if (path[sw - 1][i] > max) {
				maxindex = i;
				max = path[sw - 1][maxindex];
			}
		}

		String[] result;
		String[] object; //part-of-speech tags of the result
		result = new String[sw];
		object = new String[sw];
		result[sw - 1] = test[sw - 1] + '/' + table_pos[maxindex];
		object[sw - 1] = table_pos[maxindex];
		int t = 0;
		int front = maxindex;
		for (int i = sw - 2; i >= 0; i--) {
			t = backpointer[i + 1][front];
			result[i] = test[i] + '/' + table_pos[t] + "  ";
			object[i] = table_pos[t];
			front = t;
		}

		try {
			FileWriter f = new FileWriter("Output.txt");
			for (int i = 0; i < result.length; i++) {
				f.write(result[i] + "");
			}
			f.flush();
			f.close();
		} catch (IOException e) {
			System.out.println("error, please retry");
		}
		
		System.out.println("Total Word Count£º" + sw);
		System.out.println("The results have been output to Output.txt");
	}
}
