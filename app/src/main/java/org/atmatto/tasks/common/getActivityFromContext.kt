package org.atmatto.tasks.common

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper

tailrec fun Context.getActivity(): Activity? = when (this) {
	is Activity -> this
	is ContextWrapper -> baseContext.getActivity()
	else -> null
}
