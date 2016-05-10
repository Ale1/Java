

import java.util.ArrayList;
import java.util.Arrays;



class Mapa {

	static ArrayList<String[]> contents = new ArrayList<String[]>();  
		
	//adds new key-value, or edits existing one if already present
	public static void put(String key , String value) {

		for(int i = 0; i < contents.size()+1; i++) {
		 	//cycle through all keys. if key not found, add new key-value.
			if (i == contents.size()) {
				String[] pair = new String[] {key, value}; 
				contents.add(pair);
				break;
			}

		//  else update value  if key already exists. 
			if (contents.get(i)[0] == key) {
				contents.get(i)[1] = value;
				break;
			}
		}
	}

	// fetches value for specified parameter key
	public static String get(String key) {
		for(int i = 0; i < contents.size(); i++) {
			String[] arr = contents.get(i);
			if (arr[0] == key) {
				return arr[1];
			}	
		}
		//return null if key does not exist
		return null; 
	}

}


class MapaTest  {
	public static void main(String args[]) {
		Mapa test = new Mapa();
		System.out.println("empty:" + test.contents);
		test.put ("name","juan");
		test.put("surname", "perez");
		System.out.println("nombre: " + test.get("name"));
		System.out.println("apellido: " + test.get("surname"));
		test.put("name","tomas");
		System.out.println("nuevo nombre: " + test.get("name"));
		System.out.println("key inexistente: " + test.get("age"));
		test.put("nationality","uruguay");

		StringBuilder sb = new StringBuilder();
		for (String[] a : test.contents)
		{
    		for (String s : a) 
    		{	sb.append(s);
    			sb.append(" ");
    		}
    		sb.append( " - ");
		}
		System.out.println("full hash: " + sb);

	}

}




