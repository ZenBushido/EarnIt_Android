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
import retrofit2.converter.scalars.ScalarsConverterFactory;


/**
 * Created by mac on 27/02/18.
 */

public class RetrofitClient {

    private static Retrofit createService(
            String username, String password) {
        String authToken = Credentials.basic(username, password);
        return createService(authToken);
    }

    private static Retrofit createService(final String authToken) {
        AuthenticationInterceptor interceptor = null;
        if (!TextUtils.isEmpty(authToken))
            interceptor = new AuthenticationInterceptor(authToken);


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
                .connectTimeout(30, TimeUnit.SECONDS)
                .build();

        return new Retrofit.Builder()
                .baseUrl(AppConstant.BASE_URL + "/")
                .client(client)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addConverterFactory(new ToStringConverterFactory())
                .build();
    }

    public static RetroInterface getApiServices(String user_name, String password) {
        return createService(user_name, password).create(RetroInterface.class);
    }
}