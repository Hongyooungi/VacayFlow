package com.example.vacayflow;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewTitle = findViewById(R.id.tourname);
        textViewAddress = findViewById(R.id.address);
        imageView = findViewById(R.id.tourimage);

        String serviceKey = "tuStgmnTfQdMcpzLtQHG5fUum08y228F7BO5iMt6cYePtoD10WuRKGDNSda5550Ext9fltOVPvHG3fAJnz189A%3D%3D";

        KorPetTourService apiService = RetrofitClient.getRetrofitInstance().create(KorPetTourService.class);

        Call<PetTourResponse> call = apiService.getAreaBasedList(
                serviceKey,
                "AND",
                "MyApp",
                1,
                10
        );

        call.enqueue(new Callback<PetTourResponse>() {
            @Override
            public void onResponse(Call<PetTourResponse> call, Response<PetTourResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    PetTourResponse data = response.body();
                    if (data.getBody() != null && data.getBody().getItems() != null) {
                        List<PetTourResponse.Item> items = data.getBody().getItems();
                        List<PetTourResponse.Item> filteredItems = new ArrayList<>();
                        for (PetTourResponse.Item item : items) {
                            if (item.getFirstImage() != null && !item.getFirstImage().isEmpty()) {
                                filteredItems.add(item);
                            }
                        }
                        if (!filteredItems.isEmpty()) {
                            int randomIndex = new Random().nextInt(filteredItems.size());
                            updateUI(filteredItems.get(randomIndex));
                        } else {
                            textViewTitle.setText("이미지가 포함된 데이터가 없습니다.");
                            textViewAddress.setText("");
                            imageView.setImageResource(R.drawable.ic_launcher_foreground);
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
