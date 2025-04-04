package vn.iostar.bai1;


import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

public class ProfileActivity extends AppCompatActivity {

    private ImageView imgProfile;
    private TextView tvUserId, tvUsername, tvFullName, tvEmail, tvGender;
    private Button btnLogout, btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile); // Đảm bảo file XML tồn tại

        // Ánh xạ view
        imgProfile = findViewById(R.id.imgProfile);
        tvUserId = findViewById(R.id.tvUserId);
        tvUsername = findViewById(R.id.tvUsername);
        tvFullName = findViewById(R.id.tvFullName);
        tvEmail = findViewById(R.id.tvEmail);
        tvGender = findViewById(R.id.tvGender);
        btnLogout = findViewById(R.id.btnLogout);
        btnBack = findViewById(R.id.btnBack);

        // Giả lập dữ liệu người dùng (có thể thay bằng dữ liệu từ API)
        int userId = 3;
        String username = "trung1";
        String fullName = "Nguyễn Hữu Trung";
        String email = "trung2@gmail.com";
        String gender = "Male";
        String avatarUrl = "https://yourserver.com/path/to/avatar.jpg"; // Thay bằng URL thật

        // Set dữ liệu lên giao diện
        tvUserId.setText(String.valueOf(userId));
        tvUsername.setText(username);
        tvFullName.setText(fullName);
        tvEmail.setText(email);
        tvGender.setText(gender);

        // Load ảnh đại diện bằng Glide
        Glide.with(this)
                .load(avatarUrl)
                .placeholder(R.drawable.profile_placeholder) // Ảnh mặc định nếu load lỗi
                .error(R.drawable.profile_placeholder)
                .into(imgProfile);

        // Xử lý nút Back
        btnBack.setOnClickListener(v -> finish());

        // Xử lý nút Đăng Xuất
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ProfileActivity.this, "Đăng xuất thành công!", Toast.LENGTH_SHORT).show();

            }
        });
    }
}