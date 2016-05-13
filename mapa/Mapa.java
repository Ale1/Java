

import java.util.ArrayList;
import java.util.Arrays;

class Mapa {

	ArrayList<Object[]> contents = new ArrayList<Object[]>();  
		
	//adds new key-value, or edits existing one if already present
	public void put(String key , Object value) {


		for(int i = 0; i < contents.size()+1; i++) {
		 	//cycle through all keys. if key not found, add new key-value.
			if (i == contents.size()) {
				Object[] entry = new Object[] {key, value}; 
				contents.add(entry);
				break;
			}

		//  else update value  if key already exists. 
			if (contents.get(i)[0] == key) {
				contents.get(i)[1] = value;
				break;
			}
		}
	}

	// fetches value for specified parameter key.  includes dynamic casting to return object types. 
	public Object get(Object key) {
		for(int i = 0; i < contents.size(); i++) {
			Object[] arr = contents.get(i);
			if (arr[0] == key) {
				return (arr[1].getClass()).cast(arr[1]); // "reflection" java-hack! jeje
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
		test.put("age",25);
		System.out.println("nombre: " + test.get("name"));
		System.out.println("edad " + test.get("age"));
		test.put("name","tomas");
		System.out.println("nuevo nombre: " + test.get("name"));
		System.out.println("key inexistente: " + test.get("nationality"));
	

		StringBuilder sb = new StringBuilder();
		for (Object[] a : test.contents)
		{
    		for (Object s : a) 
    		{	sb.append(s.toString());
    			sb.append(" ");
    		}
    		sb.append( " - ");
		}
		System.out.println("full hash > " + sb);


	}
}




