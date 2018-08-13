package com.mobiledi.earnit.retrofit;


import com.mobiledi.earnit.model.AppUsage;
import com.mobiledi.earnit.model.AppUsageResponse;
import com.mobiledi.earnit.model.BlockingApp;
import com.mobiledi.earnit.model.addTask.AddTaskWithSelecteDay;
import com.mobiledi.earnit.model.addTask.AddTaskWithSelecteDayResponse;
import com.mobiledi.earnit.model.adjustBalance.AdjustBalanceResponse;
import com.mobiledi.earnit.model.adjustBalance.AdjustGoalData;
import com.mobiledi.earnit.model.deleteTask.DeleteTaskResponse;
import com.mobiledi.earnit.model.editTask.EditTaskRequest;
import com.mobiledi.earnit.model.editTask.EditTaskResponse;
import com.mobiledi.earnit.model.getChild.GetAllChildResponse;
import com.mobiledi.earnit.model.goal.GetAllGoalResponse;
import com.mobiledi.earnit.model.newModels.AppsToBeBlockedOnOverdue;
import com.mobiledi.earnit.model.newModels.User;
import com.mobiledi.earnit.model.task.GetAllTaskResponse;

import java.util.List;
import java.util.Map;
import java.util.Set;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

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

    @POST("tasks")
    Call<AddTaskWithSelecteDayResponse>
    addTAskWithSelectedDay(@Body AddTaskWithSelecteDay addTaskWithSelecteDay);

    @DELETE("tasks/{TASK_ID}")
    Call<DeleteTaskResponse>
    deleteTask(@Path("TASK_ID") int id);

    @PUT("tasks")
    Call<EditTaskResponse>
    editTask(@Body EditTaskRequest editTaskRequest);

    @PUT("mobileapplications")
    Call<ResponseBody> createAppUsages(@Body List<AppUsage> ages);

    @GET("mobileapplications/usage")
    Call<List<AppUsageResponse>>
    getAppsUsage(@QueryMap(encoded=true) Map<String, Integer> filters);

    @GET("parents/{PARENT_ID}/profile/images/{IMAGE_NAME} ")
    Call<String>
    getParentPhoto(@Path("PARENT_ID") int id, @Path("IMAGE_NAME") String imageName);

    @GET("/login")
    Call<User> login();

    @GET("childrens/parents/")
    Call<Set<String>> getParents();

    @Multipart
    @POST("parents/children/{CHILD_ID}/profile/images")
    Call<String>
    uploadChildrenProfilePictureByParent(@Path("CHILD_ID") int id, @Part MultipartBody.Part filePart);

    @Multipart
    @POST("parents/profile/images")
    Call<ResponseBody>
    uploadParentProfilePicture(@Part MultipartBody.Part filePart);

    @Multipart
    @POST("tasks/{TASK_ID}/images")
    Call<String>
    uploadTaskPicture(@Path("TASK_ID") int id, @Part MultipartBody.Part filePart);

    @GET("children/{CHILD_ID}/mobileapplications ")
    Call<List<AppsToBeBlockedOnOverdue>>
    getChildrenApps(@Path("CHILD_ID") int id);
}
