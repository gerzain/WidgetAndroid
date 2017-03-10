package com.example.gerza.apagarremotamente;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

/**
 * Created by Gerza on 12/03/2016.
 */
public class HiloApagar extends AsyncTask<String,String,Cliente>
{

    private static final String Comando= "shutdown -s";
    private Cliente cliente;
    private final Handler mHandler;
    private static final String TAG = "El hilo para apagar";

    public HiloApagar(Handler mHandler){
        this.mHandler = mHandler;
    }




    /**
     * Overriden metodo  usado en el  hilo en donde  instaciamos el Cliente .
     * @param params es para enviar un mensaje para verificar la comunicación.
     * @return Cliente es ejecutado  en el método onPostExecute  de hilo.
     */
    @Override
    protected Cliente doInBackground(final String... params) {
        Log.d(TAG, "Dentro de do In background");

        try{
            Handler var2=this.mHandler;
            String Ip="192.168.43.100";
            Cliente.MessageCallback messageCallback=new Cliente.MessageCallback()
            {
                @Override
                public void callbackMessageReceiver(String message)
                {
                        publishProgress(message);
                }
            };
            Cliente mensa=new Cliente(var2,Ip,"shutdown -s",messageCallback);
            this.cliente=mensa;

        }catch (NullPointerException e)
        {
            Log.d(TAG, "Aqui  esta un null pointer ");
            e.printStackTrace();
        }
        this.cliente.run();
        return null;
    }
    /**
     * Overriden método de la clase AsyncTask. Aquí estamos comprobando si el servidor responde correctamente..
     * @param values Si mensaje de "reinicio" se atiende, el cliente se detiene y PC se debe reiniciar.
     *               De caso contratio "vale verde" se envia un mensaje de  'Error'  y se muestra en el widget.
     */
    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
        Log.d(TAG, "En el metodo de progress update del hilo: " + values.toString());
        if(values[0].equals("shutdown")) // Aqui se modifico ya sea solo con el String
        {
           this.cliente.sendMessage(Comando);
            this.cliente.stopClient();
            this.mHandler.sendEmptyMessageDelayed(1, 2000L);

        }
        else
        {
            cliente.sendMessage("wrong");
            mHandler.sendEmptyMessageDelayed(2, 2000L);
            this.cliente.stopClient();
        }
    }
    @Override
    protected void onPostExecute(Cliente result)
    {
        super.onPostExecute(result);
        Log.d(TAG, "In on post execute");
        if(result != null && result.isRunning())
        {
            result.stopClient();
        }
        this.mHandler.sendEmptyMessageDelayed(5, 4000L);

    }
}
