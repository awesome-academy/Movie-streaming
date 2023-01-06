package vn.ztech.software.movie_streaming.ui.profile

import org.koin.androidx.viewmodel.ext.android.viewModel
import vn.ztech.software.movie_streaming.base.BaseFragment
import vn.ztech.software.movie_streaming.databinding.FragmentProfileBinding


class ProfileFragment() : BaseFragment<FragmentProfileBinding>(FragmentProfileBinding::inflate) {

    companion object {
        fun newInstance() = ProfileFragment()
    }

    override val viewModel: ProfileViewModel by viewModel()

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
