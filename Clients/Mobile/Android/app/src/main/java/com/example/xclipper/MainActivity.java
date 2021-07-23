package com.example.xclipper;

import android.content.ClipData;
import android.content.ClipboardManager;
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
    private MessagesAdapter msgAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        messages = new ArrayList<>();
        // populating the messages arraylist with sample text
        // for(int i = 0; i < 100; i++){
        //     messages.add(new Message("kevin", "test string lmao"));
        // }

        RecyclerView msgView = (RecyclerView) findViewById(R.id.recycler_view);
        msgView.setLayoutManager(new LinearLayoutManager(this));
        msgAdapter = new MessagesAdapter(this.getLayoutInflater(), messages);
        msgView.setAdapter(msgAdapter);



    }

    @Override
    protected void onStart() {
        super.onStart();
        ServerHandler serverHandler = new ServerHandler(getApplicationContext(), messages, msgAdapter);
        //test to see if we even have a connection
        // serverHandler.testRequest();
        try {
            // TODO() rewrite code here lol
            int code = serverHandler.logIn("fok12", "kk4");
            // if(code == 200){
            //     System.out.println("logged in");
            //     serverHandler.connectToServer();
            // }
        } catch (JsonProcessingException | JSONException e) {
            e.printStackTrace();
        }

        // runOnUiThread(new Runnable() {
        //     @Override
        //     public void run() {
        //         // Stuff that updates the UI
        //         // int size = messages.size();
        //         // while (true){
        //         //     if(messages.size() > size){
        //         //         msgAdapter.notifyDataSetChanged();
        //         //         size = messages.size();
        //         //     }
        //         // }
        //     }
        // });

        ClipboardManager clipBoard = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
        clipBoard.addPrimaryClipChangedListener(new ClipboardManager.OnPrimaryClipChangedListener() {
            @Override
            public void onPrimaryClipChanged() {
                ClipData clipData = clipBoard.getPrimaryClip();
                ClipData.Item item = clipData.getItemAt(0);
                String text = item.getText().toString();
                serverHandler.uploadText(text);
                // Access your context here using YourActivityName.this
            }
        });

    }

}