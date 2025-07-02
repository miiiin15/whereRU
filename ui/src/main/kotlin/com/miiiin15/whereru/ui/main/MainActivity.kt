package com.miiiin15.whereru.ui.main

import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.miiiin15.whereru.common.utils.PermissionManager
import com.miiiin15.whereru.presentation.fcm.FCMMessageMapper
import com.miiiin15.whereru.presentation.fcm.FCMMessageHolder
import com.miiiin15.whereru.ui.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        PermissionManager.askNotificationPermission(this, requestPermissionLauncher)

        // 시스템 상태바 아이콘 색상 검정으로
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true

        handleFcmPushIntent()
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (ev.action == MotionEvent.ACTION_DOWN) {
            currentFocus?.let { view ->
                val outRect = Rect()
                view.getGlobalVisibleRect(outRect)
                if (!outRect.contains(ev.rawX.toInt(), ev.rawY.toInt())) {
                    view.clearFocus()
                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(view.windowToken, 0)
                }
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    private fun handleFcmPushIntent() {
        FCMMessageMapper.fromIntent(intent)?.let { pushMessage ->
            FCMMessageHolder.set(pushMessage)
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (intent != null) {
            setIntent(intent)
            handleFcmPushIntent()
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (!isGranted) {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", packageName, null)
            }
            Toast.makeText(
                this,
                "알림을 받기 위해 알림 권한을 허용해주세요.",
                Toast.LENGTH_LONG
            ).show()
            startActivity(intent)
        }
    }

}