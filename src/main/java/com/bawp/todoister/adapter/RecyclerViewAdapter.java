package com.bawp.todoister.adapter;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.bawp.todoister.R;
import com.bawp.todoister.model.Task;
import com.bawp.todoister.util.Util;
import com.google.android.material.chip.Chip;

import java.util.List;

public class RecyclerViewAdapter extends 
        RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private final List<Task> tasks;
    private final OnTodoClickListener todoClickListener;

    public RecyclerViewAdapter(List<Task> tasks, OnTodoClickListener todoClickListener) {
        this.tasks = tasks;
        this.todoClickListener = todoClickListener;
    }

    @NonNull
    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.todo_row,
                parent,
                false
        );

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter.ViewHolder holder, int position) {
        Task task = tasks.get(position);
        String formatted = Util.formatDate(task.getDueDate());

        ColorStateList colorStateList = new ColorStateList(new int[][] {
                new int[] { -android.R.attr.state_enabled },
                new int[] { android.R.attr.state_enabled }
        },
        new int[] {
                Color.LTGRAY,
                Util.priorityColor(task)
        });

        holder.todayChip.setTextColor(colorStateList);
        holder.todayChip.setChipIconTint(colorStateList);

        holder.radioButton.setButtonTintList(colorStateList);

        holder.task.setText(task.getTask());
        holder.todayChip.setText(formatted);
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
        implements View.OnClickListener {
        public AppCompatRadioButton radioButton;
        public AppCompatTextView task;
        public Chip todayChip;
        private OnTodoClickListener onTodoClickListener;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            radioButton = itemView.findViewById(R.id.todo_radio_button);
            task = itemView.findViewById(R.id.todo_row_todo);
            todayChip = itemView.findViewById(R.id.todo_row_chip);
            this.onTodoClickListener = todoClickListener;

            itemView.setOnClickListener(this);
            radioButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int id = v.getId();
            int index = getAdapterPosition();
            Task currTask = tasks.get(index);;

            if (id == R.id.todo_row_layout) {
                onTodoClickListener.onTodoClick(
                        currTask
                );
            } else if (id == R.id.todo_radio_button) {
                onTodoClickListener.onTodoRadioButtonClick(
                    currTask,
                    radioButton
                );

                //radioButton.setChecked(false);
//                notifyDataSetChanged();
            }
        }
    }
}
