package com.example.xclipper;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MessagesViewHolder> {
    private LayoutInflater inflater;
    private List<Message> messages;

    public MessagesAdapter(LayoutInflater inflater, List<Message> messages) {
        this.inflater = inflater;
        this.messages = messages;
    }

    @Override
    public MessagesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemMessage = inflater.inflate(R.layout.item_message, parent, false);
        MessagesViewHolder messagesViewHolder = new MessagesViewHolder(itemMessage);
        messagesViewHolder.view = itemMessage;
        messagesViewHolder.from = (TextView) itemMessage.findViewById(R.id.tvFrom);
        messagesViewHolder.message = (TextView) itemMessage.findViewById(R.id.tvMessage);
//        messagesViewHolder.date = (TextView) itemMessage.findViewById(R.id.tvDate);
        return messagesViewHolder;
    }

    @Override
    public void onBindViewHolder(MessagesViewHolder holder, int position) {
        Message message = messages.get(position);
        holder.from.setText(message.getFrom());
        holder.message.setText(message.getMessage());
//        holder.date.setText("no date lol");
        holder.position = position;
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public static class MessagesViewHolder extends RecyclerView.ViewHolder {
        View view;
        TextView from, message, date;
        int position;

        public MessagesViewHolder(View itemView) {
            super(itemView);
            this.view = itemView;
        }
    }
}
