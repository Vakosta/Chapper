package org.chapper.chapper.presentation.util

import android.content.Context
import android.graphics.Bitmap
import android.view.View
import com.mikepenz.materialdrawer.AccountHeaderBuilder
import com.mikepenz.materialdrawer.Drawer
import com.mikepenz.materialdrawer.DrawerBuilder
import com.mikepenz.materialdrawer.model.DividerDrawerItem
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem
import com.mikepenz.materialdrawer.model.ProfileDrawerItem
import org.chapper.chapper.R
import org.chapper.chapper.data.ResourceGetter
import org.chapper.chapper.data.repository.SettingsRepository

class DrawerBuilderFactory(val context: Context,
                           val focus: View?,
                           private val firstName: String,
                           private val lastName: String) {
    fun getDrawerBuilder(): DrawerBuilder {
        return DrawerBuilder()
                .withSelectedItem(-1)
                .addDrawerItems(
                        PrimaryDrawerItem().withName(context.getString(R.string.search_for_devices)).withIcon(R.drawable.access_point_black).withSelectable(false),
                        PrimaryDrawerItem().withName(context.getString(R.string.enable_discoverable)).withIcon(R.drawable.lightbulb_on).withSelectable(false),
                        DividerDrawerItem(),
                        PrimaryDrawerItem().withName(context.getString(R.string.invite_friends)).withIcon(R.drawable.account_plus).withSelectable(false),
                        PrimaryDrawerItem().withName(context.getString(R.string.settings)).withIcon(R.drawable.settings).withSelectable(false),
                        PrimaryDrawerItem().withName(context.getString(R.string.faq)).withIcon(R.drawable.help_circle).withSelectable(false)
                )
                .withOnDrawerListener(object : Drawer.OnDrawerListener {
                    override fun onDrawerOpened(drawerView: View) {
                        Keyboard.hideKeyboard(context, focus)
                    }

                    override fun onDrawerClosed(drawerView: View) {

                    }

                    override fun onDrawerSlide(drawerView: View, slideOffset: Float) {

                    }
                })
    }

    fun getHeaderBuilder(): AccountHeaderBuilder {
        val profile = ProfileDrawerItem()
                .withName("$firstName $lastName")
                .withEmail(BluetoothHelper.getBluetoothAddress(context))

        val image: Bitmap? = SettingsRepository.getProfilePhoto(context)
        if (image == null)
            profile.withIcon(ResourceGetter.getDrawable(context, R.drawable.account_white))
        else
            profile.withIcon(image)

        return AccountHeaderBuilder()
                .withHeaderBackground(R.drawable.wallpaper)
                .withSelectionListEnabled(false)
                .withProfileImagesClickable(false)
                .addProfiles(
                        profile
                )
    }
}