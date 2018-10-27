package retrofit;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.firepitmedia.earnit.utils.AppConstant;

import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServiceGenerator {


    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

    static Gson gson = new GsonBuilder()
            .setLenient()
            .create();

    private static Retrofit.Builder builder =
            new Retrofit.Builder()
                    .baseUrl(AppConstant.BASE_URL+"/")
                    .addConverterFactory(GsonConverterFactory.create(gson));

    private static Retrofit retrofit = builder.build();



    public static <S> S createService(
            Class<S> serviceClass, String username, String password) {
            String authToken = Credentials.basic(username, password);

        return createService(serviceClass, authToken);
    }

    public static <S> S createService(
            Class<S> serviceClass, final String authToken) {
        if (!TextUtils.isEmpty(authToken)) {
            AuthenticationInterceptor interceptor =
                    new AuthenticationInterceptor(authToken);

            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            // set your desired log level
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            httpClient.addInterceptor(logging);

            if (!httpClient.interceptors().contains(interceptor)) {
                httpClient.addInterceptor(interceptor);

                builder.client(httpClient.build());
                retrofit = builder.build();
            }
        }

        return retrofit.create(serviceClass);
    }
}