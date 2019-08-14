package com.packapps.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.packapps.dto.ItemAudio
import com.packapps.R
import com.packapps.presenter.ListAudiosSeqFragmentPresente
import com.packapps.viewmodel.ListAudioSeqFragmentViewModel
import kotlinx.android.synthetic.main.fragment_list_audio_seq.view.*
import org.koin.android.architecture.ext.android.getViewModel
import org.koin.android.architecture.ext.android.viewModel
import org.koin.android.ext.android.get
import org.koin.android.ext.android.inject


class ListAudiosSequenceFragment : Fragment() {


    lateinit var viewModel : ListAudioSeqFragmentViewModel


    val presenter : ListAudiosSeqFragmentPresente by inject()
//    val viewModel : ListAudioSeqFragmentViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.let {
            presenter.setContexActivity(it)
        }

        viewModel = ViewModelProvider(this).get(ListAudioSeqFragmentViewModel::class.java)
        viewModel.repository = presenter.repository //Refactory it
        viewModel.composite = presenter.composite //Refactory it

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        val mView = inflater.inflate(R.layout.fragment_list_audio_seq, container, false)


        bindAdapterMain(mView)


        viewModel.pathAudioUnit.observe(viewLifecycleOwner, Observer {
            Toast.makeText(context, "Path: $it", Toast.LENGTH_LONG).show()
        })
        viewModel.getAudioUni("PauloLinhares://path")


        return mView
    }


    private fun bindAdapterMain(mView : View) {
        //create item empty
        val itemAudio = ItemAudio("My First List", "Audio aula 1", "")
        val itemAudio2 = ItemAudio("My First List", "Audio aula 1", "")
        val itemAudio3 = ItemAudio("My First List", "Audio aula 1", "")

        val list = mutableListOf<ItemAudio>()
        list.add(itemAudio)
        list.add(itemAudio2)
        list.add(itemAudio3)

        mView.rvItemsAudioSeq.layoutManager = presenter.layoutManager()
        mView.rvItemsAudioSeq.adapter = presenter.adapterMain()
        presenter.adapterMain().updateList(list)

    }

}
