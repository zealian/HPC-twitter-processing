/**
 * This file is used to handle the result of specific task searching. One searching 
 * result contains three parts: searching a word, top 10 tweeters and top 10 topics.
 * 
 * @author Anni Piao
 * @studentID 734514
 */
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class Result implements java.io.Serializable {

	Hashtable<String, Integer> tableTweeter = new Hashtable<String, Integer>();
	Hashtable<String, Integer> tableTopic = new Hashtable<String, Integer>();
	int wordCount = 0;
	String word = "";

	public Result() {
	}

	public void setResult(String word, int wordCount,
			Hashtable<String, Integer> tableTweeter,
			Hashtable<String, Integer> tableTopic) {
		this.word = word;
		this.wordCount = wordCount;
		this.tableTweeter = tableTweeter;
		this.tableTopic = tableTopic;
	}

	/**
	 * Print the result of the search
	 */
	public void outputs() {
		output(word);
		output(sortByValue(tableTweeter), "tweeter");
		output(sortByValue(tableTopic), "topic");
	}

	/**
	 * Format of word searching print out.
	 * 
	 * @param word
	 *            : the word used for searching.
	 */
	public void output(String word) {
		System.out.println("======================================");
		System.out.println("This is the result of word searching.");
		System.out.println("The word \"" + word + "\" appears " + wordCount
				+ " times.");
		System.out.println("======================================");
	}

	/**
	 * Format of top 10 tweeter or topic searching print out.
	 * 
	 * @param list
	 *            sorted result
	 * @param type
	 *            keyword, for this project, it is tweeter or topic
	 */
	public void output(List<Map.Entry<String, Integer>> list, String type) {

		System.out.println("======================================");
		System.out.println("This is the result of top 10 " + type
				+ " searching.");
		for (int i = 0; i < 10; i++) {
			System.out.println(String.format("Top %d: %s: %d", i + 1,
					list.get(i).getKey(), list.get(i).getValue()));
		}
		System.out.println("======================================");
	}

	/**
	 * Doing descending sort to a hash table by their value.
	 * 
	 * @param table
	 *            : the hash table needed to be sorted.
	 * @return a sorted list.
	 */
	public List<Map.Entry<String, Integer>> sortByValue(
			Hashtable<String, Integer> table) {

		// sort the hashtable by value, descendingly
		List<Map.Entry<String, Integer>> listOfAnswers = new ArrayList<Map.Entry<String, Integer>>(
				table.entrySet());

		java.util.Collections.sort(listOfAnswers,
				new Comparator<Map.Entry<String, Integer>>() {
					public int compare(Map.Entry<String, Integer> o1,
							Map.Entry<String, Integer> o2) {
						return o2.getValue() - o1.getValue();
					}
				});

		return listOfAnswers;
	}

	/**
	 * Merge results from two different cores.
	 * 
	 * @param r
	 *            a object of type Result, which is needed to be merged to
	 *            current object.
	 */
	public void merge(Result r) {
		this.wordCount += r.wordCount;
		mergeHashtable(this.tableTweeter, r.tableTweeter);
		mergeHashtable(this.tableTopic, r.tableTopic);
	}

	/**
	 * Merge two hashtable.
	 * 
	 * @param t1
	 *            main hash table, merge t2 to this one.
	 * @param t2
	 *            hash table needed to be merged to t1.
	 */
	public void mergeHashtable(Hashtable<String, Integer> t1,
			Hashtable<String, Integer> t2) {

		for (String key : t2.keySet()) {
			Integer n2 = t2.get(key);
			Integer n = t1.get(key);

			// search the key in the hashtable
			if (n == null)
				t1.put(key, n2);
			else {
				n += n2;
				t1.put(key, n);
			}
		}
	}

}
