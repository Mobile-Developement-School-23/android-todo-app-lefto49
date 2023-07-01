package com.todoapplication.data.network.api

import com.todoapplication.data.network.interaction.AddTaskRequest
import com.todoapplication.data.network.interaction.SingleTaskResponse
import com.todoapplication.data.network.interaction.TaskListResponse
import com.todoapplication.data.network.interaction.UpdateListRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface TodoAPI {
    @GET("list")
    suspend fun getTasks(): Response<TaskListResponse>

    @PATCH("list")
    suspend fun updateTasks(
        @Header("X-Last-Known-Revision") revision: Int,
        @Body tasks: UpdateListRequest
    ): Response<TaskListResponse>

    @GET("list/{id}")
    suspend fun getTaskById(@Path("id") id: String): Response<SingleTaskResponse>

    @POST("list")
    suspend fun addTask(
        @Header("X-Last-Known-Revision") revision: Int,
        @Body task: AddTaskRequest
    ): Response<SingleTaskResponse>

    @PUT("list/{id}")
    suspend fun updateTask(
        @Path("id") id: String, @Body task: AddTaskRequest,
        @Header("X-Last-Known-Revision") revision: Int
    ): Response<SingleTaskResponse>

    @DELETE("list/{id}")
    suspend fun deleteTask(
        @Path("id") id: String,
        @Header("X-Last-Known-Revision") revision: Int
    ): Response<SingleTaskResponse>
}