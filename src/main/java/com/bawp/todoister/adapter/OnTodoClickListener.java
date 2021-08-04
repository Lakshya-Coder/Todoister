package com.bawp.todoister.adapter;

import android.widget.RadioButton;

import com.bawp.todoister.model.Task;

public interface OnTodoClickListener {
    void onTodoClick(Task task);
    void onTodoRadioButtonClick(Task task, RadioButton radioButton);
}
