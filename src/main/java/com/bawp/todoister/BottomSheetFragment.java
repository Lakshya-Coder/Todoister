package com.bawp.todoister;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bawp.todoister.model.Priority;
import com.bawp.todoister.model.SharedViewModel;
import com.bawp.todoister.model.Task;
import com.bawp.todoister.model.TaskViewModel;
import com.bawp.todoister.util.Util;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.chip.Chip;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.Group;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import java.util.Calendar;
import java.util.Date;

public class BottomSheetFragment extends BottomSheetDialogFragment
    implements View.OnClickListener {
    private TextView enterTodo;
    private ImageButton calendarButton;
    private ImageButton priorityButton;
    private RadioGroup priorityRadioGroup;
    private RadioButton selectedRadioButton;
    private int selectedButtonId;
    private ImageButton saveButton;
    private CalendarView calendarView;
    private Group calendarGroup;
    private Date dueDate;
    private Calendar calendar = Calendar.getInstance();
    private SharedViewModel sharedViewModel;
    private boolean isEdit;
    private Priority priority;

    public BottomSheetFragment () {}

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(
                R.layout.bottom_sheet,
                container,
                false
        );

        calendarGroup = view.findViewById(R.id.calendar_group);
        calendarView = view.findViewById(R.id.calendar_view);
        calendarButton = view.findViewById(R.id.today_calendar_button);
        enterTodo = view.findViewById(R.id.enter_todo_et);
        saveButton = view.findViewById(R.id.save_todo_button);
        priorityButton = view.findViewById(R.id.priority_todo_button);
        priorityRadioGroup = view.findViewById(R.id.radioGroup_priority);

        Chip todayChip = view.findViewById(R.id.today_chip);
        Chip tomorrowChip = view.findViewById(R.id.tomorrow_chip);
        Chip nextWeakChip = view.findViewById(R.id.next_week_chip);

        todayChip.setOnClickListener(this);
        tomorrowChip.setOnClickListener(this);
        nextWeakChip.setOnClickListener(this);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (sharedViewModel.getSelectedItem().getValue() != null) {
            Task task = sharedViewModel.getSelectedItem().getValue();

            isEdit = true;
            enterTodo.setText(task.getTask());
        }
    }

    @SuppressLint("NonConstantResourceId")
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sharedViewModel = new ViewModelProvider(requireActivity())
                .get(SharedViewModel.class);

        calendarView.setOnDateChangeListener((v, year, month, dayOfMonth) -> {
            calendar.clear();
            calendar.set(year, month, dayOfMonth);

            dueDate = calendar.getTime();
        });

        priorityButton.setOnClickListener(v -> {
            Util.hideSoftKeyboard(v);

            priorityRadioGroup.setVisibility(
                    priorityRadioGroup.getVisibility() == View.GONE ?
                            View.VISIBLE : View.GONE
            );

            priorityRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
                if (priorityRadioGroup.getVisibility() == View.VISIBLE) {
                    selectedButtonId = checkedId;
                    selectedRadioButton = view.findViewById(selectedButtonId);

                    switch (selectedRadioButton.getId()) {
                        case R.id.radioButton_high:
                            priority = Priority.HIGH;
                            break;
                        case R.id.radioButton_med:
                            priority = Priority.MEDIUM;
                            break;
                        default:
                            priority = Priority.LOW;
                    }
                } else {
                    priority = Priority.LOW;
                }
            });
        });

        saveButton.setOnClickListener(v -> {
            String task = enterTodo.getText().toString().trim();

            if (
                    !TextUtils.isEmpty(task) && dueDate != null && priority != null
            ) {
                Task myTask = new Task(task, priority,
                        dueDate,
                        Calendar.getInstance().getTime(),
                        false
                );

                if (isEdit) {
                    Task updatedTask = sharedViewModel.getSelectedItem().getValue();

                    assert updatedTask != null;
                    updatedTask.setTask(task);
                    updatedTask.setCreatedDate(Calendar.getInstance().getTime());
                    updatedTask.setDueDate(dueDate);
                    updatedTask.setPriority(priority);

                    TaskViewModel.update(updatedTask);
                    sharedViewModel.setEdit(false);
                    sharedViewModel.setSelectedItem(null);
                    isEdit = false;
                } else {
                    TaskViewModel.insert(myTask);
                }
                enterTodo.setText("");
                dismiss();
            } else {
                Toast.makeText(
                        getContext(),
                        R.string.empty_field,
                        Toast.LENGTH_SHORT
                ).show();
            }
        });


        calendarButton.setOnClickListener(v -> {
            calendarGroup.setVisibility(
                calendarGroup.getVisibility() == View.GONE ? View.VISIBLE : View.GONE
            );
            Util.hideSoftKeyboard(v);
        });

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {
            case R.id.today_chip:
                calendar.add(Calendar.DAY_OF_YEAR, 0);
                dueDate = calendar.getTime();
                break;
            case R.id.tomorrow_chip:
                calendar.add(Calendar.DAY_OF_YEAR, 1);
                dueDate = calendar.getTime();
                break;
            case R.id.next_week_chip:
                calendar.add(Calendar.DAY_OF_YEAR, 7);
                dueDate = calendar.getTime();
                break;
        }

    }
}