package BackEnd;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.io.*;
public class IChatFile {
	
	static public final String InstantMessages = "496E7374616E744D657373616765"; 
	static public final String NSMutableDictionary = "4E534D757461626C6544696374696F6E617279";
	static public final String PresentityIDs = "50726573656E74697479494473";
	public Path filePath;
	public byte[] rawData;
	public String hexData;
	
	IChatFile(Path fP, byte[] rawData){
		filePath = fP;
		this.rawData = rawData;
		try {
			this.hexData = oneLineHex();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	static public IChatFile loadFile(String dir) throws IOException{
		Path path = Paths.get(dir);
		byte[] data = Files.readAllBytes(path);
		return new IChatFile(path, data);
	}
	
	static public String convertHexToString(String hex) {
		StringBuilder output = new StringBuilder("");
	    
	    for (int i = 0; i < hex.length(); i += 2) {
	        String str = hex.substring(i, i + 2);
	        output.append((char) Integer.parseInt(str, 16));
	    }
	    
	    return output.toString();
	}
	
	public ArrayList<String> searchForNumbers() throws IOException{
		ArrayList<String> phoneNumbersInConvo = new ArrayList<String>();
		StringBuilder numberString = new StringBuilder();
		String OLH = this.hexData;
		boolean writeToString = false;
		int identityBlockIndex = OLH.indexOf(IChatFile.PresentityIDs);
		int endOfidentityBlockIndex = OLH.indexOf(IChatFile.NSMutableDictionary);
		String identityBlock = new StringBuilder(OLH).substring(identityBlockIndex == -1 ? 0 : identityBlockIndex, endOfidentityBlockIndex == -1 ? 0 : endOfidentityBlockIndex);
		int count = 0;
		for(String x : convertHexStringToHexArray(identityBlock)) {
			String[] split = x.split("(?<=\\G.{2})");
			for(int i = 0; i < split.length; i++) {
				if(count == 2) {
					count = 0;
					//hexBlock.add(hex.toString());
					//hex.setLength(0);
				}
				if(split[i].equals("D2")) {
					writeToString = false;
					if(!numberString.toString().isBlank())
					phoneNumbersInConvo.add(numberString.toString());
					numberString.setLength(0);
				}
				if(split[i].equals("5B")) {
		 			writeToString = true;
		 			continue;
				}
				if(writeToString) {
					numberString.append(String.format("%s", split[i]));
					count++;
				}

			}
		}
		return phoneNumbersInConvo;
	}
	
	
	public String messageFromUUID(String UUIDHEX) throws IOException{
		boolean writeToString = false; 
		StringBuilder message = new StringBuilder();
		String OLH = this.hexData;
		int messageBlockIndex = OLH.indexOf(IChatFile.InstantMessages);
		int UUIDText = OLH.indexOf(UUIDHEX);
		String tempBlock = new StringBuilder(OLH).substring(messageBlockIndex == -1 ? 0 : messageBlockIndex, UUIDText == -1 ? 0 : UUIDText);	
		StringBuilder messageBlock = new StringBuilder(tempBlock).reverse();
		int count = 0;
		for(String x : convertHexStringToHexArray(messageBlock.toString())) {
			String[] split = x.split("(?<=\\G.{2})");
			for(int i = 0; i < split.length; i++) {
				if(split[i].equals("F5")) {
					writeToString = !writeToString;
					count++;
				}
				if(writeToString) {
					message.append(split[i]);
				}
				if(count >= 2 || split[i].equals("C5"))
					break;
			}
		}
		return message.reverse().toString();
	}
	
	public ArrayList<String> searchForUUID() throws IOException {
		ArrayList<String> hexBlock = new ArrayList<String>();
		StringBuilder hex = new StringBuilder();
		boolean writeToString = false;
		int count = 0;
		String OLH = oneLineHex();
		int messageBlockIndex = OLH.indexOf(IChatFile.InstantMessages);
		int endOfMessageBlockIndex = OLH.indexOf(IChatFile.NSMutableDictionary);
		String messageBlock = new StringBuilder(OLH).substring(messageBlockIndex == -1 ? 0 : messageBlockIndex, endOfMessageBlockIndex == -1 ? 0 : endOfMessageBlockIndex);	
		for(String x : convertHexStringToHexArray(messageBlock)) {
			String[] split = x.split("(?<=\\G.{4})");
			for(int i = 0; i < split.length; i++) {
				if(count == 2) {
					count = 0;
					//hexBlock.add(hex.toString());
					//hex.setLength(0);
				}
				if(split[i].equals("1024")) {
		 			writeToString = true;
				}
				if(writeToString) {
					hex.append(String.format("%s", split[i]));
					count++;
				}
				if(split[i].charAt(0) == 'D' && split[i].charAt(3) == '0') {
					writeToString = false;
					if(!hex.toString().isBlank())
					hexBlock.add(hex.toString());
					hex.setLength(0);
				}
			}
		}
		return hexBlock;
	}
	
	public ArrayList<String> convertHexStringToHexArray(String hexString){
		ArrayList<String> hex = new ArrayList<String>();
		StringBuilder hexLine = new StringBuilder();
		char[] hexArray = hexString.toCharArray();
		int count = 0;
		for(char x : hexArray) {
			if(x == ' ')
				continue;
			if(count == 8) {
				hex.add(hexLine.toString());
				hexLine.setLength(0);
				count = 0;
			}
			hexLine.append(x);
			count++;
		}
		if(count > 0)
			hex.add(hexLine.toString());
		
		return hex;
	}
	
	
	public ArrayList<String> convertBytesToHex() throws IOException{
		StringBuilder result = new StringBuilder();
		StringBuilder hex = new StringBuilder();
		StringBuilder input = new StringBuilder();
		ArrayList<String> hexList = new ArrayList<String>(); // Data Structure
 		boolean firstLine = true;
		int count = 0;
		int value;
		try(InputStream inputStream = Files.newInputStream(this.filePath)){
			while((value = inputStream.read()) != -1) {
				hex.append(String.format("%02X ", value));
				//print dot if the character cannot be converted.
				if(!Character.isISOControl(value)) 
					input.append((char)value);
				else 
					input.append('~');
				//reset after 15 bytes
				if(count == 14) {
					hexList.add(hex.toString());
					hex.setLength(0);
					input.setLength(0);
					count = 0;
					hex.append(String.format("%02X ", value));
				}
				else {
					count++;
				}
				
			}
			if(count > 0) {
				hex.append(String.format("%02X ", value));
				hexList.add(hex.toString());
			}
		}
		return hexList;
	}
	
	public String oneLineHex() throws IOException{
		StringBuilder hex = new StringBuilder();
		for(byte b : this.rawData) {
			hex.append(String.format("%02X", b));
		}
		
		return hex.toString().replaceAll("\\s", "");
	}
	
	//convertBytesToHex
	public String prettyPrintToHex() throws IOException{
		StringBuilder result = new StringBuilder();
		StringBuilder hex = new StringBuilder();
		StringBuilder input = new StringBuilder();
		int count = 0;
		int value;
		try(InputStream inputStream = Files.newInputStream(this.filePath)){
			while((value = inputStream.read()) != -1) {
				hex.append(String.format("%02X ", value));
				//print dot if the character cannot be converted.
				if(!Character.isISOControl(value)) 
					input.append((char)value);
				else 
					input.append('~');
				//reset after 15 bytes.
				if(count == 14) {
					result.append(String.format("%-60s | %s%n",hex, input));
					hex.setLength(0);
					input.setLength(0);
					count = 0;
				}
				else {
					count++;
				}
				
			}
			if(count > 0) {
				result.append(String.format("%-60s | %s%n", hex, input));
			}
		}
		return result.toString();
	}
	
}
