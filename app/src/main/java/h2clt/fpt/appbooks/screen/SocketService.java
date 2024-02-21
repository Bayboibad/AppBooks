package h2clt.fpt.appbooks.screen;

import android.app.Notification;
import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;


import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import h2clt.fpt.appbooks.api.ApiUserService;
import h2clt.fpt.appbooks.model.UserModel;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SocketService extends Service {
    private Socket mSocket;


    @Override
    public void onCreate() {
        super.onCreate();
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            mSocket = IO.socket("http://10.0.2.2:3000");
            //https://ncgmgl-2806.csb.app/
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        mSocket.connect();

        mSocket.on("new msg", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                String data_sv_send = (String) args[0];
                String currentDate = getCurrentDateTime();
                handleSocketEvent(data_sv_send,currentDate);
            }
        });
        mSocket.on("cmt", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                String data_sv_send = (String) args[0];
                String currentDate = getCurrentDateTime();
                handleSocketEvent(data_sv_send,currentDate);
            }
        });
        return START_STICKY;
    }

    private void handleSocketEvent(String data,String date) {

        List<UserModel> listUser = new ArrayList();

        ApiUserService.apiService.getDataUser().enqueue(new Callback<List<UserModel>>() {
            @Override
            public void onResponse(Call<List<UserModel>> call, Response<List<UserModel>> response) {
                if (response.isSuccessful()){
                    listUser.addAll(response.body());
                    Log.d("zzz", ""+listUser.size());


                    for (int i = 0; i<listUser.size();i++){
                        saveNotificationToDatabase("ComicCorner thông báo", data, date,listUser.get(i).get_id());
                    }
                    postNotify("ComicCorner thông báo:", data);

                }else {
                    Log.d("zzz", "onResponse: Không lấy được danh sách");
                }
            }
            @Override
            public void onFailure(Call<List<UserModel>> call, Throwable t) {
                Log.d("zzz", "onFailure: Get data failed");
            }
        });
    }
    private void saveNotificationToDatabase(String title, String content,String date,String user) {

        ContentValues values = new ContentValues();
        values.put("title", title);
        values.put("content", content);
        values.put("date", date);
        values.put("user", user);

    }
    private void postNotify(String title, String content) {


    }

    private String getCurrentDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
            mSocket.off();
    }
}


