package vn.ztech.software.movie_streaming.ui.profile

import android.content.Intent
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import org.koin.androidx.viewmodel.ext.android.viewModel
import vn.ztech.software.movie_streaming.R
import vn.ztech.software.movie_streaming.base.BaseFragment
import vn.ztech.software.movie_streaming.data.model.Media
import vn.ztech.software.movie_streaming.data.model.MediaDetailsWatchingHistory
import vn.ztech.software.movie_streaming.databinding.FragmentProfileBinding
import vn.ztech.software.movie_streaming.ui.auth.LoginActivity
import vn.ztech.software.movie_streaming.ui.home.ListMediaAdapter
import vn.ztech.software.movie_streaming.ui.home.ListMediaContinueWatchingAdapter
import vn.ztech.software.movie_streaming.ui.home.MyListViewModel
import vn.ztech.software.movie_streaming.ui.moviedetails.MediaDetailsFragment
import vn.ztech.software.movie_streaming.ui.player.MediaPlayerActivity
import vn.ztech.software.movie_streaming.ui.player.WatchingHistoryViewModel
import vn.ztech.software.movie_streaming.utils.extensions.loadImage
import vn.ztech.software.movie_streaming.utils.extensions.showAlertDialog


class ProfileFragment() : BaseFragment<FragmentProfileBinding>(FragmentProfileBinding::inflate) {

    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser

    override val viewModel: ProfileViewModel by viewModel()
    private val myListViewModel: MyListViewModel by viewModel()
    private val watchingHistoryViewModel: WatchingHistoryViewModel by viewModel()

    private val listMoviesContinueWatchingAdapter by lazy {
        ListMediaContinueWatchingAdapter(
            { media -> onButtonPlayClicked(media) },
            { media -> onMediaItemClick(media) }
        )
    }

    private val listMediaAdapterMyList by lazy {
        ListMediaAdapter { mediaMyList ->
            onMediaMyListClicked(mediaMyList)
        }
    }


    override fun initView() {
        binding?.apply {
            toolbar.title = user?.displayName
            toolbar.inflateMenu(R.menu.menu_profile)
            toolbar.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.menu_logout -> {
                        context?.showAlertDialog(
                            R.string.title_logout,
                            R.string.msg_logout,
                            onClickOkListener = { _, _ ->
                                logout()
                            }
                        )
                        true
                    }
                    else -> {
                        super.onOptionsItemSelected(menuItem)
                    }
                }
            }
            layoutProfile.apply {
                user?.let {
                    if (!it.displayName.isNullOrEmpty()) {
                        groupName.visibility = View.VISIBLE
                        textNameContent.text = user.displayName
                    }
                    if (!it.phoneNumber.isNullOrEmpty()) {
                        groupPhone.visibility = View.VISIBLE
                        textPhoneContent.text = user.phoneNumber
                    }
                    if (!it.email.isNullOrEmpty()) {
                        groupEmail.visibility = View.VISIBLE
                        textEmailContent.text = user.email
                    }
                    it.photoUrl?.let {
                        imageUserPhoto.visibility = View.VISIBLE
                        imageUserPhoto.loadImage(it.toString())
                    }
                }
            }
            layoutListMyList.apply {
                recyclerListMovies.adapter = listMediaAdapterMyList
                textTitle.text = "My list"
            }
            layoutListMoviesContinueWatching2.recyclerListMovies.adapter =
                listMoviesContinueWatchingAdapter
            layoutListMoviesContinueWatching2.textTitle.text = getString(R.string.continue_watching)
        }
    }

    override fun initData() {
        myListViewModel.getMyList()
        watchingHistoryViewModel.getListContinueWatching()
    }

    override fun bindData() {
        //TODO  implement later
    }

    override fun observeData() {
        myListViewModel.myList.observe(this) {
            listMediaAdapterMyList.submitData(it)
        }

        myListViewModel.loadingMyList.observe(this) {
            binding?.layoutListMyList?.layoutLoading?.constrainLoading?.isVisible = it
        }

        watchingHistoryViewModel.listContinueWatching.observe(this) {
            if (it.isNullOrEmpty()) {
                binding?.layoutListMoviesContinueWatching2?.constrainContainer?.visibility =
                    View.GONE
            } else {
                binding?.layoutListMoviesContinueWatching2?.constrainContainer?.visibility =
                    View.VISIBLE
                listMoviesContinueWatchingAdapter.submitData(it)
            }
        }

        watchingHistoryViewModel.loadingListContinueWatching.observe(this) {
            binding?.layoutListMoviesContinueWatching2?.layoutLoading?.constrainLoading?.isVisible =
                it
        }
    }

    private fun logout() {
        context?.let { context ->
            binding?.layoutLoading?.constrainLoading?.isVisible = true
            AuthUI.getInstance().signOut(context)
                .addOnSuccessListener {
                    binding?.layoutLoading?.constrainLoading?.isVisible = false
                    val intent = Intent(context, LoginActivity::class.java)
                    startActivity(intent)
                    activity?.finish()
                }
                .addOnFailureListener {
                    binding?.layoutLoading?.constrainLoading?.isVisible = false
                }
        }
    }

    private fun onMediaMyListClicked(media: Media) {
        when (media.getMediaClass()) {
            Media.OBJECT_TYPE_MOVIE -> {
                openFragment(MediaDetailsFragment.newInstance<Media.Movie>(media.toMovie()))
            }
            Media.OBJECT_TYPE_TV_SHOW -> {
                openFragment(MediaDetailsFragment.newInstance<Media.Show>(media.toShow()))
            }
        }
    }

    private fun onMediaItemClick(mediaDetailsWatchingHistory: MediaDetailsWatchingHistory) {
        val media = mediaDetailsWatchingHistory.toMedia()
        when (media.getMediaClass()) {
            Media.OBJECT_TYPE_MOVIE -> {
                openFragment(MediaDetailsFragment.newInstance<Media.Movie>(media))
            }
            Media.OBJECT_TYPE_TV_SHOW -> {
                openFragment(MediaDetailsFragment.newInstance<Media.Show>(media))
            }
        }
    }

    private fun onButtonPlayClicked(media: MediaDetailsWatchingHistory) {
        val intent = Intent(activity, MediaPlayerActivity::class.java)
        intent.putExtras(bundleOf(MediaPlayerActivity.BUNDLE_MEDIA_DETAILS_WATCHING_HISTORY to media))
        startActivity(intent)
    }

    override fun refreshData() {
        myListViewModel.getMyList(showLoading = false)
        watchingHistoryViewModel.getListContinueWatching(showLoading = false)
        super.refreshData()
    }

    override fun onResume() {
        super.onResume()
        myListViewModel.getMyList(false)
        watchingHistoryViewModel.getListContinueWatching(showLoading = false)
    }

    companion object {
        fun newInstance() = ProfileFragment()
    }
}
