package com.bignerd.android.encryptedmessaging;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class SendMessage extends AppCompatActivity {

    EditText alias, message, question, answer;
    Button insertMessage;
    String insertURL = "http://donherwig.com/insertMessage.php";
    RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        alias = (EditText) findViewById(R.id.alias);
        message = (EditText) findViewById(R.id.message);
        question = (EditText) findViewById(R.id.question);
        answer = (EditText) findViewById(R.id.password);
        insertMessage = (Button) findViewById(R.id.insert);

        requestQueue = Volley.newRequestQueue(getApplicationContext());

        insertMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userAnswer = answer.getText().toString();
                if (userAnswer.length() < 2) {
                    Toast.makeText(SendMessage.this, "Answer must be at least 2 characters!",Toast.LENGTH_LONG).show();
                } else {
                    final Encryption userMessage = new Encryption(answer.getText().toString());
                    StringRequest request = new StringRequest(Request.Method.POST, insertURL, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            //listen for response from server and let user know if alias is already used
                            if (!response.toString().isEmpty()) {
                                Toast.makeText(SendMessage.this, response.toString(), Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(SendMessage.this, "Message Sent!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }, new Response.ErrorListener() {
                        //this returns error if there is runtime error on server
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(SendMessage.this, "server error", Toast.LENGTH_SHORT).show();
                        }
                    }) {

                        //These are the values sent to the server
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> parameters = new HashMap<String, String>();
                            parameters.put("alias", alias.getText().toString());
                            parameters.put("message", userMessage.encrypt(message.getText().toString()));
                            parameters.put("question", question.getText().toString());

                            return parameters;
                        }
                    };
                    requestQueue.add(request);
                }
            }
        });

    }

}
