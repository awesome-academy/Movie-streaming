package vn.ztech.software.movie_streaming.ui.search

import org.koin.androidx.viewmodel.ext.android.viewModel
import vn.ztech.software.movie_streaming.base.BaseFragment
import vn.ztech.software.movie_streaming.databinding.FragmentSearchBinding


class SearchFragment() : BaseFragment<FragmentSearchBinding>(FragmentSearchBinding::inflate) {

    companion object {
        fun newInstance() = SearchFragment()
    }

    override val viewModel: SearchViewModel by viewModel()

    override fun initView() {
        //TODO  implement later
    }

    override fun initData() {
        //TODO  implement later
    }

    override fun bindData() {
        //TODO  implement later
    }

    override fun observeData() {
        //TODO  implement later
    }
}
