package student.inti.todolist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SendActivity extends AppCompatActivity implements
        View.OnClickListener{
    TextView mOtherUid,mHeader;
    Button mButtonDp,mButtonTp;
    EditText mTitle, mMessage, mDate, mTime, mPriority;
    int mYear, mMonth, mDay, mHour, mMinute;
    Spinner mSpinner;
    String amPm;
    private DatabaseReference mDatabaseReference;
    private FirebaseAuth mFirebaseAuth;
    String receiveUserId,receiveUserName,currentUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send);

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_brown);
        setTitle("Share Task");

        mDatabaseReference= FirebaseDatabase.getInstance().getReference().child("Users");
        mOtherUid=findViewById(R.id.otheruid);
        mHeader=findViewById(R.id.header);
        mTitle=findViewById(R.id.oTitle);
        mMessage=findViewById(R.id.oMessage);
        mDate=findViewById(R.id.oDatePicker);
        mTime=findViewById(R.id.oTimePicker);
        mPriority=findViewById(R.id.oPriority);
        mButtonDp=findViewById(R.id.oDateBtn);
        mButtonTp=findViewById(R.id.oTimeBtn);
        mSpinner=findViewById(R.id.oSpinner);

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
        //getting the user uid from userlistactivity
        receiveUserId = getIntent().getStringExtra("otheruid");
        RetrieveToUserName();
        RetrieveFromUserName();
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
        menuInflater.inflate(R.menu.new_note_menu2, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sendbt:
                addOTask();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void addOTask() {
        //set a reference to add task to based on the other user uid
        DatabaseReference Ouid=FirebaseDatabase.getInstance().getReference().child("Users").child(receiveUserId).child("Assigned Tasks").push();
        final String uniqid=Ouid.getKey();
        //getting all the text from textview and store into string
        String ttl=mTitle.getText().toString();
        String msg=mMessage.getText().toString();
        String dt = mDate.getText().toString();
        String tm = mTime.getText().toString();
        String pty = mPriority.getText().toString();
        if (ttl.trim().isEmpty() || msg.trim().isEmpty() || dt.trim().isEmpty() || tm.trim().isEmpty()) {
            Toast.makeText(this, "Please fill up the field.", Toast.LENGTH_SHORT).show();
        }
        else{
            //insert data
            Map newPost = new HashMap();
            newPost.put("title",ttl);
            newPost.put("message", msg);
            newPost.put("date", dt);
            newPost.put("time", tm);
            newPost.put("priority",pty);
            newPost.put("uniqKey",uniqid);
            newPost.put("assignedby",currentUserName);
            Ouid.setValue(newPost);
            Toast.makeText(SendActivity.this, "Task sent successfully.", Toast.LENGTH_SHORT).show();
            Intent iToBack = new Intent(SendActivity.this, Home.class);
            startActivity(iToBack);
        }
    }
    private void RetrieveFromUserName() {
        // get the current user uid
        String current_uid=mFirebaseAuth.getInstance().getCurrentUser().getUid();
        //set a reference based on user uid and getvalue
        mDatabaseReference.child(current_uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //get the value where the table name = "name"
                currentUserName=dataSnapshot.child("name").getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void RetrieveToUserName() {
        //set a reference based on the "senttaskto" user uid
        mDatabaseReference.child(receiveUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //get the value where the table name ="name"
                receiveUserName=dataSnapshot.child("name").getValue().toString();
                mHeader.setText("Send Task to "+receiveUserName);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
