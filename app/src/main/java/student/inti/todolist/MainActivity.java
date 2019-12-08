package student.inti.todolist;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.renderscript.Sampler;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity{
    Button btnLogOut;
    FloatingActionButton btnAdd;
    RecyclerView mRecyclerView;
    FirebaseAuth mFirebaseAuth;

    Button edateButton,etimeButton;
    EditText editTitle,editMessage,editDate, editTime, editPriority;
    Spinner editSpinner;
    int mYear, mMonth, mDay, mHour, mMinute;

    private FirebaseDatabase mFirebaseDatabase=FirebaseDatabase.getInstance();
    //get current user
    String uid=mFirebaseAuth.getInstance().getCurrentUser().getUid();
    private DatabaseReference mRef=mFirebaseDatabase.getReference("Users").child(uid).child("Tasks");
    private Adapter mAdapter;

    private ColorDrawable background = new ColorDrawable(Color.RED);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnLogOut=findViewById(R.id.logoutbut);
        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent iToMain=new Intent(MainActivity.this,LoginActivity.class);
                startActivity(iToMain);
            }
        });

        btnAdd=findViewById(R.id.addBut);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iToAdd=new Intent(MainActivity.this,AddActivity.class);
                startActivity(iToAdd);
            }
        });


        //Action bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Task Lists");
        loadList();
    }


    ////////////////////////////////////////////////////////////
    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {

        if(item.getTitle().equals(Common.UPDATE)){
            showUpdateDialog(mAdapter.getRef(item.getOrder()).getKey(),mAdapter.getItem(item.getOrder()));
        }
        else if(item.getTitle().equals(Common.DONE)){
            showUpdateDialog1(mAdapter.getRef(item.getOrder()).getKey(),mAdapter.getItem(item.getOrder()));
        }
        return super.onContextItemSelected(item);
    }

    private void showUpdateDialog1(final String key, final Model item) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this).
                setMessage("Task Complete?").
                setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        item.setStatus("Completed");
                        mRef.child(key).setValue(item);
                        Toast.makeText(MainActivity.this, "Jobs done!", Toast.LENGTH_SHORT).show();
                    }
                });
        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.show();

    }

    private void showUpdateDialog(final String key, final Model item){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
        alertDialog.setTitle("Edit Task");
        alertDialog.setMessage("Please fill full information");

        LayoutInflater inflater = this.getLayoutInflater();
        View add_menu_layout = inflater.inflate(R.layout.edit_layout,null);

        editTitle = add_menu_layout.findViewById(R.id.edtTitle);
        editMessage = add_menu_layout.findViewById(R.id.edtMessage);
        editDate = add_menu_layout.findViewById(R.id.edtDate);
        editTime = add_menu_layout.findViewById(R.id.edtTime);
        editPriority = add_menu_layout.findViewById(R.id.edtPriority);
        editSpinner = add_menu_layout.findViewById(R.id.rEdtSpinner);
        edateButton = add_menu_layout.findViewById(R.id.edtDateBtn);
        etimeButton = add_menu_layout.findViewById(R.id.edtTimeBtn);

        editTitle.setText(item.getTitle());
        editMessage.setText(item.getMessage());
        editDate.setText(item.getDate());
        editTime.setText(item.getTime());
        editPriority.setText(item.getPriority());

        alertDialog.setView(add_menu_layout);
        alertDialog.setIcon(R.drawable.ic_edit);

        List<String> priority = new ArrayList<>();
        priority.add("Critical");
        priority.add("Important");
        priority.add("Normal");

        ArrayAdapter<String> dataAdapter;
        dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, priority);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // Dropdownlist layout style
        editSpinner.setAdapter(dataAdapter); // attaching data adapter to spinner

        editSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                editPriority.setText(item);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        edateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePicker();
            }
        });
        etimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePicker();
            }
        });
        alertDialog.setPositiveButton("EDIT", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                //Update Information
                item.setTitle(editTitle.getText().toString());
                item.setMessage(editMessage.getText().toString());
                item.setDate(editDate.getText().toString());
                item.setTime(editTime.getText().toString());
                item.setPriority(editPriority.getText().toString());
                mRef.child(key).setValue(item);
            }
        });
        alertDialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.show();
    }

    public void datePicker(){
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog dpd = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                editDate.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
            }
        }, mYear, mMonth, mDay);
        dpd.show();
    }
    public void timePicker(){
        final Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                editTime.setText(hourOfDay+":"+minute);
            }
        },mHour,mMinute,false);
        timePickerDialog.show();
    }
    ////////////////////////////////////////////////////////////
    private void loadList()
    {
        Query query=mRef.orderByChild("Status").equalTo("Not complete");
        FirebaseRecyclerOptions<Model> options = new FirebaseRecyclerOptions.Builder<Model>().setQuery(query,Model.class).build();
        mAdapter=new Adapter(options);
        //RecyclerView
        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAdapter);


        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull final RecyclerView.ViewHolder viewHolder, int direction) {
                viewHolder.getAdapterPosition();
                new AlertDialog.Builder(viewHolder.itemView.getContext())
                        .setMessage("Are you sure you want to delete this task?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mAdapter.deleteItem(viewHolder.getAdapterPosition());
                                Toast.makeText(MainActivity.this, "Task deleted.", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mAdapter.notifyItemChanged(viewHolder.getAdapterPosition());
                            }
                        })
                        .create()
                        .show();
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                View itemView = viewHolder.itemView;
                int backgroundCornerOffset = 20;

                if (dX > 0) { // Swiping to the right
                    background.setBounds(itemView.getLeft(), itemView.getTop(),
                            itemView.getLeft() + ((int) dX) + backgroundCornerOffset,
                            itemView.getBottom());

                } else if (dX < 0) { // Swiping to the left
                    background.setBounds(itemView.getRight() + ((int) dX) - backgroundCornerOffset,
                            itemView.getTop(), itemView.getRight(), itemView.getBottom());
                } else { // view is unSwiped
                    background.setBounds(0, 0, 0, 0);
                }
                background.draw(c);
            }
        }).attachToRecyclerView(mRecyclerView);
    }
    @Override
    protected void onStop() {
        super.onStop();
        mAdapter.stopListening();
    }
    @Override
    protected void onResume(){
        super.onResume();
        mAdapter.startListening();
    }
}
