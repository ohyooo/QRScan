package com.ohyooo.qrscan.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding

abstract class BaseFragment<T : ViewBinding> : Fragment() {
    abstract val title: String
    abstract val vdb: T?

    var container: ViewGroup? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        this.container = container
        return vdb?.root.also {
            this.container = null
        }
    }
}