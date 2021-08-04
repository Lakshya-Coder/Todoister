package com.bawp.todoister;

import android.os.Bundle;

import com.bawp.todoister.adapter.OnTodoClickListener;
import com.bawp.todoister.adapter.RecyclerViewAdapter;
import com.bawp.todoister.databinding.ActivityMainBinding;
import com.bawp.todoister.databinding.DeleteAllDialogBinding;
import com.bawp.todoister.databinding.DeleteDialogBinding;
import com.bawp.todoister.model.SharedViewModel;
import com.bawp.todoister.model.Task;
import com.bawp.todoister.model.TaskViewModel;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements OnTodoClickListener {
    private ActivityMainBinding binding;
    private TaskViewModel taskViewModel;
    private RecyclerViewAdapter recyclerViewAdapter;
    private BottomSheetFragment bottomSheetFragment;
    private SharedViewModel sharedViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(
                this,
                R.layout.activity_main
        );

        bottomSheetFragment = new BottomSheetFragment();
        ConstraintLayout constraintLayout = findViewById(R.id.bottomSheet);
        BottomSheetBehavior<ConstraintLayout> bottomSheetBehavior =
                BottomSheetBehavior.from(constraintLayout);

        bottomSheetBehavior.setPeekHeight(BottomSheetBehavior.STATE_HIDDEN);

        taskViewModel = new ViewModelProvider.AndroidViewModelFactory(
                getApplication()
        ).create(TaskViewModel.class);
        sharedViewModel = new ViewModelProvider(this)
                .get(SharedViewModel.class);

        setUpRecyclerView();

        setSupportActionBar(binding.toolbar);

        binding.fab.setOnClickListener(view -> showBottomSheetDialog());
    }

    private void showBottomSheetDialog() {
        bottomSheetFragment.show(
                getSupportFragmentManager(),
                bottomSheetFragment.getTag()
        );
    }

    private void setUpRecyclerView() {
        binding.recyclerView.setLayoutManager(
                new LinearLayoutManager(this)
        );
        binding.recyclerView.setHasFixedSize(true);

        taskViewModel.allTasks.observe(this, tasks -> {
            recyclerViewAdapter = new RecyclerViewAdapter(tasks, this);
            binding.recyclerView.setAdapter(recyclerViewAdapter);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_delete_all && taskViewModel.allTasks.getValue() != null) {
            DeleteAllDialogBinding deleteDialogBinding =
                    DeleteAllDialogBinding.inflate(getLayoutInflater());

            AlertDialog alertDialog = new AlertDialog.Builder(this)
                    .setView(deleteDialogBinding.getRoot())
                    .setCancelable(false)
                    .create();
            alertDialog.getWindow().setWindowAnimations(R.style.DialogAnimation);

            alertDialog.show();

            deleteDialogBinding.cancelBtn.setOnClickListener(
                    v -> alertDialog.dismiss()
            );

            deleteDialogBinding.deleteBtn.setOnClickListener(v -> {
                alertDialog.dismiss();
                TaskViewModel.deleteAll();
            });

            alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTodoClick(Task task) {
        // Toast.makeText(this, task.getTask(), Toast.LENGTH_SHORT).show();
        sharedViewModel.setSelectedItem(task);
        sharedViewModel.setEdit(true);
        showBottomSheetDialog();
    }

    @Override
    public void onTodoRadioButtonClick(Task task, RadioButton radioButton) {
        DeleteDialogBinding deleteDialogBinding = DeleteDialogBinding.inflate(getLayoutInflater());

        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setView(deleteDialogBinding.getRoot())
                .setCancelable(false)
                .create();
        alertDialog.getWindow().setWindowAnimations(R.style.DialogAnimation);

        alertDialog.show();

        deleteDialogBinding.cancelBtn.setOnClickListener(v ->  {
            radioButton.setChecked(false);
            alertDialog.dismiss();
        });

        deleteDialogBinding.deleteBtn.setOnClickListener(v -> {
            alertDialog.dismiss();
            TaskViewModel.delete(task);
        });

        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }
}