package com.mustafakaya.fiform.view;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.mustafakaya.fiform.R;
import com.mustafakaya.fiform.databinding.ActivityReadPdfBinding;

import java.net.URLEncoder;

public class ReadPdfActivity extends AppCompatActivity {

    private ActivityReadPdfBinding binding;
    String value;
    WebView pdfView;
    String filename,fileUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityReadPdfBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            value = extras.getString("pdfname");
            filename = extras.getString("readpdf");
            fileUrl = extras.getString("fileurl");
        }
        setTitle(value);

        System.out.println(fileUrl);
        pdfView = findViewById(R.id.viewpdf);
        pdfView.getSettings().setJavaScriptEnabled(true);
        pdfView.setWebViewClient(new WebViewClient());



        ProgressDialog pd = new ProgressDialog(this);
        pd.setTitle(filename);
        pd.setMessage("Opening..!");


        pdfView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                pd.show();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                pd.dismiss();
            }
        });

        String url = "";

        try {
            url = URLEncoder.encode(fileUrl,"UTF-8");
        }catch (Exception e){
            e.printStackTrace();
        }


        pdfView.loadUrl("http://docs.google.com/gview?embedded=true&url=" + url);


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.read_file_items,menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == R.id.nav_download){
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(fileUrl)).setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI |
                    DownloadManager.Request.NETWORK_MOBILE);
            // in order for this if to run, you must use the android 3.2 to compile your app
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                request.allowScanningByMediaScanner();
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setTitle(filename);
            }
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileUrl.substring(fileUrl.lastIndexOf('/')));

            // get download service and enqueue file
            DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
            manager.enqueue(request);
        }
        return super.onOptionsItemSelected(item);
    }

}