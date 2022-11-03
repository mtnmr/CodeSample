package com.example.glideandcoil

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import coil.load
import coil.transform.CircleCropTransformation
import com.bumptech.glide.Glide

class MainActivity : AppCompatActivity() {

    val imageUrl = "https://developer.android.com/static/training/basics/firstapp/images/studio-welcome.png"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val glideImage = findViewById<ImageView>(R.id.image_view_glide)
        val coilImage = findViewById<ImageView>(R.id.image_view_coil)

        Glide.with(this)
            .load(imageUrl)
            .circleCrop()
            .placeholder(R.drawable.ic_baseline_image_24)
            .error(R.drawable.ic_launcher_foreground)
            .into(glideImage)

        coilImage.load(imageUrl){
            placeholder(R.drawable.ic_baseline_image_24)
            transformations(CircleCropTransformation())
            error(R.drawable.ic_launcher_foreground)
            crossfade(3000)
        }
    }
}