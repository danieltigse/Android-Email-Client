package com.criptext.mail.utils.virtuallist

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.criptext.mail.R
import com.criptext.mail.utils.ui.EmptyViewHolder
import com.criptext.mail.utils.ui.ProgressViewHolder

/**
 * Base class for all RecyclerView adapters that are backed up by a Virtual list and want to
 * add more searchItems as soon as the user approaches the end of the list.
 *
 * When extending this class don't override onCreateViewHolder, use onCreateActualViewHolder
 * instead. Don't override getViewItemType, use getActualViewItemType. Don't override getItemId,
 * use getActualItemId instead.
 * Created by gabriel on 4/25/18.
 */
abstract class VirtualListAdapter(private val items: VirtualList<*>)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    init {
        setHasStableIds(true)
    }


    override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
        if (holder is ProgressViewHolder && !items.hasReachedEnd)
            onApproachingEnd()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            loadMoreViewType -> createLoadMoreViewHolder(parent)
            emptyViewType -> createEmptyViewHolder(parent)
            else -> onCreateActualViewHolder(parent, viewType)
        }
    }

    open fun createLoadMoreViewHolder(parent: ViewGroup): ProgressViewHolder {
        val view = LayoutInflater.from(parent.context)
                        .inflate(R.layout.progress_item, parent, false)
        return ProgressViewHolder(view)
    }

    open fun createEmptyViewHolder(parent: ViewGroup): EmptyViewHolder {
        val view = View(parent.context)
        view.isClickable = false
        return EmptyViewHolder(view)
    }

    override fun getItemViewType(position: Int): Int =
            if (items.size == 0 && items.hasReachedEnd) emptyViewType
            else if (position == items.size) loadMoreViewType
            else getActualItemViewType(position)

    override fun getItemCount(): Int = Math.max(1, items.size + if(items.hasReachedEnd) 0 else 1)

    abstract fun onCreateActualViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder

    abstract fun getActualItemViewType(position: Int): Int

    abstract fun onApproachingEnd()

    abstract fun getActualItemId(position: Int): Long

    override fun getItemId(position: Int): Long =
        if (items.size == 0 && items.hasReachedEnd) -2
        else if (position == items.size) -1
        else getActualItemId(position)

    companion object {
        private val loadMoreViewType = -1
        protected val emptyViewType = -2
    }
}