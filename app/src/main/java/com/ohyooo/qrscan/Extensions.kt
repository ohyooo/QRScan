package com.ohyooo.qrscan

// dp to px
val Int.dp: Int get() = (this * App.INSTANCE.resources.displayMetrics.density).toInt()
