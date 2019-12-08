package student.inti.todolist;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class DetailsActivity extends AppCompatActivity {
    TextView mTitle,mMessage,mDate,mTime,mPriority,mAssign;
    String mttl,mmsg,mdt,mtm,mpty,masg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp);
        setTitle("Details");

        mTitle=findViewById(R.id.dTitle);
        mMessage=findViewById(R.id.dMessage);
        mDate=findViewById(R.id.dDate);
        mTime=findViewById(R.id.dTime);
        mPriority=findViewById(R.id.dPriority);
        mAssign=findViewById(R.id.dAssignBy);
        //getting values from ATaskActivity
        mttl=getIntent().getStringExtra("ttl");
        mmsg=getIntent().getStringExtra("msg");
        mdt=getIntent().getStringExtra("dt");
        mtm=getIntent().getStringExtra("tm");
        mpty=getIntent().getStringExtra("pty");
        masg=getIntent().getStringExtra("assign");
        //setting value
        mTitle.setText("Title: "+mttl);
        mMessage.setText("Message: "+mmsg);
        mDate.setText("Date: "+mdt);
        mTime.setText("Time: "+mtm);
        mPriority.setText("Priority: "+mpty);
        mAssign.setText("Assigned By: "+masg);
    }
}
