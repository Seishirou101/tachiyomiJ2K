package eu.kanade.tachiyomi.ui.setting

import android.app.Activity
import androidx.annotation.StringRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.biometric.BiometricPrompt
import androidx.fragment.app.FragmentActivity
import androidx.preference.CheckBoxPreference
import androidx.preference.DialogPreference
import androidx.preference.DropDownPreference
import androidx.preference.EditTextPreference
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceCategory
import androidx.preference.PreferenceGroup
import androidx.preference.PreferenceManager
import androidx.preference.PreferenceScreen
import androidx.preference.SwitchPreferenceCompat
import eu.kanade.tachiyomi.R
import eu.kanade.tachiyomi.util.system.AuthenticatorUtil
import eu.kanade.tachiyomi.util.system.AuthenticatorUtil.isAuthenticationSupported
import eu.kanade.tachiyomi.util.system.AuthenticatorUtil.startAuthentication
import eu.kanade.tachiyomi.util.system.getResourceColor
import eu.kanade.tachiyomi.util.system.toast
import eu.kanade.tachiyomi.widget.preference.AdaptiveTitlePreferenceCategory
import eu.kanade.tachiyomi.widget.preference.EditTextResetPreference
import eu.kanade.tachiyomi.widget.preference.IntListMatPreference
import eu.kanade.tachiyomi.widget.preference.ListMatPreference
import eu.kanade.tachiyomi.widget.preference.MultiListMatPreference
import eu.kanade.tachiyomi.widget.preference.TriStateListPreference
import com.fredporciuncula.flow.preferences.Preference as FlowPreference

@DslMarker
@Target(AnnotationTarget.TYPE)
annotation class DSL

inline fun PreferenceManager.newScreen(block: (@DSL PreferenceScreen).() -> Unit): PreferenceScreen =
    createPreferenceScreen(context).also {
        it.block()
    }

inline fun PreferenceGroup.preference(block: (@DSL Preference).() -> Unit): Preference = initThenAdd(Preference(context), block)

inline fun PreferenceGroup.themePreference(block: (@DSL ThemePreference).() -> Unit): ThemePreference =
    initThenAdd(ThemePreference(context), block)

inline fun PreferenceGroup.switchPreference(block: (@DSL SwitchPreferenceCompat).() -> Unit): SwitchPreferenceCompat =
    initThenAdd(SwitchPreferenceCompat(context), block)

inline fun PreferenceGroup.checkBoxPreference(block: (@DSL CheckBoxPreference).() -> Unit): CheckBoxPreference =
    initThenAdd(CheckBoxPreference(context), block)

inline fun PreferenceGroup.editTextPreference(
    activity: Activity?,
    block: (@DSL EditTextResetPreference).() -> Unit,
): EditTextResetPreference {
    return initThenAdd(EditTextResetPreference(activity, context), block) // .also(::initDialog)
}

inline fun PreferenceGroup.dropDownPreference(block: (@DSL DropDownPreference).() -> Unit): DropDownPreference =
    initThenAdd(DropDownPreference(context), block).also(::initDialog)

inline fun PreferenceGroup.listPreference(
    activity: Activity?,
    block: (@DSL ListMatPreference).()
    -> Unit,
): ListMatPreference = initThenAdd(ListMatPreference(activity, context), block)

inline fun PreferenceGroup.intListPreference(
    activity: Activity?,
    block: (@DSL IntListMatPreference).() -> Unit,
): IntListMatPreference = initThenAdd(IntListMatPreference(activity, context), block)

inline fun PreferenceGroup.multiSelectListPreferenceMat(
    activity: Activity?,
    block: (@DSL MultiListMatPreference).() -> Unit,
): MultiListMatPreference = initThenAdd(MultiListMatPreference(activity, context), block)

inline fun PreferenceGroup.triStateListPreference(
    activity: Activity?,
    block: (@DSL TriStateListPreference).() -> Unit,
): TriStateListPreference = initThenAdd(TriStateListPreference(activity, context), block)

inline fun PreferenceScreen.preferenceCategory(block: (@DSL PreferenceCategory).() -> Unit): PreferenceCategory =
    addThenInit(AdaptiveTitlePreferenceCategory(context), block)

inline fun PreferenceScreen.switchPreference(block: (@DSL SwitchPreferenceCompat).() -> Unit): SwitchPreferenceCompat =
    initThenAdd(SwitchPreferenceCompat(context), block)

