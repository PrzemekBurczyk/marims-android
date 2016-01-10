package pl.edu.agh.marims.hub.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.ResponseBody;
import pl.edu.agh.marims.hub.App;
import pl.edu.agh.marims.hub.R;
import pl.edu.agh.marims.hub.fragment.FileDetailFragment;
import pl.edu.agh.marims.hub.models.ApplicationFile;
import pl.edu.agh.marims.hub.network.MarimsApiClient;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * An activity representing a single File detail screen. This
 * activity is only used narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link FileListActivity}.
 */
public class FileDetailActivity extends AppCompatActivity {

    private FloatingActionButton fabDownload;
    private ApplicationFile file;
    private boolean packageInstalled;

    private boolean isPackageInstalled(ApplicationFile applicationFile, Context context) {
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo packageInfo = pm.getPackageInfo(applicationFile.getPackageName(), PackageManager.GET_ACTIVITIES);
            int applicationVersionCode = Integer.parseInt(applicationFile.getFileName().split("(\\()|(\\))")[1]);
            return packageInfo.versionCode == applicationVersionCode;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    private void refreshFabDownloadVisibility() {
        packageInstalled = isPackageInstalled(file, FileDetailActivity.this);
        if (packageInstalled) {
            fabDownload.setVisibility(View.GONE);
        } else {
            fabDownload.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshFabDownloadVisibility();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);

        file = (ApplicationFile) getIntent().getSerializableExtra(FileDetailFragment.ARG_ITEM_ID);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((App) getApplication()).getSocket().emit("createSession", file.toApplicationFileString());
            }
        });

        fabDownload = (FloatingActionButton) findViewById(R.id.fab_download);
        refreshFabDownloadVisibility();
        fabDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                    Toast.makeText(FileDetailActivity.this, getString(R.string.sd_card_error), Toast.LENGTH_LONG).show();
                    return;
                }
                MarimsApiClient.getInstance().getMarimsService().getFile(file.toApplicationFileString()).enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Response<ResponseBody> response) {
                        InputStream inputStream = null;
                        FileOutputStream outputStream = null;
                        try {
                            inputStream = response.body().byteStream();
//                            File downloadCacheDirectory = new File(Environment.getExternalStorageDirectory(), "/Download/");
//                            downloadCacheDirectory.mkdirs();
//                            File downloadedFile = new File(downloadCacheDirectory, file.getFileName()).getAbsoluteFile();
                            File downloadedFile = File.createTempFile(file.getFileName(), null, getCacheDir());
                            downloadedFile.setReadable(true, false);
                            outputStream = new FileOutputStream(downloadedFile);

                            byte[] buffer = new byte[4096];
                            int len;
                            while ((len = inputStream.read(buffer)) != -1) {
                                outputStream.write(buffer, 0, len);
                            }

                            Intent promptInstall = new Intent(Intent.ACTION_VIEW);
                            promptInstall.setDataAndType(Uri.fromFile(downloadedFile), "application/vnd.android.package-archive");
                            promptInstall.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(promptInstall);

                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            if (inputStream != null) {
                                try {
                                    inputStream.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            if (outputStream != null) {
                                try {
                                    outputStream.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        t.printStackTrace();
                    }
                });
            }
        });

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //
        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putSerializable(FileDetailFragment.ARG_ITEM_ID, file);
            FileDetailFragment fragment = new FileDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.file_detail_container, fragment)
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. Use NavUtils to allow users
            // to navigate up one level in the application structure. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            NavUtils.navigateUpTo(this, new Intent(this, FileListActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
