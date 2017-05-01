package vocabularylist.projects.austin.vocabularylist;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import vocabularylist.projects.austin.vocabularylist.model.Word;
import vocabularylist.projects.austin.vocabularylist.model.WordVariant;

/**
 * Created by Austin on 2016-12-23.
 */
public class WordAdapter extends ArrayAdapter<Word> {
    private List<Word> objects;

    public WordAdapter(Context context, int resource, List objects) {
        super(context, resource, objects);
        this.objects = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        View row = convertView;
        WordHolder holder = null;

        if(row == null){
            LayoutInflater inflater = ((Activity)getContext()).getLayoutInflater();
            row = inflater.inflate(R.layout.listview_item_row, parent, false);

            holder = new WordHolder();
            holder.top = (TextView) row.findViewById(R.id.wordField);
            holder.bottom = (TextView) row.findViewById(R.id.partOfSpeech);

            row.setTag(holder);
        }else {
            holder = (WordHolder) row.getTag();
        }

        Word word = objects.get(position);
        StringBuilder sb = new StringBuilder();
        List<WordVariant> wordVariants = word.getWordVariants();
        for(int i = 0; i < wordVariants.size() && i < 3; i++){
            WordVariant wv = wordVariants.get(i);
            sb.append("[" + (i+1) + "] " + wv.getValue().replaceAll("[\\d\\[\\]]", "") + " - " + wv.getPartOfSpeech());
            if(i + 1 < wordVariants.size() && i+1 < 3){
                sb.append(", ");
            }
        }
        holder.top.setText(word.getTopLevelName());
        holder.bottom.setText(sb.toString());

        return row;
    }

    private static class WordHolder {
        TextView top;
        TextView bottom;

    }
}
