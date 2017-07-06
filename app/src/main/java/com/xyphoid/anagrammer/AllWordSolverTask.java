package com.xyphoid.anagrammer;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by Chad Plaster on 7/6/2017.
 */

public class AllWordSolverTask extends AsyncSolverTask<SolverArgs, String, HashMap<String, Integer>> {

    SolverArgs _solverArgs;

    protected HashMap<String, Integer> doInBackground(SolverArgs... solverArgs) {
        this._solverArgs = solverArgs[0];
        this._solverArgs.setSolverTask(this);

        AnagramCore anagramCore = new AnagramCore(this._solverArgs);

        publishProgress("Finding all matches...\n");

        return anagramCore.findExactSubsets();
    }

    public void Publish(String s){
        publishProgress(s);
    }

    protected void onProgressUpdate(String... progress) {
        this._solverArgs.getMainActivity().PublishProgress(progress[0]);
    }

    protected void onPostExecute(HashMap<String, Integer> combos) {

        this._solverArgs.getMainActivity().PublishAppend("\n\n");
        Map<String, Integer> results = AnagramSolverHelper.sortByComparator(combos, false);

        if(results != null) {
            Set<String> keys = results.keySet();
            for (String key : keys) {
                this._solverArgs.getMainActivity().PublishAppend(key + " : " + results.get(key) + "\n");
            }
        } else {
            this._solverArgs.getMainActivity().PublishAppend("No matches.");
        }
    }
}
