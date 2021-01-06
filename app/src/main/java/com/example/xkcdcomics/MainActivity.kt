package com.example.xkcdcomics

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.example.xkcdcomics.database.ComicDatabase
import com.example.xkcdcomics.database.DatabaseComic
import com.example.xkcdcomics.database.getComicDatabase
import com.example.xkcdcomics.databinding.ActivityMainBinding
import com.example.xkcdcomics.network.ComicNetwork
import com.example.xkcdcomics.network.asDatabaseComic
import com.example.xkcdcomics.repository.ComicsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
    }
}