package ui

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment


/**
 * Created by Ehigiator David on 18/08/2021.
 * Pelpay
 * david3ti@gmail.com
 */
internal open class BaseActivity : AppCompatActivity() {

    private val fragmentManager = supportFragmentManager
    private var activeFragment = Fragment()

    protected fun addFragment(fragment: Fragment, container: Int)  {

        if ( fragmentManager.findFragmentByTag(fragment.tag) == null ) {
            fragmentManager.beginTransaction()
                    .add(container, fragment, fragment.tag)
                    .hide(fragment)
                    .commit()

            activeFragment = fragment
        }
    }

    protected fun showFragment(fragment: Fragment){

        fragmentManager
                .beginTransaction()
                .hide(activeFragment)
                .show(fragment).commit()

        activeFragment = fragment

    }

}