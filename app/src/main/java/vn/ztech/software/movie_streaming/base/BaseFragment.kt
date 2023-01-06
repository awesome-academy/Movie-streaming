package vn.ztech.software.movie_streaming.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.bumptech.glide.manager.SupportRequestManagerFragment
import vn.ztech.software.movie_streaming.ui.MainActivity
import vn.ztech.software.movie_streaming.ui.home.HomeFragment
import vn.ztech.software.movie_streaming.ui.profile.ProfileFragment
import vn.ztech.software.movie_streaming.ui.search.SearchFragment
import vn.ztech.software.movie_streaming.utils.extensions.addFragment

typealias FragmentInflate<T> = (LayoutInflater, ViewGroup?, Boolean) -> T

abstract class BaseFragment<VB : ViewBinding>(private val inflate: FragmentInflate<VB>) :
    Fragment() {

    private var _binding: VB? = null
    val binding get() = _binding

    abstract val viewModel: BaseViewModel

    abstract fun initView()

    abstract fun initData()

    abstract fun observeData()

    abstract fun bindData()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = inflate.invoke(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initData()
        observeData()
        bindData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun hideSoftInput(context: Context, v: View) {
        v.clearFocus()
        val inputManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(v.windowToken, 0)
    }

    fun openFragment(fragment: Fragment) {
        this.addFragment(
            fragment
        )
    }

    fun moveToTab(@IdRes menuId: Int) {
        clearFragments()
        (activity as MainActivity?)?.binding?.bottomNavigationView?.selectedItemId =
            menuId
    }

    private fun clearFragments() {
        activity?.supportFragmentManager?.fragments?.let {

            for (fragment in it) {
                if (!(fragment::class.java in fragmentsShouldNotClear)) {
                    activity?.supportFragmentManager?.beginTransaction()?.remove(fragment)?.commit()
                }
            }
        }

    }


    companion object {
        val fragmentsShouldNotClear = listOf<Class<*>>(
            HomeFragment::class.java,
            SearchFragment::class.java,
            ProfileFragment::class.java,
            SupportRequestManagerFragment::class.java
        )
    }

}
