package com.xyphoid.anagrammer;

/**
 * Created by Chad on 12/10/2015.
 */

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.test.suitebuilder.annotation.Suppress;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AnagramCore {

    private SortedWordDictionary sortedDictionary;
    private String progresstext = "";
    public SolverArgs solverArgs;

    public AnagramCore(SolverArgs solverArgs) {
        this.solverArgs = solverArgs;
        sortedDictionary = new SortedWordDictionary(solverArgs);
    }

    public void repairDictionary(){
        HashMap<String, String> dictionary = sortedDictionary.getMainDictionary();
        HashMap<String, String> newdict = new HashMap<String, String>();

        int count = 0;

        for(String key : dictionary.keySet()){
            String key2 = key;
            key2 = key2.replaceAll("\\,", "");
            key2 = key2.replaceAll("\\[", "");
            key2 = key2.replaceAll("\\]", "");
            key2 = key2.replaceAll("\\s", "");

            newdict.put(key2, dictionary.get(key));

            count++;
            if(count % 1000 == 0) {
                Log.d("Anagrammer2:", "Processed " + Integer.toString(count) + " of " + Integer.toString(dictionary.keySet().size()) + ".\n");
            }

        }

        String location = "/sdcard/enablenew.bin";

        try {
            FileOutputStream fileOut = new FileOutputStream(location);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(newdict);
            out.close();
            fileOut.close();
            Log.d("Anagrammer2:", "Write binary dictionary to " + location);
        } catch (IOException i){
            i.printStackTrace();
        }
    }

    public HashMap<String, Integer> findExactSubsets() {
        String query = this.solverArgs.getLetters();
        String results = "";
        query = query.replace(" ", "");
        results = query;

        HashMap<String, String> dict = sortedDictionary.getMainDictionary();

        int n = query.length();

        Double total = Math.pow(2.0, (double) n);

        HashMap<String, Integer> combos = new HashMap<String, Integer>();

        for(int i = 1; i < total.intValue(); i++){
            String s = AnagramSolverHelper.toBinaryString(i, n);
            String cat = "";
            String[] a = s.split("(?!^)");
            for(int j=0; j<a.length; j++) {
                String sub = s.substring(j,j+1);
                if(sub.equals("1")) {
                    cat += query.substring(j,j+1);
                }
            }

            if(cat.length() > 1) {
                String key = AnagramSolverHelper.sortWord(cat);
                if(dict.containsKey(key)){
                    String value = dict.get(key);
                    if(value.contains(",")){
                        String[] temp = value.split(",");
                        for(String t : temp) {
                            combos.put(t, AnagramSolverHelper.getScrabbleValue(t));
                        }
                    } else {
                        combos.put(value, AnagramSolverHelper.getScrabbleValue(value));
                    }
                }
            }
        }

        return combos.isEmpty() ? null : combos;
    }


    public Set<String> findExactAnagrams() {
        String wordString = this.solverArgs.getLetters();
        HashMap<String, String> dictionary = sortedDictionary.getMainDictionary();

        Set<String> anagrams = new HashSet<String>();
        String sortedKey = AnagramSolverHelper.sortWord(wordString);

        if(dictionary.containsKey(sortedKey)) {
            String wordlist = dictionary.get(sortedKey);
            String[] words = wordlist.split(",");


            for (String word : words) {
                anagrams.add(word);
            }
        }

        return anagrams.isEmpty() ? null : anagrams;
    }

    public Set<Set<String>> findSpecificAnagrams() {
        String wordString = this.solverArgs.getLetters().replaceAll("\\s", "");
        Set<Set<String>> anagramsSet = new HashSet<Set<String>>();
        sortedDictionary.loadDictionaryWithSpecificSubsets(wordString, this.solverArgs.getLengths());
        List<String> keyList = sortedDictionary.getDictionaryKeyList();

        int count = 0;

        for(int index = 0; index < keyList.size(); index++){
            progresstext = "Processing " + Integer.toString(index+1) +
                    " of " + Integer.toString(keyList.size());
            //Log.d("anagrammer2:", progresstext);
            this.solverArgs.getSolverTask().Publish(progresstext);
            char[] charInventory = wordString.toCharArray();
            Set<Set<String>> dictWordAnagramsSet = findAnagrams(index, charInventory, keyList);
            Set<Set<String>> tempAnagramSet = new HashSet<Set<String>>();
            if(dictWordAnagramsSet != null && !dictWordAnagramsSet.isEmpty()) {
                Set<Set<String>> mergeResult = null;
                for(Set<String> anagramSet : dictWordAnagramsSet) {
                    mergeResult = mergeAnagramKeyWords(anagramSet);
                    tempAnagramSet.addAll(mergeResult);
                }
                // print stuff to stdout if ya want;

                anagramsSet.addAll(tempAnagramSet);
            }
        }

        return anagramsSet;
    }

    public Set<Set<String>> findAllAnagrams() {

        String wordString = this.solverArgs.getLetters().replaceAll("\\s", "");
        Set<Set<String>> anagramsSet = new HashSet<Set<String>>();

        progresstext = "Pruning dictionary entries...";
        Log.d("anagrammer2:", progresstext);

        sortedDictionary.loadDictionaryWithSubsets(wordString, this.solverArgs.getMinWordLength());
        List<String> keyList = sortedDictionary.getDictionaryKeyList();

        int count = 0;

        for(int index = 0; index < keyList.size(); index++){
            progresstext = "Processing " + Integer.toString(index) +
                    " of " + Integer.toString(keyList.size());
            Log.d("anagrammer2:", progresstext);
            char[] charInventory = wordString.toCharArray();
            Set<Set<String>> dictWordAnagramsSet = findAnagrams(index, charInventory, keyList);
            Set<Set<String>> tempAnagramSet = new HashSet<Set<String>>();
            if(dictWordAnagramsSet != null && !dictWordAnagramsSet.isEmpty()) {
                Set<Set<String>> mergeResult = null;
                for(Set<String> anagramSet : dictWordAnagramsSet) {
                    mergeResult = mergeAnagramKeyWords(anagramSet);
                    tempAnagramSet.addAll(mergeResult);
                }
                // print stuff to stdout if ya want;

                anagramsSet.addAll(tempAnagramSet);
            }
        }

        return anagramsSet;
    }

    //recursive function to find all the anagrams for charInventory characters
    //starting with the word at dictionaryIndex in dictionary keyList

    private Set<Set<String>> findAnagrams(int dictionaryIndex, char[] charInventory, List<String> keyList){

        if(dictionaryIndex >= keyList.size() || charInventory.length < this.solverArgs.getMinWordLength()) {
            return null;
        }

        String searchWord = keyList.get(dictionaryIndex);
        char[] searchWordChars = searchWord.toCharArray();
        if(AnagramSolverHelper.isEquivalent(searchWordChars, charInventory)){
            Set<Set<String>> anagramsSet = new HashSet<Set<String>>();
            Set<String> anagramSet = new HashSet<String>();
            anagramSet.add(searchWord);
            anagramsSet.add(anagramSet);

            return anagramsSet;
        }

        if(AnagramSolverHelper.isSubset(searchWordChars, charInventory)){
            char[] newCharInventory = AnagramSolverHelper.setDifference(charInventory, searchWordChars);
            if(newCharInventory.length >= this.solverArgs.getMinWordLength()) {
                Set<Set<String>> anagramsSet = new HashSet<Set<String>>();
                for(int index = dictionaryIndex + 1; index < keyList.size(); index++) {
                    Set<Set<String>> searchWordAnagramsKeySet = findAnagrams(index, newCharInventory, keyList);
                    if(searchWordAnagramsKeySet != null) {
                        Set<Set<String>> mergedSets = mergeWordToSets(searchWord, searchWordAnagramsKeySet);
                        anagramsSet.addAll(mergedSets);
                    }
                }
                return anagramsSet.isEmpty() ? null : anagramsSet;
            }
        }

        return null;
    }

    private Set<Set<String>> mergeAnagramKeyWords(Set<String> anagramKeySet) {
        if(anagramKeySet == null) {
            throw new IllegalStateException("anagram keys cannot be null");
        }

        Set<Set<String>> anagramsSet = new HashSet<Set<String>>();

        for(String word: anagramKeySet) {
            Set<String> anagramWordSet = sortedDictionary.findSingleWordAnagrams(word);
            anagramsSet.add(anagramWordSet);
        }
        @SuppressWarnings("unchecked")
        Set<String>[] anagramsSetArray = anagramsSet.toArray(new Set[0]);

        return AnagramSolverHelper.setMultiplication(anagramsSetArray);
    }

    private Set<Set<String>> mergeWordToSets(String word, Set<Set<String>> sets) {
        assert !word.isEmpty();
        if(sets == null) {
            return null;
        }

        Set<Set<String>> mergedSets = new HashSet<Set<String>>();
        for(Set<String> set : sets) {
            if(set == null) {
                throw new IllegalStateException("anagram keys set cannot be null");
            }
            set.add(word);
            mergedSets.add(set);
        }

        return mergedSets;
    }

}
