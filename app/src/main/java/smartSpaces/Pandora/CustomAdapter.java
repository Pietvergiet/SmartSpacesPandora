package smartSpaces.Pandora;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class CustomAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Panel> panels;
    LayoutInflater inflter;
    private Typeface handwrittenFont;
    private Typeface horrorFont;

    public CustomAdapter(Context applicationContext, ArrayList<Panel> panels, Typeface horrorFont, Typeface handwrittenFont) {
        this.context = applicationContext;
        this.panels = panels;
        inflter = (LayoutInflater.from(applicationContext));
        this.horrorFont = horrorFont;
        this.handwrittenFont = handwrittenFont;
    }
    @Override
    public int getCount() {
        return panels.size();
    }
    @Override
    public Object getItem(int i) {
        return null;
    }
    @Override
    public long getItemId(int i) {
        return 0;
    }
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflter.inflate(R.layout.button_grid_adapter, null); // inflate the layout
        Button btn = view.findViewById(R.id.button);
        TextView text = view.findViewById(R.id.description);
        btn.setTypeface(this.horrorFont);
        text.setTypeface(this.handwrittenFont);

        // set btn properties
        btn.setText(panels.get(i).getVerb());
//        btn.setOnClickListener(listener);
        btn.setId(i);
        text.setText(panels.get(i).getObject());

        return view;
    }
}