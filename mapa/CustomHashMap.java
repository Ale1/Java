
import java.util.ArrayList;
import java.util.Arrays;



class CustomHashMap{

	// create very simple custom Hashmap in the form of array with 4 linkedlist buckets.  
	Entry[] buckets = { null, null, null, null};
	

	public void put(int key, String value){
		int hash = getHash(key);

		//go to first entry, if empty, write key-value there:
		if(buckets[hash] == null){
			buckets[hash] = (new Entry(key,value,null));
		}
		else
		{
		// grab entries and compare key, if match replace, otherwise jump to next entry
			Entry current = buckets[hash];
			Entry previous = null;

			while(current !=null){
				if(current.key == key){
					current.value = value;
					return;
				}
				else{
					previous = current;
					current = current.next;
				}
			}
			// no matches found, then grab saved previous (the last non-null entry) and insert there:
			previous.next = new Entry (key, value, null);
		}
	}


	public String get(int key){
		int hash = getHash(key);
		//if first entry is empty, return null:
		if (buckets[hash] == null){
			return null;
		}else{
		// grab first entry and compare keys, if match return value. otherwise jump to next entry. 
			Entry current = buckets[hash];
			while(current != null){
				if(current.key == key){
					return current.value;
				}
				current = current.next; //if no match go to next entry
			}
			return null; // if reached end and not match, return null
		}
	}

	private int getHash(int key){   //super simplified hash-maker.  creates Hash between 0-3 to point at index of each of the 4 buckets.  
		
		return (key % 4);
	}


	public void print(){  // prints Hashmap in friendly format for debugging
       
       for(int i=0;i<buckets.length;i++){
           if(buckets[i]!=null){
                Entry entry= buckets[i];
                while(entry!=null){
                    System.out.print("{"+ entry.key +"="+ entry.value +"}" +" ");
                    entry=entry.next;
                }
            }
        }
        System.out.println("");             
    }
}



class Entry{
	int key;
	String value;
	Entry next;

	public Entry(int key, String value, Entry next){
		this.key = key;
		this.value = value;
		this.next = next;
	}

	public String getValue(){
		return value;
	}

	public void setValue(String value){
		this.value = value;
	}

	public int getKey(){
		return key;
	}

}


class MapaTest{
	public static void main(String[] args){
		CustomHashMap test = new CustomHashMap();
		test.put(1,"Ale");
		test.put(4, "Pablo");
		test.put(1,"Juan");
		test.put(10,"Maria");
		test.put(7,"Leandro");
		test.put(9,"Pepe");
		test.put(14,"Lucia");


		assert(test.get(1) == "Juan"): "oops";
		assert(test.get(10) == "Maria" ): "woopse";
		assert(test.get(4) == "Pablo"): "caramba!";
		assert(test.get(14) == "Lucia"): "no me gusta";

		test.print();

	}
}


