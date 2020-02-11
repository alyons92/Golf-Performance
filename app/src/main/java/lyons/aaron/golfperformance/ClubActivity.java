package lyons.aaron.golfperformance;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class ClubActivity extends AppCompatActivity {

    private static final String TAG = "ClubActivity";

    EditText inputField;
    Button addClubBtn;
    MyListAdapter mListAdapter;
    DatabaseHelper mDatabaseHelper;
    ListView lv;
    AlertDialog dialog;
    AlertDialog.Builder builder;

    String itemName;
    int itemID;

    private ArrayList<String> clubData;

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.clubs_layout);
        lv = findViewById(R.id.club_list);

        builder = new AlertDialog.Builder(this);


        mDatabaseHelper = new DatabaseHelper(this);

        inputField = findViewById(R.id.new_club_text);
        addClubBtn = findViewById(R.id.add_button);
        populateListView();

        addClubBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newEntry = inputField.getText().toString();
                if(newEntry.length() != 0){
                    addData(newEntry);
                    inputField.setText("");
                    populateListView();
                }else{
                    toastMessage("Enter a name in the field first.");
                }
                mListAdapter.notifyDataSetChanged();
            }
        });

    }

    public void addData(String newEntry){
        boolean insertData = mDatabaseHelper.addClubData(newEntry);
        if (insertData){
            toastMessage("Data successfully inserted.");
        }else{
            toastMessage("Something went wrong.");
        }
    }


    private void toastMessage(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }



    public class MyListAdapter extends ArrayAdapter<String>{
        private int layout;
        public MyListAdapter(Context context, int resource, List<String> objects) {
            super(context, resource, objects);
            layout = resource;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder mainViewHolder = null;
            if(convertView == null){
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(layout, parent, false);
                ViewHolder viewHolder= new ViewHolder();
                viewHolder.text = convertView.findViewById(R.id.club_text);
                viewHolder.button = convertView.findViewById(R.id.remove_button);
                viewHolder.text.setText(getItem(position));
                convertView.setTag(viewHolder);

                viewHolder.button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        itemName = clubData.get(position);
                        Cursor data = mDatabaseHelper.getItemID(itemName);
                        itemID = -1;
                        while (data.moveToNext()) {
                            itemID = data.getInt(0);
                        }
                        if (itemID > -1) {
                        } else {
                            toastMessage("No ID Associated with the name");
                        }

                        builder.setCancelable(true);
                        builder.setTitle("Confirm Removal");
                        builder.setMessage("Confirm deletion of '" + itemName + "'");
                        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                mDatabaseHelper.deleteName(itemID, itemName);
                                populateListView();
                            }
                        });
                        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                            }
                        });
                        dialog = builder.create();
                        dialog.show();


                    }
                });
            }else{
                mainViewHolder = (ViewHolder) convertView.getTag();
                mainViewHolder.text.setText(getItem(position));
            }
            return convertView;
        }
    }

    private void populateListView(){
        clubData = new ArrayList<>();
        try {
            Cursor data = mDatabaseHelper.getClubData();
            while (data.moveToNext()) {
                clubData.add(data.getString(1));
            }
        }catch(NullPointerException e){
        }
        mListAdapter = new MyListAdapter(this, R.layout.list_item, clubData);
        lv.setAdapter(mListAdapter);
        mListAdapter.notifyDataSetChanged();
    }

    public class ViewHolder {
        TextView text;
        Button button;
    }
}
