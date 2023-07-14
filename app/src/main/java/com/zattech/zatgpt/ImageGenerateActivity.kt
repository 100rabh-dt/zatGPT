package com.zattech.zatgpt
import android.Manifest
import android.app.DownloadManager
import android.content.ContentValues
import android.content.Context
import android.content.Intent
//import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
//import android.transition.Transition
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
//import com.bumptech.glide.request.target.CustomTarget
import com.zattech.zatgpt.databinding.ActivityImageGenerateBinding
import com.google.gson.Gson
import com.zattech.zatgpt.adapter.MessageAdapter
import com.zattech.zatgpt.api.ApiUtilities
import com.zattech.zatgpt.models.MessageModel
import com.zattech.zatgpt.models.request.ImageGenerateRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.RequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import com.zattech.zatgpt.R
import java.io.OutputStream
import java.net.URL


import java.util.*

class ImageGenerateActivity : AppCompatActivity() {
    private val RQ_SPEECH_REC=102
    private lateinit var downloadBtn: Button
    private var imageUrl: String = "" // Declare imageUrl at the class level


    var  list=ArrayList<MessageModel>()
    private var permission = 0
    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        permission = if (permissions.all { it.value }) {
            1
        } else {
            0
        }
    }

//    private var permission=0
//    private val requestPermissionLauncher=registerForActivityResult(ActivityResultContracts.RequestMultiplePermission()){
//        permission =if(it){1}
//        else{0}
//    }


    private  lateinit var mLayoutManager: LinearLayoutManager
    private lateinit var adapter: MessageAdapter
    private lateinit var bindibg:ActivityImageGenerateBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        bindibg= ActivityImageGenerateBinding.inflate(layoutInflater)
//        bindibg= Activitychatleftitem.inflate(layoutInflater)

        setContentView(bindibg.root)
//        val downloadBtn = bindibg.downloadBtn

//        downloadBtn = bindibg.downloadBtn
//        bindibg.downloadBtn.setOnClickListener{}
//           downloadBtn =findViewById(R.id.downloadBtn)


        val inflater = LayoutInflater.from(this)
        val view = inflater.inflate(R.layout.chatleftitem, null)
        val downloadBtn1 = view.findViewById<Button>(R.id.downloadBtn1)
//        downloadBtn1.setOnClickListener{
//            Toast.makeText(this, "Jay Mahakal", Toast.LENGTH_LONG).show()
//
//        }

           bindibg.downloadBtn.setOnClickListener {
               // Call a method to download the image here
//               requestPermissionLauncher.launch(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE))
//               val writePermission = Manifest.permission.WRITE_EXTERNAL_STORAGE
//               val readPermission = Manifest.permission.READ_MEDIA_IMAGES
//
//               if( Build.VERSION.SDK_INT<11){
//               requestPermissionLauncher.launch(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE))
//               }
//               else{
//               requestPermissionLauncher.launch(arrayOf(Manifest.permission.READ_MEDIA_IMAGES))}
               requestPermissionLauncher.launch(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE))


               if (permission == 1) {
                   down(imageUrl, "image from zatGPT")
               } else {
                   Toast.makeText(this, "Please allow the permission", Toast.LENGTH_LONG).show()
               }
           }
//               requestPermissionLauncher.launch(arrayOf(Manifest.permission.READ_MEDIA_IMAGES))
//               if(permission==1 ) {
////                   val imageUrl=
////                   val imageUrl = "https://picsum.photos/200"
//                   down(imageUrl, "ztechno")
//               }else
//               {             Toast.makeText(this,"Please allow the permission",Toast.LENGTH_LONG).show()
//
//               }
//               downloadAndSaveImage(this, imageUrl)
//            downloadImage()




        bindibg.backBtn.setOnClickListener{finish()}
        mLayoutManager=LinearLayoutManager(this)
        mLayoutManager.stackFromEnd=true
        adapter=MessageAdapter(list)
        bindibg.recyclerView.adapter=adapter

        bindibg.recyclerView.layoutManager=mLayoutManager
