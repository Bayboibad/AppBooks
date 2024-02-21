package h2clt.fpt.appbooks.screen;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import h2clt.fpt.appbooks.R;
import h2clt.fpt.appbooks.adapter.ComicAdapter;
import h2clt.fpt.appbooks.dialog.DialogLoading;
import h2clt.fpt.appbooks.model.ComicModel;
import h2clt.fpt.appbooks.model.UserModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.socket.client.IO;
import io.socket.client.Socket;

public class MainActivity extends AppCompatActivity {
    private final String BASE_URL = "http://10.0.2.2:3000/";
    //private final String BASE_URL = "http://192.168.1.4:2806/";
    //private final String BASE_URL = "http://192.168.137.27:2806/";
    public TextView tvUsername;
    public ImageButton btnNotification,btnRefresh;
    public ImageButton btnAccount;
    public GridView grComic;
    private DialogLoading dialogLoading;

    public SwipeRefreshLayout sRL;
    private Socket mSocket;
    {
        try {
            mSocket = IO.socket("http://10.0.2.2:3000");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }


    @SuppressLint({"WrongViewCast", "MissingInflatedId", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        dialogLoading = DialogLoading.getInstance(this);

        tvUsername = findViewById(R.id.tvUsername);
        btnNotification = findViewById(R.id.btnNotification);
        btnAccount = findViewById(R.id.btnAccount);
        grComic = findViewById(R.id.grComic);
        sRL = findViewById(R.id.swiflayout);
        sRL.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getWebPage(grComic);
                sRL.setRefreshing(false);
            }
        });
        // Nhận dữ liệu từ Intent
        Bundle bundle = getIntent().getExtras();
        startService(new Intent(this, SocketService.class));
        if (bundle != null) {
            UserModel user = (UserModel) bundle.getSerializable("userLogin");

            tvUsername.setText("Hey, " + user.getFullName());

            grComic.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // Xử lý sự kiện khi một item được click
                    ComicModel selectedComic = (ComicModel) parent.getItemAtPosition(position);
                    // Thực hiện các hành động cần thiết dựa trên selectedComic
                    // Ví dụ: chuyển sang một Activity mới để hiển thị chi tiết về Comic
                    Intent intent = new Intent(MainActivity.this, DetailsComicActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("selected_comic",selectedComic);
                    bundle.putSerializable("userLogin",user);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    finish();
                }
            });
            btnAccount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent= new Intent(getApplicationContext(),AccountActivity.class);
                    startActivity(intent);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("userLogin",user);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    finish();
                }
            });

        }
        getWebPage(grComic);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(new Intent(this, SocketService.class));
    }
    void getWebPage(GridView gridView) {
        ExecutorService service = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        service.execute(new Runnable() {
            @Override
            public void run() {

                String dia_chi = BASE_URL + "comic/list";
                String noi_dung = "";
                try {
                    URL url = new URL(dia_chi);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                    InputStream inputStream = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

                    StringBuilder builder = new StringBuilder();
                    String row;

                    while ((row = reader.readLine()) != null) {
                        builder.append(row).append("\n");
                    }

                    reader.close();
                    inputStream.close();
                    connection.disconnect();
                    noi_dung = builder.toString();
//                    Log.d("zzz", "run: Noi dung: " + noi_dung);

                } catch (MalformedURLException e) {
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                final List<ComicModel> comicList = parseJsonData(noi_dung);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        // Tạo Adapter và thiết lập cho ListView
                        if (comicList.size()==0){
                            showDialogLoading();
                        }else {
                            ComicAdapter adapter = new ComicAdapter(MainActivity.this, comicList);
                            gridView.setAdapter(adapter);
                            hideDialogLoading();
                        }
                    }
                });
            }
        });
    }

    private List<ComicModel> parseJsonData(String jsonString) {
        List<ComicModel> comics = new ArrayList<>();

        try {
            JSONArray jsonArray = new JSONArray(jsonString);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject comicObject = jsonArray.getJSONObject(i);

                String id = comicObject.getString("_id");
                String name = comicObject.getString("name");
                // Lấy đối tượng author từ JSON
                JSONObject authorObject = comicObject.getJSONObject("author");
                String fullName = authorObject.getString("fullName");
                String publicationYear = comicObject.getString("publicationYear");
                String description = comicObject.getString("description");
                String coverImage = comicObject.getString("coverImage");

                JSONArray contentImageArray = comicObject.getJSONArray("contentImage");
                List<String> contentImages = new ArrayList<>();
                for (int j = 0; j < contentImageArray.length(); j++) {
                    contentImages.add(contentImageArray.getString(j));
                }

                ComicModel comic = new ComicModel(id, name,fullName, coverImage,contentImages,publicationYear,description);
                comics.add(comic);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return comics;
    }

    private void showDialogLoading() {
        dialogLoading.show();
    }

    private void hideDialogLoading() {
        dialogLoading.dismiss();
    }
}