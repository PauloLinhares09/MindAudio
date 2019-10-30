package com.packapps.ui.fragments

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

import com.packapps.R
import com.packapps.repository.database.AppDatabase
import com.packapps.repository.entity.ItemAudio
import kotlinx.android.synthetic.main.fragment_bottom_sheet_new_audio.*
import kotlinx.android.synthetic.main.fragment_bottom_sheet_new_audio.view.*
import org.koin.android.ext.android.inject


class BottomSheetNewAudioFragment : BottomSheetDialogFragment() {

    private var uri: Uri? = null

    private val appDatabase : AppDatabase by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val mView = inflater.inflate(R.layout.fragment_bottom_sheet_new_audio, container, false)

        uri?.let {
            mView.btSave.setOnClickListener {
                val name = mView.etName.text
                if (!name.isEmpty()){
                    val itemAudioDao = appDatabase.ItemAudioDao()
                    val id = itemAudioDao.insertItemAudio(ItemAudio(0, "default", name.toString(), uri.toString()))
                    Toast.makeText(context, "saved id: ${id}", Toast.LENGTH_SHORT).show()

                }else{
                    Toast.makeText(context, getString(R.string.name_empty), Toast.LENGTH_SHORT).show()
                }
            }

        }?:kotlin.run {
            Toast.makeText(context, getString(R.string.audio_unvailable), Toast.LENGTH_SHORT).show()
            dismiss()
        }

        return mView
    }

    fun setUri(uri: Uri) {
        this.uri = uri
    }
}
