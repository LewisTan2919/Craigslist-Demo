package com.example.cis_600_project

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_item_detail.*
import java.io.Serializable


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_MOV1 = "movie"


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [DetailFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [DetailFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ItemDetail : Fragment() {
    // TODO: Rename and change types of parameters
    private var product: item? = null
    private var listener: OnBackListner? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

            product = it.getSerializable(ARG_MOV1) as item

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_item_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Item_title.text=product!!.title
        Item_description.text="Description: "+product!!.description

        var id=product!!.itemid
        val itemRef = FirebaseDatabase.getInstance().reference.child("items").child(id)
        itemRef.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if(dataSnapshot != null){
                    Picasso.get().load(dataSnapshot.child("itemImageUrl").value.toString()).into(Item_poster)

                }
            }
        })


        Item_price.text="Price: $"+product!!.price.toString()

        var userID=product!!.sellerid
        val sellerRef = FirebaseDatabase.getInstance().reference.child("users").child(userID)
        sellerRef.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if(dataSnapshot != null){
                    Picasso.get().load(dataSnapshot.child("profileImageUrl").value.toString()).fit().into(Seller_photo)
                    Seller_name.text=dataSnapshot.child("username").value.toString()
                }
            }
        })





        back_btn.setOnClickListener { listener?.onBackClick(it)}
        Seller_photo.setOnClickListener{
            val intent = Intent(activity, ProfileActivity::class.java)
            intent.putExtra("userID",product!!.sellerid)
            startActivity(intent)
        }
        add_favorite.setOnClickListener {
            addToFavorite()
        }
        location.setOnClickListener {
            val intent = Intent(activity, Direction::class.java)
            intent.putExtra("location",product!!.location)
            startActivity(intent)
        }
    }

    fun addToFavorite()
    {
        val CustomerID = FirebaseAuth.getInstance().uid ?: ""
        val Itemid = product!!.itemid

        val favoriteRef = FirebaseDatabase.getInstance().getReference("/Favorite/$CustomerID/$Itemid")
        favoriteRef.setValue(product)
            .addOnSuccessListener {
                Log.d("upload"
                    , "saved the item to  favorite list")
                val intent = Intent(context, FavoriteActivity::class.java)
                startActivity(intent)
            }
            .addOnFailureListener {
                Log.d("SignUp"
                    , "Failed to set value to database: ${it.message}")
            }
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnBackListner) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments]
     * (http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnBackListner {
        // TODO: Update argument type and name
        fun onBackClick(v: View)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MovieFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: item) =
            ItemDetail().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_MOV1, param1 as Serializable)
                }
            }
    }
}
