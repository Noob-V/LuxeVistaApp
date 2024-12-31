package com.example.resort;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class AboutUsActivity extends AppCompatActivity {

    private ImageView backgroundImage;
    private TextView titleText;
    private TextView aboutText;
    private TextView contactNumber;
    private TextView contactEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        setTitle("About Us");


        backgroundImage = findViewById(R.id.backgroundImage);
        titleText = findViewById(R.id.titleText);
        aboutText = findViewById(R.id.aboutText);
        contactNumber = findViewById(R.id.contactNumber);
        contactEmail = findViewById(R.id.contactEmail);


        backgroundImage.setAlpha(0f);
        titleText.setTranslationY(-100f);
        titleText.setAlpha(0f);
        aboutText.setTranslationY(100f);
        aboutText.setAlpha(0f);
        contactNumber.setAlpha(0f);
        contactEmail.setAlpha(0f);


        applyAnimations();


        setUpContactLinks();
    }

    private void applyAnimations() {

        backgroundImage.animate()
                .alpha(1f)
                .setDuration(1000)
                .setStartDelay(500)
                .start();

        titleText.animate()
                .translationY(0f)
                .alpha(1f)
                .setDuration(800)
                .setStartDelay(1000)
                .start();

        aboutText.animate()
                .translationY(0f)
                .alpha(1f)
                .setDuration(800)
                .setStartDelay(1500)
                .start();

        contactNumber.animate()
                .alpha(1f)
                .setDuration(800)
                .setStartDelay(2000)
                .start();

        contactEmail.animate()
                .alpha(1f)
                .setDuration(800)
                .setStartDelay(2500)
                .start();
    }
    private void setUpContactLinks() {

        contactNumber.setOnClickListener(v -> {
            String phoneNumber = "tel:04566685542";
            Intent callIntent = new Intent(Intent.ACTION_DIAL, Uri.parse(phoneNumber));
            startActivity(callIntent);
        });


        contactEmail.setOnClickListener(v -> {
            String email = "mailto:info@luxevista.com";
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse(email));
            startActivity(Intent.createChooser(emailIntent, "Send email using..."));
        });
    }
}
