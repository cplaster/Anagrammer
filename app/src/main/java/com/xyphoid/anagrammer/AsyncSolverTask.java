package com.xyphoid.anagrammer;

import android.os.AsyncTask;

/**
 * Created by Chad Plaster on 7/6/2017.
 */

public abstract class AsyncSolverTask<Params, Progress, Result> extends AsyncTask<Params, Progress, Result> {

    public void Publish(Progress s) {
        publishProgress(s);
    }


}
