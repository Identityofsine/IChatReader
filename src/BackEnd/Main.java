package BackEnd;

import java.io.IOException;
import java.util.ArrayList;

public class Main {

	///Volumes/nas/NEW/PRJ/JAVAIChatReader/Example Files/Chat with Lil Jacob et al on 2020-10-15 at 13.43.54.ichat
	
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		IChatFile file = IChatFile.loadFile("/Users/kcosine/Library/Mobile Documents/com~apple~CloudDocs/ARCHI/Archive/2019-11-27/Juulia ❤️🐊 on 2019-11-27 at 06.21.42.ichat");
		//System.out.println(file.prettyPrintToHex());
		ArrayList<String> gay = file.searchForUUID();
		ArrayList<String> numbers = file.searchForNumbers();
		for(int i = 0; i < gay.size(); i++) {
			System.out.println("MESSAGE UUID : " +gay.get(i));
			System.out.println("ASCII : " + IChatFile.convertHexToString(gay.get(i)));
			System.out.println("MESSAGE : " + file.messageFromUUID(gay.get(i)));
			System.out.println("MESSAGE ASCII: " + IChatFile.convertHexToString(file.messageFromUUID(gay.get(i))));
		}
		System.out.println("Numbers: ");
		for(int i = 0; i < numbers.size(); i++) {
			System.out.println(numbers.get(i));
			System.out.println("ASCII : " + IChatFile.convertHexToString(numbers.get(i)));
		}
//		for(String x : gay) {
//			System.out.println(x);
//		}
		//System.out.println(gay.get(0));
		
	}

}
