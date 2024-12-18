package com.example.vacayflow;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import java.util.Random;
import android.view.MotionEvent;
import android.view.inputmethod.InputMethodManager;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.example.vacayflow.network.KorPetTourService;
import com.example.vacayflow.network.PetTourResponse;
import com.example.vacayflow.network.RetrofitClient;

public class MainActivity extends AppCompatActivity {

    private TextView textViewTitle;
    private TextView textViewAddress;
    private ImageView imageView;
    private ImageView Research;
    private KorPetTourService apiService;
    private EditText inputText;
    private String serviceKey = "tuStgmnTfQdMcpzLtQHG5fUum08y228F7BO5iMt6cYePtoD10WuRKGDNSda5550Ext9fltOVPvHG3fAJnz189A%3D%3D";
    private List<String> displayedImages = new ArrayList<>();
    private List<PetTourResponse.Item> allItems = new ArrayList<>();
    private List<PetTourResponse.Item> Filtered = new ArrayList<>();
    private int pageNo = 1; // 페이지 번호

    private String searchText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewTitle = findViewById(R.id.tourname);
        textViewAddress = findViewById(R.id.address);
        imageView = findViewById(R.id.tourimage);
        Research = findViewById(R.id.Research);
        inputText = findViewById(R.id.inputText);

        searchText = inputText.getText().toString().trim();
        apiService = RetrofitClient.getRetrofitInstance().create(KorPetTourService.class);

        // API 호출 및 UI 업데이트
        fetchApiData();

        // 새로고침을 위해 ImageView 클릭 리스너 추가
        Research.setOnClickListener(view -> {
            displayRandomTourData();
        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View view = getCurrentFocus();
            if (view instanceof EditText) {
                view.clearFocus(); // EditText의 포커스 제거
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0); // 키보드 닫기
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }

    private void fetchApiData() {

        int maxPage = 100;

        if (pageNo > maxPage) { // 페이지 번호가 최대 페이지를 초과하면 요청 중지
            Log.d("Pagination", "Reached maximum page limit. Stopping fetch.");
            textViewTitle.setText("데이터를 찾을 수 없습니다.");
            return;
        }

        Call<PetTourResponse> call;
            // 검색어가 있을 경우
            call = apiService.getAreaBasedList(
                        serviceKey,
                        "AND",
                        "MyApp",
                        pageNo,
                        10
            );


        call.enqueue(new Callback<PetTourResponse>() {
            @Override
            public void onResponse(Call<PetTourResponse> call, Response<PetTourResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    PetTourResponse data = response.body();
                    if (data.getBody() != null && data.getBody().getItems() != null) {
                        List<PetTourResponse.Item> items = data.getBody().getItems();

                        // 중복 제거
                        for (PetTourResponse.Item item : items) {
                            if (item.getFirstImage() != null && !item.getFirstImage().isEmpty() &&
                                    !displayedImages.contains(item.getFirstImage())) {
                                if(searchText.isEmpty()){
                                    allItems.add(item);
                                }
                                else{
                                    if(item.getTitle() != null && item.getTitle().contains(searchText)){
                                        Filtered.add(item);
                                    }
                                }

                            }
                        }

                        if (!allItems.isEmpty()) {
                            // 첫 화면 데이터 표시
                            displayRandomTourData();
                        } else {
                            Log.d("API Response", "No new items found");
                        }
                    } else {
                        Log.d("API Response", "Items is null or empty");
                    }
                } else {
                    try {
                        Log.d("API Response", "Raw Response: " + response.errorBody().string());
                    } catch (IOException e) {
                        Log.e("API Response", "Error while reading error body", e);
                    }
                }
            }

            @Override
            public void onFailure(Call<PetTourResponse> call, Throwable t) {
                textViewTitle.setText("API 호출 실패: " + t.getMessage());
                textViewAddress.setText("");
                imageView.setImageResource(R.drawable.ic_launcher_foreground);
            }
        });
    }

    private void displayRandomTourData() {
        if (allItems.isEmpty()) {
            // 모든 데이터가 표시된 경우 새로운 API 호출
            pageNo++; // 페이지 번호 증가
            fetchApiData();
            return;
        }

        if(!searchText.isEmpty()){
            allItems = Filtered;
        }

        // 표시되지 않은 항목 중에서 랜덤 선택
        int randomIndex = new Random().nextInt(allItems.size());
        PetTourResponse.Item selectedItem = allItems.remove(randomIndex);

        // 표시된 이미지 목록에 추가
        displayedImages.add(selectedItem.getFirstImage());

        updateUI(selectedItem);
    }

    private void updateUI(PetTourResponse.Item item) {
        textViewTitle.setText(item.getTitle());
        textViewAddress.setText(item.getAddr1());

        if (item.getFirstImage() != null && !item.getFirstImage().isEmpty()) {
            Log.d("Image URL", "URL: " + item.getFirstImage());
            Glide.with(this)
                    .load(item.getFirstImage())
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .error(R.drawable.ic_launcher_foreground)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            Log.e("Glide", "Image load failed", e);
                            return false; // 기본 동작을 유지
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            Log.d("Glide", "Image load successful");
                            return false; // 기본 동작을 유지
                        }
                    })
                    .into(imageView);
        } else {
            imageView.setImageResource(R.drawable.ic_launcher_foreground);
            Log.d("Image URL", "이미지 없음");
        }
    }
}
