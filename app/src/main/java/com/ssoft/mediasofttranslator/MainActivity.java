package com.ssoft.mediasofttranslator;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.ssoft.mediasofttranslator.interfaces.YandexAPIInterface;
import com.ssoft.mediasofttranslator.models.LanguageModel;
import com.ssoft.mediasofttranslator.models.TranslateModel;
import com.ssoft.mediasofttranslator.ui.dashboard.DashboardFragment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    static String API_KEY = "trnsl.1.1.20200507T081401Z.4caa33daf596e9cb.08b226257c92b02eb19f85e91d48207a7ac0c3a2";
    static String API_URL = "https://translate.yandex.net/api/v1.5/tr.json/";

    static List<String> languageModelList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupWithNavController(navView, navController);
    }

    public static void getTranslate(String inputText, String langInput, String langOutput){
        if (inputText.isEmpty()) {
            return;
        }
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        YandexAPIInterface yandexAPIInterface = retrofit.create(YandexAPIInterface.class);
        Call<TranslateModel> call = yandexAPIInterface.getTranslate(API_KEY, inputText, langInput + "-" + langOutput);
        call.enqueue(new Callback<TranslateModel>() {
            @Override
            public void onResponse(Call<TranslateModel> call, Response<TranslateModel> response) {
                if (!response.isSuccessful()){
                    Log.d("MainActivity", "Code: " + response.code());
                    return;
                }
                DashboardFragment.setOutput(response.body().getText().get(0));
            }

            @Override
            public void onFailure(Call<TranslateModel> call, Throwable t) {
                Log.d("MainActivity", "Что то пошло не так: " + t.getMessage());
            }
        });
    }
}
