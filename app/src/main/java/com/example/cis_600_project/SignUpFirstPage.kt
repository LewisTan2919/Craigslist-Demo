package com.example.cis_600_project

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.FileProvider
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.yalantis.ucrop.UCrop
import kotlinx.android.synthetic.main.fragment_sign_up_first_page.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
val REQUEST_IMAGE_CAPTURE = 1
/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [SignUpFirstPage.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [SignUpFirstPage.newInstance] factory method to
 * create an instance of this fragment.
 */
class GenericFileProvider : FileProvider(){}
class SignUpFirstPage : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var currentPhotoPath: String? = null;
    private var listener: OnFragmentInteractionListener? = null
    var selectedPhotoUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sign_up_first_page, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        signup_btn_1.setOnClickListener {
            performRegister()
            //listener?.toSignUpSecond(it);
        }
        signup_profile_photo.setOnClickListener{
            showPictureDialog();
        }
        signup_email.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                validateEmailAddress()
            }

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {
                validateEmailAddress()
            }
        })
    }
    private fun validateEmailAddress(): Boolean {

        val email = signup_email.text.toString().trim()

        val emailLayout = this.view?.findViewById<TextInputLayout>(R.id.signup_email_layout)
        if (email.isEmpty()) {
            emailLayout?.error = "Email is required. Can't be empty."
            return false
        } else {
            signup_email.setError(null)
            return true
        }
    }
    private fun showPictureDialog() {
        val pictureDialog = AlertDialog.Builder(this.context)
        pictureDialog.setTitle("Select Action")
        val pictureDialogItems = arrayOf("Select photo from gallery", "Capture photo from camera")
        pictureDialog.setItems(pictureDialogItems
        ) { dialog, which ->
            when (which) {
                0 -> choosePhotoFromGallary()
                1 -> takePhotoFromCamera()
            }
        }
        pictureDialog.show()
    }
    fun choosePhotoFromGallary() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, 0)
    }


    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File = this.context?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = "file:" + absolutePath
        }
    }
    private fun takePhotoFromCamera() {


                // Continue only if the File was successfully created
        val intent: Intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val photoFile: File? = try {
            createImageFile()
        } catch (ex: IOException) {
            // Error occurred while creating the File
            null
        }
        val uri : Uri
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            uri = FileProvider.getUriForFile(this.context!!, this.context!!.getApplicationContext().getPackageName() + ".provider", photoFile!!);
        }
        else{
            uri = Uri.fromFile(photoFile);
        }

        intent.putExtra(MediaStore.EXTRA_OUTPUT,uri)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)

    }

    fun onButtonPressed(view: View) {
        listener?.toSignUpSecond(view)
    }


    private fun openCropActivity(sourceUri: Uri, destinationUri: Uri)
    {
        UCrop.of(sourceUri, destinationUri)
            .withMaxResultSize(100, 100)
            .withAspectRatio(5f, 5f)
            .start(this.context!!, this);
    }
        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            Log.d("SignUp", "Photo was selected")
            selectedPhotoUri = data.data
            //val bitmap = MediaStore.Images.Media.getBitmap(context!!.contentResolver, selectedPhotoUri)
            //signup_profile_photo.setImageBitmap(bitmap)
            selectedPhotoUri = UCrop.getOutput(data);
            val sourcePhotoUri = data.data
            val file = createImageFile()
            val destPhotoUri = Uri.fromFile(file)
            openCropActivity(sourcePhotoUri!!, destPhotoUri)

        }
        else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            val uri = Uri.parse(currentPhotoPath)
            println(uri)
            openCropActivity(uri, uri)

        }
        else if (requestCode == UCrop.REQUEST_CROP && resultCode == Activity.RESULT_OK) {
            selectedPhotoUri = UCrop.getOutput(data!!);
            val bitmap = MediaStore.Images.Media.getBitmap(context!!.contentResolver, selectedPhotoUri)
            signup_profile_photo.setImageBitmap(bitmap)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun toSignUpSecond(view: View)
    }

    companion object {
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SignUpFirstPage().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private fun performRegister() {
        val email = signup_email.text.toString()
        val password = signup_password.text.toString()
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(context, "Please enter text in email/pw", Toast.LENGTH_SHORT).show()
            return
        }
///*if (selectedPhotoUri == null){
//Toast.makeText(context, "Please select your profile image", Toast.LENGTH_SHORT).show()
//return
//}*/
        Log.d("SignUp"
            , "Attempting to create user with email: $email")
// Firebase Authentication to create a user with email and password
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (!it.isSuccessful) return@addOnCompleteListener
// else if successful
                Log.d("SignUp"
                    , "Successfully created user with uid: ${it.result!!.user!!.uid}")
                uploadImageToFirebaseStorage()
            }
            .addOnFailureListener{
                Log.d("SignUp"
                    , "Failed to create user: ${it.message}")
                Toast.makeText(context, "Failed to create user: ${it.message}"
                    , Toast.LENGTH_SHORT).show()
            }
    }
    private fun uploadImageToFirebaseStorage() {
        println(selectedPhotoUri)
        if (selectedPhotoUri == null) return
        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")
        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {
                Log.d("SignUp"
                    , "Successfully uploaded image: ${it.metadata?.path}")
                ref.downloadUrl.addOnSuccessListener {
                    Log.d("SignUp"
                        , "File Location: $it")
                    saveUserToFirebaseDatabase(it.toString())
                }
            }
            .addOnFailureListener {
                Log.d("SignUp"
                    , "Failed to upload image to storage: ${it.message}")
                saveUserToFirebaseDatabase(it.toString())
            }
    }
    private fun saveUserToFirebaseDatabase(profileImageUrl: String) {
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        val user = User(uid, signup_fullname.text.toString(), signup_email.text.toString(), profileImageUrl,uid,uid,uid)
        ref.setValue(user)
            .addOnSuccessListener {
                Log.d("SignUp"
                    , "saved the user to Firebase Database")
// launch the Main activity, clear back stack,
// not going back to login activity with back press button
                val intent = Intent(context, MainPageActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
            .addOnFailureListener {
                Log.d("SignUp"
                    , "Failed to set value to database: ${it.message}")
            }
    }
}
