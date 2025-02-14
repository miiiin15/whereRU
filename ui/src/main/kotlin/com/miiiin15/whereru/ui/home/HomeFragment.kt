package com.miiiin15.whereru.ui.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
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
    private var currentCategory: Category = Category.RECENT

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


    // ViewPager2 + 상단 카테고리 연결 설정
    private fun setPagerView() {
        val homePagerView = binding.homePagerView
        val categoryTexts = Category.values().map { category ->
            when (category) {
                Category.RECENT -> binding.homeCategoryRecentText
                Category.ALL -> binding.homeCategoryAllText
                Category.FRIEND -> binding.homeCategoryFriendText
            }
        }

        homePagerView.adapter = ViewPagerAdapter(this)
        categoryTexts.forEachIndexed { index, textView ->
            textView.setOnClickListener {
                homePagerView.currentItem = index
            }
        }



        homePagerView.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                currentCategory = Category.values()[position]
                categoryTexts.forEachIndexed { index, textView ->
                    textView.setTextColor(
                        if (index == position) resources.getColor(R.color.black)
                        else resources.getColor(R.color.gray1)
                    )
                }
            }
        })
    }

    // ViewPager2 Adapter 바인딩 용
    fun bind(position: Int, recyclerView: RecyclerView, emptyView: LinearLayout) {
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        val lifecycleOwner = viewLifecycleOwner
        when (position) {
            0 -> {
                recyclerView.adapter = sessionListAdapter
                viewModel.sessionList.observe { data ->
                    checkDataIsNullOrEmpty(data, recyclerView, emptyView)
                    sessionListAdapter.resetAll(data)
                }
            }

            1 -> {
                recyclerView.adapter = userListAdapter
                viewModel.userList.observe { data ->
                    checkDataIsNullOrEmpty(data, recyclerView, emptyView)
                    userListAdapter.resetAll(data)
                }
            }

            2 -> {
                recyclerView.adapter = friendListAdapter
                viewModel.friendList.observe { data ->
                    checkDataIsNullOrEmpty(data, recyclerView, emptyView)
                    friendListAdapter.resetAll(data)
                }
            }
        }
    }

    // RecyclerView 데이터가 없을 때 처리
    private fun checkDataIsNullOrEmpty(
        data: List<Any>?,
        recyclerView: RecyclerView,
        emptyView: LinearLayout
    ) {
        if (data.isNullOrEmpty()) {
            recyclerView.visibility = View.GONE
            emptyView.visibility = View.VISIBLE
        } else {
            recyclerView.visibility = View.VISIBLE
            emptyView.visibility = View.GONE
        }
    }

    override fun handleEvent(event: HomeViewModel.Event) {
    }

    enum class Category {
        RECENT, ALL, FRIEND
    }

}

