package com.example.lmsmobile.data.network

import com.example.lmsmobile.data.model.LoginRequest
import com.example.lmsmobile.data.model.LoginResponse
import com.example.lmsmobile.data.model.NoteDTO
import com.example.lmsmobile.data.model.TaskDto
import com.example.lmsmobile.data.model.Result
import com.example.lmsmobile.data.model.ProfileResponse
import com.example.lmsmobile.data.model.UploadResponse
import com.example.lmsmobile.data.model.QuizResponse
import com.example.lmsmobile.data.model.QuizSummaryDTO
import com.example.lmsmobile.data.model.SubjectDto
import com.example.lmsmobile.data.model.SubmissionRequestDTO
import com.example.lmsmobile.data.model.SubmissionResponseWrapper
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @GET("tasks/schedule")
    suspend fun getScheduledTasks(@Query("degreeId") degreeId: Long): List<TaskDto>

    @GET("results/{indexNumber}")
    suspend fun getResults(@Path("indexNumber") indexNumber: String): List<Result>

    // Active quiz for a student
    @GET("online-quizzes/student/{studentIndex}")
    suspend fun getActiveQuiz(@Path("studentIndex") studentIndex: String): Response<QuizResponse>

    // Quiz list for a student
    @GET("online-quizzes/list/{studentIndex}")
    suspend fun listQuizzesForStudent(@Path("studentIndex") studentIndex: String): Response<List<QuizSummaryDTO>>

    @POST("online-quizzes/submit")
    suspend fun submitQuiz(@Body request: SubmissionRequestDTO): Response<SubmissionResponseWrapper>

    @GET("auth/users/profile")
    suspend fun getProfile(@Query("indexNumber") indexNumber: String): Response<ProfileResponse>

    @Multipart
    @POST("auth/users/{indexNumber}/upload-profile")
    suspend fun uploadProfileImage(
        @Path("indexNumber") indexNumber: String,
        @Part image: MultipartBody.Part
    ): Response<UploadResponse>

    @GET("subjects/by-student/{indexNumber}")
    suspend fun getSubjects(@Path("indexNumber") index: String): List<SubjectDto>

    @GET("notes/by-subject/{subjectId}")
    suspend fun getNotes(@Path("subjectId") subjectId: Long): List<NoteDTO>

    @POST("notes")
    suspend fun saveNote(@Body note: NoteDTO): NoteDTO

    /**
     * Upload a note with text, image, and optional PDF.
     */
    @Multipart
    @POST("notes/upload")
    suspend fun uploadNote(
        @Part("text") text: RequestBody,
        @Part("subjectId") subjectId: RequestBody,
        @Part image: MultipartBody.Part? = null,
        @Part pdf: MultipartBody.Part? = null
    ): Response<NoteDTO>   // return NoteDto so you get back the saved note with pdfUri
    // If backend only returns status, keep Response<Unit>

    @PUT("notes/{noteId}")
    suspend fun updateNote(
        @Path("noteId") noteId: Long,
        @Body note: NoteDTO
    ): NoteDTO

    @DELETE("notes/{noteId}")
    suspend fun deleteNote(@Path("noteId") noteId: Long): Response<Void>
}