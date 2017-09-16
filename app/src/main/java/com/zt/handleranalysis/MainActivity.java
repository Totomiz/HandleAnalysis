package com.zt.handleranalysis;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    private static final String TAG="TestHandler";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void onClick(View view) {
        SubHandlerThread subHandlerThread=new SubHandlerThread();
        subHandlerThread.start();
        //主线程中用子线程的handler每隔一秒发送消息。
        while (true){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(subHandlerThread.handler!=null){
                Message message=new Message();
                message.obj=Thread.currentThread().getName();
                subHandlerThread.handler.sendMessageDelayed(message,1000);
            }

        }
    }
    //在子线程中创建Handler
    public class SubHandlerThread extends Thread {
        public Handler handler;

        @Override
        public void run() {
            setName("SubHandlerThread");
            Looper.prepare();
            handler = new Handler(new Handler.Callback() {
                @Override
                public boolean handleMessage(Message msg) {
                    Log.d(TAG, "handleMessage in Callback: " + msg.obj);
                    return false;
                }
            }) {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    Log.d(TAG, "handleMessage: Received: " + msg.obj);
                }
            };

            //方式一
            //Runnable runnable=new RepeatMessage(handler);
            //runnable.run();
            //方式二
            Thread subThread = new Thread(new RepeatMessage(handler), "sub thread");
            subThread.start();
            Looper.loop();
        }
    }

    //子线程中用子线程的handler每隔一秒发送消息到handler.
    public class RepeatMessage implements Runnable{
        private Handler handler;
        public RepeatMessage(Handler h){
            this.handler=h;
        }

        @Override
        public void run() {
            //让子线程每隔一秒发送一个消息的方式一
//            while (true){
//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                if(handler!=null) {
//                    Message msg = new Message();
//                    handler.sendMessage(msg);
//                    Log.d(TAG, "run: "+count);
//                }
//            }

            //让子线程每隔一秒发送消息的方式二
            if(handler!=null) {
                Message msg = new Message();
                msg.obj=Thread.currentThread().getName();
                handler.sendMessage(msg);
                handler.postDelayed(RepeatMessage.this,1000);
            }
        }
    }

}
