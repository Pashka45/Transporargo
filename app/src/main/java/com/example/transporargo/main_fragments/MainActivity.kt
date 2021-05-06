package com.example.transporargo.main_fragments

//import com.example.transporargo.main_fragments.data.remote.Network
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.transporargo.R
import com.example.transporargo.authentication.AuthenticationActivity
import com.example.transporargo.main_fragments.ui.request_form.RequestFormViewModel
import com.example.transporargo.main_fragments.ui.search.SearchViewModel
import com.example.transporargo.utils.Authentication
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var formViewModel: RequestFormViewModel
    private lateinit var searchViewModel: SearchViewModel
    private lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)

        val navView: NavigationView = findViewById(R.id.nav_view)

        val navDrawerHeader: View = navView.getHeaderView(0)
        navDrawerHeader.findViewById<TextView>(R.id.user_email).text = Authentication.email
        navDrawerHeader.findViewById<TextView>(R.id.user_name).text = Authentication.fullName

        val navController = findNavController(R.id.nav_host_fragment)

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.searchFragment, R.id.myRequestsFragment, R.id.addRequestFragment
            ), drawerLayout
        )

        formViewModel = ViewModelProvider(
            this,
            RequestFormViewModel.Factory(this.application)
        ).get(RequestFormViewModel::class.java)

        searchViewModel = ViewModelProvider(
            this,
            SearchViewModel.Factory(this.application)
        ).get(SearchViewModel::class.java)

        mainViewModel = ViewModelProvider(
            this,
            MainViewModel.Factory(this.application)
        ).get(MainViewModel::class.java)


        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main_activity, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    fun setActionBarTitle(title: String) {
        supportActionBar?.title = title
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_logout -> {
                val intent = Intent(this, AuthenticationActivity::class.java)
                Authentication.clear()
                mainViewModel.deleteUser()
                startActivity(intent)
                finish()
                return true
            }
            else -> Log.i("itemid", item.itemId.toString())
        }

        return super.onOptionsItemSelected(item)
    }
}