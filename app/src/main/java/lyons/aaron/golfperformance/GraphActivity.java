package lyons.aaron.golfperformance;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import java.text.ParseException;
import java.util.Date;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class GraphActivity extends AppCompatActivity {

    private static final String TAG = "GraphActivity";
    private ArrayList<String> clubData;
    private ArrayList<String> dateData;
    private ArrayList<Entry> distanceData;
    private ArrayList<Entry> clubSpeedData;
    private ArrayList<Entry> ballSpeedData;
    String myFormat = "dd/MM/yyyy";
    String saveFormat = "yyyy-MM-dd";

    DatabaseHelper mDatabaseHelper;
    ArrayAdapter<String> mListAdapter;
    Spinner spinner;
    Button generateGraph;
    private EditText inputStartDate;
    private EditText inputEndDate;
    final Calendar myCalendar = Calendar.getInstance();
    Boolean startPicked = false;
    LineChart chart;
    String formatStart;
    String formatEnd;
    long calTimeStart;
    long calTimeEnd;
    LineData lineData;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.graph_layout);
        mDatabaseHelper = new DatabaseHelper(this);
        inputStartDate = findViewById(R.id.input_start_date);
        inputEndDate = findViewById(R.id.input_end_date);
        generateGraph = findViewById(R.id.generate_button);
        chart = findViewById(R.id.chart);
        chart.setPinchZoom(true);


        final DatePickerDialog.OnDateSetListener dateListen = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, day);
                updateLabel();
            }
        };

        inputStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startPicked = true;
                new DatePickerDialog(GraphActivity.this, dateListen, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        inputEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startPicked = false;
                new DatePickerDialog(GraphActivity.this, dateListen, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        generateGraph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(chart.getData() != null){
                    chart.clearValues();
                    chart.notifyDataSetChanged();
                    chart.invalidate();
                }

                dateData = new ArrayList<>();
                distanceData = new ArrayList<>();
                clubSpeedData = new ArrayList<>();
                ballSpeedData = new ArrayList<>();

                String name = spinner.getSelectedItem().toString();
                Cursor dataID = mDatabaseHelper.getItemID(name);
                int itemID = -1;
                while(dataID.moveToNext()){
                    itemID = dataID.getInt(0);
                }
                if(itemID>-1){
                }else{
                    toastMessage("No ID Associated with the name");
                }
                Cursor data = mDatabaseHelper.getDateData(formatStart, formatEnd, itemID);
                Log.d(TAG, formatStart + " " + formatEnd);
                int count = 0;

                while (data.moveToNext()) {
                    dateData.add(data.getString(0));
                    int position = calcDatePosition(count);
                    distanceData.add(new Entry(position, (float) data.getDouble(1)));
                    clubSpeedData.add(new Entry(position, (float) data.getDouble(2)));
                    ballSpeedData.add(new Entry(position, (float) data.getDouble(3)));
                    count++;
                }
                    chart.getDescription().setEnabled(false);
                    setYAxis();
                    setXAxis();
                    setData();

            }
        });

        spinner = findViewById(R.id.spinner);
        populateSpinner();
    }

    private void updateLabel(){

        SimpleDateFormat sdf = new SimpleDateFormat(myFormat);
        SimpleDateFormat sdfSave = new SimpleDateFormat(saveFormat);
        if(startPicked){
            inputStartDate.setText(sdf.format(myCalendar.getTime()));
            formatStart = sdfSave.format(myCalendar.getTime());
            myCalendar.setTime(dateFromString(formatStart));
            calTimeStart = myCalendar.getTimeInMillis();
        }else{
            inputEndDate.setText(sdf.format(myCalendar.getTime()));
            formatEnd = sdfSave.format(myCalendar.getTime());
            myCalendar.setTime(dateFromString(formatEnd));
            calTimeEnd = myCalendar.getTimeInMillis();
        }

    }

    public void populateSpinner(){
        clubData = new ArrayList<>();
        try {
            Cursor data = mDatabaseHelper.getClubData();
            while (data.moveToNext()) {
                clubData.add(data.getString(1));
            }
        } catch (NullPointerException e) {
        }
        mListAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, clubData);
        mListAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(mListAdapter);

    }

    private void setData(){
        LineDataSet distanceSet;
        LineDataSet clubSpeedSet;
        LineDataSet ballSpeedSet;

        distanceSet = new LineDataSet(distanceData, "Distance");
        distanceSet.setFillAlpha(110);
        distanceSet.setColor(Color.BLUE);
        distanceSet.setCircleColor(Color.BLUE);
        distanceSet.setLineWidth(1f);
        distanceSet.setCircleRadius(3f);
        distanceSet.setDrawCircleHole(false);
        distanceSet.setValueTextSize(9f);
        distanceSet.setDrawFilled(false);
        distanceSet.setMode(LineDataSet.Mode.LINEAR);


        clubSpeedSet = new LineDataSet(clubSpeedData, "Club Speed");
        clubSpeedSet.setFillAlpha(110);
        clubSpeedSet.setColor(Color.RED);
        clubSpeedSet.setCircleColor(Color.RED);
        clubSpeedSet.setLineWidth(1f);
        clubSpeedSet.setCircleRadius(3f);
        clubSpeedSet.setDrawCircleHole(false);
        clubSpeedSet.setValueTextSize(9f);
        clubSpeedSet.setDrawFilled(false);
        clubSpeedSet.setMode(LineDataSet.Mode.LINEAR);

        ballSpeedSet = new LineDataSet(ballSpeedData, "Ball Speed");
        ballSpeedSet.setFillAlpha(110);
        ballSpeedSet.setColor(Color.GREEN);
        ballSpeedSet.setCircleColor(Color.GREEN);
        ballSpeedSet.setLineWidth(1f);
        ballSpeedSet.setCircleRadius(3f);
        ballSpeedSet.setDrawCircleHole(false);
        ballSpeedSet.setValueTextSize(9f);
        ballSpeedSet.setDrawFilled(false);
        ballSpeedSet.setMode(LineDataSet.Mode.LINEAR);


        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(distanceSet);
        dataSets.add(clubSpeedSet);
        dataSets.add(ballSpeedSet);

        lineData = new LineData(dataSets);
        chart.setData(lineData);
        chart.notifyDataSetChanged();
        chart.invalidate();

    }

    public void setYAxis(){
        final YAxis yAxisLeft = chart.getAxisLeft();
        yAxisLeft.setAxisMinimum(0);
        yAxisLeft.setAxisMaximum(200);
        yAxisLeft.setGranularity(10);
        yAxisLeft.setTextSize(12f);
        yAxisLeft.setTextColor(Color.BLACK);
        yAxisLeft.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return value == yAxisLeft.getAxisMinimum() ? (int) value + "" : (int) value + "";
            }
        });
        chart.getAxisRight().setEnabled(false);
    }

    public void setXAxis(){
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setLabelCount(20);
        xAxis.setTextColor(Color.BLACK);
        xAxis.setTextSize(12f);
        xAxis.setGranularity(1);
        xAxis.setLabelRotationAngle(-90f);
        DateFormatAxis dfa = new DateFormatAxis(generateXAxisStrings());
        xAxis.setValueFormatter(dfa);

    }


    private void toastMessage(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private ArrayList<String> generateXAxisStrings(){
        ArrayList<String> xAxis = new ArrayList<>();
        long msDiff = calTimeEnd - calTimeStart;
        long daysDiff = TimeUnit.MILLISECONDS.toDays(msDiff);
        Calendar c = Calendar.getInstance();
        c.setTime(dateFromString(formatStart));
        for(int i=0; i<=(int)daysDiff; i++){
            String newDate = stringFromDate(c.getTime());
            xAxis.add(newDate);
            c.add(Calendar.DATE, 1);
        }
        convertDatesToMonthDay(xAxis);
        return xAxis;

    }

    private void convertDatesToMonthDay(ArrayList<String> array){
        SimpleDateFormat newSdf = new SimpleDateFormat("dd/MM");
        int numDates = array.size();
        for(int i=0; i<numDates; i++){
            Date actual = dateFromString(array.get(i));
            String newDate = newSdf.format(actual);
            array.set(i, newDate);
        }
    }

    private String stringFromDate(Date d){
        SimpleDateFormat sdf = new SimpleDateFormat(saveFormat);
        String newDate = sdf.format(d);
        return newDate;
    }

    private Date dateFromString(String s){
        SimpleDateFormat format = new SimpleDateFormat(saveFormat);
        Date newDate;
        try{
            newDate = format.parse(s);
            return newDate;
        }catch (ParseException e){
            e.printStackTrace();
            return null;
        }
    }
    private int calcDatePosition(int i){
        String stringDate = dateData.get(i);
        Date date = dateFromString(stringDate);
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        long timeDate = c.getTimeInMillis();
        long dateDif = timeDate - calTimeStart;
        long daysDiff = TimeUnit.MILLISECONDS.toDays(dateDif);
        return (int) daysDiff;
    }
}

