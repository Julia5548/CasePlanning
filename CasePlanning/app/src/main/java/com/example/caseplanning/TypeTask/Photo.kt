package com.example.caseplanning.TypeTask

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.core.net.toFile
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import butterknife.ButterKnife
import butterknife.OnClick
import com.example.caseplanning.CreateTask.CreateTaskWindow
import com.example.caseplanning.CreateTask.MyViewModel
import com.example.caseplanning.DataBase.UriTypeTask
import com.example.caseplanning.Increase.PhotoIncrease
import com.example.caseplanning.R
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

/*написать комменты, проблема с видом фотографии, нет потверждения на правильность фотографии, нажатие на фотографию, чтобы открылась полность вся, загрузка из галереи */
class Photo : Fragment() {

    val CAMERA_REQUEST = 1001
    val PERMISSION_CODE = 1000
    var photo_image: ImageView? = null
    private var pageViewModel: MyViewModel? = null
    var videoUri: Uri? = null
    val BYTES_PER_PX = 4.0f
    var audioFile: String? = null
    var timeAudio: String? = null
    private var mCurrentFile: String? = null
    private var mPhotoFile: File? = null
    private var photoUri: Uri? = null
    private var mBitmap : Bitmap? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.photo, container, false)
        ButterKnife.bind(this, view)


        pageViewModel!!.uri.observe(requireActivity(), Observer { uri ->
            if (uri != null) {
                mCurrentFile = uri.photoUri
                videoUri = uri.videoUri
                audioFile = uri.audioUri
                timeAudio = uri.timeAudio
            }
        })

        if (mCurrentFile == null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (context!!.checkSelfPermission(Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_DENIED ||
                    context!!.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_DENIED
                ) {
                    val permission: Array<String> = arrayOf(
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                    requestPermissions(permission, PERMISSION_CODE)
                } else {
                    openCamera()
                }

            } else {
                openCamera()
            }
        }else {

            var bitmap:Bitmap? = null
            if (photo_image != null) {
                (photo_image!!.drawable as? BitmapDrawable)!!.bitmap.recycle()
            }
            photo_image = view.findViewById<ImageButton>(R.id.photoImage)

            bitmap = BitmapFactory.decodeFile(mCurrentFile)
            mBitmap = bitmap

            loadPhoto()
        }

        return view
    }


    /*открываем камеру*/
    fun openCamera() {


        mPhotoFile = createPhotoFile()
        photoUri = createPhotoUri(mPhotoFile)
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(context!!.packageManager) != null) {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri!!)
            startActivityForResult(intent, CAMERA_REQUEST)
        }
    }

    private fun createPhotoUri(file: File?): Uri {

        return FileProvider.getUriForFile(
            context!!,
            "${context!!.applicationContext.packageName}.provider",
            file!!
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        var bitmap : Bitmap? = null
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            //фотка сделана, извлекаем картинку

            bitmap = MediaStore.Images.Media.getBitmap(context!!.contentResolver, photoUri!!)

            mBitmap = bitmap
            if (photo_image != null)
                (photo_image!!.drawable as? BitmapDrawable)!!.bitmap.recycle()

            photo_image = view!!.findViewById<ImageButton>(R.id.photoImage)

            pageViewModel!!.uri.value =
                UriTypeTask(photoUri = mCurrentFile, videoUri = videoUri, audioUri = audioFile, timeAudio = timeAudio)

            loadPhotoinGallery()
            loadPhoto()

        } else {
            Log.d("Ошибка", "Не получилось сохранить фотографию")
        }
    }

    private fun loadPhotoinGallery() {
        val intent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)

        intent.data = photoUri!!
        context!!.sendBroadcast(intent)
    }

    @SuppressLint("SimpleDateFormat")
    @Throws(IOException::class)
    private fun createPhotoFile(): File {

        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "DSC_${timeStamp}_"
        val storage = context!!.getExternalFilesDir(Environment.DIRECTORY_PICTURES)

        val imageFile = File.createTempFile(
            imageFileName,
            ".JPG",
            storage
        )
        mCurrentFile = imageFile.absolutePath
        return imageFile
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pageViewModel = ViewModelProviders.of(requireActivity()).get(MyViewModel::class.java)

    }

    /*разрешение доступа к камере*/
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openCamera()
                } else {
                    Toast.makeText(
                        context, "В доступе было отказано",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }


    /*увелечение фотографии*/
   @OnClick(R.id.photoImage)
    fun photoZoom() {

        val photoIncrease: Fragment = PhotoIncrease()

        val transaction: FragmentTransaction = fragmentManager!!.beginTransaction()

        transaction.replace(R.id.linerLayout, photoIncrease)
        transaction.addToBackStack(null)
        transaction.commit()


    }

    override fun onDestroyView() {
        super.onDestroyView()
        pageViewModel = null
        photo_image?.setImageBitmap(null)
        mBitmap = null
        mCurrentFile = null
        mPhotoFile = null
        photoUri = null
        videoUri = null
        timeAudio = null
    }

    private fun loadPhoto() {

        if (readBitmapInfo() > MemUtils().megabytesFree()) {
            subSampleImage(32)
        } else {
            photo_image!!.setImageBitmap(mBitmap)
        }
    }

    private fun subSampleImage(powerOf: Int) {

        if (powerOf < 1 || powerOf > 32) {
            return
        }

        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = false
        options.inSampleSize = powerOf
        val bitmap = BitmapFactory.decodeFile(mCurrentFile, options)
        photo_image!!.setImageBitmap(bitmap)

    }

    private fun readBitmapInfo(): Float {

        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(mCurrentFile, options)
        val imageHeight = options.outHeight
        val imageWidth = options.outWidth

        return imageWidth * imageHeight * BYTES_PER_PX / MemUtils().BYTES_IN_MB
    }

    inner class MemUtils {

        val BYTES_IN_MB = 1024.0f * 1024.0f

        fun megabytesFree(): Float {

            val runtime = Runtime.getRuntime()
            val byteUsed = runtime.totalMemory()
            val mbUsed = byteUsed / BYTES_IN_MB
            return megabytesAvailable() - mbUsed
        }

        private fun megabytesAvailable(): Float {

            val runtime = Runtime.getRuntime()
            val bytesAvailable = runtime.maxMemory()
            return bytesAvailable / BYTES_IN_MB

        }
    }
}