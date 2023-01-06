package vn.ztech.software.movie_streaming.ui.home

import vn.ztech.software.movie_streaming.base.BaseFragment
import vn.ztech.software.movie_streaming.databinding.FragmentHomeBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import vn.ztech.software.movie_streaming.R
import vn.ztech.software.movie_streaming.utils.extensions.showAlertDialog


class HomeFragment() : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate) {

    val TAG = "HomeFragment"

    companion object {
        fun newInstance() = HomeFragment()
    }

    override val viewModel: HomeViewModel by viewModel()

    override fun initView() {
        //todo implement later
    }

    override fun initData() {
        viewModel.getTrendingMovies()
    }

    override fun bindData() {
        //todo implement later
    }

    override fun observeData() {
        viewModel.trendingMovies.observe(this){
            binding?.tv?.text = it.toString()
        }

        viewModel.error.observe(this){
            context?.showAlertDialog(
                getString(R.string.error_title),
                String.format(resources.getString(R.string.error_msg), it.message)
            )
        }
    }
}
