package com.xyphoid.anagrammer;

import java.util.Arrays;
import java.util.Set;


/**
 * Created by Chad Plaster on 7/6/2017.
 */


public class MultiWordSolverTask extends AsyncSolverTask<SolverArgs, String, Set<Set<String>>> {

    SolverArgs _solverArgs;

    protected Set<Set<String>> doInBackground(SolverArgs... solverArgs) {
        this._solverArgs = solverArgs[0];
        this._solverArgs.setSolverTask(this);

        AnagramCore anagramCore = new AnagramCore(this._solverArgs);

        return anagramCore.findSpecificAnagrams();
    }

    public void Publish(String s){
        publishProgress(s);
    }

    protected void onProgressUpdate(String... progress) {
        this._solverArgs.getMainActivity().PublishProgress(progress[0]);
    }

    protected void onPostExecute(Set<Set<String>> anagram) {

        this._solverArgs.getMainActivity().PublishAppend("\n\n");

        if(!anagram.isEmpty()) {
            Boolean match = false;
            for (Set<String> combo : anagram) {
                int size = combo.size();

                if(size == this._solverArgs.getLengths().length) {
                    int[] clengths = new int[size];
                    String tempstring = "";
                    int iter2 = 0;
                    for (String s : combo) {
                        tempstring += s + " ";
                        clengths[iter2] = s.length();
                        iter2++;
                    }
                    Arrays.sort(clengths);

                    if(Arrays.equals(this._solverArgs.getLengths(), clengths)) {
                        match = true;
                        this._solverArgs.getMainActivity().PublishAppend(tempstring + "\n");
                    }
                }
            }
            if(!match) {
                this._solverArgs.getMainActivity().PublishAppend("No matches found.\n");
            }
        } else {
            this._solverArgs.getMainActivity().PublishAppend("No matches found.\n");
        }
    }
}

