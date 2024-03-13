package com.example.mvi_compose.ui.rxJavaExamples

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mvi_compose.R


/**
 * Adapter for the [RecyclerView] in [GalleryFragment].
 */

class ReposRxJava2FlatMapAdapter :
    RecyclerView.Adapter<ReposRxJava2FlatMapAdapter.ReposFlatMapHolder>() {

    private val TAG = "RecyclerAdapter"

    private var posts: MutableList<Post> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReposFlatMapHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_rxjava2_flatmap_repos, null, false)
        return ReposFlatMapHolder(view)
    }

    override fun onBindViewHolder(holder: ReposFlatMapHolder, position: Int) {
        val photo = posts.get(position)
        if (photo != null) {
            holder.bind(photo)
        }
    }


    override fun getItemCount(): Int {
        return posts.size
    }

    fun setPosts(mPosts: MutableList<Post>) {
        posts = mPosts
        notifyDataSetChanged()
    }

    fun updatePost(post: Post) {
        val position = posts.indexOf(post)
        Log.d("rxjavatag", "size is: ${posts.size}")
        Log.d("rxjavatag", "post size is: ${post.comments.size}")
        Log.d("rxjavatag", "comments is: ${post.comments}")
//        posts[position] = posts.set(position, post)
        posts.set(position, post)
        notifyItemChanged(posts.indexOf(post))
    }

    fun getPosts(): List<Post?> {
        return posts
    }

    class ReposFlatMapHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var title: TextView
        var numComments: TextView
        var progressBar: ProgressBar
        fun bind(post: Post) {
            title.setText(post.title)
            if (post.comments.isEmpty()) {
                showProgressBar(true)
                numComments.text = ""
            } else {
                showProgressBar(false)
                numComments.setText("" + post.comments.size)
            }
        }

        private fun showProgressBar(showProgressBar: Boolean) {
            if (showProgressBar) {
                progressBar.visibility = View.VISIBLE
            } else {
                progressBar.visibility = View.GONE
            }
        }

        init {
            title = itemView.findViewById(R.id.title)
            numComments = itemView.findViewById(R.id.num_comments)
            progressBar = itemView.findViewById(R.id.progress_bar)
        }
    }


}