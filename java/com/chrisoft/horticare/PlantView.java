package com.chrisoft.horticare;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.chrisoft.horticare.model.Recipe;
import com.chrisoft.horticare.util.Constants;
import com.chrisoft.horticare.util.DataManager;
import com.chrisoft.horticare.util.HttpUtil;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.widget.ExpandableListView.*;

/**
 * A customized dialog used to display recipe information in view mode
 * Created by Chris on 13/08/2015.
 */
public class PlantView extends Dialog {

    ImageView img;
    ProgressBar pb;
    ExpandableListView expListView;
    RelativeLayout attr_layout;
    TextView txtName;
    ScrollView mainScrollView;

    public PlantView(Activity context) {
        super(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
    }


    public void setUp(final Activity dialog, Object data) throws JSONException {
        JSONObject obj = (JSONObject) data;
        setContentView(R.layout.viewplant);
        setTitle(obj.getString("name"));
        setCancelable(true);

        //there are a lot of settings, for dialog, check them all out!

        //set up text
        TextView txtDesc = (TextView) this.findViewById(R.id.txtDesc);

        txtDesc.setText(obj.getString("description"));

        //set up text
        txtName = (TextView) this.findViewById(R.id.txtName);
        txtName.setText(obj.getString("name"));


        //set up text
        TextView txtAuthor = (TextView) this.findViewById(R.id.txtAuthor);
        txtAuthor.setText("By : " + obj.getString("author"));

        expListView = (ExpandableListView)this.findViewById(R.id.attributes);
        attr_layout = (RelativeLayout)this.findViewById(R.id.attr_layout);
        mainScrollView = (ScrollView)this.findViewById(R.id.mainScrollView);

        //set up image view
        img = (ImageView) this.findViewById(R.id.ImageView01);

        pb = (ProgressBar) this.findViewById(R.id.progressBar);

        try {
            if (MainActivity.checkNetwork(getContext())) {
                BitmapAsyncTask task = new BitmapAsyncTask(obj);
                task.execute();

                AttributesAsyncTask attr = new AttributesAsyncTask(obj);
                attr.execute();
            } else {
                BitmapOfflineAsyncTask task = new BitmapOfflineAsyncTask(obj);
                task.execute();

                AttributeOfflineAsyncTask attr = new AttributeOfflineAsyncTask(obj);
                attr.execute();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT);
    }

    private class BitmapAsyncTask extends AsyncTask {

        private JSONObject obj;

        public BitmapAsyncTask(JSONObject obj) {
            this.obj = obj;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pb.setVisibility(ProgressBar.VISIBLE);
        }

        @Override
        protected Object doInBackground(Object... params) {
            try {
                String URL = this.obj.getString("imageURL");
                if (URL.startsWith("uploads")) URL = Constants.URL_HOME + "/" + URL;
                return HttpUtil.getBitmapFromURL(URL);
            } catch (Exception e) {
                return null;
            }
        }

        private void hideProgress() {
            if (pb.getVisibility() == ProgressBar.VISIBLE) {
                pb.setVisibility(ProgressBar.GONE);
            }
        }

        protected void onPostExecute(Object result) {
            super.onPostExecute(result);

            if (result != null) {
                img.setImageBitmap((Bitmap) result);
                try {
                    BitmapSaveAsyncTask task = new BitmapSaveAsyncTask(this.obj.getString("pid"), (Bitmap) result);
                    task.execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(getContext(), "Unable to load image.", Toast.LENGTH_SHORT).show();
            }

            hideProgress();
        }

        protected void onProgressUpdate(Integer... progress) {
        }
    }

    private class BitmapOfflineAsyncTask extends AsyncTask {

        private JSONObject obj;

        public BitmapOfflineAsyncTask(JSONObject data) {
            this.obj = data;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pb.setVisibility(ProgressBar.VISIBLE);
        }

        @Override
        protected Object doInBackground(Object... params) {
            try {
                return DataManager.getCachedImage(getContext(), "PLANT_" + this.obj.getString("pid"));
            } catch (Exception e) {
                return null;
            }
        }

        private void hideProgress() {
            if (pb.getVisibility() == ProgressBar.VISIBLE) {
                pb.setVisibility(ProgressBar.GONE);
            }
        }

        protected void onPostExecute(Object result) {
            super.onPostExecute(result);
            if (result != null) {
                img.setImageBitmap((Bitmap) result);
            } else {
                Toast.makeText(getContext(), "Unable to load image.", Toast.LENGTH_SHORT).show();
            }
            hideProgress();
        }
    }

    private class BitmapSaveAsyncTask extends AsyncTask {

        private Bitmap image;
        private String id;

        public BitmapSaveAsyncTask(String id, Bitmap image) {
            this.image = image;
            this.id = id;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Object doInBackground(Object... params) {
            try {
                DataManager.saveImageCache(getContext(), "PLANT_" + this.id, this.image);
                return true;
            } catch (Exception e) {
                return false;
            }
        }

        protected void onPostExecute(Object result) {
            super.onPostExecute(result);
            boolean b = (boolean) result;
            if (b == true) {
                //Toast.makeText(getContext(), "Image saved offline to " + getContext().getCacheDir(),Toast.LENGTH_SHORT).show();
            }
        }

        protected void onProgressUpdate(Integer... progress) {
        }
    }

    private class AttributeOfflineAsyncTask extends AsyncTask {

        private JSONObject obj;

        public AttributeOfflineAsyncTask(JSONObject obj) {
            this.obj = obj;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pb.setVisibility(ProgressBar.VISIBLE);
        }

        @Override
        protected Object doInBackground(Object... params) {
            return getCachedData();
        }

        protected void onPostExecute(Object result) {
            super.onPostExecute(result);

            if (result != null) {
                try {
                    showAttributes((JSONArray) result);
                } catch (JSONException e) {
                    //Not loaded
                }
            }

            hideProgress();
        }

        protected void onProgressUpdate(Integer... progress) {
        }

        public JSONArray getCachedData() {
            JSONArray result = null;
            try {
                String data = DataManager.readFromCache(getContext(), "PLANT_ATTR_" + obj.getString("pid"));
                if (data != null) {
                    result = new JSONArray(data);
                    return result;
                }
            } catch (Exception e) {
                Log.e("HORTICARE", "OfflineAsyncTask", e);
            }
            return null;
        }

        private void hideProgress() {
            if (pb.getVisibility() == ProgressBar.VISIBLE) {
                pb.setVisibility(ProgressBar.GONE);
            }
        }
    }

    private class AttributesAsyncTask extends AsyncTask {

        private JSONObject obj;

        public AttributesAsyncTask(JSONObject obj) {
            this.obj = obj;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pb.setVisibility(ProgressBar.VISIBLE);
        }

        @Override
        protected Object doInBackground(Object... params) {
            return postData();
        }

        protected void onPostExecute(Object result) {
            super.onPostExecute(result);

            if (result != null) {
                try {
                    showAttributes((JSONArray) result);
                } catch (JSONException e) {
                    //Not loaded
                }
            }

            hideProgress();
        }

        public JSONArray postData() {
            JSONArray result = null;
            try {

                // Execute HTTP Post Request
                String response = HttpUtil.GetData(Constants.URL_API + "/plants/attributes/" + obj.getString("pid"));

                if (response != null) {
                    result = new JSONArray(response);
                    boolean cached = DataManager.saveToCache(getContext(), "PLANT_ATTR_" + obj.getString("pid"), response);
                    return result;
                }
            } catch (Exception e) {
                Log.e("HORTICARE", "OnlineAsyncTask", e);
            }
            return null;
        }

        private void hideProgress() {
            if (pb.getVisibility() == ProgressBar.VISIBLE) {
                pb.setVisibility(ProgressBar.GONE);
            }
        }
    }

    public void showAttributes(JSONArray attr) throws JSONException {
        if (attr == null || attr.length() == 0) return;
        List<ParentItem> itemList = new ArrayList<ParentItem>();
        for (int i = 0; i < attr.length(); i++) {
            JSONObject obj = (JSONObject) attr.get(i);
            ParentItem parent1 = new ParentItem(obj.getString("name"));
            parent1.getChildItemList().add(obj);
            itemList.add(parent1);
        }
        ExpandableListViewAdapter2 adapter = new ExpandableListViewAdapter2(getContext(), itemList);
        expListView.setAdapter(adapter);

        // Listview Group expanded listener
        expListView.setOnGroupExpandListener(new OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {
                ViewGroup.LayoutParams param = attr_layout.getLayoutParams();
                param.height = 2000;
                attr_layout.setLayoutParams(param);
                attr_layout.requestLayout();
            }
        });

        // Listview Group collasped listener
        expListView.setOnGroupCollapseListener(new OnGroupCollapseListener() {

            @Override
            public void onGroupCollapse(int groupPosition) {
                setListViewHeightBasedOnItems(expListView);
                /*ViewGroup.LayoutParams param = attr_layout.getLayoutParams();
                param.height = 500;
                attr_layout.setLayoutParams(param);
                attr_layout.requestLayout();*/
            }
        });

        setListViewHeightBasedOnItems(expListView);
        /*
        ViewGroup.LayoutParams param = attr_layout.getLayoutParams();
        param.height = 500;
        attr_layout.setLayoutParams(param);
        attr_layout.requestLayout();*/

        txtName.setFocusable(true);
        txtName.requestFocus();
        mainScrollView.pageScroll(View.FOCUS_UP);
    }

    /**
     * Sets ListView height dynamically based on the height of the items.
     *
     * @param listView to be resized
     * @return true if the listView is successfully resized, false otherwise
     */
    public boolean setListViewHeightBasedOnItems(ListView listView) {

        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter != null) {

            int numberOfItems = listAdapter.getCount();

            // Get total height of all items.
            int totalItemsHeight = 0;
            for (int itemPos = 0; itemPos < numberOfItems; itemPos++) {
                View item = listAdapter.getView(itemPos, null, listView);
                item.measure(0, 0);
                totalItemsHeight += item.getMeasuredHeight();
            }

            // Get total height of all item dividers.
            int totalDividersHeight = listView.getDividerHeight() *
                    (numberOfItems - 1);

            // Set list height.
            ViewGroup.LayoutParams params = attr_layout.getLayoutParams();
            params.height = totalItemsHeight + totalDividersHeight;
            //listView.setLayoutParams(params);
            //listView.requestLayout();
            attr_layout.setLayoutParams(params);
            attr_layout.requestLayout();

            return true;

        } else {
            return false;
        }

    }
}


class ParentItem {

    private List<JSONObject> childItemList;
    private String title;
    public ParentItem(String title) {
        childItemList = new ArrayList<JSONObject>();
        this.title = title;
    }

    public List<JSONObject> getChildItemList() {
        return childItemList;
    }

    public String getTitle() {
        return title;
    }
}

class ExpandableListViewAdapter2 extends BaseExpandableListAdapter {

    private static final class ViewHolder {
        TextView textLabel;
    }

    private final List<ParentItem> itemList;
    private final LayoutInflater inflater;

    public ExpandableListViewAdapter2(Context context, List<ParentItem> itemList) {
        this.inflater = LayoutInflater.from(context);
        this.itemList = itemList;
    }

    @Override
    public JSONObject getChild(int groupPosition, int childPosition) {

        return itemList.get(groupPosition).getChildItemList().get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return itemList.get(groupPosition).getChildItemList().size();
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView,
                             final ViewGroup parent) {
        View resultView = convertView;
        ViewHolder holder;


        if (resultView == null) {

            resultView = inflater.inflate(R.layout.attribute_item, null); //TODO change layout id
            holder = new ViewHolder();
            holder.textLabel = (TextView) resultView.findViewById(R.id.lblListItem); //TODO change view id
            resultView.setTag(holder);
        } else {
            holder = (ViewHolder) resultView.getTag();
        }

        final JSONObject item = getChild(groupPosition, childPosition);

        try {
            holder.textLabel.setText(item.getString("description"));
        } catch (JSONException e) {
            //e.printStackTrace();
        }

        return resultView;
    }

    @Override
    public ParentItem getGroup(int groupPosition) {
        return itemList.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return itemList.size();
    }

    @Override
    public long getGroupId(final int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View theConvertView, ViewGroup parent) {
        View resultView = theConvertView;
        ViewHolder holder;

        if (resultView == null) {
            resultView = inflater.inflate(R.layout.attribute_header, null); //TODO change layout id
            holder = new ViewHolder();
            holder.textLabel = (TextView) resultView.findViewById(R.id.lblListHeader); //TODO change view id
            resultView.setTag(holder);
        } else {
            holder = (ViewHolder) resultView.getTag();
        }

        final ParentItem item = getGroup(groupPosition);

        holder.textLabel.setText(item.getTitle());

        return resultView;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

}
