package ie.dit.birdr;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * http://www.technotalkative.com/android-asynchronous-image-loading-in-listview/
 */

public class LazyAdapter extends BaseAdapter implements Filterable {

    private Activity a;
    private ArrayList<String> birdNames = new ArrayList<>();
    private ArrayList<String> birdUrls = new ArrayList<>();
    private static LayoutInflater inflater=null;
    private List<String> filterResults = new ArrayList<String>();

    public LazyAdapter(Activity a, ArrayList<String> birdNames, ArrayList<String> birdUrls) {
        this.a=a;
        this.birdNames=birdNames;
        this.birdUrls=birdUrls;
        inflater = (LayoutInflater)a.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    // custom filter from https://stackoverflow.com/questions/14663725/list-view-filter-android
    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filterResults = (List<String>) results.values;
                notifyDataSetChanged();
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                FilterResults results = new FilterResults();
                ArrayList<String> FilteredArrayNames = new ArrayList<String>();

                // perform your search here using the searchConstraint String.
                constraint = constraint.toString().toLowerCase();
                for (int i = 0; i < birdNames.size(); i++) {
                    String dataNames = birdNames.get(i);
                    if (dataNames.toLowerCase().startsWith(constraint.toString()))  {
                        FilteredArrayNames.add(dataNames);
                    }
                }

                results.count = FilteredArrayNames.size();
                results.values = FilteredArrayNames;

                return results;
            }
        };

        return filter;
    }

    public int getCount() {
        return birdNames.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View vi=convertView;
        if(convertView==null)
            vi = inflater.inflate(R.layout.bird_image_list, null);

        TextView text=(TextView)vi.findViewById(R.id.AllBirdName);;
        ImageView image=(ImageView)vi.findViewById(R.id.AllBirdImage);
        text.setText(birdNames.get(position));

        Picasso.with(image.getContext().getApplicationContext()).load(birdUrls.get(position)).
                resize(50,50).centerCrop().placeholder(R.drawable.loading).
                into(image);

        return vi;
    }

}
