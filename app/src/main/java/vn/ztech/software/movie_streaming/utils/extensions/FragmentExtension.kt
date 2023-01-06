package vn.ztech.software.movie_streaming.utils.extensions

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import vn.ztech.software.movie_streaming.R

fun Fragment.addFragment(fragment: Fragment) {
    activity?.supportFragmentManager?.let { fragmentManager ->
        fragmentManager.beginTransaction()
            .apply {
                add(R.id.container, fragment)
                addToBackStack(fragment::class.java.simpleName)
                commit()
            }
    }
}

fun Fragment.replaceFragment(fragment: Fragment) {
    activity?.supportFragmentManager?.let { fragmentManager ->
        fragmentManager.beginTransaction()
            .apply {
                setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                replace(R.id.container, fragment)
                addToBackStack(null)
                commit()
            }
    }
}

fun Fragment.popFragment(fragment: Fragment) {
    activity?.supportFragmentManager?.let { fragmentManager ->
        fragmentManager.beginTransaction()
            .apply {
                remove(fragment)
                commit()
                fragmentManager.popBackStack()
            }
    }
}
