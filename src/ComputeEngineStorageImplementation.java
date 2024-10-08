import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ComputeEngineStorageImplementation implements ComputeEngineStorageSystem{
	int userData;
	char[] sortedData;
	
	private Map<UUID, InputConfig> dataStore = new HashMap<>();
	// puts user entered integer into map and links it to random key
	public UUID sendData(InputConfig userData) throws Exception {
		if (userData == null) {
			throw new IllegalArgumentException("User entry cannot be null");
		}
		UUID userKey = UUID.randomUUID();
		try {
			dataStore.put(userKey,userData);
		}catch(Exception e) {
			throw new Exception("Error storing data " +e);
		}
		return userKey;
	}
	
	@Override
	public char[] retreiveCharArr(UUID key) throws Exception {
		if(key == null) {
			throw new IllegalArgumentException("key cannot be null");
		}
		try {
			InputConfig userInt = dataStore.get(key);
			char[] arr = ComputeEngine.mkArr(userInt);
			return arr;
			
		}catch(IllegalArgumentException e) {
			throw new IllegalArgumentException("Invalid key: "+key, e);
			
		}catch(Exception e) {
			throw new RuntimeException("Error retrieving data for key: "+key, e);
		}
		

	}

	@Override
	public ArrayList<char[]> retreiveCharAl(UUID key) throws IOException{
		DataStorageSystem dss = new DataStorageImplementation();
		ComputeEngine cpe = new ComputeEngine();
		if(key == null) {
			throw new IllegalArgumentException("Key cannot be null");
		}
		try {
			InputConfig file = dataStore.get(key);
			UUID fileKey = dss.sendData(file);
			ArrayList<Integer> userInts = dss.recieveData(fileKey);
			ArrayList<char[]> charAl = cpe.readFile(userInts);
			dss.mkFile(charAl);
			System.out.println("A file with the information was created called UserData");
			return charAl;
			
		}catch(IllegalArgumentException e) {
			throw new IllegalArgumentException("Invalid key: "+key, e);
		}catch(Exception e) {
			throw new RuntimeException("Error retrieving data for key: "+key, e);
		}
		
		
		
		
		//File userFile = userFile(charAl);
		
	
		
	}
	
	
//
//	public char[] retreiveSortedData(UUID key) throws IOException{
//		// retrieves user entered integer from map
//		// if user enters integer
//		if(dataStore.get(key) instanceof IntegerInputConfig) {
//			InputConfig userInt = dataStore.get(key);
//			char[] arr = ComputeEngine.mkArr(userInt);
//			return arr;
//		}
//		// if user enters file name
//		if(dataStore.get(key) instanceof FileInputConfig) {
//			InputConfig file = dataStore.get(key);
//			DataStorageSystem dss = new DataStorageImplementation();
//			ComputeEngine cpe = new ComputeEngine();
//			UUID fileKey = dss.sendData(file);
//			ArrayList<Integer> userInts = dss.recieveData(fileKey);
//			ArrayList<char[]> charAl = cpe.readFile(userInts);
//			
//			userFile(charAl);
//			// just for compiling
//			// not sure what it should be returning 
//			return charAl;
//			
//		} else {
//			return null;
//		}
//	}
	
	
	public File userFile(ArrayList<char[]> charAl) throws IOException{
		if(charAl.isEmpty()) {
			throw new IllegalArgumentException("ArrayList cannot be null");
		}
		try {
			DataStorageSystem dss = new DataStorageImplementation();
			File userFile = dss.mkFile(charAl);
			return userFile;
		}catch(Exception e) {
			throw new RuntimeException("error creating file");
		}
		
		
	}

	


}