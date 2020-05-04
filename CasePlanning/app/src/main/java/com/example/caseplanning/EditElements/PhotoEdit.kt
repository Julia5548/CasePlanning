package com.example.caseplanning.EditElements

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import butterknife.ButterKnife
import butterknife.OnClick
import com.example.caseplanning.Increase.PhotoIncrease
import com.example.caseplanning.R

class PhotoEdit(var mCurrentFilePhoto: String?) : Fragment() {

    var photo_image: ImageView? = null
    private var mBitmap: Bitmap? = null
    val BYTES_PER_PX = 4.0f

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.photo, container, false)
        ButterKnife.bind(this, view)

        if (mCurrentFilePhoto != null) {
            if (photo_image != null) {
                (photo_image!!.drawable as? BitmapDrawable)!!.bitmap.recycle()
            }
            photo_image = view.findViewById<ImageButton>(R.id.photoImage)
            loadPhoto()
        }
        return view
    }

    private fun loadPhoto() {

        if (readBitmapInfo() > MemUtils().megabytesFree()) {
            subSampleImage(32)
        } else {
            mBitmap = BitmapFactory.decodeFile(mCurrentFilePhoto)
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
        val bitmap = BitmapFactory.decodeFile(mCurrentFilePhoto, options)
        photo_image!!.setImageBitmap(bitmap)

    }

    private fun readBitmapInfo(): Float {

        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(mCurrentFilePhoto, options)
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

    /*увелечение фотографии*/
    @OnClick(R.id.photoImage)
    fun photoZoom() {

        val photoIncrease: Fragment = PhotoIncrease()

        val transaction: FragmentTransaction = fragmentManager!!.beginTransaction()

        transaction.replace(R.id.linerLayout, photoIncrease)
        transaction.addToBackStack(null)
        transaction.commit()


    }

}