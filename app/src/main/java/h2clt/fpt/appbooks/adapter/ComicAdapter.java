package h2clt.fpt.appbooks.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

import h2clt.fpt.appbooks.R;
import h2clt.fpt.appbooks.model.ComicModel;

public class ComicAdapter extends ArrayAdapter<ComicModel> {

    private final String BASE_URL = "http://10.0.2.2:3000/";
    //private final String BASE_URL = "http://192.168.1.4:2806/";
    //private final String BASE_URL = "http://192.168.137.27:2806/";
    public ComicAdapter(Context context, List<ComicModel> comics) {
        super(context, 0, comics);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ComicModel comic = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.comic_item, parent, false);
        }

//        TextView tvName = convertView.findViewById(R.id.tvName);
        ImageView imgCover = convertView.findViewById(R.id.imgCover);

//        String name = comic.getName();
//        if (name.length() > 18) {
//            name = name.substring(0, 18) + "...";
//        }
//
//        tvName.setText(name);

        // Sử dụng Picasso để tải ảnh từ URL và hiển thị trong ImageView
        String fullCoverImageUrl = BASE_URL + comic.getCoverImage();
        Picasso.get().load(fullCoverImageUrl).into(imgCover);

        return convertView;
    }
}
