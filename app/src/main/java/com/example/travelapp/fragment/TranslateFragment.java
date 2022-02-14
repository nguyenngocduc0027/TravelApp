package com.example.travelapp.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.travelapp.R;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;

public class TranslateFragment extends Fragment {

    private TranslatorOptions tlOptions;
    private Translator germanVietnameseTranslator;
    private TextView translatedTextView;
    private EditText edtTxt;
    private Button translateBtn;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View translate_fragment_view = inflater.inflate(R.layout.fragment_translate, container, false);

        //code new here
        tlOptions = new TranslatorOptions.Builder()
                .setSourceLanguage(TranslateLanguage.GERMAN)
                .setTargetLanguage(TranslateLanguage.VIETNAMESE)
                .build();

        germanVietnameseTranslator = Translation.getClient(tlOptions);
        getLifecycle().addObserver(germanVietnameseTranslator);

        edtTxt = translate_fragment_view.findViewById(R.id.idEdtLanguage);
        translatedTextView = translate_fragment_view.findViewById(R.id.idTVTranslated);
        translateBtn = translate_fragment_view.findViewById(R.id.idBtnTranslate);

        translateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String string = edtTxt.getText().toString();
                System.out.println(string);
                downloadModel(string);
            }
        });

        // do not delete
        return translate_fragment_view;
    }

    private void downloadModel(String input) {
        DownloadConditions conditions = new DownloadConditions.Builder()
                .requireWifi()
                .build();
        germanVietnameseTranslator.downloadModelIfNeeded(conditions)
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
        germanVietnameseTranslator.translate(input)
                .addOnSuccessListener(s -> {
                    System.out.println("Translation successful!");
                    translatedTextView.setText(s);
                })
                .addOnFailureListener(e ->
                {
                    System.out.println("Translation failed:" + e.getMessage());
                });
    }
}