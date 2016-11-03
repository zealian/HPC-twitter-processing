/**
 * This java file used to enforcing word, top 10 tweeter or topic mining from a specified file.
 * 
 * @author Anni Piao
 * @studentID 734514
 */
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TwitterMining {
	private String file;
	private String word;
	private String regexWord;
	private String regexTweeter;
	private String regexTopic;

	public TwitterMining() {
	}

	/**
	 * Constructor of one TwitterMining object.
	 * 
	 * @param file
	 * @param word
	 * @param regexTweeter
	 *            Regular expression of tweeter
	 * @param regexTopic
	 *            Regular expression of topic
	 */
	public TwitterMining(String file, String word, String regexTweeter,
			String regexTopic) {
		this.file = file;
		this.word = word;
		this.regexWord = "(([[^a-zA-Z]+|\\s](" + regexWord + "))|^" + regexWord
				+ ")[[^a-zA-Z]*|\\s|\\$]";
		this.regexTweeter = regexTweeter;
		this.regexTopic = regexTopic;
	}

	/**
	 * Main running procedure. 1. Read how many lines in the file. 2. skip some
	 * lines in order to achieve better parallel speed.
	 * 
	 * @param rank
	 *            current rank of the core
	 * @param size
	 *            total number of cores
	 * @return an object of type Result
	 */
	public Result run(int rank, int size) {

		int ln = lineReader();
		Result result = new Result();

		try (BufferedReader br = new BufferedReader(new FileReader(file))) {

			String sCurrentLine;
			int countLine = 0;

			/**
			 * Line skipper
			 */
			int bottom = ln * rank / size;
			int top = ln * (rank + 1) / size - 1;

			if (rank == size - 1)
				top = ln - 1;

			while ((sCurrentLine = br.readLine()) != null) {
				if (countLine > bottom - 1)
					break;

				countLine++;
			}

			Hashtable<String, Integer> tableTweeter = new Hashtable<String, Integer>();
			Hashtable<String, Integer> tableTopic = new Hashtable<String, Integer>();
			int wordCount = 0;

			/**
			 * When matching, update corresponding table or value.
			 */
			while ((sCurrentLine = br.readLine()) != null && countLine <= top) {

				matchNcount(sCurrentLine, regexTweeter, tableTweeter);
				matchNcount(sCurrentLine, regexTopic, tableTopic);

				Pattern r = Pattern.compile(regexWord);
				Matcher m = r.matcher(sCurrentLine);
				while (m.find())
					wordCount++;

				countLine++;
			}

			result.setResult(word, wordCount, tableTweeter, tableTopic);

		} catch (IOException e) {
			e.printStackTrace();
		}

		return result;

	}

	/**
	 * Read how many lines in the file.
	 * 
	 * @return numbers of lines.
	 */
	public int lineReader() {
		int ln = 0;
		try (LineNumberReader lnr = new LineNumberReader(new FileReader(file))) {
			lnr.skip(Long.MAX_VALUE);
			ln = lnr.getLineNumber();
			lnr.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ln;
	}

	/**
	 * For topic or tweeter, matching and result managing.
	 * 
	 * @param sCurrentLine
	 * @param regex
	 * @param table
	 */
	public void matchNcount(String sCurrentLine, String regex,
			Hashtable<String, Integer> table) {

		Pattern r = Pattern.compile(regex);// create a pattern object
		Matcher m = r.matcher(sCurrentLine);// create matcher object

		// print out all matches
		while (m.find()) {

			String key = m.group().toLowerCase();
			Integer n = table.get(key);

			// search the key in the hashtable
			if (n == null)
				table.put(key, 1);
			else
				table.put(key, ++n);
		}
	}
}
