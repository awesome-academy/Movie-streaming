package vn.ztech.software.movie_streaming.utils.extensions

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import vn.ztech.software.movie_streaming.R
import kotlin.reflect.KClass

fun Fragment.addFragment(fragment: Fragment) {
    activity?.supportFragmentManager?.let { fragmentManager ->
        fragmentManager.beginTransaction()
            .apply {
                add(R.id.frame_container, fragment)
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
                replace(R.id.frame_container, fragment)
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

inline fun <T : Any> List<Fragment>.findByType(clazz: KClass<T>): Fragment? {
    return this.firstOrNull { it::class == clazz }
}

