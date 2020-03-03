package sample;

import at.orderlibrary.Order;
import at.orderlibrary.Type;
import at.orderlibrary.TypeRequest;
import javafx.concurrent.Task;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.locks.ReentrantLock;

public class ServerTask extends Task {
    private ReentrantLock lock;
    public ServerTask() {
        lock=new ReentrantLock();
    }

    @Override
    protected Object call() throws Exception {



        return null;
    }
}
