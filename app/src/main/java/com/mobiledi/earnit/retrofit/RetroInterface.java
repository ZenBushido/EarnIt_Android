package com.mobiledi.earnit.retrofit;




import com.mobiledi.earnit.model.adjustBalance.AdjustBalanceResponse;
import com.mobiledi.earnit.model.adjustBalance.AdjustGoalData;
import com.mobiledi.earnit.model.getChild.GetAllChildResponse;
import com.mobiledi.earnit.model.goal.GetAllGoalResponse;
import com.mobiledi.earnit.model.task.GetAllTaskResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * Created by mac on 27/02/18.
 */

public interface RetroInterface {

    //@GET("/goals/{goalid}")
    //Call<Response<GoalResponse>>
  //  deleteGoal(@Path("UserID") int goalid);
/*
    @GET("dayTaskStatuses")
    Call<GetTaskResponse>
    getTasks();*/

    @GET("goals/{CHILDREN_ID}")
    Call<List<GetAllGoalResponse>>
    getGoals(@Path("CHILDREN_ID") int id );

    @GET("childrens/{ACCOUNT_ID}")
    Call<List<GetAllChildResponse>>
    getAllChild(@Path("ACCOUNT_ID") int id);

    @POST("adjustments")
    Call<AdjustBalanceResponse>
    adjustBalance(@Body AdjustGoalData adjustGoalData);


    @GET("tasks/{CHILDREN_ID}")
    Call<List<GetAllTaskResponse>>
    getAllTask(@Path("CHILDREN_ID") int id);


}
