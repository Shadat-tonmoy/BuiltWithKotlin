package com.stcodesapp.documentscanner.ui.imageEffect

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import com.stcodesapp.documentscanner.R
import com.stcodesapp.documentscanner.base.BaseFragment
import com.stcodesapp.documentscanner.ui.helpers.ActivityNavigator
import kotlinx.android.synthetic.main.image_effect_fragment.*
import javax.inject.Inject

class ImageEffectFragment : BaseFragment() {

    companion object {
        fun newInstance() = ImageEffectFragment()
        private const val TAG = "ImageEffectFragment"
    }

    @Inject lateinit var viewModel : ImageEffectViewModel
    @Inject lateinit var activityNavigator: ActivityNavigator

    interface Listener
    {
        fun onEffectValueChanged(brightness: Int, contrast : Float, hue : Int, saturtion : Int)
    }

    var listener : Listener? = null



    override fun onAttach(context: Context)
    {
        super.onAttach(context)
        activityComponent.inject(this)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        return inflater.inflate(R.layout.image_effect_fragment, container, false)
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
        brightnessSeekBar.setOnSeekBarChangeListener(seekBarListener)
        contrastSeekBar.setOnSeekBarChangeListener(seekBarListener)
    }

    private val seekBarListener = object : SeekBar.OnSeekBarChangeListener{
        override fun onProgressChanged(seekBar: SeekBar?, p1: Int, p2: Boolean) {}

        override fun onStartTrackingTouch(seekBar: SeekBar?) {}

        override fun onStopTrackingTouch(seekBar: SeekBar?)
        {
            if(seekBar != null)
            {
                var brightness = brightnessSeekBar.progress - 50
                if(brightness < 0) brightness *= 2
                val contrast = contrastSeekBar.progress - 50
                val finalContrastValue = 1 + (contrast.toFloat()/100)*2
                listener?.onEffectValueChanged(brightness, finalContrastValue, 2,3)

                Log.e(TAG, "onStopTrackingTouch: contrast : $finalContrastValue")
            }

        }

    }

}