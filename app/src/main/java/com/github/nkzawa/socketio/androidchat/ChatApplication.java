package com.github.nkzawa.socketio.androidchat;

import android.app.Application;

import io.socket.client.IO;
import io.socket.client.Socket;


public class ChatApplication extends Application {

    private Socket mSocket;
    {
        try {
            mSocket = IO.socket(Constants.CHAT_SERVER_URL);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public Socket getSocket() {
        return mSocket;
    }
}
