package com.ssoft.mediasofttranslator.interfaces;

import com.ssoft.mediasofttranslator.models.LanguageModel;
import com.ssoft.mediasofttranslator.models.TranslateModel;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface YandexAPIInterface {

    @GET("getLangs")
    Call<LanguageModel> getLangs(@Query("key") String API_KEY);

    @GET("translate")
    Call<TranslateModel> getTranslate(@Query("key") String API_KEY, @Query("text") String TEXT, @Query("lang") String LANG);
}
