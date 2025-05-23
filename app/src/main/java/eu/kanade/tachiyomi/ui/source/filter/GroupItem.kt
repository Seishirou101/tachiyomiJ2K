package eu.kanade.tachiyomi.ui.source.filter

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractExpandableHeaderItem
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.flexibleadapter.items.ISectionable
import eu.davidea.viewholders.ExpandableViewHolder
import eu.kanade.tachiyomi.R
import eu.kanade.tachiyomi.source.model.Filter
import eu.kanade.tachiyomi.util.view.setAnimVectorCompat

class GroupItem(
    val filter: Filter.Group<*>,
) : AbstractExpandableHeaderItem<GroupItem.Holder, ISectionable<*, *>>() {
    init {
        isExpanded = false
    }

    override fun getLayoutRes(): Int = R.layout.navigation_view_group

    override fun getItemViewType(): Int = 101

    override fun createViewHolder(
        view: View,
        adapter: FlexibleAdapter<IFlexible<RecyclerView.ViewHolder>>,
    ): Holder = Holder(view, adapter)

    override fun bindViewHolder(
        adapter: FlexibleAdapter<IFlexible<RecyclerView.ViewHolder>>,
        holder: Holder,
        position: Int,
        payloads: MutableList<Any?>?,
    ) {
        holder.title.text = filter.name

        holder.icon.setAnimVectorCompat(
            if (isExpanded) {
                R.drawable.anim_expand_more_to_less
            } else {
                R.drawable.anim_expand_less_to_more
            },
        )

        holder.itemView.setOnClickListener(holder)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        return filter == (other as GroupItem).filter
    }

    override fun hashCode(): Int = filter.hashCode()

    open class Holder(
        view: View,
        adapter: FlexibleAdapter<IFlexible<RecyclerView.ViewHolder>>,
    ) : ExpandableViewHolder(view, adapter, true) {
        val title: TextView = itemView.findViewById(R.id.title)
        val icon: ImageView = itemView.findViewById(R.id.expand_icon)

        override fun shouldNotifyParentOnClick(): Boolean = true
    }
}
