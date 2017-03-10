package com.example.gerza.apagarremotamente;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;
import android.os.Handler;
import android.widget.RemoteViews;

import java.lang.ref.WeakReference;


    public class MainActivity extends AppWidgetProvider {

    public static final int Apagado = 1;
    public static final int Error = 2;
    public static final int Eviando = 3;
    public static final int Conectando = 4;
    public static final int Enviado = 5;

    private static final String Apagar = "com.example.gerza.apagarremotamente.Apagar";
    static RemoteViews views;
    static ComponentName widget;
    static AppWidgetManager awManager;
    Handler mHandler;


    public MainActivity()
    {

    }


    private Handler getmHandler(final Context context)
    {

        final String mTag = "Handler ejecutando";
        this.mHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg)
            {
                switch (msg.what)
                {
                    case 1:

                        Log.d(mTag, "En el proceso de apagado");

                        views     = new RemoteViews(context.getPackageName(), R.layout.activity_main);
                        widget    = new ComponentName(context, MainActivity.class);
                        awManager = AppWidgetManager.getInstance(context);
                        views.setTextViewText(R.id.state, "Apagando la computadora");
                        awManager.updateAppWidget(widget,views);
                        break;
                   
                    case 2:
                        Log.d(mTag, "Aqui ocurre el error");

                        views     = new RemoteViews(context.getPackageName(), R.layout.activity_main);
                        widget    = new ComponentName(context, MainActivity.class);
                        awManager = AppWidgetManager.getInstance(context);
                        views.setTextViewText(R.id.state, "Ya ocurrio un error");
                        awManager.updateAppWidget(widget,views);
                        break;
                    case 3:
                        Log.d(mTag, "En el hilo de  envio de mensaje ");

                        views     = new RemoteViews(context.getPackageName(), R.layout.activity_main);
                        widget    = new ComponentName(context, MainActivity.class);
                        awManager = AppWidgetManager.getInstance(context);
                        views.setTextViewText(R.id.state, "Enviado mensaje");
                        awManager.updateAppWidget(widget,views);
                        break;
                    case 4:
                        Log.d(mTag, "Estableciendo la conexion");

                        views     = new RemoteViews(context.getPackageName(), R.layout.activity_main);
                        widget    = new ComponentName(context, MainActivity.class);
                        awManager = AppWidgetManager.getInstance(context);
                        views.setTextViewText(R.id.state, "Conectando..");
                        awManager.updateAppWidget(widget,views);
                        break;
                    case 5:
                        Log.d(mTag, "En espera de ejecución ");

                        views     = new RemoteViews(context.getPackageName(), R.layout.activity_main);
                        widget    = new ComponentName(context, MainActivity.class);
                        awManager = AppWidgetManager.getInstance(context);
                        views.setTextViewText(R.id.state, "Esperando comando");
                        awManager.updateAppWidget(widget,views);
                        break;


                }//Termina la evalucacion de los casos



                return true;
            }//Implementacion del handler
        });
        return  this.mHandler;
    }



        @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        for (int currentId : appWidgetIds) {

            views = new RemoteViews(context.getPackageName(), R.layout.activity_main);

            //Creamos lo que debe pasar y  las acciones
            Intent shutdownIntent = new Intent(context, MainActivity.class);
            shutdownIntent.setAction(Apagar);
            PendingIntent pendingShutdown = PendingIntent.getBroadcast(context, 0, shutdownIntent, 0);


            //enviamos la información cuando se presiona el boton de apagar
            views.setOnClickPendingIntent(R.id.shutdownButton, pendingShutdown);

            //Actualizamos el widget
            appWidgetManager.updateAppWidget(currentId, views);

        }
    }

    @Override
    public void onReceive(Context context, Intent intent)
    {
        super.onReceive(context, intent);

        views = new RemoteViews(context.getPackageName(), R.layout.activity_main);
        widget = new ComponentName(context, MainActivity.class);
        awManager = AppWidgetManager.getInstance(context);


       //Verificamos la accion e iniciamos un el hilo para apagar la computadora
        if (intent.getAction().equals(Apagar))
        {
            new HiloApagar(this.getmHandler(context)).execute("");

        }
    }





}

