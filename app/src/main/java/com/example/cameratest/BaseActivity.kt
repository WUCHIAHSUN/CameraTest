package com.example.cameratest

import android.os.Bundle
import android.widget.FrameLayout
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager

open class BaseActivity: AppCompatActivity(), FragmentManager.OnBackStackChangedListener {

    private var mFragmentManager: FragmentManager? = null
    var mProgressBar: ProgressBar? = null
    private var baseFragment: FrameLayout? = null
    private var bFragmentAdd = false
    private var mBackListener: ActivityListener.onBackPressedListener? = null

    companion object{
        var pageFragment: BaseFragment? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.base_activity)
        mFragmentManager = supportFragmentManager
        baseFragment = findViewById(R.id.basefragment)
        mProgressBar = findViewById(R.id.progress)
        mFragmentManager!!.addOnBackStackChangedListener(this)

    }


    fun init(fragment: BaseFragment) {
        try {
            if (mFragmentManager != null && mFragmentManager!!.backStackEntryCount > 0) return
            if (actionBar != null) {
                actionBar!!.hide()
            }
            supportFragmentManager.beginTransaction().add(R.id.basefragment, fragment)
                .commitAllowingStateLoss()
            pageFragment = fragment
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun gotoFragment(fragment: BaseFragment) {
        gotoFragment(fragment, false)
    }

    fun gotoFragment(fragment: BaseFragment, add: Boolean) {
        pageFragment = fragment
        bFragmentAdd = add
        val fragmentTransaction = mFragmentManager!!.beginTransaction()
        try {
            fragmentTransaction.setCustomAnimations(R.anim.in_left_page, R.anim.out_left_page, R.anim.out_right_page, R.anim.in_right_page)
            if (add) {
                if (!fragment.isAdded()) {
                    fragmentTransaction.add(R.id.basefragment, fragment)
                } else {
                    return
                }
            } else {
                fragmentTransaction.replace(R.id.basefragment, fragment)
            }
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commitAllowingStateLoss()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    fun clearBackstack() {
        if (mFragmentManager == null) return
        if (mFragmentManager!!.backStackEntryCount <= 0) return
        val entry = mFragmentManager!!.getBackStackEntryAt(0)
        mFragmentManager!!.popBackStack(
            entry.id,
            FragmentManager.POP_BACK_STACK_INCLUSIVE
        )
        mFragmentManager!!.executePendingTransactions()
    }

    fun setBackPressedListener(listener: ActivityListener.onBackPressedListener) {
        mBackListener = listener
    }

    override fun onBackStackChanged() {
        if (bFragmentAdd) {
            val currentFragment = supportFragmentManager.findFragmentById(R.id.basefragment)
            if (currentFragment != pageFragment) {
                currentFragment!!.onResume()
            }
        }
    }

    fun goBackStack() {
        mFragmentManager!!.popBackStack()
    }

    fun getBaseFragment(): FrameLayout? {
        return baseFragment
    }

    override fun onBackPressed() {
        if (mBackListener != null) {
            if (mBackListener!!.onFragmentBackPressed()) {
                try {
                    super.onBackPressed()
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            } else {
                return
            }
        } else {
            super.onBackPressed()
        }
    }
}