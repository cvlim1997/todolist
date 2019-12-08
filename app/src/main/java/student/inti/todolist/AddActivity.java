package student.inti.todolist;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.icu.text.CaseMap;
import android.icu.text.DateFormat;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddActivity extends AppCompatActivity implements
        View.OnClickListener{
    Button mButtonDp,mButtonTp;
    EditText mTitle, mMessage, mDate, mTime, mPriority;
    int mYear, mMonth, mDay, mHour, mMinute;
    Spinner mSpinner;
    FirebaseAuth mAuth;
    String amPm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_brown);
        setTitle("Add new Task");

        mAuth = FirebaseAuth.getInstance();
        mTitle = findViewById(R.id.Title);
        mMessage = findViewById(R.id.Message);
        mDate = findViewById(R.id.datepicker);
        mTime = findViewById(R.id.timepicker);
        mPriority = findViewById(R.id.Priority);
        mSpinner = findViewById(R.id.rSpinner);
        mButtonDp = findViewById(R.id.dateBtn);
        mButtonTp = findViewById(R.id.timeBtn);

        mButtonDp.setOnClickListener(this);
        mButtonTp.setOnClickListener(this);

        List<String> priority = new ArrayList<>();
        priority.add("Critical");
        priority.add("Important");
        priority.add("Normal");

        ArrayAdapter<String> dataAdapter;
        dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, priority);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // Dropdownlist layout style
        mSpinner.setAdapter(dataAdapter); // attaching data adapter to spinner

        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                mPriority.setText(item);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    public void onClick(View v)
    {
        if (v == mButtonDp) {
            final Calendar c = Calendar.getInstance();
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog dpd = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    Calendar cal = Calendar.getInstance();
                    cal.set(Calendar.YEAR,year);
                    cal.set(Calendar.MONTH,month);
                    cal.set(Calendar.DAY_OF_MONTH,dayOfMonth);
                    String dateformat = java.text.DateFormat.getDateInstance(java.text.DateFormat.FULL).format(cal.getTime());
                    mDate.setText(dateformat);
                    //mDate.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                }
            }, mYear, mMonth, mDay);
            dpd.show();
        }
        if(v==mButtonTp){
            final Calendar c = Calendar.getInstance();
            mHour = c.get(Calendar.HOUR_OF_DAY);
            mMinute = c.get(Calendar.MINUTE);
            TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    if (hourOfDay>=12){
                        amPm="PM";
                    }else{
                        amPm="AM";
                    }
                    mTime.setText(String.format("%02d:%02d",hourOfDay,minute)+" "+amPm);
                    //mTime.setText(hourOfDay+":"+minute+amPm);
                }
            },mHour,mMinute,false);
            timePickerDialog.show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.new_note_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.uploadbt:
                addTask();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void addTask() {
        String user_id = mAuth.getCurrentUser().getUid();
        DatabaseReference current_user_db = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id).child("Tasks").push();
        final String uniqid=current_user_db.getKey();
        String ttl = mTitle.getText().toString();
        String msg = mMessage.getText().toString();
        String dt = mDate.getText().toString();
        String tm = mTime.getText().toString();
        String pty = mPriority.getText().toString();
        String status = "Not complete";
        if (ttl.trim().isEmpty() || msg.trim().isEmpty() || dt.trim().isEmpty() || tm.trim().isEmpty()) {
            Toast.makeText(this, "Please fill up the field.", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Map newPost = new HashMap();
            newPost.put("title", ttl);
            newPost.put("message", msg);
            newPost.put("date", dt);
            newPost.put("time", tm);
            newPost.put("priority",pty);
            newPost.put("uniqKey",uniqid);
            newPost.put("status",status);
            current_user_db.setValue(newPost);
            Toast.makeText(AddActivity.this, "Task created successfully.", Toast.LENGTH_SHORT).show();
            Intent iToBack = new Intent(AddActivity.this, Home.class);
            startActivity(iToBack);
        }
    }
}
