package com.xyphoid.anagrammer;

import android.util.Log;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Chad on 11/2/2015.
 */
public class Processor {

    HashMap<String, String> dict;
    HashMap<String, Integer> s_values;

    public Processor(){
        dict = new HashMap<String, String>();
        s_values = new HashMap<String, Integer>();
        s_values.put("e,a,i,o,n,r,t,l,s,u", 1);
        s_values.put("d,g", 2);
        s_values.put("b,c,m,p", 3);
        s_values.put("f,h,v,w,y", 4);
        s_values.put("k", 5);
        s_values.put("j,x", 8);
        s_values.put("q,z", 10);
    }

    static String Alphabetize(String s){
        String[] a = s.split("(?!^)");
        Arrays.sort(a);
        return Arrays.toString(a).toString();
    }

    public String Show(String query){
        String v = Alphabetize(query);
        if(dict.containsKey(v)){
            return dict.get(v);
        } else {
            return "No Matches.";
        }
    }

    private String RemoveAt(String s, int index){
        return s.substring(0, index) + s.substring(index + 1);
    }

    private String toBinaryString(int i, int length){
        String binary = Integer.toBinaryString(i);
        int len = binary.length();
        while(len < length){
            binary = "0" + binary;
            len++;
        }

        return binary;
    }

    public HashMap<String, Integer> ShowAll(String query) {
        String results = "";
        query = query.replace(" ", "");
        results = query;

        int n = query.length();

        Double total = Math.pow(2.0, (double) n);

        HashMap<String, Integer> combos = new HashMap<String, Integer>();

        for(int i = 1; i < total.intValue(); i++){
            String s = toBinaryString(i, n);
            String cat = "";
            String[] a = s.split("(?!^)");
            for(int j=0; j<a.length; j++) {
                String sub = s.substring(j,j+1);
                if(sub.equals("1")) {
                    cat += query.substring(j,j+1);
                }
            }

            if(cat.length() > 1) {
                String key = Alphabetize(cat);
                if(dict.containsKey(key)){
                    String value = dict.get(key);
                    if(value.contains(",")){
                        String[] temp = value.split(",");
                        for(String t : temp) {
                            combos.put(t, getScrabbleValue(t));
                        }
                    } else {
                        combos.put(value, getScrabbleValue(value));
                    }
                }
            }
        }

        return combos;
    }

    public HashMap<Set<String>, Integer> ShowMultiword(String query, HashMap<String, Integer> input) {
        query = query.replace(" ", "");
        Set<String> words = input.keySet();
        HashMap<Integer, Set<String>> reduced_set = new HashMap<Integer, Set<String>>();
        HashMap<Set<String>, Integer> results = new HashMap<Set<String>, Integer>();

        for(String word : words) {
            int length = word.length();
            if(reduced_set.containsKey(length)) {
                reduced_set.get(length).add(word);
            } else {
                Set<String> s = new Set<String>() {
                    @Override
                    public boolean add(String object) {
                        return false;
                    }

                    @Override
                    public boolean addAll(Collection<? extends String> collection) {
                        return false;
                    }

                    @Override
                    public void clear() {

                    }

                    @Override
                    public boolean contains(Object object) {
                        return false;
                    }

                    @Override
                    public boolean containsAll(Collection<?> collection) {
                        return false;
                    }

                    @Override
                    public boolean isEmpty() {
                        return false;
                    }

                    @Override
                    public Iterator<String> iterator() {
                        return null;
                    }

                    @Override
                    public boolean remove(Object object) {
                        return false;
                    }

                    @Override
                    public boolean removeAll(Collection<?> collection) {
                        return false;
                    }

                    @Override
                    public boolean retainAll(Collection<?> collection) {
                        return false;
                    }

                    @Override
                    public int size() {
                        return 0;
                    }

                    @Override
                    public Object[] toArray() {
                        return new Object[0];
                    }

                    @Override
                    public <T> T[] toArray(T[] array) {
                        return null;
                    }
                };
                s.add(word);
                reduced_set.put(length, s);
            }
        }

        String key = Alphabetize(query);
        int keylength = query.length();

        for(String word: words) {
            int wordlength = word.length();
            int difflength = keylength - wordlength;
            //HashMap<Integer, HashSet<Integer>> set = GetIntegerSet(1, difflength);

        }
            /*
            for(int i = difflength; i <= keylength; i++){
                HashSet<Integer> sets = new HashSet<Integer>();
                GetIntegerSets(i, i, sets);
                String out = "Set " + Integer.toString(i) + ": [";
                for(Integer j : sets) {
                    out += " " + j.toString();
                }
                out += "]\n";
                Log.d("anagrammer2", out);
            }
            */



        return results;
    }


    private HashSet<Integer> GetIntegerSets(int base, int max, HashSet<Integer> set){
        if(base == 0){
            return(set);
        } else {
            for(Integer i = 1; i <= max && i <= base; i++) {
                if(!set.contains(i)) {
                    set.add(i);
                    GetIntegerSets(base - 1, i, set);
                }
            }
        }

        return(set);
    }

    private int getScrabbleValue(String query){
        String[] letters = query.split("(?!^)");
        Set<String> keys = s_values.keySet();
        int score = 0;

        for(String l : letters) {
            for(String k : keys){
                if(k.contains(l)) {
                    score += s_values.get(k);
                }
            }
        }

        return score;
    }

    public static Map<String, Integer> sortByComparator(Map<String, Integer> unsortMap, final boolean order)
    {

        List<Map.Entry<String, Integer>> list = new LinkedList<Map.Entry<String, Integer>>(unsortMap.entrySet());

        // Sorting the list based on values
        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
            public int compare(Map.Entry<String, Integer> o1,
                               Map.Entry<String, Integer> o2) {
                if (order) {
                    return o1.getValue().compareTo(o2.getValue());
                } else {
                    return o2.getValue().compareTo(o1.getValue());

                }
            }
        });

        // Maintaining insertion order with the help of LinkedList
        Map<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
        for (Map.Entry<String, Integer> entry : list)
        {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }


    public HashMap<String, String> getDict() {
        return dict;
    }

    public void Save(String location){
        try {
            FileOutputStream fileOut = new FileOutputStream(location);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(dict);
            out.close();
            fileOut.close();
            Log.d("Anagrammer2:", "Write binary dictionary to " + location);
        } catch (IOException i){
            i.printStackTrace();
        }
    }

    public void Load(InputStream location){
        Log.d("Anagrammer2:", "Trying to deserialize database...");
        try
        {
            //FileInputStream fileIn = new FileInputStream(location);
            ObjectInputStream in = new ObjectInputStream(location);
            dict = (HashMap<String, String>) in.readObject();
            in.close();
            //fileIn.close();
        }catch(IOException i)
        {
            i.printStackTrace();
            return;
        }catch(ClassNotFoundException c)
        {
            System.out.println("Hashmap class not found");
            c.printStackTrace();
            return;
        }
        Log.d("Anagrammer2:", "Deserialization successful.");
    }

    /*
    public void Load(InputStream stream){
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

        String line = null;

        try {
            int total = 172820;
            int i = 0;
            while ((line = reader.readLine()) != null) {
                i++;
                String a = Alphabetize(line);
                String v = null;

                Log.d("Anagrammer2:", "Processing " + Integer.toString(i) + " of " + total);

                if (dict.containsKey(a)) {
                    v = dict.get(a);
                    dict.put(a, v + "," + line);
                } else {
                    dict.put(a, line);
                }
            }
        } catch (IOException e){
            e.printStackTrace();
        }

        Save("/sdcard/enable.bin");
    }
    */
}
