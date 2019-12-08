package student.inti.todolist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.CalendarView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.text.DateFormat;
import java.util.Calendar;

public class CalendarActivity extends AppCompatActivity {
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    RecyclerView mRecyclerView;
    FirebaseAuth mFirebaseAuth;
    Adapter mAdapter;
    String uid=mFirebaseAuth.getInstance().getCurrentUser().getUid();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_brown);
        setTitle("Calendar");

        final TextView textview = findViewById(R.id.dateTextView);
        final CalendarView cv = findViewById(R.id.calendar);
        mFirebaseDatabase=FirebaseDatabase.getInstance();
        mDatabaseReference=mFirebaseDatabase.getReference("Users").child(uid).child("Tasks");

        //getting today's date
        Calendar today = Calendar.getInstance();
        //formatting date
        final String todaydate = DateFormat.getDateInstance(DateFormat.FULL).format(today.getTime());
        textview.setText(todaydate);

        cv.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                //getting today's date
                Calendar myCalendar = Calendar.getInstance();
                //settings year,month,day
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                // TODO Auto-generated method stub
                String myFormat = DateFormat.getDateInstance(DateFormat.FULL).format(myCalendar.getTime());
                textview.setText(myFormat);
                //storing the value in a variable
                final String currentDate=textview.getText().toString();
                //passing the value to the function
                showData(currentDate);
            }
        });

    }

    public void showData(final String currentDate) {
        Query dquery = mDatabaseReference.orderByChild("date").equalTo(currentDate);
        FirebaseRecyclerOptions<Model> options = new FirebaseRecyclerOptions.Builder<Model>().setQuery(dquery,Model.class).build();
        mAdapter=new Adapter(options);
        mRecyclerView=findViewById(R.id.rRecyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.startListening();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_bar, menu);
        return super.onCreateOptionsMenu(menu);
    }
}
