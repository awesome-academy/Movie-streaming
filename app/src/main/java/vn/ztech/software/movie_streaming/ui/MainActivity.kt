package vn.ztech.software.movie_streaming.ui

import android.view.MenuItem
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewbinding.ViewBinding
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.navigation.NavigationBarView
import org.koin.androidx.viewmodel.ext.android.viewModel
import vn.ztech.software.movie_streaming.R
import vn.ztech.software.movie_streaming.base.BaseActivity
import vn.ztech.software.movie_streaming.base.BaseFragment
import vn.ztech.software.movie_streaming.databinding.ActivityMainBinding
import vn.ztech.software.movie_streaming.ui.home.HomeFragment
import vn.ztech.software.movie_streaming.ui.profile.ProfileFragment
import vn.ztech.software.movie_streaming.ui.search.SearchFragment
import vn.ztech.software.movie_streaming.utils.Constant
import vn.ztech.software.movie_streaming.utils.extensions.findByType
import vn.ztech.software.movie_streaming.utils.extensions.showAlertDialog
import kotlin.reflect.KClass

class MainActivity : BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate),
    NavigationBarView.OnItemSelectedListener {

    private val menuIds = listOf(R.id.menu_home, R.id.menu_search, R.id.menu_profile)

    override val viewModel: MainViewModel by viewModel()

    override fun initialize() {
        binding?.bottomNavigationView?.setOnItemSelectedListener(this)
        binding?.pager?.adapter = pagerAdapter
        /** Disable swipe behavior for view pager */
        binding?.pager?.isUserInputEnabled = false
    }

    private val pagerAdapter = PagerAdapter(this)

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        binding?.pager?.currentItem = menuIds.indexOf(item.itemId)
        return true
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            val clazz = pagerAdapter.getFragmentType(binding?.pager?.currentItem)
            (supportFragmentManager.fragments.findByType(clazz) as? BaseFragment<ViewBinding>)?.refreshData()
            super.onBackPressed()
        } else {
            this.showAlertDialog(
                R.string.dialog_title_quit_app,
                R.string.dialog_message_quit_app,
                onClickOkListener = { _, _ -> finish() }
            )
        }
    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    companion object {
        val listFragmentMenu = listOf<Class<*>>(
            HomeFragment::class.java,
            SearchFragment::class.java,
            ProfileFragment::class.java,
        )
    }

    inner class PagerAdapter(fragmentActivity: FragmentActivity) :
        FragmentStateAdapter(fragmentActivity) {
        override fun getItemCount(): Int {
            return menuIds.size
        }

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                Constant.ScreenNumber.Home.ordinal -> HomeFragment.newInstance()
                Constant.ScreenNumber.Search.ordinal -> SearchFragment.newInstance()
                Constant.ScreenNumber.Profile.ordinal -> ProfileFragment.newInstance()
                else -> HomeFragment.newInstance()
            }
        }

        fun getFragmentType(position: Int?): KClass<*> {
            return when (position) {
                Constant.ScreenNumber.Home.ordinal -> HomeFragment::class
                Constant.ScreenNumber.Search.ordinal -> SearchFragment::class
                Constant.ScreenNumber.Profile.ordinal -> ProfileFragment::class
                else -> HomeFragment::class
            }
        }
    }
}
