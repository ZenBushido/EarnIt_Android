package retrofit;

/**
 * Created by adox on 20.01.2018..
 */

        /*Base url is already defined in service generator class*/

import com.firepitmedia.earnit.model.Goal;
import com.firepitmedia.earnit.model.getChild.Task;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface GetGoalsFromChildInterface {

    @GET("goals/{CHILDREN_ID}")
    Call<List<Goal>> listRepos(@Path("CHILDREN_ID") Integer CHILDREN_ID);

    @PUT("tasks")
    Call<Task> updateTask(@Body Task task);
}
