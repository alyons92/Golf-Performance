package lyons.aaron.golfperformance;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class PerformanceActivity extends AppCompatActivity {

    private static final String TAG = "PerformanceActivity";

    private Button saveBtn;
    private EditText inputDistance;
    private EditText inputClubSpeed;
    private EditText inputBallSpeed;
    private TextView dateText;
    private TextView clubText;

    DatabaseHelper mDatabaseHelper;

    private String selectedName;
    private String selectedDate;
    private String saveDate;
    private int selectedID;
    Boolean update = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.performance_layout);
        saveBtn = findViewById(R.id.save_button);
        dateText = findViewById(R.id.text_date);
        clubText = findViewById(R.id.text_club);
        inputDistance = findViewById(R.id.input_distance);
        inputClubSpeed = findViewById(R.id.input_speed);
        inputBallSpeed = findViewById(R.id.input_ball_speed);

        mDatabaseHelper = new DatabaseHelper(this);

        Intent receivedIntent = getIntent();

        selectedID = receivedIntent.getIntExtra("id", -1);
        selectedName = receivedIntent.getStringExtra("name");
        selectedDate = receivedIntent.getStringExtra("dateText");
        saveDate = receivedIntent.getStringExtra("date");
        Log.d(TAG, saveDate);


        clubText.setText(selectedName);
        dateText.setText(selectedDate);

        populateData(saveDate, selectedID);

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String distanceString = inputDistance.getText().toString();
                String clubSpeedString = inputClubSpeed.getText().toString();
                String ballSpeedString = inputBallSpeed.getText().toString();
                if(!distanceString.equals("") && !clubSpeedString.equals("") && !ballSpeedString.equals("")){
                    Double distItem = Double.parseDouble(distanceString);
                    Double clubSpeedItem = Double.parseDouble(clubSpeedString);
                    Double ballSpeedItem = Double.parseDouble(ballSpeedString);
                    if(!update) {
                        addData(saveDate, selectedID, distItem, clubSpeedItem, ballSpeedItem);
                    }else{
                        updateData(saveDate, selectedID, distItem, clubSpeedItem, ballSpeedItem);
                    }
                }else{
                    toastMessage("You must enter a distance, club speed and ball speed");
                }
            }
        });


    }

    public void addData(String dateItem, int clubItem, Double distanceItem, Double clubSpeedItem, Double ballSpeedItem){
        boolean insertData = mDatabaseHelper.addPerformanceData(dateItem, clubItem, distanceItem, clubSpeedItem, ballSpeedItem);
        if (insertData){
            update = true;
            toastMessage("Data successfully inserted.");
        }else{
            toastMessage("Something went wrong.");
        }
    }

    public void updateData(String dateItem, int clubItem, Double distanceItem, Double clubSpeedItem, Double ballSpeedItem){
        mDatabaseHelper.updatePerformance(dateItem, clubItem, distanceItem, clubSpeedItem, ballSpeedItem);
        if (true){
            update = true;
            toastMessage("Data successfully updated.");
        }else{
            toastMessage("Something went wrong.");
        }
    }

    public void populateData(String date, int id) {
        try {
            Cursor data = mDatabaseHelper.getPerformanceData(date, id);
            while (data.moveToNext()) {
                update = true;
                inputDistance.setText(data.getString(0));
                inputClubSpeed.setText(data.getString(1));
                inputBallSpeed.setText(data.getString(2));
            }
        } catch (NullPointerException e) {
        }
    }

    private void toastMessage(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
