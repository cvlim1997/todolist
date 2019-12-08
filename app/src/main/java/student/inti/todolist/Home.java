package student.inti.todolist;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Menu;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Home extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    RecyclerView mRecyclerView;
    FirebaseAuth mFirebaseAuth;
    TextView Name;
    Button edateButton,etimeButton;
    EditText editTitle,editMessage,editDate, editTime, editPriority;
    Spinner editSpinner;
    String amPm;
    int mYear, mMonth, mDay, mHour, mMinute;

    private FirebaseDatabase mFirebaseDatabase=FirebaseDatabase.getInstance();
    //get current user
    String uid=mFirebaseAuth.getInstance().getCurrentUser().getUid();
    //set path based on reference
    private DatabaseReference mRef=mFirebaseDatabase.getReference("Users").child(uid).child("Tasks");
    //calling adapter
    private Adapter mAdapter;

    private ColorDrawable background = new ColorDrawable(Color.RED);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home2);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Task Lists");
        setSupportActionBar(toolbar);


        FloatingActionButton fab = findViewById(R.id.addBt);
        fab.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent iToAdd=new Intent(Home.this,AddActivity.class);
                startActivity(iToAdd);
            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle=new ActionBarDrawerToggle(this,drawer,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        final NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mFirebaseDatabase.getReference("Users").child(uid).child("name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name=dataSnapshot.getValue(String.class);
                View headerView;
                headerView = navigationView.getHeaderView(0);
                Name = (TextView)headerView.findViewById(R.id.hName);
                Name.setText(name);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        loadList();

    }

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
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Home.this).
                setMessage("Task Complete?").
                setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        item.setStatus("Completed");
                        mRef.child(key).setValue(item);
                        Toast.makeText(Home.this, "Jobs done!", Toast.LENGTH_SHORT).show();
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
        //prompt an alert dialog
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Home.this);
        alertDialog.setTitle("Edit Task");
        alertDialog.setMessage("Please fill full information");

        LayoutInflater inflater = this.getLayoutInflater();
        View add_menu_layout = inflater.inflate(R.layout.edit_layout,null);
        //adding layout
        editTitle = add_menu_layout.findViewById(R.id.edtTitle);
        editMessage = add_menu_layout.findViewById(R.id.edtMessage);
        editDate = add_menu_layout.findViewById(R.id.edtDate);
        editTime = add_menu_layout.findViewById(R.id.edtTime);
        editPriority = add_menu_layout.findViewById(R.id.edtPriority);
        editSpinner = add_menu_layout.findViewById(R.id.rEdtSpinner);
        edateButton = add_menu_layout.findViewById(R.id.edtDateBtn);
        etimeButton = add_menu_layout.findViewById(R.id.edtTimeBtn);
        //get the value from model
        editTitle.setText(item.getTitle());
        editMessage.setText(item.getMessage());
        editDate.setText(item.getDate());
        editTime.setText(item.getTime());
        editPriority.setText(item.getPriority());

        alertDialog.setView(add_menu_layout);
        alertDialog.setIcon(R.drawable.ic_edit);
        //array for spinner
        List<String> priority = new ArrayList<>();
        priority.add("Critical");
        priority.add("Important");
        priority.add("Normal");

        ArrayAdapter<String> dataAdapter;
        //creating spinner based on the array
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
                item.setStatus("Not complete");
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
                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.YEAR,year);
                cal.set(Calendar.MONTH,month);
                cal.set(Calendar.DAY_OF_MONTH,dayOfMonth);
                String dateformat = java.text.DateFormat.getDateInstance(java.text.DateFormat.FULL).format(cal.getTime());
                editDate.setText(dateformat);
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
                if (hourOfDay>=12){
                    amPm="PM";
                }else{
                    amPm="AM";
                }
                editTime.setText(String.format("%02d:%02d",hourOfDay,minute)+" "+amPm);
            }
        },mHour,mMinute,false);
        timePickerDialog.show();
    }
    ////////////////////////////////////////////////////////////
    private void loadList()
    {
        //query where status is = "Not complete"
        Query query=mRef.orderByChild("status").equalTo("Not complete");
        FirebaseRecyclerOptions<Model> options = new FirebaseRecyclerOptions.Builder<Model>().setQuery(query,Model.class).build();
        //setting the adapter based on the options
        mAdapter=new Adapter(options);
        //RecyclerView
        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAdapter);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView,
                                  @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
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
                                //delete task based on position
                                mAdapter.deleteItem(viewHolder.getAdapterPosition());
                                Toast.makeText(Home.this, "Task deleted.", Toast.LENGTH_SHORT).show();
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
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView,
                                    @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    public boolean onNavigationItemSelected(MenuItem item){
        int id = item.getItemId();
        if(id==R.id.nav_home){
            Intent menuIntent = new Intent(Home.this,Home.class);
            startActivity(menuIntent);
        }
        else if(id==R.id.nav_completedtask){
            Intent ctIntent = new Intent(Home.this,CompletedTask.class);
            startActivity(ctIntent);
        }
        else if(id==R.id.nav_Userslist){
            Intent usIntent=new Intent(Home.this,UserListActivity.class);
            startActivity(usIntent);
        }
        else if(id==R.id.nav_Calendar){
            Intent cIntent=new Intent(Home.this,CalendarActivity.class);
            startActivity(cIntent);
        }
        else if(id==R.id.nav_Assigned){
            Intent aIntent=new Intent(Home.this,ATaskActivity.class);
            startActivity(aIntent);
        }
        else if (id == R.id.nav_logout) {
            Intent signIn = new Intent(Home.this,LoginActivity.class);
            FirebaseAuth.getInstance().signOut();
            signIn.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(signIn);
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

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
