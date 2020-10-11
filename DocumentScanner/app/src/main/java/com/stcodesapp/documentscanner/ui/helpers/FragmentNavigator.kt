package com.stcodesapp.documentscanner.ui.helpers

import android.view.Gravity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.transition.Slide
import com.stcodesapp.documentscanner.constants.Tags
import com.stcodesapp.documentscanner.ui.home.HomeFragment


class FragmentNavigator(private val fragmentFrameWrapper: FragmentFrameWrapper?,
                        private val fragmentManager: FragmentManager?) {

    private var homeFragment : HomeFragment? = null

    companion object{
        private const val TAG = "FragmentNavigator"
    }

    fun loadHomeFragment()
    {
        getCurrentFragment()?.let { if(it.tag == Tags.HOME_FRAGMENT) return }
        if(homeFragment == null) homeFragment = HomeFragment.newInstance()
        replaceFragmentAndClearBackStack(homeFragment!!,Tags.HOME_FRAGMENT)
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

    private fun getCurrentFragment(): Fragment?
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