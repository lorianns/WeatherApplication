package com.lorianns.mobile.weatherapplication.ui;


import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.lorianns.mobile.weatherapplication.R;
import com.lorianns.mobile.weatherapplication.rest.Utils;

/**
 * Created by lorianns on 12/23/16.
 */

public class InputLocationDialogFragment extends DialogFragment {

    private EditText etZipCode;
    private Button btnSubmit;
    private static String sDialogTitle;

    public interface InputLocationDialogListener {
        void onSubmitLocation(String zipCode);
    }

    public InputLocationDialogFragment(){}

    public void setDialogTitle(String title) {
        sDialogTitle = title;
    }

    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle saveInstanceState){

        View view = inflater.inflate(
                R.layout.dialog_location, container);

        etZipCode = (EditText) view.findViewById(R.id.etZipCode);
        btnSubmit = (Button) view.findViewById(R.id.btnSubmit);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String value = etZipCode.getText().toString();
                if(Utils.isProbablyValidUSZipCode(value)) {
                    InputLocationDialogListener listener = (InputLocationDialogListener) getActivity();
                    listener.onSubmitLocation(value);
                    dismiss();
                }
                else
                    etZipCode.setError("Error ZipCode");
            }
        });

        etZipCode.requestFocus();
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        getDialog().setTitle(sDialogTitle);

        return view;
    }

}
