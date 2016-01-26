package pl.edu.agh.marims.screenstreamer.app;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class PageFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_page, container, false);

        view.findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ((TextView) view.findViewById(R.id.textView)).setText("");
                    }
                }, 500);
                ((TextView) view.findViewById(R.id.textView)).setText("CLICK!");
            }
        });
        view.findViewById(R.id.button).setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ((TextView) view.findViewById(R.id.textView)).setText("");
                    }
                }, 500);
                ((TextView) view.findViewById(R.id.textView)).setText("LONG CLICK!");
                return true;
            }
        });

        view.findViewById(R.id.buttonSout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Sample System.out output");
            }
        });
        view.findViewById(R.id.buttonLogd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("TAG D", "Sample Log.d output");
            }
        });
        view.findViewById(R.id.buttonLogw).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.w("TAG W", "Sample Log.w output");
            }
        });
        view.findViewById(R.id.buttonLoge).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("TAG E", "Sample Log.e output");
            }
        });
        view.findViewById(R.id.buttonStackTrace).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    throw new Exception("Sample handled exception");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        view.findViewById(R.id.buttonException).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                throw new RuntimeException("Sample Runtime exception");
            }
        });

        Spinner spinner = (Spinner) view.findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.planets_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        WebView webView = (WebView) view.findViewById(R.id.webView);
        webView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
            }
        });
        webView.setWebViewClient(new WebViewClient() {
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(getActivity(), "Oh no! " + description, Toast.LENGTH_SHORT).show();
            }
        });

//        LinearLayout listContainer = (LinearLayout) view.findViewById(R.id.listContainer);
//        for (int i = 0; i < 50; i++) {
//            TextView textView = new TextView(getActivity());
//            textView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
//            textView.setText("TextView number " + i);
//            textView.setTextSize(20.0f);
//            listContainer.addView(textView);
//        }

        webView.loadUrl("http://google.com");
        return view;
    }
}
