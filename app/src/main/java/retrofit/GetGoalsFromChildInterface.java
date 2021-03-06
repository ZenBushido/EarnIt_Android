package retrofit;

/**
 * Created by adox on 20.01.2018..
 */

        /*Base url is already defined in service generator class*/

import com.mobiledi.earnit.model.Goal;
import com.mobiledi.earnit.model.TaskV2Model;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface GetGoalsFromChildInterface {

    @GET("goals/{CHILDREN_ID}")
    Call<List<Goal>> listRepos(@Path("CHILDREN_ID") Integer CHILDREN_ID);
}
