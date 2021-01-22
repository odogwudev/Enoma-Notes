package com.odogwudev.enoma_notes;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.ViewHolder> {

    private ArrayList<Model> model;
    Context context;

    public NoteAdapter(Context c, ArrayList<Model> arrayList){
        context = c;
        model = arrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.design_note,parent,false);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(),NoteDetailActivity.class);
                v.getContext().startActivity(intent);
            }
        });
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        holder.textTitle.setText(model.get(position).getTitle());
        holder.textDescp.setText(model.get(position).getDescription());
        holder.textTime.setText(timestampToString((Long) model.get(position).getTimestamp()));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent edit = new Intent(v.getContext(),EditNoteActivity.class);
                edit.putExtra("title",model.get(position).getTitle());
                edit.putExtra("description",model.get(position).getDescription());
                edit.putExtra("NoteID",model.get(position).getNoteID());
                v.getContext().startActivity(edit);
            }
        });
    }

    @Override
    public int getItemCount() {
        return  model == null ? 0 : model.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView textTitle,textDescp,textTime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textTitle = itemView.findViewById(R.id.title);
            textDescp = itemView.findViewById(R.id.description);
            textTime = itemView.findViewById(R.id.timestamp);
        }
    }

    private String timestampToString(long time) {

        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time);
        return DateFormat.format("dd-MM-yyyy", cal).toString();

    }
}