package com.miiiin15.whereru.ui.home

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.activity.OnBackPressedCallback
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.miiiin15.whereru.presentation.fcm.FCMMessageHolder
import com.miiiin15.whereru.presentation.model.JoinedSessionUiModel
import com.miiiin15.whereru.presentation.model.UserUiModel
import com.miiiin15.whereru.presentation.navigation.HomeNavigationTarget
import com.miiiin15.whereru.presentation.viewmodel.HomeViewModel
import com.miiiin15.whereru.presentation.viewmodel.HomeViewModel.Event.*
import com.miiiin15.whereru.ui.R
import com.miiiin15.whereru.ui.base.BaseFragment
import com.miiiin15.whereru.ui.custom.SimpleMotionLayoutListener
import com.miiiin15.whereru.ui.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment :
    BaseFragment<FragmentHomeBinding, HomeViewModel, HomeViewModel.Event>(R.layout.fragment_home) {
    override val viewModel: HomeViewModel by viewModels()
    private var currentCategory: Category = Category.RECENT

    private var viewPagerAdapter: ViewPagerAdapter? = null

    private var isAdapterInitialized =
        mutableMapOf("sessionList" to false, "allUserList" to false, "friendList" to false)

    private val sessionListAdapter: SessionListAdapter by lazy {
        SessionListAdapter(object : OnSessionItemClickListener {
            override fun onSessionItemClick(session: JoinedSessionUiModel) =
                recentSessionClickAction(session)
        }, object : OnLoadMoreListener {
            override fun onLoadMore() {
                if (viewModel.hasMoreData["sessionList"] == true) {
                    viewModel.loadPaginatedSessionList(true)
                }
            }
        })
    }
    private val userListAdapter: UserListAdapter by lazy {
        UserListAdapter(object : OnUserItemClickListener {
            override fun onUserItemClick(user: UserUiModel) =
                allUserClickAction(user)
        }, object : OnLoadMoreListener {
            override fun onLoadMore() {
                if (viewModel.hasMoreData["userList"] == true) {
                    viewModel.loadPaginatedUserList(true)
                }
            }
        })
    }
    private val friendListAdapter: UserListAdapter by lazy {
        UserListAdapter(object : OnUserItemClickListener {
            override fun onUserItemClick(user: UserUiModel) {
                // TODO : 친구 목록 아이템 클릭 액션
            }
        }, object : OnLoadMoreListener {
            override fun onLoadMore() {
                // TODO : 친구 목록 더보기 액션
            }
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 화면 전환 정지 걸어 놓고 로그인 화면에서 로딩 시작한 것 처럼
        viewModel.showLoading("homeFragment")
        postponeEnterTransition()

        // 다른 화면 갔다가 돌아 왔을 때
        findNavController().addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.homeFragment) {
                if (!viewModel.profileFetched.value!!) {
                    viewModel.fetchProfile()
                    viewModel.loadPaginatedSessionList(false)
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

            homeTitleContainer.setOnClickListener {
                viewModel.event(Navigate(HomeNavigationTarget.ToProfileEdit))
            }

            homeLogoutButton.setOnClickListener {
                showCustomAlert("로그아웃 하시겠습니까?") { viewModel.logout() }
            }

            homeNavigateLocationButton.setOnClickListener {
                val sessionIdDisable = viewModel.myProfile.value?.sessionId.isNullOrBlank()
                showCustomBottomSheet(
                    "위치를 ${if (sessionIdDisable) "공유 하지 않고 있습니다." else "공유 중 입니다."}",
                    "세션 삭제",
                    "세션 ${if (sessionIdDisable) "생성" else "입장"}",
                    onLeftButtonClick = {
                        showDeleteSessionAlert()
                    },
                    onRightButtonClick = {
                        viewModel.checkSessionID()
                    }
                )
            }

            homeRefreshButton.setOnClickListener {
                binding.homeCategoryMotionLayout.transitionToEnd()
                when (currentCategory) {
                    Category.RECENT -> viewModel.loadPaginatedSessionList(false)
                    Category.ALL -> viewModel.loadPaginatedUserList(false)
                    Category.FRIEND -> {}
                }
            }

            homeCategoryMotionLayout.addTransitionListener(object : SimpleMotionLayoutListener() {
                override fun onTransitionCompleted(parent: MotionLayout, currentId: Int) {
                    homeCategoryMotionLayout.progress = 0f
                }
            }
            )
        }

        viewModel {
            myProfile observe { my ->
                if (my.nickname != null) {
                    binding.homeTitleContainer.visibility = View.VISIBLE
                    binding.homeTitleText.apply {
                        text = my.nickname
                        binding.homeTitleShimmer.visibility = View.GONE
                    }
                }
                if (!my.profileImageUrl.isNullOrBlank()) {
                    binding.homeProfileImage.backgroundTintList =
                        ColorStateList.valueOf(my.profileImageUrl!!.toInt())
                }
            }

            sessionList observe { data ->
                if (isAdapterInitialized["sessionList"] == true) {
                    sessionListAdapter.resetAll(data)

                    // 그려지면 전환
                    sessionListAdapter.recyclerView.doOnPreDraw {
                        viewModel.hideLoading("homeFragment")
                        startPostponedEnterTransition()
                    }
                }

                // 없어도 전환
                if (data.isEmpty()) {
                    viewModel.hideLoading("homeFragment")
                    startPostponedEnterTransition()
                }
            }
            userList observe { data ->
                if (isAdapterInitialized["allUserList"] == true) {
                    userListAdapter.resetAll(data)
                }
            }
            friendList observe { data ->
                if (isAdapterInitialized["friendList"] == true) {
                    friendListAdapter.resetAll(data)
                }
            }
        }

        setViewPager()
    }


    override fun onResume() {
        super.onResume()
        FCMMessageHolder.consume()?.let { message ->
            FcmActionHandler.handleFcmAction(
                context = requireContext(),
                message = message,
                showCustomBottomSheet = { title, leftButtonText, rightButtonText, onLeftButtonClick, onRightButtonClick ->
                    showCustomBottomSheet(
                        title,
                        leftButtonText,
                        rightButtonText,
                        onLeftButtonClick,
                        onRightButtonClick
                    )
                },
                showCustomAlert = { msg, onConfirm ->
                    showCustomAlert(msg, onConfirm)
                },
                sendResponsePushMessage = { msg, accepted ->
                    if (accepted) {
                        viewModel.checkSessionID {
                            viewModel.sendResponsePushMessage(msg, accepted)
                        }
                    } else {
                        viewModel.sendResponsePushMessage(msg, accepted)
                    }
                },
                participationSession = { sessionId, nickname ->
                    viewModel.participationSession(sessionId, nickname)
                }
            )
        }
    }


    // ViewPager2 + 상단 카테고리 연결 설정
    private fun setViewPager() {
        val homePagerView = binding.homePagerView
        val categoryTexts = Category.values().map { category ->
            when (category) {
                Category.RECENT -> binding.homeCategoryRecentText
                Category.ALL -> binding.homeCategoryAllText
                Category.FRIEND -> binding.homeCategoryFriendText
            }
        }

        categoryTexts.forEachIndexed { index, textView ->
            textView.setOnClickListener {
                homePagerView.currentItem = index
            }
        }

        homePagerView.apply {
            adapter = ViewPagerAdapter(this@HomeFragment).also { viewPagerAdapter = it }
            offscreenPageLimit = Category.values().size
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
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

    }

    // ViewPager2 Adapter 내부 바인딩 용
    fun bindViewPager(position: Int, recyclerView: RecyclerView, emptyView: LinearLayout) {
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        when (position) {
            0 -> {
                recyclerView.adapter = sessionListAdapter
                sessionListAdapter.setEmptyView(emptyView)
                isAdapterInitialized["sessionList"] = true
            }

            1 -> {
                recyclerView.adapter = userListAdapter
                userListAdapter.setEmptyView(emptyView)
                isAdapterInitialized["allUserList"] = true
            }

            2 -> {
                recyclerView.adapter = friendListAdapter
                friendListAdapter.setEmptyView(emptyView)
                isAdapterInitialized["friendList"] = true
            }
        }
    }


    private fun showDeleteSessionAlert() {
        viewModel.myProfile.value?.sessionId?.let {
            showCustomAlert("세션을 삭제 하시겠습니까?") {
                viewModel.deleteSession()
            }
        } ?: showCustomAlert("공유중인 세션이 없습니다.")
    }

    private fun recentSessionClickAction(session: JoinedSessionUiModel) {
        if (!viewModel.isSessionFetched() || !viewModel.isProfileFetched()) return

        showCustomBottomSheet(
            "${session.hostNickname}님의 세션\n최근 입장 시간 : ${session.participationTime}",
            "삭제",
            "재입장",
            onLeftButtonClick = {
                viewModel.exitSession(session.sessionId)
            },
            onRightButtonClick = {
                viewModel.participationSession(session.sessionId, session.hostNickname)
            }
        )
    }

    private fun allUserClickAction(user: UserUiModel) {
        if (!viewModel.isUserFetched() || !viewModel.isProfileFetched()) return

        val notOpenedSession = user.sessionId.isNullOrBlank()
        val rightButtonText = if (notOpenedSession) "공유 요청" else "세션 입장"
        val sessionStateText = if (notOpenedSession) {
            "위치 공유 세션 : Closed"
        } else {
            "위치 공유 세션 : Open"
        }

        showCustomBottomSheet(
            "${user.nickname}\n마지막 접속 : ${user.lastLoginAt}\n${sessionStateText}",
            "친구 추가",
            rightButtonText,
            onLeftButtonClick = {
                // TODO : 친구추가 기능 구현
            },
            onRightButtonClick = {
                if (notOpenedSession) {
                    showCustomAlert("위치 공유를 요청 하시겠습니까?") {
                        viewModel.sendRequestLocationPushMessage(user)
                    }
                } else {
                    viewModel.participationSession(user.sessionId!!, user.nickname!!)
                }
            }
        )
    }

    override fun handleEvent(event: HomeViewModel.Event) {
        when (event) {
            is Navigate -> {
                when (event.target) {
                    HomeNavigationTarget.ToLiveLocation -> {
                        val action = HomeFragmentDirections.actionHomeToLiveLocation()
                        findNavController().navigate(action)
                        viewModel.clearTrigger()
                    }

                    HomeNavigationTarget.ToSetting -> {}
                    HomeNavigationTarget.ToProfileEdit -> {
                        val action = HomeFragmentDirections.actionHomeToProfile()
                        findNavController().navigate(action)
                        viewModel.clearTrigger()
                    }

                    HomeNavigationTarget.Logout -> {
                        val intent = requireActivity().intent
                        requireActivity().finish()
                        requireActivity().startActivity(intent)
                    }

                    null -> {}
                }
            }

            is ShowAlert -> {
                showCustomAlert(event.message)
            }

            is ShowErrorAlert -> {
                showCustomAlert(event.throwable.message ?: "Unknown error") {
                    startPostponedEnterTransition()
                }
            }
        }
    }

    enum class Category {
        RECENT, ALL, FRIEND
    }

}

