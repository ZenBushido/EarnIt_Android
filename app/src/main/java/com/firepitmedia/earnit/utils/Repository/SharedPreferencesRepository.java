package com.firepitmedia.earnit.utils.Repository;

import android.content.Context;

public class SharedPreferencesRepository extends Repository implements SharedPreferencesRepositoryContract {

    public SharedPreferencesRepository(Context mContext) {
        super(mContext);
    }
}
