import java.util.Scanner;
import java.util.UUID;

public class UserInterface{
	public static void main(String[] args) {
		
		@SuppressWarnings("resource")
		Scanner userData = new Scanner(System.in);
		System.out.println("If you would like to enter an integer enter 1/nif you would like to enter a file enter 2:");
		int userChoice = userData.nextInt();
		if(userChoice == 1) {
			System.out.println("please enter an integer: ");
			
			String userInput = userData.next();
			
			InputConfig userInputConfig = new IntegerInputConfig((Integer.parseInt(userInput))); 
			
			
			ComputeEngineStorageSystem css = new ComputeEngineStorageImplementation();
			UUID key = css.sendData(userInputConfig);
			char[] sortedArr = css.retreiveSortedData(key);
			
			for(int i = 0; i < sortedArr.length; i++) {
				System.out.print(sortedArr[i] + " ");
			}
		}
		if(userChoice == 2) {
			System.out.println("Enter the file name: ");
			
			InputConfig userInputConfig = new 
			
		}
		else {
			throw new Exception("Incorrect input");
		}
		
		
	}
	
	
	
	
}