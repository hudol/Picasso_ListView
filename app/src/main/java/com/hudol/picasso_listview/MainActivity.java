package com.hudol.picasso_listview;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.hudol.picasso_listview.entity.Item;
import com.hudol.picasso_listview.utils.PicassoUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ListView listView;
    private ProgressDialog dialog;
    private Myadapter adapter;
    private OKManager manager;

    List<Item> list = new ArrayList<>();
    private String source_path ="http://litchiapi.jstv.com/api/GetFeeds?column=17&PageSize=20&pageIndex=1&val=AD908EDAB9C3ED111A58AF86542CCF50";
   // private String source_path="http://litchiapi.jstv.com/api/GetFeeds";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        manager = OKManager.getInstance();
        listView = (ListView) this.findViewById(R.id.listView);
        listView.setOnScrollListener(new ListScroller());//添加滚动事件
        dialog = new ProgressDialog(this);
        dialog.setTitle("load ....");
        new Mytask().execute(source_path);



    }

    public  class ListScroller implements AbsListView.OnScrollListener{
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            final Picasso picasso = Picasso.with(MainActivity.this);
            if (scrollState == SCROLL_STATE_IDLE || scrollState == SCROLL_STATE_TOUCH_SCROLL) {
                picasso.resumeTag(MainActivity.this);
            } else {
                picasso.pauseTag(MainActivity.this);
            }
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

        }
    }
    class  Myadapter extends BaseAdapter{
        public List<Item> data;
        public Myadapter(List<Item>data){
            this.data = data;
        }
        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
          ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = getLayoutInflater().inflate(R.layout.layout, parent, false);
                holder.subject = (TextView) convertView.findViewById(R.id.subject);
                holder.summary = (TextView) convertView.findViewById(R.id.summary);
                holder.img = (ImageView) convertView.findViewById(R.id.imageView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.subject.setText(data.get(position).getSubject());
            holder.summary.setText(data.get(position).getSummary());
            PicassoUtils.loadImageWithSize(MainActivity.this,"http://litchiapi.jstv.com/"+data.get(position).getCover(),400,300,holder.img);
            return convertView;
        }
    }

    /**
     * 用来减少访问布局ID
     */
    private static class ViewHolder{
        TextView subject;
        TextView summary;
        ImageView img;
    }
    class Mytask extends AsyncTask<String, Void, List<Item>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.show();
        }

        @Override
        protected List<Item> doInBackground(String... params) {


            manager.asyncJsonStringByURL(source_path, new OKManager.Func1() {
                @Override
                public void onResponse(String result) {
                    System.out.println("777788"+result);
                    try{
                        JSONArray  jsonArray = new JSONObject(result).getJSONObject("paramz").getJSONArray("feeds");
                        for (int i = 0; i< 10; i++){
                            JSONObject element = jsonArray.getJSONObject(i).getJSONObject("data");
                            Item item  = new Item();
                            item.setCover(element.getString("cover"));
                            item.setSubject(element.getString("subject"));
                            item.setSummary(element.getString("summary"));
                            list.add(item);
                        }
                    }catch(JSONException e){
                        e.printStackTrace();
                    }
                }
            });
             //System.out.println(json_value+"666666666666666666");

            return list;
        }

        @Override
        protected void onPostExecute(final List<Item> items) {
            super.onPostExecute(items);
            adapter = new Myadapter(items);
            listView.setAdapter(adapter);

            dialog.dismiss();
        }
    }
}
