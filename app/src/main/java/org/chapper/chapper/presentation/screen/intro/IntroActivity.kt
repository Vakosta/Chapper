package org.chapper.chapper.presentation.screen.intro

import agency.tango.materialintroscreen.MaterialIntroActivity
import agency.tango.materialintroscreen.SlideFragmentBuilder
import android.Manifest
import android.os.Bundle
import org.chapper.chapper.R


class IntroActivity : MaterialIntroActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableLastSlideAlphaExitTransition(false)
        hideBackButton()

        addSlide(SlideFragmentBuilder()
                .backgroundColor(R.color.colorPrimary)
                .buttonsColor(R.color.colorAccent)
                .image(R.drawable.forum_white)
                .title("Привет.")
                .description("Это лучший оффлайн мессенджер.")
                .build())

        addSlide(SlideFragmentBuilder()
                .backgroundColor(R.color.colorSecondary)
                .buttonsColor(R.color.colorAccent)
                .image(R.drawable.vector_polygon_white)
                .title("Единая сеть.")
                .description("Ваши сообщения дойдут до получателя даже если устройства не видят друг друга.")
                .build())

        addSlide(SlideFragmentBuilder()
                .backgroundColor(R.color.colorPrimary)
                .buttonsColor(R.color.colorAccent)
                .image(R.drawable.security_white)
                .title("Защищённый.")
                .description("Благодаря надёжному шифрованию ваши сообщения останутся в безопасности.")
                .build())

        addSlide(SlideFragmentBuilder()
                .backgroundColor(R.color.colorSecondary)
                .buttonsColor(R.color.colorAccent)
                .image(R.drawable.nature_people_white)
                .title("Работает как рация.\nНо лучше.")
                .description("Общайтесь и координируйтесь — заграницей, на природе или работе — везде, где нет интернета.")
                .build())

        addSlide(SlideFragmentBuilder()
                .neededPermissions(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION))
                .backgroundColor(R.color.colorPrimary)
                .buttonsColor(R.color.colorAccent)
                .image(R.drawable.bluetooth_connect_white)
                .title("Начнём настройку.")
                .description("Пожалуйста, разрешите приложению использовать ваш Bluetooth.")
                .build())

        addSlide(RegisterSlide())
    }

    override fun onBackPressed() {
        // Nothing
    }
}