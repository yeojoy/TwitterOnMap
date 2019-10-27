package me.yeojoy.twitteronmap.app

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import me.yeojoy.twitteronmap.controller.AppView

open class BaseActivity: AppCompatActivity(), AppView {
    override fun getContext(): Context {
        return this
    }
}