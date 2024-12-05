import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import io.grpc.Grpc;
import io.grpc.InsecureChannelCredentials;
import io.grpc.ManagedChannel;

public class ComputeEngineStorageImplementation implements ComputeEngineStorageSystem{
	int userData;
	char[] sortedData;
	private final ExecutorService executorService = Executors.newFixedThreadPool(4);
	
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
	public char[] retrieveCharArr(UUID key) throws Exception {
        if (key == null) {
            throw new IllegalArgumentException("key cannot be null");
        }

        Callable<char[]> task = () -> {
            InputConfig userInt = dataStore.get(key);
            if (userInt == null) {
                throw new IllegalArgumentException("Invalid key: " + key);
            }
            return ComputeEngine.mkArr(userInt);
        };

        Future<char[]> future = executorService.submit(task);
        
        try {
            return future.get(); // Waits for the task to complete and retrieves the result
        } catch (ExecutionException e) {
            throw new RuntimeException("Error retrieving data for key: " + key, e.getCause());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Restore interrupted status
            throw new RuntimeException("Thread was interrupted while retrieving data for key: " + key, e);
        }
		
    }
	
	public ArrayList<char[]> retrieveCharAl(UUID key, String fileName) throws Exception{
		String target = "localhost:61073";  // Boilerplate TODO: make sure the server/port match the server/port you want to connect to
		
		  ManagedChannel channel = Grpc.newChannelBuilder(target, InsecureChannelCredentials.create())
		          .build();
		  try {  
			DataStorageSystem dss = new DataStoreClient(channel);
			ComputeEngine cpe = new ComputeEngine();
			if(key == null) {
				throw new IllegalArgumentException("Key cannot be null");
			}
			
			Callable<ArrayList<char[]>> task = () -> {
				InputConfig file = dataStore.get(key);
				UUID fileKey = dss.sendData(file);
				ArrayList<Integer> userInts = dss.recieveData(fileKey);
	
				
				List<Future<char[]>> futureList = new ArrayList<>();
				
				for(Integer userInt : userInts) {
					Callable<char[]> readFileTask = () -> cpe.readFile(userInt);
		            futureList.add(executorService.submit(readFileTask));
				}
				
				ArrayList<char[]> charAl = new ArrayList<>();
				for(Future<char[]> future : futureList) {
					try {
						charAl.add(future.get());
					} catch(ExecutionException | InterruptedException e){
						throw new RuntimeException("Error during readfile" +e);
					}
				}
				dss.mkFile(charAl,fileName);
	
				System.out.println("A file with the information was created called UserData");
				return charAl;
			};
			
			Future<ArrayList<char[]>> future = executorService.submit(task);
			try {
				return future.get();
				
			}catch(IllegalArgumentException e) {
				throw new IllegalArgumentException("Invalid key: "+key, e);
			}catch(Exception e) {
				throw new RuntimeException("Error retrieving data for key: "+key, e);
			}
		}finally {
	      channel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
		  }
		
	}
	@Override
	public ArrayList<char[]> retrieveCharAl(UUID key) throws Exception{
//		DataStorageSystem dss = new DataStorageImplementation();
//		ComputeEngine cpe = new ComputeEngine();
//		if(key == null) {
//			throw new IllegalArgumentException("Key cannot be null");
//		}
//		
//		Callable<ArrayList<char[]>> task = () -> {
//			InputConfig file = dataStore.get(key);
//			UUID fileKey = dss.sendData(file);
//			ArrayList<Integer> userInts = dss.recieveData(fileKey);
//			ArrayList<char[]> charAl = cpe.readFile(userInts);
//			dss.mkFile(charAl);
//			System.out.println("A file with the information was created called UserData");
//			return charAl;
//		};
//		
//		Future<ArrayList<char[]>> future = executorService.submit(task);
//		try {
//			return future.get();
//			
//		}catch(IllegalArgumentException e) {
//			throw new IllegalArgumentException("Invalid key: "+key, e);
//		}catch(Exception e) {
//			throw new RuntimeException("Error retrieving data for key: "+key, e);
//		}
		return retrieveCharAl(key,"UserData.txt");
		
		
		
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