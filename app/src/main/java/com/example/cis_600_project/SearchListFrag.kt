package com.example.cis_600_project

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import jp.wasabeef.recyclerview.animators.adapters.AlphaInAnimationAdapter
import kotlinx.android.synthetic.main.fragment_searchlist.rview3

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [RecyclerFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [RecyclerFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SearchListFrag : Fragment(),SearchListAdapter.MyItemClickListener{
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnRecyclerInteractionListener? = null
    lateinit var myAdapter :SearchListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        setHasOptionsMenu(true)
    }

    override fun onItemAddFavorite(product: item) {
        addToFavorite(product)
    }
    override fun onItemClickedFromAdapter(product : item) {
        onItemClickedFromRecyclerViewFragment(product)
    }
    interface OnRecyclerInteractionListener {
        fun onItemClicked(product : item)
    }
    fun onItemClickedFromRecyclerViewFragment(product : item) {
        listener?.onItemClicked(product)
    }
    fun addToFavorite(product: item)
    {
        val CustomerID = FirebaseAuth.getInstance().uid ?: ""
        val Itemid = product!!.itemid

        val favoriteRef = FirebaseDatabase.getInstance().getReference("/Favorite/$CustomerID/$Itemid")
        favoriteRef.setValue(product)
            .addOnSuccessListener {
                Log.d("upload"
                    , "saved the item to  favorite list")
                val intent = Intent(this.context, FavoriteActivity::class.java)

                startActivity(intent)
            }
            .addOnFailureListener {
                Log.d("SignUp"
                    , "Failed to set value to database: ${it.message}")
            }
    }

    private fun animationAdapter() {
        rview3.adapter = AlphaInAnimationAdapter(myAdapter).apply {
            // Change the durations.
            setDuration(3000)
            setStartPosition(200)
// Disable the first scroll mode.
            setFirstOnly(false)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_searchlist, container, false)
    }
    override fun onStart() {
        super.onStart()
        myAdapter = SearchListAdapter(activity!!.applicationContext)
        rview3.hasFixedSize()
        rview3.layoutManager= GridLayoutManager(context, 2)
        myAdapter.setMyItemClickListener(this)
        rview3.adapter = myAdapter
// default Item Animator
        rview3.itemAnimator?.addDuration = 1000L
        rview3.itemAnimator?.removeDuration = 1000L
        rview3.itemAnimator?.moveDuration = 1000L
        rview3.itemAnimator?.changeDuration = 1000L
        animationAdapter()
// Button Actions!!
        rview3.swapAdapter(myAdapter, true)
        rview3.scrollBy(0, 0)
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnRecyclerInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        if( menu?.findItem(R.id.mainpage_search) == null )
            inflater?.inflate(R.menu.main_page_toolbar, menu)
        val search = menu?.findItem(R.id.mainpage_search)!!.actionView as SearchView
        if ( search != null ){
            search.setOnQueryTextListener( object: SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    val pos = myAdapter.findFirst( query!!)
                    if (pos >= 0) {
                        rview3.smoothScrollToPosition(pos)
                        Toast.makeText(context, "Search Movie " + query + " found ... ", Toast.LENGTH_SHORT).show()
                    } else {
                        rview3.smoothScrollToPosition(0)
                        Toast.makeText(context, "Search Movie " + query + " not found ... ", Toast.LENGTH_SHORT).show()
                    }
                    return true
                }
                override fun onQueryTextChange(newText: String?): Boolean {
                    return true
                }
            })
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item?.itemId){

            R.id.mainpage_neighbor ->{
                val intent = Intent(context, FindNeighborActivity::class.java)
                intent.putExtra("action", 1)
                startActivity(intent)
            }

        }
        return super.onOptionsItemSelected(item)
    }

    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SearchListFrag().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
