package com.example.cis_600_project

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_upload.*
import java.util.*

class UploadActivity : AppCompatActivity() {

    var selectedPhotoUri: Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload)

        upload_image.setOnClickListener {
            Log.d("upload"
                , "Try to show photo selector")
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }
        upload_item_btn.setOnClickListener{

            uploadImageToFirebaseStorage()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
// proceed and check what the selected image was....
            Log.d("upload", "Photo was selected")
            selectedPhotoUri = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(this!!.contentResolver, selectedPhotoUri)
            upload_image.setImageBitmap(bitmap)
        }


    }private fun uploadImageToFirebaseStorage() {
        println(selectedPhotoUri)
        if (selectedPhotoUri == null) return
        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")
        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {
                Log.d("upload"
                    , "Successfully uploaded image: ${it.metadata?.path}")
                ref.downloadUrl.addOnSuccessListener {
                    Log.d("upload"
                        , "File Location: $it")
                    saveItemToFirebaseDatabase(it.toString())
                }
            }
            .addOnFailureListener {
                Log.d("SignUp"
                    , "Failed to upload image to storage: ${it.message}")
                saveItemToFirebaseDatabase(it.toString())
            }
    }

    private fun saveItemToFirebaseDatabase(profileImageUrl: String) {
        val Itemid = UUID.randomUUID().toString()
        val ItemImageUrl=profileImageUrl
        val sellerid = FirebaseAuth.getInstance().uid ?: ""
        val title=Title_txt.text.toString()
        val price=Price_txt.text.toString()
        val location=Location_txt.text.toString()
        val description=Description_txt.text.toString()

        val ref = FirebaseDatabase.getInstance().getReference("/items/$Itemid")
        val item = item(Itemid,ItemImageUrl,sellerid,title,price,location,description,false)
        ref.setValue(item)
            .addOnSuccessListener {
                Log.d("upload"
                    , "saved the item to Firebase Database")
            }
            .addOnFailureListener {
                Log.d("SignUp"
                    , "Failed to set value to database: ${it.message}")
            }
        val sellingRef = FirebaseDatabase.getInstance().getReference("/Selling/$sellerid/$Itemid")
        sellingRef.setValue(item)
            .addOnSuccessListener {
                Log.d("upload"
                    , "saved the item to  selling list")
                val intent = Intent(this, MainPageActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
            .addOnFailureListener {
                Log.d("SignUp"
                    , "Failed to set value to database: ${it.message}")
            }
    }
}
