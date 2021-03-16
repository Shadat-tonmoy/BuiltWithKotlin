package com.stcodesapp.documentscanner.ui.helpers

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.transition.Slide
import com.stcodesapp.documentscanner.constants.Tags
import com.stcodesapp.documentscanner.database.entities.Image
import com.stcodesapp.documentscanner.ui.filterOption.FilterOptionFragment
import com.stcodesapp.documentscanner.ui.home.HomeFragment
import com.stcodesapp.documentscanner.ui.imageEffect.ImageEffectFragment
import com.stcodesapp.documentscanner.ui.more.MoreFragment
import com.stcodesapp.documentscanner.ui.paperEffect.PaperEffectFragment
import com.stcodesapp.documentscanner.ui.savedFiles.SavedFilesFragment


class FragmentNavigator(private val fragmentFrameWrapper: FragmentFrameWrapper?,
                        private val fragmentManager: FragmentManager?) {

    private var homeFragment : HomeFragment? = null
    private var savedFilesFragment : SavedFilesFragment? = null
    private var moreFragment : MoreFragment? = null

    companion object{
        private const val TAG = "FragmentNavigator"
    }

    fun loadHomeFragment()
    {
        getCurrentFragment()?.let { if(it.tag == Tags.HOME_FRAGMENT) return }
        if(homeFragment == null) homeFragment = HomeFragment.newInstance()
        replaceFragmentAndClearBackStack(homeFragment!!,Tags.HOME_FRAGMENT)
    }

    fun loadSavedFilesFragment()
    {
        getCurrentFragment()?.let { if(it.tag == Tags.SAVED_FILES_FRAGMENT) return }
        if(savedFilesFragment == null) savedFilesFragment = SavedFilesFragment.newInstance()
        replaceFragment(savedFilesFragment!!,Tags.SAVED_FILES_FRAGMENT)
    }

    fun loadMoreFragment()
    {
        getCurrentFragment()?.let { if(it.tag == Tags.MORE_FRAGMENT) return }
        if(moreFragment == null) moreFragment = MoreFragment.newInstance()
        replaceFragment(moreFragment!!,Tags.MORE_FRAGMENT)
    }

    fun loadFilterFragment(chosenImage : Image, imagePosition : Int)
    {
        getCurrentFragment()?.let { if(it.tag == Tags.FILTER_OPTION_FRAGMENT)
            fragmentManager?.popBackStack()
            return
        }
        addFragment(FilterOptionFragment.newInstance(chosenImage,imagePosition),true, false, Tags.FILTER_OPTION_FRAGMENT)
    }

    fun loadPaperEffectFragment(listener : PaperEffectFragment.Listener)
    {
        getCurrentFragment()?.let { if(it.tag == Tags.PAPER_EFFECT_FRAGMENT)
            fragmentManager?.popBackStack()
            return
        }
        val fragment = PaperEffectFragment.newInstance()
        fragment.listener = listener
        addFragment(fragment,true, false, Tags.PAPER_EFFECT_FRAGMENT)
    }

    fun loadImageEffectFragment(listener : ImageEffectFragment.Listener)
    {
        getCurrentFragment()?.let { if(it.tag == Tags.IMAGE_EFFECT_FRAGMENT)
            fragmentManager?.popBackStack()
            return
        }
        val fragment = ImageEffectFragment.newInstance()
        fragment.listener = listener
        addFragment(fragment,true, false, Tags.IMAGE_EFFECT_FRAGMENT)
    }

    fun isHomeFragmentLoaded() : Boolean
    {
        getCurrentFragment()?.let { if(it.tag == Tags.HOME_FRAGMENT) return true}
        return false
    }

    fun isSavedFilesFragmentLoaded() : Boolean
    {
        getCurrentFragment()?.let { if(it.tag == Tags.SAVED_FILES_FRAGMENT) return true}
        return false
    }

    fun isMoreFragmentLoaded() : Boolean
    {
        getCurrentFragment()?.let { if(it.tag == Tags.MORE_FRAGMENT) return true}
        return false
    }

    private fun replaceFragment(newFragment: Fragment, tag: String)
    {
        replaceFragment(newFragment, true, false, tag)
    }

    private fun replaceFragmentAndClearBackStack(newFragment: Fragment, tag: String) {
        replaceFragment(newFragment, false, true, tag)
    }

    private fun addFragment(newFragment: Fragment, tag: String)
    {
        addFragment(newFragment, true, true, tag)
    }

    fun getCurrentFragment(): Fragment?
    {
        return fragmentManager!!.findFragmentById(getFragmentFrameId())
    }



    private fun replaceFragment(newFragment: Fragment, addToBackStack: Boolean, clearBackStack: Boolean, tag: String)
    {
        if (getCurrentFragment() != null && tag == getCurrentFragment()!!.tag) return
        if (clearBackStack)
        {
            if (fragmentManager!!.isStateSaved)
            {
                // If the state is saved we can't clear the back stack. Simply not doing this, but
                // still replacing fragment is a bad idea. Therefore we abort the entire operation.
                return
            }
            // Remove all entries from back stack
            fragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        }
        val fragmentTransaction = fragmentManager!!.beginTransaction()
        if (addToBackStack) fragmentTransaction.addToBackStack(tag)

        // Change to a new fragment
        fragmentTransaction.replace(getFragmentFrameId(), newFragment, tag)
        if (fragmentManager.isStateSaved)
        {
            // We acknowledge the possibility of losing this transaction if the app undergoes
            // save&restore flow after it is committed.
            fragmentTransaction.commitAllowingStateLoss()
        }
        else
        {
            fragmentTransaction.commit()
        }
    }

    private fun replaceFragmentInsideContainer(newFragment: Fragment, addToBackStack: Boolean, showAnimation: Boolean,tag: String, containerID:Int)
    {
        val fragmentTransaction = fragmentManager!!.beginTransaction()
        for (fragment in fragmentManager.fragments)
            if(fragment.tag==tag) fragmentManager.beginTransaction().remove(fragment).commit()

        if (addToBackStack) fragmentTransaction.addToBackStack(tag)
        if (showAnimation){ newFragment.enterTransition = Slide(Gravity.BOTTOM) }
        // Change to a new fragment
        fragmentTransaction.replace(containerID, newFragment, tag)
        if (fragmentManager.isStateSaved)
        {
            // We acknowledge the possibility of losing this transaction if the app undergoes
            // save&restore flow after it is committed.
            fragmentTransaction.commitAllowingStateLoss()
        }
        else
        {
            fragmentTransaction.commit()
        }
    }

    private fun addFragment(newFragment: Fragment, addToBackStack: Boolean, showAnimation: Boolean, tag: String)
    {
        if (getCurrentFragment() != null && tag == getCurrentFragment()!!.tag) return

        val fragmentTransaction = fragmentManager!!.beginTransaction()
        if (addToBackStack) fragmentTransaction.addToBackStack(tag)
        if (showAnimation){ newFragment.enterTransition = Slide(Gravity.BOTTOM)
            newFragment.exitTransition = Slide(Gravity.TOP) }
        // Change to a new fragment
        fragmentTransaction.add(getFragmentFrameId(), newFragment, tag)
        if (fragmentManager.isStateSaved)
        {
            // We acknowledge the possibility of losing this transaction if the app undergoes
            // save&restore flow after it is committed.
            fragmentTransaction.commitAllowingStateLoss()
        }
        else
        {
            fragmentTransaction.commit()
        }
    }

    private fun getFragmentFrameId(): Int {
        return fragmentFrameWrapper!!.getFragmentFrame()!!.id
    }
}