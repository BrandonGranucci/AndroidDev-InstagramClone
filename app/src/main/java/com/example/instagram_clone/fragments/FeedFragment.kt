package com.example.instagram_clone.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Recycler
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.instagram_clone.MainActivity
import com.example.instagram_clone.Post
import com.example.instagram_clone.PostAdapter
import com.example.instagram_clone.R
import com.parse.FindCallback
import com.parse.ParseException
import com.parse.ParseQuery


open class FeedFragment : Fragment() {

    lateinit var postsRecyclerView: RecyclerView

    lateinit var adapter : PostAdapter

    lateinit var swipeContainer: SwipeRefreshLayout

    var allPosts: MutableList<Post> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_feed, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // This is where we set up our views and click listeners

        swipeContainer = view.findViewById(R.id.swipeContainer)

        swipeContainer.setOnRefreshListener {
            Log.i(TAG,"Refreshing timeline")
            queryPosts()
        }

        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
            android.R.color.holo_green_light,
            android.R.color.holo_orange_light,
            android.R.color.holo_red_light)

        postsRecyclerView = view.findViewById(R.id.postRecyclerView)

        // Steps to populate RecyclerView
        // 1. Create layout for each row in list (item_post.xml) --- DONE
        // 2. Create data source for each row (this is the Post class) --- DONE
        // 3. Create adapter that will bridge data and row layout (PostAdapter) --- DONE
        // 4. Set adapter on RecyclerView
        adapter = PostAdapter(requireContext(), allPosts as ArrayList<Post>)
        postsRecyclerView.adapter = adapter
        // 5. Set layout manager on RecyclerView
        postsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        queryPosts()

    }

    // Query for all posts in our server
    open fun queryPosts() {

        // Specify which class to query
        val query: ParseQuery<Post> = ParseQuery.getQuery(Post::class.java)
        // Find all Post objects
        query.include(Post.KEY_USER)
        // Return posts in descending order: newer posts appear at top
        query.addDescendingOrder("createdAt")

        //--------------------------------------
        // TODO Only return most recent 20 posts
        //--------------------------------------
        query.setLimit(20)
        query.findInBackground(object: FindCallback<Post> {
            override fun done(posts: MutableList<Post>?, e: ParseException?) {
                if(e != null) {
                    // Something went wrong
                    Log.e(TAG, "Error fetching posts")
                } else {
                    if (posts != null) {
                        //Clear out old posts
                        adapter.clear()
                        for (post in posts) {
                            Log.i(TAG, "Post: " + post.getDescription() + " , username: " + post.getUser()?.username + ", Timestamp: " + post.createdAt)
                        }
                        allPosts.addAll(posts)
                        adapter.notifyDataSetChanged()
                        // Now we call setRefreshing(false) to signal refresh has finished
                        swipeContainer.setRefreshing(false)
                    }
                }
            }

        })
    }


    companion object {
        const val TAG = "FeedFragment"
    }
}