package com.firepitmedia.earnit.utils.Repository;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.firepitmedia.earnit.MyApplication;
import com.firepitmedia.earnit.retrofit.RetroInterface;
import com.firepitmedia.earnit.retrofit.RetrofitClient;
import com.firepitmedia.earnit.utils.AppConstant;

import static android.content.Context.MODE_PRIVATE;

public abstract class Repository {

    private Context mContext;

    protected RetroInterface mRetroInterface;
    protected SharedPreferences mPreferences;

    private String email;
    private String password;

    public Repository(Context mContext) {
        this.mContext = mContext;
        init();
    }

    private void init() {
        mPreferences = mContext.getSharedPreferences(AppConstant.FIREBASE_PREFERENCE, MODE_PRIVATE);
        //get credentials from App singleton
        email = MyApplication.getInstance().getEmail();
        password = MyApplication.getInstance().getPassword();
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)){
            //get credentials from shared preferences
            email = mPreferences.getString(AppConstant.EMAIL, "");
            password = mPreferences.getString(AppConstant.PASSWORD, "");
            if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)){
                mRetroInterface = getRetrofitInterface();
            } else {
                throw new RuntimeException("Class Repository cannot get credentials. It seems that " +
                        "the user has never logged in yet.");
            }
        } else {
            mRetroInterface = getRetrofitInterface();
        }
    }

    private RetroInterface getRetrofitInterface(){
        return RetrofitClient.getApiServices(email, password);
    }

}
