package vocabularylist.projects.austin.vocabularylist;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Austin on 2017-04-13.
 */
public class OfflineWordsAdapter extends ArrayAdapter<String> {
    private ArrayList<String> objects;

    private class StringHolder{
        TextView title;
    }
    //constructor
    public OfflineWordsAdapter(Context context, int resource, List<String> objects) {
        super(context, resource, objects);
        this.objects = new ArrayList<String>(objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View parentView = convertView;
        StringHolder rowHolder = null;

        if(parentView == null){
            //view was not made yet
            /* 1. Get Layout Inflater, and Inflate the template
               2. Populate the holder (linking fields)
               3. Set as tag for information -> link fields with layout
             */
            LayoutInflater layoutInflater = ((Activity) getContext()).getLayoutInflater();
            parentView = layoutInflater.inflate(R.layout.offline_listview_row, parent, false);

            rowHolder = new StringHolder();
            rowHolder.title = (TextView) parentView.findViewById(R.id.offline_title);
        } else {
            rowHolder = (StringHolder) parentView.getTag();
        }

        parentView.setTag(rowHolder);
        rowHolder.title.setText(objects.get(position));


        return parentView;
    }
}
