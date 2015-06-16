package com.github.nkzawa.socketio.androidchat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;


/**
 * A login screen that offers login via username.
 */
public class LoginActivity extends Activity {

    private EditText mUsernameView;

    private String mUsername;


    private Socket mSocket;
    {
        //mSocket = IO.socket("http://chat.socket.io");
            //IO.setDefaultSSLContext(mySSLContext);
            IO.Options opts = new IO.Options();
            //opts.port = 13001;
            //opts.secure = true;
            //opts.query = "userID=6&userToken=maxengines";
            //opts.sslContext = mySSLContext;
        opts.query = "userID=6&userToken=maxengines";

        try {

            mSocket = IO.socket("http://korrio.co:13000/",opts);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        //mSocket = IO.socket("https://www.vdomax.com:13001?userID=6&userToken=maxengines&t=1424892568111");

    }

    private int cid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Set up the login form.
        mUsernameView = (EditText) findViewById(R.id.username_input);
        mUsernameView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    //attemptLogin();
                    try {
                        attemptJoinRoom();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    return true;
                }
                return false;
            }
        });

        Button signInButton = (Button) findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                //attemptLogin();
                Intent intent = new Intent();
                intent.putExtra("username", "myuser");
                intent.putExtra("numUsers", 3);
                intent.putExtra("userId", mUsernameView.getText().toString());
                setResult(RESULT_OK, intent);
                finish();

            }
        });

        //mSocket.on("login", onLogin);

        mSocket.on("connect",onConnect);
        mSocket.on("error",onError);

        //mSocket.on("SendMessage",onSendMessage);
        //mSocket.on("JoinRoom",onJoinroom);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mSocket.off("login", onLogin);
    }

    /**
     * Attempts to sign in the account specified by the login form.
     * If there are form errors (invalid username, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        // Reset errors.
        mUsernameView.setError(null);

        // Store values at the time of the login attempt.
        String username = mUsernameView.getText().toString().trim();

        // Check for a valid username.
        if (TextUtils.isEmpty(username)) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            mUsernameView.setError(getString(R.string.error_field_required));
            mUsernameView.requestFocus();
            return;
        }

        mUsername = username;

        // perform the user login attempt.
        mSocket.emit("add user", username);
    }

    private void appendMessage(String msg, boolean myMsg) {
        Log.i("msg:", msg);
    }

    private void attemptJoinRoom() throws JSONException {
        /*
        JSONObject jo = new JSONObject("{'CONVERSATION_ID':''," +
                "'CONVERSATION_TYPE':'group'," +
                "'USERID':'6'," +
                "'FRIENDID':''," +
                "'LIVE_USER_ID':'1301'}");
                */

        JSONObject jo = new JSONObject();
        jo.put("CONVERSATION_ID","");
        jo.put("CONVERSATION_TYPE","group");
        jo.put("USERID","6");
        jo.put("FRIENDID","");
        jo.put("LIVE_USER_ID","1301");

        // perform the user login attempt.
        mSocket.emit("JoinRoom", jo);
    }

    private void sendMessage(String msg) throws JSONException {
        /*
        JSONObject jo = new JSONObject("{'CONVERSATION_ID':"+cid+"'MESSAGECHAT':'"+msg+"'," +
                "'MESSAGETYPE':0," +
                "'USERID':'6'," +
                "'FRIENDID':''," +
                "'LIVE_USER_ID':'1301'}");
                */

        JSONObject jo = new JSONObject();
        jo.put("CONVERSATION_ID",cid);
        jo.put("MESSAGECHAT",msg);
        jo.put("MESSAGETYPE",0);
        jo.put("USERID","6");
        jo.put("FRIENDID","");
        jo.put("LIVE_USER_ID","1301");

        mSocket.emit("SendMessage", jo);
    }

    private Emitter.Listener onLogin = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            JSONObject data = (JSONObject) args[0];

            int numUsers;
            try {
                numUsers = data.getInt("numUsers");
            } catch (JSONException e) {
                return;
            }

            Intent intent = new Intent();
            intent.putExtra("username", mUsername);
            intent.putExtra("numUsers", numUsers);
            setResult(RESULT_OK, intent);
            finish();
        }
    };

    private Emitter.Listener onJoinroom = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            JSONObject data = (JSONObject) args[0];


            try {
                cid = data.getInt("conversation_id");
            } catch (JSONException e) {
                return;
            }

        }
    };

    private Emitter.Listener onSendMessage = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            //JSONObject data = (JSONObject) args[0];

            appendMessage("msg", false);

        }
    };

    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.i("onConnect","connect success");
            try {
                attemptJoinRoom();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    private Emitter.Listener onError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.e("onError","something error");

        }
    };
}



