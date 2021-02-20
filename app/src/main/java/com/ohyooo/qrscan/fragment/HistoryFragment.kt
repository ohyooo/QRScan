package com.ohyooo.qrscan.fragment

import android.os.Bundle
import android.text.util.Linkify
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.size
import androidx.databinding.Observable
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import com.ohyooo.qrscan.databinding.FragmentHistoryBinding
import com.ohyooo.qrscan.dp

class HistoryFragment : BaseFragment<FragmentHistoryBinding>() {

    override val title = "History"

    private val vm by activityViewModels<ResultViewModel>()

    override val vdb by lazy { FragmentHistoryBinding.inflate(layoutInflater) }

    private val histories = ArrayList<String>(MAX_COUNT)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()

        // todo, move to activity
        vm.result.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                vm.result.get()?.let {
                    if (it != histories.lastOrNull()) {
                        add(it)
                    }
                }
            }
        })
    }

    private fun initViews() {
        vdb.delete.visibility = if (histories.isEmpty()) View.GONE else View.VISIBLE
        vdb.delete.setOnClickListener {
            vdb.delete.visibility = View.GONE
            histories.clear()
            vdb.history.adapter?.notifyDataSetChanged()
        }
        vdb.history.adapter = object : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
                val v = TextView(parent.context).apply {
                    setTextIsSelectable(true)
                    autoLinkMask = Linkify.ALL
                    setPadding(0, 2.dp, 0, 2.dp)
                }
                return object : RecyclerView.ViewHolder(v) {}
            }

            override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
                (holder.itemView as TextView).text = histories[position]
            }

            override fun getItemCount() = histories.size
        }
    }

    private fun add(s: String) {
        vdb.delete.visibility = View.VISIBLE
        if (histories.size > MAX_COUNT) {
            histories.removeFirst()
        }
        histories.add(s)
        vdb.history.adapter?.notifyItemInserted(vdb.history.size)
    }

    companion object {
        private const val MAX_COUNT = 15
    }
}