//        downloadBtn.visibility = View.GONE


        bindibg.micbtn.setOnClickListener{
            askSpeechInput()
        }
        bindibg.sendbtn.setOnClickListener{
            if( bindibg.userMsg.text!!.isEmpty()){
                Toast.makeText(this, "Please ask your question", Toast.LENGTH_SHORT).show()
            }else{
                bindibg.tempimage.setVisibility(View.GONE)
                bindibg.textView2.text = null

//                bindibg.downloadBtn.setVisibility(View.VISIBLE)

                callAPI()
            }
        }
    }

     private  fun down( url: String, fileName: String){
         try {
             val downloadManager=getSystemService(DOWNLOAD_SERVICE) as DownloadManager
             val imageLink= Uri.parse(url)
             val  request=DownloadManager.Request(imageLink)
             request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE or DownloadManager.Request.NETWORK_WIFI)
                 .setMimeType("image/jpeg")
                 .setAllowedOverRoaming(false)
                 .setAllowedNetworkTypes(DownloadManager.Request.VISIBILITY_VISIBLE)
                 .setTitle(fileName)
                 .setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES,File.separator+fileName+".jpg")
             downloadManager.enqueue(request)
             Toast.makeText(this,"Downloaded",Toast.LENGTH_LONG).show()


         }catch (e:Exception){
             Toast.makeText(this,"Failed to download",Toast.LENGTH_LONG).show()

         }





     }
    suspend fun downloadAndSaveImage(context: Context, imageUrl: String): String? {
        return withContext(Dispatchers.IO) {
            try {
                val connection = URL(imageUrl).openConnection()
                connection.doInput = true
                connection.connect()
                val inputStream = connection.getInputStream()
                val bitmap = BitmapFactory.decodeStream(inputStream)

                val fileName = UUID.randomUUID().toString() + ".jpg"
                val directory = getSaveDirectory(context)
                directory.mkdirs()
                val file = File(directory, fileName)
                val outputStream: OutputStream = FileOutputStream(file)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                outputStream.flush()
                outputStream.close()

                // Add the image to the device's gallery
                val savedImagePath = saveImageToGallery(context, file, fileName)

                // Notify the gallery app of the new image
                scanImageFile(context, file)

                return@withContext savedImagePath
            } catch (e: Exception) {
                e.printStackTrace()
                return@withContext null
            }
        }
    }

    private fun getSaveDirectory(context: Context): File {
        val directory =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // On Android 10 (API level 29) and above, save in Pictures directory
                context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            } else {
                // On older versions, save in the root of the external storage
                Environment.getExternalStorageDirectory()
            }
        return File(directory?.absolutePath ?: "")
    }

    private fun saveImageToGallery(context: Context, imageFile: File, fileName: String): String? {
        val contentResolver = context.contentResolver
        val imageCollection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        } else {
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }

        val values = ContentValues().apply {
            put(MediaStore.Images.Media.TITLE, fileName)
            put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis() / 1000)
            put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis())
            put(MediaStore.Images.Media.DATA, imageFile.absolutePath)
        }

        val uri = contentResolver.insert(imageCollection, values)
        return uri?.toString()
    }

    private fun scanImageFile(context: Context, imageFile: File) {
        val filePaths = arrayOf(imageFile.absolutePath)
        MediaScannerConnection.scanFile(context, filePaths, null, null)
    }



    private fun callAPI(){
        list.add(MessageModel(true,false,bindibg.userMsg.text.toString()))
        Toast.makeText(this, "Please wait few secon" +
                "ds ", Toast.LENGTH_LONG).show()


        adapter.notifyItemInserted(list.size-1)
        bindibg.recyclerView.recycledViewPool.clear()
        bindibg.recyclerView.smoothScrollToPosition(list.size-1)
        val apiInterface= ApiUtilities.getApiInterface()
        val requestBody= RequestBody .create(MediaType.parse("application/json"),
            Gson().toJson(
                ImageGenerateRequest(
                    1,
                    bindibg.userMsg.text.toString(),
                    "1024x1024"
                )
            )
        )
        val contentType="application/json"
        val authorization="Bearer ${Utils.API_KEY}"
                bindibg.userMsg.text!!.clear()
        Toast.makeText(this, "Please wait a few seconds ", Toast.LENGTH_LONG).show()

        lifecycleScope.launch  (Dispatchers.IO ){
            try {
                val response = apiInterface.generateImage(contentType, authorization, requestBody)
                val textResponse = response.data.first().url
                imageUrl=textResponse
                list.add(MessageModel(false, true, textResponse))
//                downloadBtn.visibility = View.VISIBLE

                withContext(Dispatchers.Main){
                    adapter.notifyItemInserted(list.size - 1)
                    bindibg.recyclerView.recycledViewPool.clear()
                    bindibg.recyclerView.smoothScrollToPosition(list.size - 1)
                }
            }catch(e:Exception){
                withContext (Dispatchers.Main){
                    Toast.makeText(this@ImageGenerateActivity,e.message,Toast.LENGTH_SHORT).show()
                    bindibg.userMsg.text!!.clear()
                }
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode==RQ_SPEECH_REC && resultCode== RESULT_OK) {
            val result = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            bindibg.userMsg.setText(result?.get(0).toString())


        }
    }


    private fun downloadImage() {
        // Get the URL of the image from the corresponding message model

//        val imageUrl = list.lastOrNull { it.isUser }?.textResponse
//        val imageUrl="https://picsum.photos/200\n"

        // Perform the image download using your preferred method
        // For example, using an image downloading library like Glide or Picasso
        // Here's an example using Glide:
        Glide.with(this)
            .asBitmap()
            .load(imageUrl)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
        Toast.makeText(this@ImageGenerateActivity, "Failed to download image", Toast.LENGTH_SHORT).show()
                    // Save the image to the device's external storage or app-specific directory
                    // Here's an example using the app-specific directory:
                    val imageFileName = "downloaded_image.jpg"
                    val imageFile = File(getExternalFilesDir(null), imageFileName)

                    try {
                        FileOutputStream(imageFile).use { outputStream ->
                            resource.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                        }
                        Toast.makeText(this@ImageGenerateActivity, "Image downloaded", Toast.LENGTH_SHORT).show()
                    } catch (e: IOException) {
                        e.printStackTrace()
                        Toast.makeText(this@ImageGenerateActivity, "Failed to download image", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    // Not used in this example
                }
            })
    }



    private fun askSpeechInput(){
        if (!SpeechRecognizer.isRecognitionAvailable(this)){
            Toast.makeText(this,"Something went wrong",Toast.LENGTH_SHORT).show()
        }else{
            val i= Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            i.putExtra(RecognizerIntent.EXTRA_PROMPT,"Say Something!")
            startActivityForResult(i,RQ_SPEECH_REC)


        }



    }

}