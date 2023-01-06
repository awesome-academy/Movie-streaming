package vn.ztech.software.movie_streaming.ui.profile

import vn.ztech.software.movie_streaming.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.viewModel
import vn.ztech.software.movie_streaming.databinding.FragmentProfileBinding


class ProfileFragment() : BaseFragment<FragmentProfileBinding>(FragmentProfileBinding::inflate) {

    companion object {
        fun newInstance() = ProfileFragment()
    }

    override val viewModel: ProfileViewModel by viewModel()

    override fun initView() {
        //todo implement later
    }

    override fun initData() {
        //todo implement later
    }

    override fun bindData() {
        //todo implement later
    }

    override fun observeData() {
        //todo implement later
    }
}
