package com.bignerd.android.encryptedmessaging;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ReceiveMessage extends AppCompatActivity {
    Button search, decrypt;
    EditText alias, answer;
    TextView message, decryptedMessage;
    String getMessage = "http://192.168.1.235/readMessage.php";
    String encryptedMessage = "filler to not crash app";
    String actualMessage;
    RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive_message);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        search = (Button) findViewById(R.id.getMessageButton);
        decrypt = (Button) findViewById(R.id.decrypt);
        alias = (EditText) findViewById(R.id.searchAlias);
        answer = (EditText) findViewById(R.id.inputAnswer);
        message = (TextView) findViewById(R.id.messageTextView);
        decryptedMessage = (TextView) findViewById(R.id.decryptedMessage);

        requestQueue = Volley.newRequestQueue(getApplicationContext());

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StringRequest request = new StringRequest(Request.Method.POST, getMessage, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String resp = response.toString();
                        System.out.println(resp);

                        //server will return "Alias doesn't exist if the alias isn't in database
                        if (!resp.equals("Alias doesn't exist!")) {
                            JSONObject answer;
                            //create a JsonObject from returned JSON string from server
                            try {
                                answer = new JSONObject(response.toString());
                                JSONArray temp = answer.getJSONArray("serverData");
                                JSONObject tempMessage = temp.getJSONObject(0);
                                encryptedMessage = tempMessage.getString("message");
                                JSONObject question = temp.getJSONObject(0);
                                String questionString = question.getString("question");

                                message.setText(questionString);


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }else{
                            Toast.makeText(ReceiveMessage.this, response.toString(), Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Toast.makeText(ReceiveMessage.this, error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }) {

                    //alias gets sent to server for database query
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> parameters = new HashMap<String, String>();
                        parameters.put("alias", alias.getText().toString());

                        return parameters;
                    }
                };
                requestQueue.add(request);
            }

        });

        decrypt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userAnswer = answer.getText().toString();
                if(userAnswer.length()>1) {
                    Encryption mes = new Encryption(answer.getText().toString());
                    decryptedMessage.setText(mes.decrypt(encryptedMessage));
                }else{
                    Toast.makeText(ReceiveMessage.this, "Answer must be at least 2 characters!",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

}
