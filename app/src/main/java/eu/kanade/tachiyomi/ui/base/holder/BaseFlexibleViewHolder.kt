package eu.kanade.tachiyomi.ui.base.holder

import android.view.View
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.viewholders.FlexibleViewHolder
import eu.kanade.tachiyomi.util.system.isLTR

abstract class BaseFlexibleViewHolder(
    view: View,
    adapter: FlexibleAdapter<*>,
    stickyHeader: Boolean = false,
) : FlexibleViewHolder(view, adapter, stickyHeader) {
    override fun getRearRightView(): View? = if (contentView.resources.isLTR) getRearEndView() else getRearStartView()

    override fun getRearLeftView(): View? = if (contentView.resources.isLTR) getRearStartView() else getRearEndView()

    open fun getRearStartView(): View? = null

    open fun getRearEndView(): View? = null
}
