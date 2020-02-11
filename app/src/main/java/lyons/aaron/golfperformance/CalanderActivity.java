package lyons.aaron.golfperformance;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.CalendarView;
import java.util.Date;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;

public class CalanderActivity extends AppCompatActivity {

    private static final String TAG = "CalanderActivity";

    private CalendarView mCalendarView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calander_layout);
        mCalendarView = findViewById(R.id.calendarView);

        final SimpleDateFormat saveFormat = new SimpleDateFormat("YYYY-MM-DD");

        mCalendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView calendarView, int i, int i1, int i2) {

                String date = i + "/" + (i1+1) + "/" + i2;
                                Intent intent = new Intent(CalanderActivity.this, MainActivity.class);
                intent.putExtra("date", date);
                startActivity(intent);
            }
        });
    }
}
