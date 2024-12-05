import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import com.example.grpc.DataStorageImplementationServiceGrpc;
import com.example.grpc.DataStorageImplementationServiceGrpc.DataStorageImplementationServiceBlockingStub;
import com.example.grpc.DataStore.DSRecieveDataRequest;
import com.example.grpc.DataStore.DSRecieveDataResponse;
import com.example.grpc.DataStore.DSSendDataRequest;
import com.example.grpc.DataStore.DSSendDataResponse;
import com.example.grpc.DataStore.MkFileRequest;
import com.example.grpc.DataStore.MkFileResponse;

import io.grpc.Channel;
import io.grpc.Grpc;
import io.grpc.InsecureChannelCredentials;
import io.grpc.ManagedChannel;
import io.grpc.StatusRuntimeException;

public class DataStoreClient implements DataStorageSystem{ // Boilerplate TODO: change to <servicename>Client
    private final DataStorageImplementationServiceBlockingStub blockingStub; // Boilerplate TODO: update to appropriate blocking stub
    DataStorageSystem dss = new DataStorageImplementation();
    public DataStoreClient(Channel channel) {
        blockingStub = DataStorageImplementationServiceGrpc.newBlockingStub(channel);  // Boilerplate TODO: update to appropriate blocking stub
    }

@Override
public UUID sendData(InputConfig userdata) throws Exception {
	// TODO Auto-generated method stub
	if(userdata instanceof IntegerInputConfig) {
		DSSendDataRequest request = DSSendDataRequest.newBuilder().setIntInput(userdata.getUserData()).build();
		DSSendDataResponse response;
		try {
			response = blockingStub.sendData(request);
		} catch (StatusRuntimeException e) {
			e.printStackTrace();
			return null;
		}
	//	String stringResponse = response.toString();
		return UUID.fromString(response.getKey());
	}else {
		String stringFile = "";
		try (BufferedReader br = new BufferedReader(new FileReader(userdata.getUserFileData()))) {
			String line; 
			while((line = br.readLine()) != null){
				stringFile = stringFile + line;
			}
		}
		DSSendDataRequest request = DSSendDataRequest.newBuilder().setFileInput(stringFile).build();
	    DSSendDataResponse response;
	    try {
	        response = blockingStub.sendData(request);
	    } catch (StatusRuntimeException e) {
	        e.printStackTrace();
	        return null;
	    }
	    //String stringResponse = response.toString();
		return UUID.fromString(response.getKey());
	}
}

@Override
public ArrayList<Integer> recieveData(UUID key) throws Exception {
	// TODO Auto-generated method stub
	DSRecieveDataRequest request = DSRecieveDataRequest.newBuilder().setKey(key.toString()).build();
	DSRecieveDataResponse response;
    try {
        response = blockingStub.recieveData(request);
    } catch (StatusRuntimeException e) {
        e.printStackTrace();
        return null;
    }
    String stringResponse = response.toString();
    ArrayList<Integer> intAl = new ArrayList<>();
    for(int i = 0; i < stringResponse.length(); i++) {
    	int x = (stringResponse.charAt(i));
    	intAl.add(x);
    }
	return intAl;
}

@Override
public File mkFile(ArrayList<char[]> charAl) throws IOException {
	// TODO Auto-generated method stub
	return mkFile(charAl,"UserData.txt");
}

@Override
public File mkFile(ArrayList<char[]> charAl, String fileName) {
	// TODO Auto-generated method stub
	MkFileRequest request = MkFileRequest.newBuilder().build();
	for(int i = 0; i < charAl.size(); i++) {
		String stringInput = "";
		char[] charAr = charAl.get(i);
		for(int j = 0; j < charAr.length ; j++) {
			stringInput = stringInput + charAr[j];
		}
		request.toBuilder().setCharArrays(i,stringInput).build();
	}
	
	MkFileResponse response;
    try {
        response = blockingStub.mkFile(request);
    } catch (StatusRuntimeException e) {
        e.printStackTrace();
        return null;
    }
    String stringResponse = response.toString();
    File userFile = new File(fileName);
	try {
		BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
        while(stringResponse != null) {   
        	for (int i = 0; i < charAl.size(); i++) {
            	char[] charAr = charAl.get(i);
            	String line = stringResponse.substring(0,charAr.length); 	
            	writer.write(line);
            	writer.newLine();
            	stringResponse = stringResponse.substring(charAr.length);	
            }      
        }
        writer.close();
	}catch(IOException e) {
		System.out.println("Error while writing file");
	}
	return userFile;
	}
}
// Boilerplate TODO: replace this method with actual client call/response logic
//public String sendDataOrder() { 
//	DSSendDataRequest request = DSSendDataRequest.newBuilder().build();
//  DSSendDataResponse response;
//  try {
//      response = blockingStub.sendData(request);
//  } catch (StatusRuntimeException e) {
//      e.printStackTrace();
//      return null;
//  }
//  return response.getKey();  
//}
//public String recieveDataOrder() { 
//	DSRecieveDataRequest request = DSRecieveDataRequest.newBuilder().build();
//	DSRecieveDataResponse response;
//  try {
//      response = blockingStub.recieveData(request);
//  } catch (StatusRuntimeException e) {
//      e.printStackTrace();
//      return null;
//  }
//  return response.getIntArrays();
//}
//public String mkFileOrder() { 
//	MkFileRequest request = MkFileRequest.newBuilder().build();
//	MkFileResponse response;
//  try {
//      response = blockingStub.mkFile(request);
//  } catch (StatusRuntimeException e) {
//      e.printStackTrace();
//      return null;
//  }
//  return response.getFile();
//}
//public static void main(String[] args) throws Exception {
//  String target = "localhost:61073";  // Boilerplate TODO: make sure the server/port match the server/port you want to connect to
//
//  ManagedChannel channel = Grpc.newChannelBuilder(target, InsecureChannelCredentials.create())
//          .build();
//  try {
//      DataStoreClient client = new DataStoreClient(channel); // Boilerplate TODO: update to this class name
//      client.sendDataOrder();
//      client.recieveDataOrder();
//      client.mkFileOrder();
//  } finally {
//      channel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
//  }
//}