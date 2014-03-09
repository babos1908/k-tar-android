package com.baboslabs.ktar;

import java.io.File;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.app.DownloadManager;
import android.app.DownloadManager.Query;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends Activity {

	private final String EXT_REL_PATH = "Android/data/com.baboslabs.ktar/";
	private final File EXT_ABS_PATH = getExternalFilesDir(EXT_REL_PATH);
	private long enqueue;
    private DownloadManager dm;
 
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // ListView Example
        ListView listView = (ListView)findViewById(R.id.listViewDemo);
        String [] array = {"Antonio", "Giovanni", "Michele", "Giuseppe", "Leonardo", "Alessandro"};
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.row, R.id.textViewList, array);
        listView.setAdapter(arrayAdapter);
        
        // Manage download file complete
        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
                    //long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);
                    Query query = new Query();
                    query.setFilterById(enqueue);
                    Cursor c = dm.query(query);
                    if (c.moveToFirst()) {
                        int columnIndex = c.getColumnIndex(DownloadManager.COLUMN_STATUS);
                        if (DownloadManager.STATUS_SUCCESSFUL == c.getInt(columnIndex)) {
                        	Toast.makeText(getApplicationContext(), "File received!!", Toast.LENGTH_LONG).show();
                            /*ImageView view = (ImageView) findViewById(R.id.imageView1);
                            String uriString = c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                            view.setImageURI(Uri.parse(uriString));*/
                        }
                    }
                }
            }
        };
        registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }
 
    public void onClick(View view) {
    	// Check if storage is mounted
    	if(isStorageMounted()){
    		// Call download manager to download file
	        dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
	        DownloadManager.Request req = new DownloadManager.Request(Uri.parse("https://s3.amazonaws.com/vubico-storage/test_file/Titanium+Raw.txt"));
	        req.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE)
	           .setAllowedOverRoaming(false)
	           .setTitle("Song")
	           .setDescription("Song description")
	           .setDestinationInExternalPublicDir(EXT_REL_PATH, "song.txt");
	        enqueue = dm.enqueue(req);
    	}
    }

    
    
    
    
    
    public boolean isStorageMounted(){
    	if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
    		// Ok
    		return true;
    		}
    	else {
    		// Not mounted
    		Toast.makeText(getApplicationContext(), "The SDCard is not available!", Toast.LENGTH_LONG).show();
    		return false;
    	}
    }
	
}
