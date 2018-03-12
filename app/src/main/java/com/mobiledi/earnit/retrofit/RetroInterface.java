package com.mobiledi.earnit.retrofit;




import com.mobiledi.earnit.model.goal.GetAllGoalResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.GET;
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

    @GET("/earnit-api/goals/{CHILDREN_ID}")
    Call<List<GetAllGoalResponse>>
    getGoals(@Path("CHILDREN_ID") int id );



}
