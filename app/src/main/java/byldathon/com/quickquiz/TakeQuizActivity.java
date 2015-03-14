package byldathon.com.quickquiz;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.lorentzos.flingswipe.SwipeFlingAdapterView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;


public class TakeQuizActivity extends Activity {
    private ArrayList<String> questionsArrayList, answersArrayList;
    private ArrayAdapter<String> arrayAdapter;
    private RelativeLayout takeQuizBackGround;
    private int i;

    SwipeFlingAdapterView flingContainer;

    public static int totalNoOfQuestions = 0;
    public static int score = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_quiz);


        flingContainer = (SwipeFlingAdapterView) findViewById(R.id.frame);
        takeQuizBackGround = (RelativeLayout) findViewById(R.id.take_quiz_layout);
        Button rightButton = (Button) findViewById(R.id.right);
        Button leftButton = (Button) findViewById(R.id.left);

        questionsArrayList = new ArrayList<>();
        answersArrayList = new ArrayList<>();
        try {

            JSONObject object = new JSONObject(loadJSONFromAsset());
            JSONArray jsonArray = object.getJSONArray("Quiz");
            int i = 0;
            totalNoOfQuestions = jsonArray.length();
//            questionsArrayList.add("Let's Begin!");
//            answersArrayList.add("Yo");
            while(i < jsonArray.length()) {
                String question = jsonArray.getJSONObject(i).getString("question");
                questionsArrayList.add(question);
                String answer = jsonArray.getJSONObject(i).getString("answer");
                answersArrayList.add(answer);
                i++;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }



        arrayAdapter = new ArrayAdapter<>(this, R.layout.item, R.id.helloText, questionsArrayList);

        rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flingContainer.getTopCardListener().selectRight();

            }
        });

        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flingContainer.getTopCardListener().selectLeft();
            }
        });

        flingContainer.setAdapter(arrayAdapter);
        flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            @Override
            public void removeFirstObjectInAdapter() {
                // this is the simplest way to delete an object from the Adapter (/AdapterView)
               Log.d("LIST", "removed object!");
               questionsArrayList.get(0);
               answersArrayList.get(0);
               arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onLeftCardExit(Object dataObject) {
                //Do something on the left!
                //You also have access to the original object.
                //If you want to use it just cast it (String) dataObject
                if(questionsArrayList.get(0).contains("End of Quiz.")){
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.putExtra("Score", score);
                    intent.putExtra("TotalQuestions", totalNoOfQuestions);
                    startActivity(intent);
                }
                if(answersArrayList.get(0).equals("false")) {
                    questionsArrayList.remove(0);
                    answersArrayList.remove(0);
                    score++;
                    Toast.makeText(getApplicationContext(), "Correct", Toast.LENGTH_SHORT).show();
//                    Log.d("Raghav", answersArrayList.get(0) + " " + questionsArrayList.get(0));
                }
                else {
                    Toast.makeText(getApplicationContext(), "Incorrect!", Toast.LENGTH_SHORT).show();
                  //  Log.d("Raghav", answersArrayList.get(0) + " " + questionsArrayList.get(0));
                    questionsArrayList.remove(0);
                    answersArrayList.remove(0);
                    Vibrator vibrator = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                    vibrator.vibrate(250);
                    takeQuizBackGround.setBackgroundColor(getResources().getColor(android.R.color.holo_red_dark));
                }

            }

            @Override
            public void onRightCardExit(Object dataObject) {
                if(questionsArrayList.get(0).contains("End of Quiz.")){
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.putExtra("Score", score);
                    intent.putExtra("TotalQuestions", totalNoOfQuestions);
                    startActivity(intent);
                }
                if(answersArrayList.get(0).equals("true")) {
                    questionsArrayList.remove(0);
                    answersArrayList.remove(0);
                    score++;
                    Toast.makeText(getApplicationContext(), "Correct", Toast.LENGTH_SHORT).show();
                  //  Log.d("Raghav", answersArrayList.get(0) + " " + questionsArrayList.get(0));
                }
                else {
                    Toast.makeText(getApplicationContext(), "Incorrect!", Toast.LENGTH_SHORT).show();
                    questionsArrayList.remove(0);
                    answersArrayList.remove(0);
                   // Log.d("Raghav", answersArrayList.get(0) + " " + questionsArrayList.get(0));
                    Vibrator vibrator = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                    vibrator.vibrate(250);
                    takeQuizBackGround.setBackgroundColor(getResources().getColor(android.R.color.holo_red_dark));
                }
            }

            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {
                // Ask for more data here

                questionsArrayList.add("End of Quiz. Thanks for taking the Quiz.");
                answersArrayList.add("Yo!");
                Toast.makeText(getApplicationContext(), ""+score, Toast.LENGTH_LONG).show();
                arrayAdapter.notifyDataSetChanged();
                Log.d("LIST", "notified");

                //i++;
            }


            @Override
            public void onScroll(float scrollProgressPercent) {
                View view = flingContainer.getSelectedView();
                view.findViewById(R.id.item_swipe_right_indicator).setAlpha(scrollProgressPercent < 0 ? -scrollProgressPercent : 0);
                view.findViewById(R.id.item_swipe_left_indicator).setAlpha(scrollProgressPercent > 0 ? scrollProgressPercent : 0);
            }
        });


        // Optionally add an OnItemClickListener
        flingContainer.setOnItemClickListener(new SwipeFlingAdapterView.OnItemClickListener() {
            @Override
            public void onItemClicked(int itemPosition, Object dataObject) {
                Toast.makeText(getApplicationContext(), "Clicked!", Toast.LENGTH_SHORT).show();
                // makeToast(MyActivity.this, "Clicked!");
            }
        });

    }
    public String loadJSONFromAsset() {
        String json = null;
        try {

            InputStream is = getApplicationContext().getAssets().open("quiz.json");

            int size = is.available();

            byte[] buffer = new byte[size];

            is.read(buffer);

            is.close();

            json = new String(buffer, "UTF-8");


        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;

    }



}
