package com.mobiledi.earnit.retrofit;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mobiledi.earnit.utils.AppConstant;

import java.util.concurrent.TimeUnit;

import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * Created by mac on 27/02/18.
 */

public class RetrofitClient {

    public static Retrofit createService(
            String username, String password) {
        String authToken = Credentials.basic(username, password);
        return createService(authToken);
    }

    public static Retrofit createService(final String authToken) {
        AuthenticationInterceptor interceptor = null;
        if (!TextUtils.isEmpty(authToken))
            interceptor =    new AuthenticationInterceptor(authToken);




        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        final OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .addInterceptor(logging)
                .retryOnConnectionFailure(true)
                .readTimeout(30, TimeUnit.SECONDS)
                .connectTimeout(30, TimeUnit.SECONDS).build();

        return new Retrofit.Builder()
                .baseUrl(AppConstant.BASE_URL+"/")
                .client(client)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();


    }

    public static RetroInterface getApiServices(String user_name, String passowrd) {
        return createService(user_name, passowrd).create(RetroInterface.class);
    }
}