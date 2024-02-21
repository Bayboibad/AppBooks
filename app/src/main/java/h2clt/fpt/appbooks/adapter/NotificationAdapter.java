package h2clt.fpt.appbooks.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


import java.util.List;

import h2clt.fpt.appbooks.R;
import h2clt.fpt.appbooks.model.NotificationModel;

public class NotificationAdapter extends ArrayAdapter<NotificationModel> {

    public NotificationAdapter(Context context, List<NotificationModel> notifications) {
        super(context, 0, notifications);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.notification_item, parent, false);
        }

        NotificationModel notification = getItem(position);

        TextView titleTextView = convertView.findViewById(R.id.titleTextView);
        TextView contentTextView = convertView.findViewById(R.id.contentTextView);
        TextView date = convertView.findViewById(R.id.date);

        titleTextView.setText(notification.getTitle());
        contentTextView.setText(notification.getContent());
        date.setText(""+notification.getDate());

        return convertView;
    }
}
