package com.xyphoid.anagrammer;

/**
 * Created by Chad on 12/10/2015.
 */
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class AnagramSolverHelper {

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


    public static int getScrabbleValue(String query){

        HashMap<String, Integer> s_values = new HashMap<String, Integer>();
        s_values.put("e,a,i,o,n,r,t,l,s,u", 1);
        s_values.put("d,g", 2);
        s_values.put("b,c,m,p", 3);
        s_values.put("f,h,v,w,y", 4);
        s_values.put("k", 5);
        s_values.put("j,x", 8);
        s_values.put("q,z", 10);

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

    public static String toBinaryString(int i, int length){
        String binary = Integer.toBinaryString(i);
        int len = binary.length();
        while(len < length){
            binary = "0" + binary;
            len++;
        }

        return binary;
    }

    public static int addToWordSet(Set<String> wordSet, String words){
        String[] r = words.split(",");
        int count = 0;

        for(String word : r){
            wordSet.add(word);
            count ++;
        }

        return count;
    }

    /*
      * sort the characters in word string
      *
      * @param wordString - string to sort
      *
      * @return string with sorted characters
      */
    public static String sortWord(String wordString) {
        if (wordString.isEmpty()) {
            return null;
        }
        byte[] charBytes = wordString.getBytes();
        Arrays.sort(charBytes);

        return new String(charBytes);
    }

    /*
      * checks if the first character array is subset of second character array
      *
      * @param charArr1 - character array charArr1 to check for subset
      *
      * @param charArr2 - checking for subset against character array charArr2
      *
      * @return true is charArray1 is subset of charArray2, false otherwise
      */
    public static boolean isSubset(char[] charArr1, char[] charArr2) {
        if (charArr1.length > charArr2.length) {
            return false;
        }
        List<Character> charList1 = toList(charArr1);
        List<Character> charList2 = toList(charArr2);
        // cannot do containsAll as there can be duplicate characters
        for (Character charValue : charList1) {
            if (charList2.contains(charValue)) {
                charList2.remove(charValue);
            } else {
                return false;
            }
        }
        return true;
    }

    /*
      * converts character array to character list
      */
    private static List<Character> toList(char[] charArr) {
        assert charArr != null;
        List<Character> charList = new ArrayList<Character>();
        for (char ch : charArr) {
            charList.add(ch);
        }
        return charList;
    }

    /*
      * converts character list to character array
      */
    private static char[] toCharArray(List<Character> charList) {
        if (charList == null || charList.isEmpty()) {
            return new char[0];
        }

        char[] charArr = new char[charList.size()];
        for (int index = 0; index < charList.size(); index++) {
            charArr[index] = charList.get(index);
        }
        return charArr;
    }

    /*
      * checks if two character arrays are equivalent;
      * char arrays are equivalent if:
      * 1. the number of elements in them are equal, and
      * 2. all the elements are same (not necessarily in same order)
      * complexity should be O(n)
      *
      * @param charArr1 - first character array for equivalence check
      *
      * @param charArr2 - second character array for equivalence check
      *
      * @return true is charArr1 is equivalent to charArr2, false otherwise
      */
    public static boolean isEquivalent(char[] charArr1, char[] charArr2) {
        if (charArr1.length != charArr2.length) {
            return false;
        }
        int sum1 = 0;
        int sum2 = 0;
        for (int index = 0; index < charArr1.length; index++) {
            sum1 += charArr1[index];
            sum2 += charArr2[index];
        }
        // in most cases it would return from here
        if (sum1 != sum2) {
            return false;
        }
        List<Character> charList1 = toList(charArr1);
        List<Character> charList2 = toList(charArr2);
        for (Character charValue : charList1) {
            charList2.remove(charValue);
        }
        return charList2.isEmpty();
    }

    /*
      * calculates set difference for 2 character arrays i.e. charArr1 - charArr2 removes all charArr2 elements from charArr1
      * complexity should be O(n)
      *
      * @param charArr1 - first character array for set difference
      *
      * @param charArr2 - second character array for set difference
      *
      * @return resultant character array of set difference between charArr1 and charArr2
      */
    public static char[] setDifference(char[] charArr1, char[] charArr2) {
        List<Character> list1 = toList(charArr1);
        List<Character> list2 = toList(charArr2);
        for (Character charObj : list2) {
            list1.remove(charObj);
        }
        return toCharArray(list1);
    }

    /*
      * function to perform set multiplication of all the sets of strings passed
      *
      * @param setsArray - muliple sets to multiply (can be a set of strings array)
      *
      * @return returns set consisting of set of strings after cartesian product is applied
      */
    public static Set<Set<String>> setMultiplication(Set<String>... setsArray) {
        if (setsArray == null || setsArray.length == 0) {
            return null;
        }
        return setMultiplication(0, setsArray);
    }

    // recursive function to calculate the cartesian product of all the sets of strings passed
    private static Set<Set<String>> setMultiplication(int index, Set<String>... setsArray) {
        Set<Set<String>> setsMultiplied = new HashSet<Set<String>>();
        if (index == setsArray.length) {
            setsMultiplied.add(new HashSet<String>());
        } else {
            for (String obj : setsArray[index]) {
                for (Set<String> set : setMultiplication(index + 1, setsArray)) {
                    set.add(obj);
                    setsMultiplied.add(set);
                }
            }
        }

        return setsMultiplied;
    }
}
