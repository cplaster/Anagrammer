package com.xyphoid.anagrammer;

import android.os.AsyncTask;

import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by Chad Plaster on 7/6/2017.
 */

public class SolverArgs {

    private HashMap<String, String> _dictionary;
    private MainActivity _mainActivity;
    private AsyncSolverTask _solverTask;
    private String _letters;
    private int[] _lengths;
    private int _minWordLength;


    public SolverArgs(HashMap<String, String> dictionary, MainActivity mainActivity, String letters, int[] lengths) {

        this.setDictionary(dictionary);
        this.setMainActivity(mainActivity);
        this.setLetters(letters);
        if(lengths != null) {
            Arrays.sort(lengths);
            this._minWordLength = lengths[0];
        } else {
            this._minWordLength = 0;
        }

        this.setLengths(lengths);

    }

    public HashMap<String, String> getDictionary(){
        return _dictionary;
    }

    public void setDictionary(HashMap<String, String> dictionary){
        _dictionary = dictionary;
    }

    public MainActivity getMainActivity(){
        return _mainActivity;
    }

    public void setMainActivity(MainActivity mainActivity) {
        _mainActivity = mainActivity;
    }

    public AsyncSolverTask getSolverTask(){
        return _solverTask;
    }

    public void setSolverTask(AsyncSolverTask solverTask){
        _solverTask = solverTask;
    }

    public String getLetters() {
        return _letters;
    }

    public void setLetters(String letters){
        _letters = letters;
    }

    public int[] getLengths() {
        return _lengths;
    }

    public void setLengths(int[] lengths) {
        _lengths = lengths;
    }

    public int getMinWordLength(){
        return _minWordLength;
    }
}
