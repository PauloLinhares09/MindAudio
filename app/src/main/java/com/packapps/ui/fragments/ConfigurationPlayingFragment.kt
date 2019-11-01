package com.packapps.ui.fragments

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders

import com.packapps.R
import com.packapps.repository.RepositoryLocal
import com.packapps.repository.entity.ItemAudio
import com.packapps.repository.entity.PlayConfig
import com.packapps.ui.viewmodel.ConfigurationViewModel
import kotlinx.android.synthetic.main.fragment_configuration_playing.*
import kotlinx.android.synthetic.main.fragment_configuration_playing.view.*
import org.koin.android.ext.android.inject

class ConfigurationPlayingFragment : DialogFragment() {

    private var howManyTimes: Int = 1
    private var itemAudio: ItemAudio? = null

    lateinit var viewModel : ConfigurationViewModel


    private val manyTimes = arrayOf(1, 2, 5, 10)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this).get(ConfigurationViewModel::class.java)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val mView = inflater.inflate(R.layout.fragment_configuration_playing, container, false)

        onClick(mView)

        //how much times
        buttonHowManyTimes(mView)

        return mView
    }

    private fun buttonHowManyTimes(mView: View) {
        mView.bt1x.isActivated = true
        mView.bt1x.setOnClickListener {
            mView.bt1x.isActivated = !bt1x.isActivated
            mView.bt2x.isActivated = false
            howManyTimes = 1
        }
        mView.bt2x.setOnClickListener {
            mView.bt1x.isActivated = false
            mView.bt2x.isActivated = !bt2x.isActivated

            howManyTimes = 2

        }
    }

    private fun onClick(mView: View) {
        mView.btCancel.setOnClickListener {
            itemAudio?.let {
                viewModel.savePlayConfig(PlayConfig(0, it.id, howManyTimes, 1))
            }

            dismiss()
        }
        mView.btApply.setOnClickListener {

            dismiss()
        }
    }

    fun setItemAudio(itemAudio: ItemAudio?) {
       this.itemAudio = itemAudio
    }

}
