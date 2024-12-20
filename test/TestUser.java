
import java.io.File;
import java.util.ArrayList;
import java.util.UUID;


public class TestUser {
	
	// TODO 3: change the type of this variable to the name you're using for your
	// User <-> ComputeEngine API; also update the parameter passed to the constructor
	private final ComputeEngineTestVersion coordinator;

	public TestUser(ComputeEngineTestVersion coordinator) {
		this.coordinator = coordinator;
	}

	public void run(String outputPath) throws Exception {
		char delimiter = ';';
		String inputPath = "test" + File.separatorChar + "testInputFile.test";//breakpoint
		InputConfig userFileInputConfig = new FileInputConfig(inputPath);
		// TODO 4: Call the appropriate method(s) on the coordinator to get it to 
		// run the compute job specified by inputPath, outputPath, and delimiter
		
		coordinator.sendData(userFileInputConfig);
		ArrayList<char[]> userCharAl = coordinator.retrieveCharAl(coordinator.sendData(userFileInputConfig),outputPath);
		for(char[] arr : userCharAl) {
			System.out.println(arr);
		}
		
	}

}