package com.ohyooo.qrscan

import android.view.View
import android.view.ViewGroup


// dp to px
val Int.dp: Int get() = (this * App.INSTANCE.resources.displayMetrics.density).toInt()

val View.mlp get() = this.layoutParams as ViewGroup.MarginLayoutParams