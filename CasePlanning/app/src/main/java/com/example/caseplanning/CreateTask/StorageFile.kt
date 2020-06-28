package com.example.caseplanning.CreateTask

import android.content.Context
import android.graphics.BitmapFactory
import android.widget.ImageView
import android.widget.Toast
import android.widget.VideoView
import androidx.core.net.toUri
import androidx.fragment.app.FragmentManager
import com.example.caseplanning.DataBase.Task
import com.example.caseplanning.EditElements.EditTask
import com.example.caseplanning.R
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.*

class StorageFile() {

    private lateinit var mStorage: FirebaseStorage
    private lateinit var mStorageReference: StorageReference
    private lateinit var mNameFiles: String
    private lateinit var mPath: String
    private lateinit var mContext: Context

    constructor(nameFile: String, path: String, context: Context) : this() {
        mStorage = FirebaseStorage.getInstance()
        //ссылка для закрузки и удаления айлов
        mStorageReference = mStorage.reference
        mNameFiles = nameFile
        mPath = path
        mContext = context
    }

    fun loadImages() {
        //сслылка на фото
        val imagesRef: StorageReference? = mStorageReference.child("images/$mNameFiles")
        var inputStream: InputStream? = null
        try {
            inputStream = FileInputStream(File(mPath))
            val upload = imagesRef?.putStream(inputStream)!!
            upload.addOnFailureListener {
                Toast.makeText(mContext, "Изображение не удалось загрузить", Toast.LENGTH_SHORT)
                    .show()
                inputStream.close()
            }.addOnSuccessListener { taskSnapshot ->
                val downloadFile = taskSnapshot.metadata
                Toast.makeText(
                    mContext,
                    "Изображение $downloadFile успешно загружено",
                    Toast.LENGTH_SHORT
                ).show()
                inputStream.close()
            }
        } catch (exception: IOException) {
            exception.stackTrace
            inputStream?.close()
        }
    }

    fun loadVideo() {
        //сслылка на видео
        val videoRef: StorageReference? = mStorageReference.child("videos/${mNameFiles}")
        val upload = videoRef?.putFile(mPath.toUri())!!
        upload.addOnFailureListener {
            Toast.makeText(mContext, "Видео не удалось загрузить", Toast.LENGTH_SHORT)
                .show()
        }.addOnSuccessListener { taskSnapshot ->
            val downloadFile = taskSnapshot.metadata
            Toast.makeText(
                mContext,
                "Видео $downloadFile успешно загружено",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun loadAudio() {

        //сслылка на аудио
        val audioRef: StorageReference? = mStorageReference.child("audios/${mNameFiles}")
        var inputStream: InputStream? = null
        try {
            inputStream = FileInputStream(File(mPath))
            val upload = audioRef?.putStream(inputStream)!!
            upload.addOnFailureListener {
                Toast.makeText(mContext, "Аудио не удалось загрузить", Toast.LENGTH_SHORT)
                    .show()
                inputStream.close()
            }.addOnSuccessListener { taskSnapshot ->
                val downloadFile = taskSnapshot.metadata
                Toast.makeText(
                    mContext,
                    "Аудио $downloadFile успешно загружено",
                    Toast.LENGTH_SHORT
                ).show()
                inputStream.close()
            }
        } catch (exception: IOException) {
            exception.stackTrace
            inputStream?.close()
        }
    }

    fun loadImagesFilesMemory(imageView: ImageView, name: String) {

        mStorage = FirebaseStorage.getInstance()
        //ссылка для закрузки и удаления айлов
        mStorageReference = mStorage.reference

        val downloadFile = mStorageReference.child("images/$name")
        val name_image = name.split(".")

        val localFile = File.createTempFile(name_image[0], "jpg")

        downloadFile.getFile(localFile).addOnSuccessListener {
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = false
            options.inSampleSize = 32
            val bitmap = BitmapFactory.decodeFile(localFile.path, options)!!
            imageView.setImageBitmap(bitmap)
        }.addOnFailureListener { exception ->
            exception.stackTrace
        }
    }

    fun loadVideoFilesMemory(video: VideoView, name: String, context: Context) {

        mStorage = FirebaseStorage.getInstance()
        //ссылка для закрузки и удаления айлов
        mStorageReference = mStorage.reference

        val downloadFile = mStorageReference.child("videos/$name")
        val localFile = downloadFile.downloadUrl
            .addOnSuccessListener { uri ->
                video.setVideoURI(uri)
                video.seekTo(1)
            }.addOnFailureListener {
                Toast.makeText(context, "Видео не удалось загрузить", Toast.LENGTH_SHORT)
                    .show()

            }
    }

    fun loadAudioFilesMemory(name: String, context: Context): String {

        mStorage = FirebaseStorage.getInstance()
        //ссылка для закрузки и удаления айлов
        mStorageReference = mStorage.reference

        val downloadFile = mStorageReference.child("audios/$name")
        val name_audio = name.split(".")
        val localFile = File.createTempFile(name_audio[0], "mp3")

        downloadFile.getFile(localFile).addOnSuccessListener {
            Toast.makeText(context, "Аудио удалось загрузить", Toast.LENGTH_SHORT)
                .show()

        }.addOnFailureListener { exception ->
            exception.stackTrace
        }
        return localFile.path
    }
}