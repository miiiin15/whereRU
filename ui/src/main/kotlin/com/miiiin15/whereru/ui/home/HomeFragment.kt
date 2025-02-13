package com.miiiin15.whereru.ui.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.miiiin15.whereru.presentation.navigation.HomeNavigationTarget
import com.miiiin15.whereru.presentation.viewmodel.HomeViewModel
import com.miiiin15.whereru.ui.R
import com.miiiin15.whereru.ui.base.BaseFragment
import com.miiiin15.whereru.ui.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment :
    BaseFragment<FragmentHomeBinding, HomeViewModel, HomeViewModel.Event>(R.layout.fragment_home) {
    override val viewModel: HomeViewModel by viewModels()

    private val sessionListAdapter: SessionListAdapter by lazy {
        SessionListAdapter()
    }
    private val userListAdapter: UserListAdapter by lazy {
        UserListAdapter()
    }
    private val friendListAdapter: UserListAdapter by lazy {
        UserListAdapter()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 돌아 왔을 때 내 프로필 정보 조회
        findNavController().addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.homeFragment) {
                if (!viewModel.fetched.value!!) {
                    viewModel.fetchProfile()
                }
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    showCustomAlert("앱을 종료하시겠습니까?") {
                        requireActivity().finish()
                    }
                }
            }
        )
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding {
            vm = viewModel

            homeNavigateLocationButton.setOnClickListener {
                viewModel.checkSessionID()
            }
        }

        viewModel {
            myProfile observe { my ->
                if (my.nickname != null) {
                    binding.homeTitleText.apply {
                        text = my.nickname
                        visibility = View.VISIBLE
                        binding.homeTitleShimmer.visibility = View.GONE
                    }
                }
            }

            navigationTarget observe { target ->
                when (target) {
                    HomeNavigationTarget.ToLiveLocation -> {
                        val action = HomeFragmentDirections.actionHomeToLiveLocation()
                        findNavController().navigate(action)
                        viewModel.clearTrigger()
                    }

                    HomeNavigationTarget.ToSetting -> {}
                    HomeNavigationTarget.ToProfileEdit -> {}
                    null -> {}
                }
            }
        }

        setPagerView()
    }

    override fun handleEvent(event: HomeViewModel.Event) {
    }

    private fun setPagerView() {
        val homePagerView = binding.homePagerView
        val categoryTexts = listOf(
            binding.homeCategoryRecentText,
            binding.homeCategoryAllText,
            binding.homeCategoryFriendText
        )

        homePagerView.adapter = ViewPagerAdapter()
        categoryTexts.forEachIndexed { index, textView ->
            textView.setOnClickListener {
                homePagerView.currentItem = index
            }
        }

        homePagerView.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                categoryTexts.forEachIndexed { index, textView ->
                    textView.setTextColor(
                        if (index == position) resources.getColor(R.color.black)
                        else resources.getColor(R.color.gray1)
                    )
                }
            }
        })
    }

    private inner class ViewPagerAdapter : RecyclerView.Adapter<ViewPagerAdapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.scroll_session, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(position)
        }

        override fun getItemCount(): Int = 3

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            private val recyclerView: RecyclerView = view.findViewById(R.id.home_list_recycler)

            fun bind(position: Int) {
                recyclerView.layoutManager = LinearLayoutManager(itemView.context)
                when (position) {
                    0 -> {
                        recyclerView.adapter = sessionListAdapter
                        viewModel.sessionList.observe { data ->
                            sessionListAdapter.resetAll(data)
                        }
                    }

                    1 -> {
                        recyclerView.adapter = userListAdapter
                        viewModel.userList.observe { data ->
                            userListAdapter.resetAll(data)
                        }
                    }

                    2 -> {
                        recyclerView.adapter = friendListAdapter
                        viewModel.friendList.observe { data ->
                            friendListAdapter.resetAll(data)
                        }
                    }
                }
            }
        }
    }

}