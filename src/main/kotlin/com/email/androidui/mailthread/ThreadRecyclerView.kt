package com.email.androidui.mailthread

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.email.scenes.mailbox.EmailThreadAdapter
import com.email.scenes.mailbox.data.EmailThread

class ThreadRecyclerView(val recyclerView: RecyclerView,
                         threadEventListener: EmailThreadAdapter.OnThreadEventListener?)  {

    val ctx: Context = recyclerView.context
    private val emailThreadAdapter = EmailThreadAdapter(ctx, threadEventListener)

    init {
        recyclerView.layoutManager = LinearLayoutManager(ctx)
        recyclerView.adapter = emailThreadAdapter
    }

    fun setThreadList(threadList: List<EmailThread>) {
        emailThreadAdapter.threads = threadList
        notifyThreadSetChanged()
    }

    fun setThreadListener(threadEventListener: EmailThreadAdapter.OnThreadEventListener?) {
        emailThreadAdapter.threadListener = threadEventListener
    }

    fun notifyThreadSetChanged() {
        emailThreadAdapter.notifyDataSetChanged()
    }

    fun notifyThreadRangeInserted(positionStart: Int, itemCount: Int) {
        emailThreadAdapter.notifyItemRangeInserted(positionStart, itemCount)
    }

    fun notifyThreadRemoved(position: Int) {
        emailThreadAdapter.notifyItemRemoved(position)
    }

    fun notifyThreadChanged(position: Int) {
        emailThreadAdapter.notifyItemChanged(position)
    }

}