/**
 * The main task of this project is to search a term or something 
 * matching a regular expression in a csv file. The task contains 
 * three sub-parts: searching a term, searching top 10 tweeters, 
 * searching top 10 topics. 
 * 
 * This class contains the main method of the project.
 * 
 * @author Anni Piao
 * @studentID 734514
 */
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import mpi.*;

public class MPIRun {
	public static void main(String[] args) throws Exception {

		int tag = 100;
		double t1, t2;
		byte[] recvbuff = new byte[100000000];

		MPI.Init(args);// MPI initialize
		t1 = MPI.Wtime();// timer start

		int rank = MPI.COMM_WORLD.Rank();
		int size = MPI.COMM_WORLD.Size();

		TwitterMining tm = new TwitterMining("twitter.csv", "the",
				"@[a-zA-Z0-9_]{1,15}", "#[a-zA-Z0-9_]+");// filename, searching
															// term, searching
															// regex, searching
															// regex

		Result result = new Result();
		result = tm.run(rank, size); // run the searching and return the result

		if (rank == 0) { // master
			// receiving and merge results from other cores
			for (int i = 1; i < size; i++) {
				MPI.COMM_WORLD.Recv(recvbuff, 0, recvbuff.length, MPI.BYTE, i,
						tag + i);
				result.merge(deserialize(recvbuff));
			}

			result.outputs(); // print out the result on screen

			t2 = MPI.Wtime();// timer stop
			System.out.printf("\nRuntime: %f\n", t2 - t1);
		} else { // worker
			MPI.COMM_WORLD.Send(serialize(result), 0, serialize(result).length,
					MPI.BYTE, 0, tag + rank);
		}
		MPI.Finalize();
	}

	/**
	 * Serialization an object to byte[]
	 * 
	 * @param obj
	 *            object needed to be serialized
	 * @return serialized byte[]
	 * @throws IOException
	 */
	public static byte[] serialize(Object obj) throws IOException {
		ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
		ObjectOutputStream objectOutputStream = new ObjectOutputStream(
				byteOutputStream);

		objectOutputStream.writeObject(obj);
		objectOutputStream.flush();
		objectOutputStream.close();

		return byteOutputStream.toByteArray();
	}

	/**
	 * Deserialize an byte[] to object
	 * 
	 * @param b
	 * @return object result of deserialization
	 * @throws IOException
	 */
	public static Result deserialize(byte[] b) throws IOException {
		Result r = null;

		try {
			ByteArrayInputStream byteInputStream = new ByteArrayInputStream(b);
			ObjectInputStream objectInputStream = new ObjectInputStream(
					byteInputStream);
			r = (Result) objectInputStream.readObject();
			objectInputStream.close();
			byteInputStream.close();
		} catch (IOException i) {
			i.printStackTrace();
		} catch (ClassNotFoundException c) {
			System.out.println("Class not found");
			c.printStackTrace();
		}

		return r;
	}
}
