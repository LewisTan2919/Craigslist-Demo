package com.example.cis_600_project

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.ScaleAnimation
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso

class SoldListAdapter(context : Context,userID:String) :
    androidx.recyclerview.widget.RecyclerView.Adapter<SoldListAdapter.MovieViewHolder>() {
    val items = ArrayList<item>()
    var myListener: MyItemClickListener? = null
    var lastPosition = -1 // for animation
    val TAG = "FB Adapter"

    private val mDatabase: DatabaseReference = FirebaseDatabase.getInstance().reference
    val userID = FirebaseAuth.getInstance().uid ?: ""
    private val mRef = mDatabase.child("Sold/$userID")


    var childEventListener = object: ChildEventListener{
        override fun onCancelled(p0: DatabaseError) {
            Log.d(TAG, "child event listener - onCancelled" + p0.toException())
            Toast.makeText(context, "Fail to load data", Toast.LENGTH_SHORT).show()
        }
        override fun onChildMoved(p0: DataSnapshot, p1: String?) {
            Log.d(TAG, "child event listener - onChildMoved" +p0.toString())
            val data = p0.getValue<item>(item::class.java)
            val key = p0.key
        }
        override fun onChildChanged(p0: DataSnapshot, p1: String?) {
            Log.d(TAG, "child event listener - onChildChanged" +p0.toString())
            val data = p0.getValue<item>(item::class.java)
            val key = p0.key
            if(data != null && key != null) {
                for( (index, item) in items.withIndex()){
                    if(item.itemid.equals(key)){
                        items.removeAt(index)
                        items.add(index, data)
                        notifyItemChanged(index)
                        break
                    }
                }
            }
        }
        override fun onChildAdded(p0: DataSnapshot, p1: String?) {
            Log.d(TAG, "child event listener - onChildAdded" +p0.toString())
            val data = p0.getValue<item>(item::class.java)
            val key = p0.key
            if(data != null && key != null) {
                var insertPos = items.size
                for( item in items ){
                    if(item.itemid.equals(key))
                        return
                }
                items.add(insertPos, data)
                notifyItemInserted(insertPos)
            }
        }
        override fun onChildRemoved(p0: DataSnapshot) {
            Log.d(TAG, "child event listener - onChildRemoved" +p0.toString())
            val data = p0.getValue<item>(item::class.java)
            val key = p0.key
            if(data != null && key != null) {
                var delPos = -1
                for( (index, item) in items.withIndex()){
                    if(item.itemid.equals(key)){
                        delPos = index
                        break
                    }
                }
                if( delPos != -1 ){
                    items.removeAt(delPos)
                    notifyItemRemoved(delPos)
                }
            }
        }
    }




    init {

        mRef.addChildEventListener(childEventListener)


    }


    // Adapter Listener!!!
    interface MyItemClickListener {
        fun onItemClickedFromAdapter(product: item)
        fun onItemRemove(position: Int)
    }


    fun setMyItemClickListener(listener: MyItemClickListener) {
        this.myListener = listener
    }




    // MUST DO!!
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): MovieViewHolder {
        val layoutInflater = LayoutInflater.from(p0.context) // p0 is parent
        val view: View
// p1 -> View Type, check getItemViewType function!!
        view = when (p1) {
            1 -> {
                layoutInflater.inflate(R.layout.soldlist_item, p0, false)
            }
            else -> {
                layoutInflater.inflate(R.layout.soldlist_common_item, p0, false)
            }
        }
        return MovieViewHolder(view)
    }

    // MUST DO!!
    override fun getItemCount(): Int {
        return items.size
    }

    // MUST DO!!
    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val item = items[position]
        val itemRef = FirebaseDatabase.getInstance().reference.child("items").child(item.itemid)
        itemRef.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if(dataSnapshot != null){
                    Picasso.get().load(dataSnapshot.child("itemImageUrl").value.toString()).fit().into(holder.itemPhoto)
                }
            }
        })
        holder.itemTitle.text = item.title
        setAnimation(holder.itemPhoto, position)
//setAnimation(holder.itemView, position)
    }

    private fun setAnimation(view: View, position: Int) {
        if (position != lastPosition) {
            when (getItemViewType(position)) {
                1 -> {
                    val animation =
                        AnimationUtils.loadAnimation(view.context, android.R.anim.slide_in_left)
                    animation.duration = 700
                    animation.startOffset = position * 100L
                    view.startAnimation(animation)
                }
                2 -> {
                    val animation = AlphaAnimation(0.0f, 1.0f)
                    animation.duration = 1000
                    animation.startOffset = position * 50L
                    view.startAnimation(animation)
                }
                else -> {
                    val animation = ScaleAnimation(
                        0.0f, 1.0f, 0.0f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f,
                        Animation.RELATIVE_TO_SELF, 0.5f
                    )
                    animation.duration = 500
                    animation.startOffset = position * 200L
                    view.startAnimation(animation)
                }
            }
//animation.startOffset = position * 100L
            lastPosition = position
        }
    }

    // Three type of item view depending on position
    override fun getItemViewType(position: Int): Int {
        val currentID = FirebaseAuth.getInstance().uid ?: ""
            return 1
    }



    fun getItem(index: Int): Any {
        return items[index]
    }

    fun findFirst(query: String?):Int
    {

        return 0
    }


    fun addMovie(pos: Int) {

        notifyItemInserted(pos+1)
    }

    fun deleteMovie(position: Int){
        var movie = items[position]
// delete from Firebase
        mRef.child(position.toString()).removeValue()
        items.removeAt(position)
        notifyItemRemoved(position)
    }

    fun removeItem(position: Int) {
        val product=items[position]
        mRef.child(product.itemid).removeValue()
    }


    inner class MovieViewHolder(view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view){
        val itemPhoto = view.findViewById<ImageView>(R.id.sold_image)
        val itemTitle = view.findViewById<TextView>(R.id.sold_title)
        val itemRemove=view.findViewById<ImageView>(R.id.sold_delete)
        init{
            view.setOnClickListener {
                if(myListener != null){
                    if(adapterPosition != androidx.recyclerview.widget.RecyclerView.NO_POSITION){
                        myListener!!.onItemClickedFromAdapter(items[adapterPosition])
// myListener is the fragment that contains the recyclerview
                    }
                }
            }
            view.setOnLongClickListener {
                if(myListener != null){
                    if(adapterPosition != androidx.recyclerview.widget.RecyclerView.NO_POSITION){
                        myListener!!.onItemRemove(adapterPosition)
                    }
                }
                true
            }
            itemRemove.setOnClickListener {
                if(myListener != null){
                    if(adapterPosition != androidx.recyclerview.widget.RecyclerView.NO_POSITION){
                        myListener!!.onItemRemove(adapterPosition)
// myListener is the fragment that contains the recyclerview
                    }
                }
            }
        }


    }

}
