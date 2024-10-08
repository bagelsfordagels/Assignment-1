import java.util.ArrayList;
import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DataStoreAndComputeEngineTest{
	//checks to make sure that the amount of char[] is the same as the amount of integers in the file
	@Test
	public void arrayConversionTest() throws Exception{
		DataStorageSystem testDSImplementation = new DataStorageImplementation();
		ComputeEngine testComputeEngine = new ComputeEngine();
		InputConfig testFile = new FileInputConfig("TestFile");
		UUID key = testDSImplementation.sendData(testFile);
		ArrayList<Integer> testAl = testDSImplementation.recieveData(key);
		ArrayList<char[]> sortedFileArr = testComputeEngine.readFile(testAl);
		Assertions.assertEquals(sortedFileArr.size(),testAl.size());
	}
}