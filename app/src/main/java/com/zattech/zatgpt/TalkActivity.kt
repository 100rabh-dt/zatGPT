//package com.example.zatgpt
//
//import androidx.appcompat.app.AppCompatActivity
//import android.os.Bundle
//
//class TalkActivity : AppCompatActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_talk)
//    }
//}
//
//



package com.zattech.zatgpt
//import android.text.Editable

import android.Manifest
import android.app.Activity
import android.Manifest.permission.RECORD_AUDIO

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.zattech.zatgpt.databinding.ActivityTalkBinding
import com.google.gson.Gson
import com.zattech.zatgpt.adapter.MessageAdapter
import com.zattech.zatgpt.api.ApiUtilities
import com.zattech.zatgpt.models.MessageModel
import com.zattech.zatgpt.models.request.ChatRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.RequestBody
import java.util.*

class TalkActivity : AppCompatActivity(), TextToSpeech.OnInitListener {
    private var userInput: String=""
    lateinit var tts:TextToSpeech
//    private lateinit var downloadBtn: Button


    private val RQ_SPEECH_REC=102
    private lateinit var binding: ActivityTalkBinding
    var  list=ArrayList<MessageModel>()
    private  lateinit var mLayoutManager:LinearLayoutManager
    private lateinit var adapter: MessageAdapter
    private var isBackButtonPressed = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTalkBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Request the RECORD_AUDIO permission
//        val requestCode = RQ_SPEECH_REC
//
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), requestCode)
//        }
//        binding.backBtn.setOnClickListener { finish() }
//                setContentView(R.layout.activity_talk)

        mLayoutManager = LinearLayoutManager(this)
        mLayoutManager.stackFromEnd = true
        adapter = MessageAdapter(list)
        binding.recyclerView.adapter = adapter

        binding.recyclerView.layoutManager = mLayoutManager
        tts = TextToSpeech(this, this)

        binding.micbtn.setOnClickListener {
            binding.imageView2.setVisibility(View.GONE)
            binding.textView2.setVisibility(View.GONE)
            askSpeechInput()
        }
    }


//        binding.sendbtn.setOnClickListener{
//            if( binding.userMsg.text!!.isEmpty()){
//                Toast.makeText(this, "Please ask your question", Toast.LENGTH_SHORT).show()
//            }else{
//
//                callAPI()
//            }
//        }


//    stopspeech

    override fun onPause() {
        super.onPause()
        if (!isBackButtonPressed) {
            if (::tts.isInitialized) {
                tts.stop()
            }
        }
    }

    override fun onBackPressed() {
        isBackButtonPressed = true
        if (::tts.isInitialized) {
            tts.stop()
            tts.shutdown()
        }
        super.onBackPressed()
    }







//
//
    private fun callAPI(){
        binding.recyclerView.recycledViewPool.clear()
//        val userIntput=result
//        val userInput = binding.userMsg.text.toString()
//        list.add(MessageModel(true, false, userInput))
//        list.add(MessageModel(true,false,binding.userMsg.text.toString()))
        adapter.notifyItemInserted(list.size-1)
        binding.recyclerView.smoothScrollToPosition(list.size-1)

        val apiInterface= ApiUtilities.getApiInterface()
//        list.add(MessageModel(false, false,"Typing..."))
        val requestBody=RequestBody.create(MediaType.parse("application/json"),
            Gson().toJson(
                ChatRequest(
                    250,
                    "text-davinci-003",
                    userInput,
                    0.7
                )
            )
        )
        val contentType="application/json"
        val authorization="Bearer ${Utils.API_KEY}"
//        binding.userMsg.text!!.clear()

        lifecycleScope.launch(Dispatchers.IO )

        {
            try {
                val response = apiInterface.getChat(contentType, authorization, requestBody)
                val textResponse = response.choices.first().text



                tts.speak(textResponse,TextToSpeech.QUEUE_FLUSH,null)
                list.add(MessageModel(false, false,textResponse))
                withContext(Dispatchers.Main) {
                    adapter.notifyItemInserted(list.size-1)
                    binding.recyclerView.recycledViewPool.clear()
                    binding.recyclerView.smoothScrollToPosition(list.size - 1)
                }
//


            } catch (e:Exception){
                withContext (Dispatchers.Main){
                    Toast.makeText(this@TalkActivity,e.message,Toast.LENGTH_SHORT).show()
//                    binding.userMsg.text!!.clear()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode==RQ_SPEECH_REC && resultCode==Activity.RESULT_OK) {
            val result: ArrayList<String>? = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            userInput = result?.get(0).toString()

            // Set the value of val result to val userInput
//            val  userInput=result
            list.add(MessageModel(true, false, userInput,null))


//            binding.userMsg.setText(userInput)

            callAPI()
//            binding.userMsg.setText(result?.get(0).toString())
//            val userInput = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)




        }
    }

//    private fun askSpeechInput() {
//        val currentVersion = Build.VERSION.SDK_INT
//        val speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
//
//        // Check if speech recognition is available on the device
//        if (!SpeechRecognizer.isRecognitionAvailable(this)) {
//            Toast.makeText(this, "This feature is not available on your device", Toast.LENGTH_SHORT).show()
//            return
//        }
//
//        // Check if the user has granted the MANAGE_VOICE_RECORDING permission
//        val permission = Manifest.permission.MANAGE_VOICE_RECORDING
//        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
//            // The user has not granted the permission, request it
//            requestPermissions(arrayOf(permission), RQ_SPEECH_REC)
//            return
//        }
//
//        // Start the speech recognition
//        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
//        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
//        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
//        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say Something!")
//        speechRecognizer.startListening(intent)
//    }

    private fun askSpeechInput(){



        if (!SpeechRecognizer.isRecognitionAvailable(this)){
            Toast.makeText(this,"This feature is not available on your device",Toast.LENGTH_SHORT).show()
        }else{
            val i=Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            i.putExtra(RecognizerIntent.EXTRA_LANGUAGE,Locale.getDefault())
            i.putExtra(RecognizerIntent.EXTRA_PROMPT,"Say Something!")
            startActivityForResult(i,RQ_SPEECH_REC)


        }



    }

    override fun onInit(status: Int) {
        if(status==TextToSpeech.SUCCESS){
            val res:Int=tts.setLanguage(Locale.getDefault())
            if(res==TextToSpeech.LANG_MISSING_DATA || res ==TextToSpeech.LANG_NOT_SUPPORTED ){
                Toast.makeText(this,"Language not supportes",Toast.LENGTH_SHORT).show()
            }
        }
        else{
            Toast.makeText(this,"Failed to initialize",Toast.LENGTH_SHORT).show()
        }
    }

}

