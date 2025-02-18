package com.miiiin15.whereru.ui.home

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.miiiin15.whereru.presentation.fcm.FCMMessageHolder
import com.miiiin15.whereru.presentation.model.JoinedSessionUiModel
import com.miiiin15.whereru.presentation.model.PushMessageUiModel
import com.miiiin15.whereru.presentation.model.PushUiType
import com.miiiin15.whereru.presentation.model.ResponseUiType
import com.miiiin15.whereru.presentation.model.UserUiModel
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
        SessionListAdapter(object : OnSessionItemClickListener {
            override fun onSessionItemClick(session: JoinedSessionUiModel) =
                recentSessionClickAction(session)
        }
        )
    }
    private val userListAdapter: UserListAdapter by lazy {
        UserListAdapter(object : OnUserItemClickListener {
            override fun onUserItemClick(user: UserUiModel) =
                allUserClickAction(user)
        })
    }
    private val friendListAdapter: UserListAdapter by lazy {
        UserListAdapter(object : OnUserItemClickListener {
            override fun onUserItemClick(user: UserUiModel) {
                // TODO : 친구 목록 아이템 클릭 액션
            }
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 다른 화면 갔다가 돌아 왔을 때
        findNavController().addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.homeFragment) {
                if (!viewModel.fetched.value!!) {
                    viewModel.fetchProfile()
                }
                viewModel.getRecentSessionList()
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

        setViewPager()
    }

    override fun onResume() {
        super.onResume()
        FCMMessageHolder.consume()?.let { message ->
            fcmAction(message)
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

    // ViewPager2 Adapter 내부 바인딩 용
    fun bindViewPager(position: Int, recyclerView: RecyclerView, emptyView: LinearLayout) {
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        when (position) {
            0 -> {
                recyclerView.adapter = sessionListAdapter
                sessionListAdapter.setEmptyView(emptyView)
                viewModel.sessionList.observe { data ->
                    sessionListAdapter.resetAll(data)
                }
            }

            1 -> {
                recyclerView.adapter = userListAdapter
                userListAdapter.setEmptyView(emptyView)
                viewModel.userList.observe { data ->
                    userListAdapter.resetAll(data)
                }
            }

            2 -> {
                recyclerView.adapter = friendListAdapter
                friendListAdapter.setEmptyView(emptyView)
                viewModel.friendList.observe { data ->
                    friendListAdapter.resetAll(data)
                }
            }
        }
    }

    private fun recentSessionClickAction(session: JoinedSessionUiModel) {
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

    private fun fcmAction(message: PushMessageUiModel) {

        // 포그라운드에서 수신한 PushMessage 삭제
        val notificationManager =
            requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(message.notificationId)

        when (message.type) {
            // 위치 공유 요청
            PushUiType.REQUEST_LOCATION -> {
                showCustomBottomSheet(
                    "${message.fromNickname}님이 ${message.timestamp} 위치 공유 요청을 보냈습니다.\n수락하시겠습니까?",
                    "거절",
                    "수락",
                    onLeftButtonClick = {
                        viewModel.sendResponsePushMessage(message, false)
                    },
                    onRightButtonClick = {
                        viewModel.checkSessionID()
                            viewModel.sendResponsePushMessage(message, true)

                    }
                )
            }

            // 위치 공유 응답
            PushUiType.RESPONSE_LOCATION -> {
                message.response?.let {
                    when (it) {
                        ResponseUiType.ACCEPT -> {
                            showCustomAlert("${message.fromNickname}님이 위치 공유를 수락했습니다.\n 참여 하시겠습니까?") {
                                viewModel.participationSession(
                                    message.sessionId,
                                    message.fromNickname
                                )
                            }
                        }

                        ResponseUiType.DECLINE -> {
                            showCustomAlert("${message.fromNickname}님이 위치 공유를 거절했습니다.")
                        }
                    }
                } ?: run {
                    showCustomAlert("응답을 받을 수 없습니다.")
                }
            }

            // 세션 종료
            PushUiType.CANCEL_SESSION -> {}
        }

    }

    override fun handleEvent(event: HomeViewModel.Event) {
    }

    enum class Category {
        RECENT, ALL, FRIEND
    }

}

