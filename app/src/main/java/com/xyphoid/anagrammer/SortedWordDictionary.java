package com.xyphoid.anagrammer;

/**
 * Created by Chad on 12/10/2015.
 */

import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class SortedWordDictionary {

    private final Map<String, Set<String>> sortedStringMap = new TreeMap<String, Set<String>>();
    private boolean isDictionaryLoaded = false;
    private boolean isMainDictionaryLoaded = false;
    private String lastWordString = "";
    private HashMap<String, String> mainDictionary = new HashMap<String, String>();
    private SolverArgs _solverArgs;

    public SortedWordDictionary(SolverArgs solverArgs) {
        _solverArgs = solverArgs;
        mainDictionary = _solverArgs.getDictionary();
        isMainDictionaryLoaded = true;
    }

    public void loadDictionaryWithSpecificSubsets(String wordString, int[] lengths) {

        if(isDictionaryLoaded && wordString == lastWordString) {
            return;
        }

        HashMap<Integer, Integer> mapLengths = new HashMap<Integer, Integer>();

        for(int i : lengths){
            if(mapLengths.containsKey(i)) {
                Integer value = mapLengths.get(i);
                value++;
                mapLengths.put(i, value);
            } else {
                mapLengths.put(i, 0);
            }
        }

        Log.d("Anagrammer2:", "Pruning treemap for <" + wordString + ">\n");

        int count = 0;

        for(String key : mainDictionary.keySet()) {
            if(key == null
                    || key.isEmpty()
                    || (wordString != null && !wordString.isEmpty() && (!mapLengths.containsKey(key.length())
                    || !AnagramSolverHelper
                    .isSubset(key.toCharArray(), wordString
                            .replaceAll("\\s", "").toLowerCase()
                            .toCharArray())))) {
                continue;
            }
            Set<String> wordSet = sortedStringMap.get(key);
            if(wordSet != null){
                count += AnagramSolverHelper.addToWordSet(wordSet, mainDictionary.get(key));
            } else {
                wordSet = new TreeSet<String>();
                count += AnagramSolverHelper.addToWordSet(wordSet, mainDictionary.get(key));
                sortedStringMap.put(key, wordSet);
            }
        }

        Log.d("Anagrammer2:", "Pruned wordlist contains " + Integer.toString(count) + " words in " + Integer.toString(sortedStringMap.keySet().size()) + " keys.\n");

        isDictionaryLoaded = true;
        lastWordString = wordString;
    }

    public void loadDictionaryWithSubsets(String wordString, int minWordSize) {


        if(isDictionaryLoaded && wordString == lastWordString) {
            return;
        }

        Log.d("Anagrammer2:", "Pruning treemap for <" + wordString + ">\n");

        int count = 0;

        for(String key : mainDictionary.keySet()) {
            if(key == null
                    || key.isEmpty()
                    || (wordString != null && !wordString.isEmpty() && (key
                    .length() < minWordSize || !AnagramSolverHelper
                    .isSubset(key.toCharArray(), wordString
                            .replaceAll("\\s", "").toLowerCase()
                            .toCharArray())))) {
                continue;
            }
            Set<String> wordSet = sortedStringMap.get(key);
            if(wordSet != null){
                count += AnagramSolverHelper.addToWordSet(wordSet, mainDictionary.get(key));
            } else {
                wordSet = new TreeSet<String>();
                count += AnagramSolverHelper.addToWordSet(wordSet, mainDictionary.get(key));
                sortedStringMap.put(key, wordSet);
            }
        }

        Log.d("Anagrammer2:", "Pruned wordlist contains " + Integer.toString(count) + " words in " + Integer.toString(sortedStringMap.keySet().size()) + " keys.\n");

        isDictionaryLoaded = true;
        lastWordString = wordString;

    }

    public boolean addWord(String wordString){

        if(wordString.isEmpty()) {
            return false;
        }

        String sortedWord = AnagramSolverHelper.sortWord(wordString);
        Set<String> wordSet = sortedStringMap.get(sortedWord);
        if(wordSet != null) {
            wordSet.add(wordString);
        }
        else {
            wordSet = new TreeSet<String>();
            wordSet.add(wordString);
            sortedStringMap.put(sortedWord, wordSet);
        }

        return true;
    }

    public Set<String> findSingleWordAnagrams(String wordString) {

        if(!isDictionaryLoaded) {
            throw new IllegalStateException("dictionary not loaded.");
        } else {
            if(wordString == null || wordString.isEmpty()) {
                throw new IllegalStateException("word string invalid");
            }
            return sortedStringMap.get(AnagramSolverHelper.sortWord(wordString));
        }
    }

    public List<String> getDictionaryKeyList() {
        assert sortedStringMap != null;
        return new ArrayList<String>(sortedStringMap.keySet());
    }

    public boolean isDictionaryLoaded() {
        return isDictionaryLoaded;
    }

    public boolean isMainDictionaryLoaded() {
        return isMainDictionaryLoaded;
    }

    public HashMap<String, String> getMainDictionary() {
        if(!isMainDictionaryLoaded) {
            throw new IllegalStateException("main dictionary must be loaded first!");
        }
        return mainDictionary;
    }

    @Override
    public String toString() {
        return "isDictionaryLoaded?: " + isDictionaryLoaded + "\nDictionary: " + sortedStringMap;
    }
}
