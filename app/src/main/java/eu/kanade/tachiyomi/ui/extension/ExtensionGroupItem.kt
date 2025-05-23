package eu.kanade.tachiyomi.ui.extension

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractHeaderItem
import eu.davidea.flexibleadapter.items.IFlexible
import eu.kanade.tachiyomi.R

/**
 * Item that contains the group header.
 *
 * @param name The header name.
 * @param size The number of items in the group.
 */
data class ExtensionGroupItem(
    val name: String,
    val size: Int,
    var canUpdate: Boolean? = null,
    var installedSorting: Int? = null,
) : AbstractHeaderItem<ExtensionGroupHolder>() {
    /**
     * Returns the layout resource of this item.
     */
    override fun getLayoutRes(): Int = R.layout.extension_card_header

    /**
     * Creates a new view holder for this item.
     */
    override fun createViewHolder(
        view: View,
        adapter: FlexibleAdapter<IFlexible<RecyclerView.ViewHolder>>,
    ): ExtensionGroupHolder = ExtensionGroupHolder(view, adapter)

    /**
     * Binds this item to the given view holder.
     */
    override fun bindViewHolder(
        adapter: FlexibleAdapter<IFlexible<RecyclerView.ViewHolder>>,
        holder: ExtensionGroupHolder,
        position: Int,
        payloads: MutableList<Any?>?,
    ) {
        holder.bind(this)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other is ExtensionGroupItem) {
            return name == other.name
        }
        return false
    }

    override fun hashCode(): Int = name.hashCode()
}
