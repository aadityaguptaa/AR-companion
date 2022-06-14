package com.adityagupta.arcompanion

import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.adityagupta.arcompanion.api.helpers.RetrofitHelper
import com.adityagupta.arcompanion.api.helpers.WikipediaHelper
import com.adityagupta.arcompanion.api.interfaces.Api
import com.adityagupta.arcompanion.api.interfaces.WikipediaAPI
import com.adityagupta.arcompanion.databinding.ActivityMeaningBinding
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.create
import java.io.IOException


class MeaningActivity : AppCompatActivity() {

    private lateinit var viewBinding: ActivityMeaningBinding
    lateinit var mediaPlayer: MediaPlayer


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meaning)
        viewBinding = ActivityMeaningBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        val word = intent.getStringExtra("word")
        var rootedWord = ""

        val oxfordApi = RetrofitHelper.getInstance().create(Api::class.java)
        val wikipediaAPI = WikipediaHelper.getInstance().create(WikipediaAPI::class.java)
        
        GlobalScope.launch {
            val result = oxfordApi.getRootWord(word?: "hello")
            rootedWord = result.body()!!.results[0].lexicalEntries[0].inflectionOf[0].text.toString()

            val finalResult =  oxfordApi.getDefinition(rootedWord?: "hello")

            runOnUiThread(Runnable {

                viewBinding.progressBar.visibility = View.INVISIBLE
                viewBinding.consLayout5.visibility = View.VISIBLE
                viewBinding.wordTitle.text = finalResult.body()?.results?.get(0)?.word ?: "hello"
                viewBinding.wordDef1.text = finalResult.body()?.results?.get(0)?.lexicalEntries?.get(0)?.entries?.get(0)?.senses?.get(0)?.definitions?.get(0) ?: "none"
                viewBinding.wordExample1.text = finalResult.body()?.results?.get(0)?.lexicalEntries?.get(0)?.entries?.get(0)?.senses?.get(0)?.examples?.get(0)?.text ?: "none"
                viewBinding.speaker.setOnClickListener {
                    playAudio(finalResult.body()?.results?.get(0)?.lexicalEntries?.get(0)?.entries?.get(0)?.pronunciations?.get(0)?.audioFile.toString())
                }

                Log.i("something",finalResult.body()?.results?.get(0)?.lexicalEntries?.get(0)?.entries?.get(0)?.pronunciations?.get(0)?.audioFile.toString() )
            })
        }


    }

    private fun playAudio(audio: String?) {
        val audioUrl = audio

        // initializing media player
        mediaPlayer = MediaPlayer()

        // below line is use to set the audio
        // stream type for our media player.
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC)

        // below line is use to set our
        // url to our media player.
        try {
            mediaPlayer.setDataSource(audioUrl)
            // below line is use to prepare
            // and start our media player.
            mediaPlayer.prepare()
            mediaPlayer.start()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        // below line is use to display a toast message.
        Toast.makeText(this, "Audio started playing..", Toast.LENGTH_SHORT).show()
    }


}