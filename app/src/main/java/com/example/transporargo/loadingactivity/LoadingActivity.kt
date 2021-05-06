package com.example.transporargo.loadingactivity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.transporargo.R
import com.example.transporargo.authentication.AuthenticationActivity
import com.example.transporargo.main_fragments.MainActivity

class LoadingActivity : AppCompatActivity() {

    private lateinit var viewModel: LoadingViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loading)

        viewModel = ViewModelProvider(
            this,
            LoadingViewModel.Factory(this.application)
        ).get(LoadingViewModel::class.java)

        viewModel.startMainActivity.observe(this, Observer {
            if (it) {
                startRequestListActivity()
            }
        })

        viewModel.startAuthActivity.observe(this, Observer {
            if (it) {
                startAuthActivity()
            }
        })

        viewModel.getUserInfo()
    }

    private fun startRequestListActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    private fun startAuthActivity() {
        val intent = Intent(this, AuthenticationActivity::class.java)
        startActivity(intent)
        finish()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

}