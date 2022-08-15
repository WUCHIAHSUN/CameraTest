package com.example.cameratest

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.google.firebase.analytics.FirebaseAnalytics

open class BaseFragment: Fragment(), ActivityListener.onBackPressedListener {
    val TAG: String = javaClass.getName()
    var mActivity: BaseActivity? = null
    var isProgress = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val activity: FragmentActivity? = getActivity()
        if (BaseActivity::class.java.isInstance(activity)) {
            mActivity = activity as BaseActivity
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (mActivity != null) {
            mActivity?.setBackPressedListener(this)
        }
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    fun gotoNextPage(fragment: BaseFragment) {
        gotoNextPage(fragment, false)
    }

    open fun gotoNextPage(fragment: BaseFragment, add: Boolean) {
        if (mActivity != null) {
            mActivity?.gotoFragment(fragment, add)
        }
    }

    fun gotoBackPage(fragment: BaseFragment) {
        gotoBackPage(fragment, true)
    }

    fun gotoBackPage(fragment: BaseFragment, clear: Boolean) {
        if (mActivity != null) {
            mActivity?.gotoFragment(fragment)
            if (clear) mActivity?.clearBackstack()
        } else {
            Log.d(TAG, "mActivity is null(gotoBackPage)")
        }
    }

    fun gotoBack() {
        if (mActivity != null) {
            mActivity?.goBackStack()
        } else {
            Log.d(TAG, "mActivity is null(gotoBackPage)")
        }
    }


    fun getBaseActivity(): BaseActivity? {
        if (mActivity == null) {
            Log.d(TAG, "mActivity is null(getBaseActivity)")
        }
        return mActivity
    }

    override fun onFragmentBackPressed(): Boolean {
        return if (isProgress) false else true
    }

    @SuppressLint("ClickableViewAccessibility")
    fun showProgressView() {
        if (mActivity != null) {
            isProgress = true
            mActivity?.mProgressBar?.visibility = View.VISIBLE
            mActivity?.mProgressBar?.setOnTouchListener { v, event -> true }
        }
    }

    fun dismissProgressView() {
        if (mActivity != null) {
            isProgress = false
            mActivity?.mProgressBar?.visibility = View.GONE
        }
    }

    open fun firebaseAnalyze(title: String, name: String) {
        val analytics = FirebaseAnalytics.getInstance(getBaseActivity()!!)
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, name)
        analytics.logEvent(title, bundle)
    }
}
