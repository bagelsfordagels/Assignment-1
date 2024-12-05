import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import com.example.grpc.ComputeProto.CharArrayListResponse;
import com.example.grpc.ComputeProto.CharArrayResponse;
import com.example.grpc.ComputeProto.UUIDRequest;
import com.example.grpc.ComputeProto.UUIDResponse;
import com.example.grpc.DataStorageImplementationServiceGrpc.DataStorageImplementationServiceImplBase;
import com.example.grpc.DataStore.DSRecieveDataResponse;
import com.example.grpc.DataStore.DSSendDataResponse;
import com.example.grpc.DataStore.MkFileResponse;

import io.grpc.stub.StreamObserver;

public class DataStoreServiceImpl extends DataStorageImplementationServiceImplBase{

    private final DataStorageSystem dss;

    public DataStoreServiceImpl() {
        this.dss = new DSImpOptimization();
    }
    @Override
    public void sendData(com.example.grpc.DataStore.DSSendDataRequest request,
            io.grpc.stub.StreamObserver<com.example.grpc.DataStore.DSSendDataResponse> responseObserver) {
        try {
        	String strRequest = request.toString();
        	InputConfig requestIC = new FileInputConfig(strRequest);
            UUID key = dss.sendData(requestIC);
            DSSendDataResponse response = DSSendDataResponse.newBuilder().setKey(key.toString()).build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }
    @Override
    public void recieveData(com.example.grpc.DataStore.DSRecieveDataRequest request,
            io.grpc.stub.StreamObserver<com.example.grpc.DataStore.DSRecieveDataResponse> responseObserver) {
        try {
        	//String strRequest = request.toString();
            ArrayList<Integer> intAl = dss.recieveData(UUID.fromString(request.getKey()));
            CharArrayResponse.Builder responseBuilder = CharArrayResponse.newBuilder();
            for (int i : intAl) {
                responseBuilder.addCharArray(String.valueOf(i));
            }
            String strResponse = "";
            for(int i = 0; i < intAl.size(); i++) {
            	strResponse = strResponse + intAl.get(i);
            }
            DSRecieveDataResponse response = DSRecieveDataResponse.newBuilder().setIntArrays(strResponse).build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }
    @Override
    public void mkFile(com.example.grpc.DataStore.MkFileRequest request,
            io.grpc.stub.StreamObserver<com.example.grpc.DataStore.MkFileResponse> responseObserver) {
        try {
        	ArrayList<char[]> charArrAl = new ArrayList<>();
        	for(int i = 0; i<request.getSerializedSize(); i++) {
        		String s = request.getCharArrays(i);
        		char[] charArr = new char[s.length()];
        		for(int j = 0; j < charArr.length; j++) {
        			charArr[j] = s.charAt(j);
        		}
        		charArrAl.add(charArr);
        	}
            File userFile = dss.mkFile(charArrAl);
            CharArrayListResponse.Builder responseBuilder = CharArrayListResponse.newBuilder();
            try (BufferedReader br = new BufferedReader(new FileReader(userFile))) {
                String line;
                while ((line = br.readLine()) != null) {
                	CharArrayResponse.Builder charArrayResponseBuilder = CharArrayResponse.newBuilder();
                	charArrayResponseBuilder.addCharArray(line);
                	responseBuilder.addCharArrayList(charArrayResponseBuilder.build());
                }
            String strResponse = "";
            try (BufferedReader br2 = new BufferedReader(new FileReader(userFile))){
            	String line2;
                while ((line2 = br.readLine()) != null) {
                	strResponse = strResponse + line2;
                }
            }
                MkFileResponse response = MkFileResponse.newBuilder().setFile(strResponse).build();
                responseObserver.onNext(response);
                responseObserver.onCompleted();
            } catch (IOException e) {
            	System.out.println("Error while reading file in DataStoreServiceImpl");
                e.printStackTrace();
            }
        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }
}