package com.example.vacayflow.network;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface KorPetTourService {
    @GET("areaBasedList")
    Call<PetTourResponse> getAreaBasedList(
            @Query(value = "serviceKey", encoded = true) String serviceKey, // API 키
            @Query("MobileOS") String mobileOS,    // AND 또는 IOS
            @Query("MobileApp") String mobileApp,  // 앱 이름
            @Query("pageNo") int pageNo,           // 페이지 번호
            @Query("numOfRows") int numOfRows      // 한 페이지당 결과 수
    );
}
