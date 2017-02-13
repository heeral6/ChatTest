package com.example.heeralthakur.tabstest;


import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static com.example.heeralthakur.tabstest.R.id.lvChats;
import static com.example.heeralthakur.tabstest.R.styleable.View;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {


    ListView listView;
    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);
        String[] menu = {"Dummy 1", "Dummy 2", "Dummy 3"};

        listView = (ListView) view.findViewById(lvChats);


        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .showImageForEmptyUri(R.drawable.facebook_placeholder) // resource or drawable
                .cacheOnDisk(true)
                .build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getActivity())
                .defaultDisplayImageOptions(defaultOptions)
                .build();
        ImageLoader.getInstance().init(config);
        new JsonClass().execute("http://haptik.mobi/android/test_data/");


      /*  ArrayAdapter<String> listArrayAdapter = new ArrayAdapter<String>(
                getActivity(), android.R.layout.simple_list_item_1, menu
        );

        listView.setAdapter(listArrayAdapter);*/

        // Inflate the layout for this fragment
        return view;
    }

    class JsonClass extends AsyncTask<String, String, List<ChatModel>>{

        @Override
        protected List<ChatModel> doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(params[0]);

                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream inputStream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(inputStream));

                StringBuffer stringBuffer = new StringBuffer();

                String line = "";
                while ((line = reader.readLine()) != null) {

                    stringBuffer.append(line);
                }

                String finalJson = stringBuffer.toString();

                JSONObject parentObject = new JSONObject(finalJson);
                String count = parentObject.getString("count");
                JSONArray parentArray = parentObject.getJSONArray("messages");

                StringBuffer finalBufferData = new StringBuffer();

                List<ChatModel> chatModelList = new ArrayList<>();

                for (int i = 0; i < parentArray.length(); i++) {
                    ChatModel model = new ChatModel();
                    JSONObject finalObj = parentArray.getJSONObject(i);
                    model.setBody(finalObj.getString("body"));

                    model.setUsername(finalObj.getString("username"));

                    model.setName(finalObj.getString("Name"));

                    model.setMessage_time(finalObj.getString("message-time"));
                    //String image=
                    model.setImage_url(finalObj.getString("image-url"));
                    //finalBufferData.append(body+ " -  "+username+ "  - "+name+"   -   "+ time+"  \n   ");

                    chatModelList.add(model);

                }

                return chatModelList;

            } catch (Exception e) {
                e.printStackTrace();
            } finally {

                if (connection != null) {
                    connection.disconnect();

                }
                try {
                    if (reader != null) {
                        reader.close();
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
        @Override
        protected void onPostExecute( List<ChatModel> result) {
            super.onPostExecute(result);

            ChatAdapter adapter=new ChatAdapter(getActivity(),R.layout.row_layout,result);
            listView.setAdapter(adapter);
        }

    }


    public class ChatAdapter extends ArrayAdapter{

        private List<ChatModel> ChatModelList;
        private int resource;
        private LayoutInflater inflator;
        public ChatAdapter(Context context, int resource, List<ChatModel> objects) {
            super(context, resource, objects);

            ChatModelList=objects;
            this.resource=resource;
            inflator = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder=null;

            if(convertView==null){
                holder=new ViewHolder();
                convertView=inflator.inflate(resource,null);
                holder.body=(TextView)convertView.findViewById(R.id.textView);
                holder.imageView=(ImageView)convertView.findViewById(R.id.profile);
                holder.time=(TextView)convertView.findViewById(R.id.time);
                holder.username=(TextView)convertView.findViewById(R.id.useranme);
                convertView.setTag(holder);
            }else{
                holder= (ViewHolder) convertView.getTag();
            }



            final ProgressBar progressBar =(ProgressBar)convertView.findViewById(R.id.progressBar);

            ImageLoader.getInstance().displayImage(ChatModelList.get(position).getImage_url(), holder.imageView, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {
                    progressBar.setVisibility(android.view.View.VISIBLE);
                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                    progressBar.setVisibility(android.view.View.GONE);

                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    progressBar.setVisibility(android.view.View.GONE);
                }

                @Override
                public void onLoadingCancelled(String imageUri, View view) {
                    progressBar.setVisibility(android.view.View.GONE);
                }
            }); // Default options will be used


            holder.body.setText(ChatModelList.get(position).getBody());
            holder.time.setText(ChatModelList.get(position).getMessage_time());
            holder.username.setText(ChatModelList.get(position).getUsername());


            return  convertView;
        }

        class ViewHolder{

            private TextView body;
            private ImageView imageView;
            private TextView time;
            private TextView username;

        }
    }
}
