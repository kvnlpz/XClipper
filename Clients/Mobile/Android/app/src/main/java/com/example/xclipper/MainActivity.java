package com.example.xclipper;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fasterxml.jackson.core.JsonProcessingException;

import org.json.JSONException;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.Adapter adapter;
    private ArrayList<Message> messages;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        recyclerView = findViewById(R.id.recycler_view);
        messages = new ArrayList<>();
        for(int i = 0; i < 100; i++){
            messages.add(new Message("kevin", "test string lmao"));
//            stringArrayList.add("String " + i);
        }
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));

//        ArrayList<Message> messages = new ArrayList<Message>();

        RecyclerView msgView = (RecyclerView) findViewById(R.id.recycler_view);
        msgView.setLayoutManager(new LinearLayoutManager(this));
        MessagesAdapter msgAdapter = new MessagesAdapter(this.getLayoutInflater(), messages);
        msgView.setAdapter(msgAdapter);



    }

    @Override
    protected void onStart() {
        super.onStart();
        ServerHandler serverHandler = new ServerHandler(getApplicationContext());
        serverHandler.testRequest();
        try {
            serverHandler.logIn("miguelNarc", "passwordnine");
        } catch (JsonProcessingException | JSONException e) {
            e.printStackTrace();
        }
    }
}