package eu.kanade.tachiyomi.ui.source.filter

import android.view.View
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.viewholders.FlexibleViewHolder
import eu.kanade.tachiyomi.R
import eu.kanade.tachiyomi.source.model.Filter

open class CheckboxItem(
    val filter: Filter.CheckBox,
) : AbstractFlexibleItem<CheckboxItem.Holder>() {
    override fun getLayoutRes(): Int = R.layout.navigation_view_checkbox

    override fun createViewHolder(
        view: View,
        adapter: FlexibleAdapter<IFlexible<RecyclerView.ViewHolder>>,
    ): Holder = Holder(view, adapter)

    override fun bindViewHolder(
        adapter: FlexibleAdapter<IFlexible<RecyclerView.ViewHolder>>,
        holder: Holder,
        position: Int,
        payloads: MutableList<Any>,
    ) {
        val view = holder.check
        view.text = filter.name
        view.isChecked = filter.state
        holder.itemView.setOnClickListener {
            view.toggle()
            filter.state = view.isChecked
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        return filter == (other as CheckboxItem).filter
    }

    override fun hashCode(): Int = filter.hashCode()

    class Holder(
        view: View,
        adapter: FlexibleAdapter<IFlexible<RecyclerView.ViewHolder>>,
    ) : FlexibleViewHolder(view, adapter) {
        val check: CheckBox = itemView.findViewById(R.id.nav_view_item)
    }
}
