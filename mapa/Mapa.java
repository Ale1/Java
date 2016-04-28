

import java.util.ArrayList;
import java.util.Arrays;



interface Mapa {

	ArrayList<String[]> pseudo_hash = new ArrayList<String[]>();  
		
	//adds new key-value, or edits existing one if already present
	public static void put(String key , String value) {

		for(int i = 0; i < pseudo_hash.size()+1; i++) {
			
		 //if cycle through all keys. if key not found, add new key-value.
			if (i == pseudo_hash.size()) {
				String[] pair = new String[] {key, value}; 
				pseudo_hash.add(pair);
				break;
			}

		//  else update value  if key already exists. 
			if (pseudo_hash.get(i)[0] == key) {
				pseudo_hash.get(i)[1] = value;
				break;
			}
		}
	}

	// fetches value for specified parameter key
	public static String fetch(String key) {
		for(int i = 0; i < pseudo_hash.size(); i++) {
			String[] arr = pseudo_hash.get(i);
			if (arr[0] == key) {
				return arr[1];
			}	
		}
		//return null if key does not exist
		return null; 
	}

}



