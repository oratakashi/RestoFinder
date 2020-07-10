package com.oratakashi.resto.data.network

import com.oratakashi.resto.data.model.collection.ResponseCollection
import com.oratakashi.resto.data.model.main.ResponseMain
import com.oratakashi.resto.data.model.review.ResponseReview
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiEndPoint {
    @GET("search")
    fun getNearby(
        @Query("lat") lat: String,
        @Query("lon") lang: String,
        @Query("radius") radius: String = "2000"
    ): Single<ResponseMain>

    @GET("collections")
    fun getCollection(
        @Query("lat") lat: String,
        @Query("lon") lang: String
    ): Single<ResponseCollection>

    @GET("reviews")
    fun getReviews(
        @Query("res_id") res_id: String,
        @Query("start") start: Int = 0,
        @Query("count") count: Int = 10
    ): Single<ResponseReview>

    @GET("search")
    fun getByCollection(
        @Query("collection_id") collection_id: String
    ): Single<ResponseMain>

    @GET("search")
    fun getSearch(
        @Query("q") keyword: String,
        @Query("lat") lat: String,
        @Query("lon") lang: String,
        @Query("radius") radius: String = "2000"
    ): Single<ResponseMain>
}