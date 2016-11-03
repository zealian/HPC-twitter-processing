# HPC-twitter-processing
Tasks for this project are:
1. Count	the	number	of	times	a	given	term	(word/string)	appears.

2. the	Tweeter (the	Twitter	user)	that	is	mentioned	the	most.	 A	Tweeter	is	prefixed	with	the @	symbol,	e.g.	searching	for “@rosinnott”	would	return	the	Tweets	that	mention	the	user	with Twitter	username	“rosinnott”.	This	search	should	list	the	top	10	most	mentioned	Tweeters, i.e.	 the	 top	 ten	 as	 ranked	 by	 the	 number	 of	 times	 their	 Twitter	 usernames occur in	 the twitter.csv	file.

3. the	topic	that	is	most	frequently	discussed.	Twitter	topics	are	prefixed	with	the	#	symbol,	e.g. searching	for “#TonyAbbott”	would	return	Tweets	on	the	topic	of	“TonyAbbott”.	This	search should	include	the	top	10	topics	and	the	number	of	times	they	have	been	tweeted	about.

There are 3 .java (MPIRun.java, Result.java, TwitterMining.java) and 3 .sh (1node_1core.sh, 1node_8cores.sh, 2nodes_8cores.sh) files to complete this assignment. They will be introduced briefly below.

A.	Result.java
This is the class that contains the result of three searching tasks. Result of task 1 stores in word (String) and wordCount (int) , both of the result of task 2 and task 3 store in a hash table (tableTweeter, tableTopic).

Except for some simple method used to present the result (outputs and output), it also has some sorting and merging methods, to do some work for the ultimate result gathered by master. Sorting methods realized by iterating key of hash table and reading out the value to a list, then overriding the comparator of sorting method, to enforce descending sort. Merge method is used for two hash table merging. It is realized by iterating key of one hash table, and searching current key in other hash table, when found, put the key and sum of values from two hash table to a merged hash table, if not found, just put the key and value to a merged hash table.  

B.	TwitterMining.java
This is the class managing the whole searching process. Processes can be summarized as:
a.	Read how many lines in the file, using LineReader;
b.	Read a range of lines in the file by BufferedReader, the range is calculated by the core rank and size information;
c.	Do matching work (task 1, 2, 3) while reading each line, until to the end of the range. Regular expressions are used to match targets;
d.	Return an object of type Result, including the result of three tasks.

C.	MPIRun.java
This is the class that contains the main method. It undertakes these jobs:
a.	Initiate MPI, and set a timer right after initiation;
b.	Do search work (TwitterMining.run(rank,size));
c.	Use MPI to send the serialized object to master;
d.	Master receives objects from other cores and deserialize them;
e.	Merge all results to master(method is provided by object Result);
f.	Set another timer and print out results as well as running time;
g.	Close MPI communication.

To run the code, a TwitterMining must be initialized, and use the run method of TwitterMining. E.g.
TwitterMining tm = new TwitterMining("twitter.csv", "the","@[a-zA-Z0-9_]{1,15}", "#[a-zA-Z0-9_]+");//filename, searching term, searching regex, searching regex

		Result result = new Result();
		result = tm.run(rank, size); //result is the current searching result
		result.merge(r);  //merge r to result
		result.outputs();  //print out results on screan
