package com.example.gerza.apagarremotamente;

import android.os.Handler;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Created by Gerza on 12/03/2016.
 */
public class Cliente {

    private static final String TAG = "Cliente";
    private final Handler mHandler;
    private String  ipNumber, incomingMessage, command;
    BufferedReader in;
    PrintWriter out;
    private Cliente.MessageCallback listener= null;
    private boolean mRun= false;

    public Cliente(Handler mHandler, String s, String command, Cliente.MessageCallback listener)
    {

        this.listener         = listener;
        this.ipNumber         = s;
        this.command          = command ;
        this.mHandler         = mHandler;
    }

    public void sendMessage(String message){
        if (out != null && !out.checkError())
        {
            this.out.println(message);
            this.out.flush();
            this.mHandler.sendEmptyMessageDelayed(3, 1000L);
            Log.d(TAG, "Mensaje enviado: " + message);

        }
    }
    public void stopClient(){
        Log.d(TAG, "Cliente detenido!");
        mRun = false;
    }
    public void run()
    {

        mRun = true;

        try {

            InetAddress serverAddress = InetAddress.getByName(ipNumber);

            Log.d(TAG, "Conectando...");

            /**
             * Envio un mensaje vacio  hacia la actividad prncipal
             * para actualizar el estado( 'Conectando...' ).
             *
             *
             */
            mHandler.sendEmptyMessageDelayed(4,1000L);


            /**
             * Creamos un socket indicando el puerto.
             *
             *
             */
            Socket socket;
            socket = new Socket(serverAddress, 5000);


            try
            {

                // Creamos un  PrintWriter  para enviar un mensaje al servidor plocs
                out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);

                //Creamos un  BufferedReader para ver los mensajes que envie el servidor plocs.
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                Log.d(TAG, "Entrada/Salida se creo vamos bien");

                //Enviar el mensaje con el comando esperando que funcione   dede el hilo  AsyncTask
                this.sendMessage(command);

                //
                mHandler.sendEmptyMessageDelayed(3,2000L);

                //Obtener todos los mensajes   while mRun = true
                while (mRun)
                {
                    incomingMessage = in.readLine();
                    if (incomingMessage != null && listener != null)
                    {
                        /**
                         * El mensaje  entrante se pasa al objeto de mensaje de devolución(Callback).
                         * A continuación se recupera por AsyncTask y se pasa al método PublishProgress.
                         *
                         */
                        listener.callbackMessageReceiver(incomingMessage);

                    }
                    incomingMessage = null;

                }

                Log.d(TAG, "Mensaje recibido: " +incomingMessage);

            } catch (Exception e) {

                Log.d(TAG, "Error", e);
                mHandler.sendEmptyMessageDelayed(2, 2000L);

            } finally
            {

                out.flush();
                out.close();
                in.close();
                socket.close();
                mHandler.sendEmptyMessageDelayed(5, 3000L);
                Log.d(TAG, "Cerramos el socket");
            }

        } catch (Exception e)
        {

            Log.d(TAG, "Error", e);
            mHandler.sendEmptyMessageDelayed(2, 2000L);

        }

    }
    public boolean isRunning() {
        return this.mRun;
    }
    public interface MessageCallback
    {
        /**
         *
         * @param message Mensaje recibido desde el servidor .
         */
        void callbackMessageReceiver(String message);
    }


}
