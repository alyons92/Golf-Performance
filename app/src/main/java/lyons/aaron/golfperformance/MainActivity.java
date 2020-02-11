package lyons.aaron.golfperformance;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private TextView dateText;
    private Button calendarBtn;
    private Button clubBtn;
    private Button graphBtn;
    private ListView listView;
    private AdView mAdView;
    String date;
    String saveDate;

    final Calendar myCalendar = Calendar.getInstance();

    private ArrayList<String> clubData;
    DatabaseHelper mDatabaseHelper;
    ListAdapter mListAdapter;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
                mAdView = findViewById(R.id.adView);
                AdRequest adRequest = new AdRequest.Builder().build();
                mAdView.loadAd(adRequest);
            }
        });
        dateText = findViewById(R.id.date_text);
        calendarBtn = findViewById(R.id.calender_button);
        clubBtn = findViewById(R.id.clubs_button);
        graphBtn = findViewById(R.id.graph_button);

        updateLabel();
        mDatabaseHelper = new DatabaseHelper(this);


        final DatePickerDialog.OnDateSetListener dateListen = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, day);
                updateLabel();
            }
        };

        calendarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(MainActivity.this, dateListen, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        clubBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ClubActivity.class);
                startActivity(intent);
            }
        });

        graphBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(clubData.size()==0){
                    toastMessage("Please create clubs first.");
                }else {
                    Intent intent = new Intent(MainActivity.this, GraphActivity.class);
                    startActivity(intent);
                }
            }
        });


        listView = findViewById(R.id.club_list);
        populateListView();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        populateListView();
    }

    private void toastMessage(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void populateListView() {
        clubData = new ArrayList<>();
        try {
            Cursor data = mDatabaseHelper.getClubData();
            while (data.moveToNext()) {
                clubData.add(data.getString(1));
            }
        } catch (NullPointerException e) {

        }
        mListAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, clubData);
        listView.setAdapter(mListAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String name = mListAdapter.getItem(i).toString();
                Cursor data = mDatabaseHelper.getItemID(name);
                int itemID = -1;
                while(data.moveToNext()){
                    itemID = data.getInt(0);
                }
                if(itemID > -1){
                    Intent performanceScreenIntent = new Intent(MainActivity.this, PerformanceActivity.class);
                    performanceScreenIntent.putExtra("id", itemID);
                    performanceScreenIntent.putExtra("name", name);
                    performanceScreenIntent.putExtra("date", saveDate);
                    performanceScreenIntent.putExtra("dateText", date);
                    startActivity(performanceScreenIntent);
                }else{
                    toastMessage("No ID associated with that name");
                }
            }
        });
    }

    private void updateLabel() {
        String myFormat = "dd/MM/yyyy";
        String saveFormat = "YYYY-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat);
        SimpleDateFormat sdfSave = new SimpleDateFormat(saveFormat);
        saveDate = (sdfSave.format(myCalendar.getTime())) + " 00:00:00.000";
        date = sdf.format(myCalendar.getTime());
        dateText.setText(date);

    }
}
