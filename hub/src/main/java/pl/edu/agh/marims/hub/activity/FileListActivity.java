package pl.edu.agh.marims.hub.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import pl.edu.agh.marims.hub.App;
import pl.edu.agh.marims.hub.R;
import pl.edu.agh.marims.hub.fragment.FileDetailFragment;
import pl.edu.agh.marims.hub.models.ApplicationFile;
import pl.edu.agh.marims.hub.models.LoggedUser;
import pl.edu.agh.marims.hub.models.User;
import pl.edu.agh.marims.hub.network.MarimsApiClient;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * An activity representing a list of Files. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link FileDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class FileListActivity extends AppCompatActivity {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    private SimpleItemRecyclerViewAdapter adapter;

    private List<ApplicationFile> files;
    private List<User> users;

    private App.DataListener dataListener = new App.BaseDataListener() {
        @Override
        public void onFilesUpdated(final List<ApplicationFile> files) {
            FileListActivity.this.files = files;
            FileListActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    refreshFilesList();
                }
            });
        }

        @Override
        public void onUsersUpdated(List<User> users) {
            FileListActivity.this.users = users;
            FileListActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    refreshFilesList();
                }
            });
        }
    };

    private void refreshFilesList() {
        LoggedUser loggedUser = MarimsApiClient.getInstance().getLoggedUser();
        User currentUser = null;
        if (users != null) {
            for (User user : users) {
                if (loggedUser.getId().equals(user.getId())) {
                    currentUser = user;
                    break;
                }
            }
            if (currentUser != null && files != null) {
                List<ApplicationFile> filesToDisplay = new ArrayList<>();
                for (ApplicationFile file : files) {
                    if (currentUser.getAuthorOfFiles().contains(file.toApplicationFileString()) || currentUser.getMemberOfFiles().contains(file.toApplicationFileString())) {
                        filesToDisplay.add(file);
                    }
                }
                adapter.setItems(filesToDisplay);
                adapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        View recyclerView = findViewById(R.id.file_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MarimsApiClient.getInstance().getMarimsService().getFiles().enqueue(new Callback<List<String>>() {
                    @Override
                    public void onResponse(Response<List<String>> response) {
                        List<ApplicationFile> files = new ArrayList<>();
                        for (String fileString : response.body()) {
                            files.add(new ApplicationFile(fileString));
                        }
                        FileListActivity.this.files = files;
                        refreshFilesList();
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        t.printStackTrace();
                    }
                });
            }
        });

        if (findViewById(R.id.file_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        ((App) getApplication()).addDataListener(dataListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        ((App) getApplication()).removeDataListener(dataListener);
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        adapter = new SimpleItemRecyclerViewAdapter(new ArrayList<ApplicationFile>());
        recyclerView.setAdapter(adapter);
    }

    public class SimpleItemRecyclerViewAdapter extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private List<ApplicationFile> mValues;

        public SimpleItemRecyclerViewAdapter(List<ApplicationFile> items) {
            mValues = items;
        }

        public void setItems(List<ApplicationFile> items) {
            mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.file_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mItem = mValues.get(position);
            holder.mIdView.setText(String.valueOf(position + 1));
            holder.mContentView.setText(mValues.get(position).getFileName());

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mTwoPane) {
                        Bundle arguments = new Bundle();
                        arguments.putSerializable(FileDetailFragment.ARG_ITEM_ID, holder.mItem);
                        FileDetailFragment fragment = new FileDetailFragment();
                        fragment.setArguments(arguments);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.file_detail_container, fragment)
                                .commit();
                    } else {
                        Context context = v.getContext();
                        Intent intent = new Intent(context, FileDetailActivity.class);
                        intent.putExtra(FileDetailFragment.ARG_ITEM_ID, holder.mItem);
                        context.startActivity(intent);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView mIdView;
            public final TextView mContentView;
            public ApplicationFile mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mIdView = (TextView) view.findViewById(R.id.id);
                mContentView = (TextView) view.findViewById(R.id.content);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mContentView.getText() + "'";
            }
        }
    }
}
