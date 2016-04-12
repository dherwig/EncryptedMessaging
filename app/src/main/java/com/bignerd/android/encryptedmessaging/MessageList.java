package com.bignerd.android.encryptedmessaging;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MessageList extends ListActivity {

    RequestQueue requestQueue;
    ArrayList<Message> messageList = new ArrayList<Message>();
    ArrayAdapter<Message> adapter;
    String getAllMessagesURL = "http://192.168.1.235/getAll.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Message List");

        requestQueue = Volley.newRequestQueue(MessageList.this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, getAllMessagesURL, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray messages = response.getJSONArray("messages");
                    for (int i = 0; i < messages.length(); i++) {
                        JSONObject message = messages.getJSONObject(i);
                        Message temp = new Message(message.getString("alias"),message.getString("question"));
                        messageList.add(temp);
                    }
                    adapter = new ArrayAdapter<Message>(MessageList.this ,android.R.layout.simple_list_item_1,messageList);
                    setListAdapter(adapter);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MessageList.this, error.toString(), Toast.LENGTH_SHORT).show();

            }
        });
        requestQueue.add(jsonObjectRequest);





    }
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Message m = (Message) l.getAdapter().getItem(position);

        Intent i = new Intent(MessageList.this, ReceiveMessage.class);
        i.putExtra("alias", m.getAlias());
        MessageList.this.startActivity(i);

    }

}
