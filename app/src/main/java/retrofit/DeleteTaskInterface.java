package retrofit;

import com.mobiledi.earnit.model.TaskV2Model;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by adox on 21.01.2018..
 */

    public interface DeleteTaskInterface {

        @DELETE("tasks/{CHILDREN_ID}")
        Call<String> listRepos(@Path("CHILDREN_ID") Integer TaskID);
}