fun PreferenceGroup.infoPreference(
    @StringRes infoRes: Int,
): Preference =
    initThenAdd(
        Preference(context),
    ) {
        iconRes = R.drawable.ic_info_outline_24dp
        iconTint = context.getResourceColor(android.R.attr.textColorSecondary)
        summaryRes = infoRes
        isSelectable = false
    }

inline fun PreferenceScreen.preferenceScreen(block: (@DSL PreferenceScreen).() -> Unit): PreferenceScreen =
    addThenInit(preferenceManager.createPreferenceScreen(context), block)

fun initDialog(dialogPreference: DialogPreference) {
    with(dialogPreference) {
        if (dialogTitle == null) {
            dialogTitle = title
        }
    }
}

fun <P : Preference> PreferenceGroup.add(p: P): P =
    p.apply {
        this.isIconSpaceReserved = false
        this.isSingleLineTitle = false
        addPreference(this)
    }

inline fun <P : Preference> PreferenceGroup.initThenAdd(
    p: P,
    block: P.() -> Unit,
): P =
    p.apply {
        this.isIconSpaceReserved = false
        this.isSingleLineTitle = false
        block()
        addPreference(this)
    }

inline fun <P : Preference> PreferenceGroup.addThenInit(
    p: P,
    block: P.() -> Unit,
): P =
    p.apply {
        this.isIconSpaceReserved = false
        this.isSingleLineTitle = false
        addPreference(this)
        block()
    }

inline fun Preference.onClick(crossinline block: () -> Unit) {
    setOnPreferenceClickListener {
        block()
        true
    }
}

inline fun Preference.onChange(crossinline block: (Any?) -> Boolean) {
    setOnPreferenceChangeListener { _, newValue -> block(newValue) }
}

fun <T> Preference.bindTo(preference: FlowPreference<T>) {
    key = preference.key
    defaultValue = preference.defaultValue
}

fun <T> ListPreference.bindTo(preference: FlowPreference<T>) {
    key = preference.key
    defaultValue = preference.defaultValue.toString()
}

fun <T> EditTextPreference.bindTo(preference: FlowPreference<T>) {
    key = preference.key
    defaultValue = preference.defaultValue.toString()
}

fun <T> ListMatPreference.bindTo(preference: FlowPreference<T>) {
    key = preference.key
    val defValue = preference.defaultValue
    defaultValue = if (defValue is Set<*>) defValue else defValue.toString()
}

@Deprecated(
    "Do not bind tri-states prefs with a single preference",
    ReplaceWith("bindTo(preference, excludePreference = )"),
    DeprecationLevel.ERROR,
)
fun <T> TriStateListPreference.bindTo(preference: FlowPreference<T>) {
    key = preference.key
}

fun TriStateListPreference.bindTo(
    includePreference: FlowPreference<Set<String>>,
    excludePreference: FlowPreference<Set<String>>,
) {
    key = includePreference.key
    excludeKey = excludePreference.key
    defaultValue = includePreference.defaultValue to excludePreference.defaultValue
}

fun <T> IntListMatPreference.bindTo(preference: FlowPreference<T>) {
    key = preference.key
    defaultValue = preference.defaultValue
}

fun SwitchPreferenceCompat.requireAuthentication(
    activity: FragmentActivity?,
    title: String,
    subtitle: String? = null,
    confirmationRequired: Boolean = true,
) {
    onPreferenceChangeListener =
        Preference.OnPreferenceChangeListener { _, newValue ->
            newValue as Boolean
            if (activity != null && context.isAuthenticationSupported()) {
                activity.startAuthentication(
                    title,
                    subtitle,
                    confirmationRequired,
                    callback =
                        object : AuthenticatorUtil.AuthenticationCallback() {
                            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                                super.onAuthenticationSucceeded(result)
                                isChecked = newValue
                            }

                            override fun onAuthenticationError(
                                errorCode: Int,
                                errString: CharSequence,
                            ) {
                                super.onAuthenticationError(errorCode, errString)
                                activity.toast(errString.toString())
                            }
                        },
                )
                false
            } else {
                true
            }
        }
}

var Preference.defaultValue: Any?
    get() = null // set only
    set(value) {
        setDefaultValue(value)
    }

var Preference.titleRes: Int
    get() = 0 // set only
    set(value) {
        setTitle(value)
    }

var Preference.iconRes: Int
    get() = 0 // set only
    set(value) {
        icon = AppCompatResources.getDrawable(context, value)
    }

var Preference.summaryRes: Int
    get() = 0 // set only
    set(value) {
        setSummary(value)
    }

var Preference.iconTint: Int
    get() = 0 // set only
    set(value) {
        icon?.setTint(value)
    }
