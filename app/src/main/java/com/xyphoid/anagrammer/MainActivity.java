package com.xyphoid.anagrammer;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import java.io.IOException;
import java.io.InputStream;
import java.nio.IntBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import android.os.Handler;
import android.os.Message;

public class MainActivity extends AppCompatActivity {

    Map<String, Integer> results;
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AssetManager assetManager = getAssets();

        try{
            final InputStream input = assetManager.open("enablenew.bin");

            final AnagramSolver anagramSolver = new AnagramSolver(2, input, this);

            final Button buttonAll = (Button)findViewById(R.id.buttonAll);
            final Button buttonExact = (Button)findViewById(R.id.buttonExact);
            final Button buttonMultiword = (Button)findViewById(R.id.buttonMultiword);
            final EditText textQuery = (EditText)findViewById(R.id.textQuery);
            final EditText textMultiword = (EditText)findViewById(R.id.textMultiword);
            final TextView anagrams = (TextView)findViewById(R.id.textResults);

            buttonAll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    anagrams.setText("");

                    HashMap<String, Integer> combos = anagramSolver.findExactSubsets(textQuery.getText().toString());
                    results = AnagramSolverHelper.sortByComparator(combos, false);

                    //HashMap<String, Integer> combos = p.ShowAll(search.getText().toString());
                    //results = p.sortByComparator(combos, false);

                    if(results != null) {
                        Set<String> keys = results.keySet();
                        for (String key : keys) {
                            anagrams.append(key + " : " + results.get(key) + "\n");
                        }
                    } else {
                        anagrams.append("No matches.");
                    }

                }
            });

            buttonExact.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    anagrams.setText("");

                        /*
                        String results = p.Show(search.getText().toString());
                        String[] r = results.split(",");
                        */


                    Set<String> r = anagramSolver.findExactAnagrams(textQuery.getText().toString());

                    if(r != null) {
                        for (String s : r) {
                            anagrams.append(s + "\n");
                        }
                    } else {
                        anagrams.append("No matches.");
                    }
                }
            });

            buttonMultiword.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    anagrams.setText("");
                    int minWordLength = 0;
                    if(textMultiword.getText().length() == 0) {
                        anagrams.setText("Invalid input for Multiword.");
                        return;
                    }
                    String[] multiword = textMultiword.getText().toString().split(",");
                    int[] lengths = new int[multiword.length];


                    int iter = 0;
                    for(String s : multiword){
                        int i = Integer.parseInt(s);
                        /*if(i > minWordLength) {  <--this was the bug!
                            minWordLength = i;
                        } # This would make it the largest value, we want the smallest*/
                        lengths[iter] = i;
                        iter++;
                    }

                    Arrays.sort(lengths);
                    /* the following line is where we should define the smallest value
                       This should fix the bug...
                     */
                    minWordLength = lengths[0];


                    Set<Set<String>> anagram;

                    if (minWordLength == 0) {
                        anagram = anagramSolver.findAllAnagrams(textQuery.getText().toString());
                    }
                    else {
                        anagram = anagramSolver.findAllAnagrams(textQuery.getText().toString(), minWordLength);
                    }

                    if(!anagram.isEmpty()) {
                        Boolean match = false;
                        for (Set<String> combo : anagram) {
                            int size = combo.size();

                            if(size == lengths.length) {
                                int[] clengths = new int[size];
                                String tempstring = "";
                                int iter2 = 0;
                                for (String s : combo) {
                                    tempstring += s + " ";
                                    clengths[iter2] = s.length();
                                    iter2++;
                                }
                                Arrays.sort(clengths);

                                if(Arrays.equals(lengths, clengths)) {
                                    match = true;
                                    anagrams.append(tempstring + "\n");
                                }
                            }
                        }
                        if(!match) {
                            anagrams.append("No matches found.\n");
                        }
                    } else {
                        anagrams.append("No matches found.\n");
                    }

                }
            });






        } catch(IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
