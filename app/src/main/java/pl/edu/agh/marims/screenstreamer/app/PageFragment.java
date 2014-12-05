package pl.edu.agh.marims.screenstreamer.app;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.Random;

/**
 * Created by Przemek on 2014-06-08.
 */
public class PageFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_page, container, false);

        int[] buttonIds = new int[]{R.id.button1, R.id.button2, R.id.button3};

        for (Integer id : buttonIds) {

            view.findViewById(id).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getActivity(), "Clicked", Toast.LENGTH_SHORT).show();
                }
            });

        }


        Random random = new Random();
        view.setBackgroundColor(Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256)));
        return view;
    }
}
