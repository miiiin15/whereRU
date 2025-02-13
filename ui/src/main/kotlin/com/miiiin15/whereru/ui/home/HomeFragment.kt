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
import com.miiiin15.whereru.presentation.model.LocationSessionUiModel
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

        val viewPager = binding.viewPager
        viewPager.adapter = ViewPagerAdapter()

        val categoryTexts = listOf(
            binding.homeCategoryAllText,
            binding.homeCategoryRecentText,
            binding.homeCategoryFavoriteText
        )

        categoryTexts.forEachIndexed { index, textView ->
            textView.setOnClickListener {
                viewPager.currentItem = index
            }
        }

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
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
    }

    override fun handleEvent(event: HomeViewModel.Event) {
    }

    private inner class ViewPagerAdapter : RecyclerView.Adapter<ViewPagerAdapter.ViewHolder>() {

        private val fakeData = listOf(
            listOf(
                LocationSessionUiModel("host1", true, "2023-01-01 10:00:00"),
                LocationSessionUiModel("host2", false, "2023-01-02 11:00:00"),
                LocationSessionUiModel("host3", true, "2023-01-03 12:00:00"),
                LocationSessionUiModel("host4", false, "2023-01-04 13:00:00"),
                LocationSessionUiModel("host5", true, "2023-01-05 14:00:00"),
                LocationSessionUiModel("host11", false, "2023-01-06 15:00:00"),
                LocationSessionUiModel("host12", true, "2023-01-07 16:00:00"),
                LocationSessionUiModel("host13", false, "2023-01-08 17:00:00"),
                LocationSessionUiModel("host14", true, "2023-01-09 18:00:00"),
                LocationSessionUiModel("host15", false, "2023-01-10 19:00:00")
            ),
            listOf(
                LocationSessionUiModel("host6", true, "2023-01-06 15:00:00"),
                LocationSessionUiModel("host7", false, "2023-01-07 16:00:00"),
                LocationSessionUiModel("host8", true, "2023-01-08 17:00:00"),
                LocationSessionUiModel("host9", false, "2023-01-09 18:00:00"),
                LocationSessionUiModel("host10", true, "2023-01-10 19:00:00"),
                LocationSessionUiModel("host16", false, "2023-01-11 20:00:00"),
                LocationSessionUiModel("host17", true, "2023-01-12 21:00:00"),
                LocationSessionUiModel("host18", false, "2023-01-13 22:00:00"),
                LocationSessionUiModel("host19", true, "2023-01-14 23:00:00"),
                LocationSessionUiModel("host20", false, "2023-01-15 00:00:00")
            ),
            listOf(
                LocationSessionUiModel("host11", true, "2023-01-11 20:00:00"),
                LocationSessionUiModel("host12", false, "2023-01-12 21:00:00"),
                LocationSessionUiModel("host13", true, "2023-01-13 22:00:00"),
                LocationSessionUiModel("host14", false, "2023-01-14 23:00:00"),
                LocationSessionUiModel("host15", true, "2023-01-15 00:00:00"),
                LocationSessionUiModel("host21", false, "2023-01-16 01:00:00"),
                LocationSessionUiModel("host22", true, "2023-01-17 02:00:00"),
                LocationSessionUiModel("host23", false, "2023-01-18 03:00:00"),
                LocationSessionUiModel("host24", true, "2023-01-19 04:00:00"),
                LocationSessionUiModel("host25", false, "2023-01-20 05:00:00")
            )
        )

        private val pageBackgroundColors = listOf(
            "#FF00FF",
            "#00FF00",
            "#0000FF"
        )

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.scroll_session, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val sessions = fakeData[position]
            holder.bind(sessions)
            holder.itemView.setBackgroundColor(
                android.graphics.Color.parseColor(
                    pageBackgroundColors[position]
                )
            )
        }

        override fun getItemCount(): Int = fakeData.size

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            private val recyclerView: RecyclerView = view.findViewById(R.id.session_list_recycler)

            fun bind(sessions: List<LocationSessionUiModel>) {
                recyclerView.layoutManager = LinearLayoutManager(itemView.context)
                recyclerView.adapter = SessionListItemAdapter(sessions)
            }
        }
    }
}