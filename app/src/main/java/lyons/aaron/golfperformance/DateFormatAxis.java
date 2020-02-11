package lyons.aaron.golfperformance;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class DateFormatAxis implements IAxisValueFormatter {
    private ArrayList<String> mValues;


    public DateFormatAxis(ArrayList<String> values){
        this.mValues = values;
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {

        return mValues.get((int)value);
    }
}
