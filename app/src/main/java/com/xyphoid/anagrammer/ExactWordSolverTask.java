package com.xyphoid.anagrammer;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Chad Plaster on 7/6/2017.
 */


public class ExactWordSolverTask extends AsyncSolverTask<SolverArgs, String, Set<String>> {

    SolverArgs _solverArgs;

    protected Set<String> doInBackground(SolverArgs... solverArgs){
        this._solverArgs = solverArgs[0];
        this._solverArgs.setSolverTask(this);

        AnagramCore anagramCore = new AnagramCore(this._solverArgs);

        publishProgress("Finding all matches...\n");

        return anagramCore.findExactAnagrams();

    }

    public void Publish(String s){
        publishProgress(s);
    }

    protected void onProgressUpdate(String... progress) {
        this._solverArgs.getMainActivity().PublishProgress(progress[0]);
    }

    protected void onPostExecute(Set<String> results){
        if(results != null) {
            for (String s : results) {
                this._solverArgs.getMainActivity().PublishAppend(s + "\n");
            }
        } else {
            this._solverArgs.getMainActivity().PublishAppend("No matches.");
        }
    }
}
