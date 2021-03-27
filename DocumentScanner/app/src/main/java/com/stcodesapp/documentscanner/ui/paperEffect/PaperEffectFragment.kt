package com.stcodesapp.documentscanner.ui.paperEffect

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import com.stcodesapp.documentscanner.R
import com.stcodesapp.documentscanner.base.BaseFragment
import com.stcodesapp.documentscanner.ui.helpers.ActivityNavigator
import kotlinx.android.synthetic.main.paper_effect_fragment.*
import javax.inject.Inject

class PaperEffectFragment : BaseFragment()
{
    @Inject lateinit var viewModel : PaperEffectViewModel
    @Inject lateinit var activityNavigator: ActivityNavigator

    companion object
    {
        fun newInstance() = PaperEffectFragment()
    }

    interface Listener
    {
        fun onTextColorSeekBarChanged(blockSize: Int, c : Int)

        fun onBackgroundSeekBarChanged(blockSize: Int, c : Int)
    }

    var listener : Listener? = null



    override fun onAttach(context: Context)
    {
        super.onAttach(context)
        activityComponent.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        return inflater.inflate(R.layout.paper_effect_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    private fun init()
    {
        initUserInteraction()
    }

    private fun initUserInteraction()
    {
        textColorSeekBar.setOnSeekBarChangeListener(seekBarListener)
        backgroundSeekBar.setOnSeekBarChangeListener(seekBarListener)
    }

    private val seekBarListener = object : SeekBar.OnSeekBarChangeListener{
        override fun onProgressChanged(seekBar: SeekBar?, p1: Int, p2: Boolean) {}

        override fun onStartTrackingTouch(seekBar: SeekBar?) {}

        override fun onStopTrackingTouch(seekBar: SeekBar?)
        {
            if(seekBar != null)
            {
                if(seekBar.id == textColorSeekBar.id) listener?.onTextColorSeekBarChanged(textColorSeekBar.progress, backgroundSeekBar.progress)
                else if(seekBar.id == backgroundSeekBar.id) listener?.onBackgroundSeekBarChanged(textColorSeekBar.progress, backgroundSeekBar.progress)
            }

        }

    }
}