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

public class ContentImageAdapter extends ArrayAdapter<String> {

    private final String BASE_URL = "http://10.0.2.2:3000/";

    //private final String BASE_URL = "http://192.168.1.4:2806/";
    //private final String BASE_URL = "http://192.168.137.27:2806/";

    public ContentImageAdapter(Context context, List<String> contentImages) {
        super(context, 0, contentImages);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String contentImage = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.content_image_item, parent, false);
        }

        ImageView imgContent = convertView.findViewById(R.id.imgContentImage);

        // Sử dụng Picasso để tải ảnh từ URL và hiển thị trong ImageView
        String fullImageUrl = BASE_URL + contentImage;
        Picasso.get().load(fullImageUrl).into(imgContent);

        return convertView;
    }
}