package pl.edu.agh.marims.hub.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import pl.edu.agh.marims.hub.App;
import pl.edu.agh.marims.hub.R;
import pl.edu.agh.marims.hub.activity.FileDetailActivity;
import pl.edu.agh.marims.hub.activity.FileListActivity;
import pl.edu.agh.marims.hub.models.ApplicationFile;
import pl.edu.agh.marims.hub.models.Session;

/**
 * A fragment representing a single File detail screen.
 * This fragment is either contained in a {@link FileListActivity}
 * in two-pane mode (on tablets) or a {@link FileDetailActivity}
 * on handsets.
 */
public class FileDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    private ApplicationFile file;
    private SimpleItemRecyclerViewAdapter adapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public FileDetailFragment() {
    }

    private App.DataListener dataListener = new App.BaseDataListener() {
        @Override
        public void onSessionsUpdated(final List<Session> sessions) {
            FileDetailFragment.this.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    System.out.println(sessions.size());
                    List<Session> fileSessions = new ArrayList<>();
                    for (Session session : sessions) {
                        if (file.toApplicationFileString().equals(session.getFile())) {
                            fileSessions.add(session);
                        }
                    }
                    adapter.setItems(fileSessions);
                    adapter.notifyDataSetChanged();
                }
            });
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        ((App) getActivity().getApplication()).addDataListener(dataListener);
    }

    @Override
    public void onPause() {
        super.onPause();
        ((App) getActivity().getApplication()).removeDataListener(dataListener);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            file = (ApplicationFile) getArguments().getSerializable(ARG_ITEM_ID);

            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle(file.getFileName());
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View recyclerView = inflater.inflate(R.layout.file_detail, container, false);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);

        return recyclerView;
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        adapter = new SimpleItemRecyclerViewAdapter(new ArrayList<Session>());
        recyclerView.setAdapter(adapter);
    }

    public class SimpleItemRecyclerViewAdapter extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private List<Session> mValues;

        public SimpleItemRecyclerViewAdapter(List<Session> items) {
            mValues = items;
        }

        public void setItems(List<Session> items) {
            mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.file_detail_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mItem = mValues.get(position);

            Date creationDate = new Date(mValues.get(position).getCreationTimestamp());
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            String creationDateString = simpleDateFormat.format(creationDate);

            holder.mCreationDateView.setText(creationDateString);
            holder.mIdView.setText(mValues.get(position).getId());

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView mCreationDateView;
            public final TextView mIdView;
            public Session mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mCreationDateView = (TextView) view.findViewById(R.id.creation);
                mIdView = (TextView) view.findViewById(R.id.id);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mIdView.getText() + "'";
            }
        }
    }
}
