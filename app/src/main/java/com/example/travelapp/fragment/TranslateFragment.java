package com.example.travelapp.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.travelapp.R;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;

import java.util.Arrays;

public class TranslateFragment extends Fragment {

    private TranslatorOptions tlOptions;
    private Translator translator;
    private TextView translatedTextView;
    private EditText edtTxt;
    private Button translateBtn;
    private Spinner srcLngSpinner;
    private Spinner destLngSpinner;

    private String selectedSrcLanguage = TranslateLanguage.GERMAN;
    private String selectedDestLanguage = TranslateLanguage.VIETNAMESE;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View translate_fragment_view = inflater.inflate(R.layout.fragment_translate, container, false);

        //code new here
        setTranslateOptions();
        setTranslator();
        getLifecycle().addObserver(translator);

        edtTxt = translate_fragment_view.findViewById(R.id.idEdtLanguage);
        translatedTextView = translate_fragment_view.findViewById(R.id.idTVTranslated);
        translateBtn = translate_fragment_view.findViewById(R.id.idBtnTranslate);
        srcLngSpinner = translate_fragment_view.findViewById(R.id.idSpinnerSrcLang);
        destLngSpinner = translate_fragment_view.findViewById(R.id.idSpinnerDestLang);

        translateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String string = edtTxt.getText().toString();
                System.out.println(string);
                downloadModel(string);
            }
        });

        ArrayAdapter srcArrayAdapter = ArrayAdapter.createFromResource(getContext(), R.array.languages_array, android.R.layout.simple_spinner_item);
        ArrayAdapter destArrayAdapter = ArrayAdapter.createFromResource(getContext(), R.array.languages_array, android.R.layout.simple_spinner_item);
        srcArrayAdapter.setDropDownViewResource(R.layout.dropdown_item);
        destArrayAdapter.setDropDownViewResource(R.layout.dropdown_item);
        int srcPos = srcArrayAdapter.getPosition(mapCountryCodeToLanguage(selectedSrcLanguage));
        int destPos = destArrayAdapter.getPosition(mapCountryCodeToLanguage(selectedDestLanguage));

        srcLngSpinner.setAdapter(srcArrayAdapter);
        srcLngSpinner.setSelection(srcPos);
        srcLngSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedSrcLanguage = mapLanguageToCountryCode(parent.getItemAtPosition(position).toString());
                System.out.println("New source language: " + selectedSrcLanguage);
                setTranslateOptions();
                setTranslator();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        destLngSpinner.setAdapter(destArrayAdapter);
        destLngSpinner.setSelection(destPos);
        destLngSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedDestLanguage = mapLanguageToCountryCode(parent.getItemAtPosition(position).toString());
                System.out.println("New destination language: " + selectedDestLanguage);
                setTranslateOptions();
                setTranslator();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // do not delete
        return translate_fragment_view;
    }

    private void downloadModel(String input) {
        DownloadConditions conditions = new DownloadConditions.Builder()
                .requireWifi()
                .build();
        translator.downloadModelIfNeeded(conditions)
                .addOnSuccessListener(
                        unused -> {
                            translate(input);
                        }
                )
                .addOnFailureListener(
                        e -> {
                            System.out.println(e.toString());
                        }
                );
    }

    private void translate(String input) {
        translator.translate(input)
                .addOnSuccessListener(s -> {
                    System.out.println("Translation successful!");
                    translatedTextView.setText(s);
                })
                .addOnFailureListener(e ->
                {
                    System.out.println("Translation failed:" + e.getMessage());
                });
    }

    private void setTranslateOptions() {
        tlOptions = new TranslatorOptions.Builder()
                .setSourceLanguage(selectedSrcLanguage)
                .setTargetLanguage(selectedDestLanguage)
                .build();
    }

    private void setTranslator() {
        translator = Translation.getClient(tlOptions);
    }

    private String mapLanguageToCountryCode(String language) {
        switch(language.toLowerCase()) {
            case "german": return TranslateLanguage.GERMAN;
            case "vietnamese": return TranslateLanguage.VIETNAMESE;
            case "english": return TranslateLanguage.ENGLISH;
        }
        return TranslateLanguage.ENGLISH;
    }

    private String mapCountryCodeToLanguage(String countryCode) {
        switch (countryCode) {
            case "de": return "German";
            case "vi": return "Vietnamese";
            case "en": return "English";
        }
        return "English";
    }
}