package com.robam.stream.ui.fragment;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.serialport.helper.SerialPortHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.robam.foodmaterialdetect.FoodMaterialDetectCallback;
import com.robam.foodmaterialdetect.FoodMaterialHelper;
import com.robam.stream.R;

public class HomeFragment extends Fragment {

    private Button detect;
    private Button btTake;

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.home_fragment, container, false);
        detect = rootView.findViewById(R.id.bt_detect);
        btTake = rootView.findViewById(R.id.bt_take);

        detect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FoodMaterialHelper.classify(getContext(), "/vendor/model/ji.jpg", new FoodMaterialDetectCallback() {
                    @Override
                    public void onResult(String result) {
                        Log.e("onResult", result);
                    }
                });
            }
        });
        btTake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavController navController = Navigation.findNavController(getView());
                navController.navigate(R.id.navigation_takephoto);
            }
        });
        return  rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        SerialPortHelper.getInstance().closeDevice();
    }
}