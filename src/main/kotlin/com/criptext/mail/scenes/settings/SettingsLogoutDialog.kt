package com.criptext.mail.scenes.settings

import android.content.Context
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.criptext.mail.R
import com.criptext.mail.scenes.settings.profile.ProfileUIObserver


class SettingsLogoutDialog(val context: Context) {

    private var dialog: AlertDialog? = null
    private val res = context.resources

    fun showLogoutDialog(observer: ProfileUIObserver?, isLastDeviceWith2FA: Boolean) {

        val dialogBuilder = AlertDialog.Builder(context)
        val inflater = (context as AppCompatActivity).layoutInflater
        val dialogView = inflater.inflate(R.layout.settings_custom_logout_dialog, null)

        if(isLastDeviceWith2FA)
            dialogView.findViewById<TextView>(R.id.logout_dialog_message).text =
                    context.getText(R.string.logout_dialog_message_2_fa)


        dialogBuilder.setView(dialogView)

        dialog = createDialog(dialogView, dialogBuilder, observer)
    }

    private fun createDialog(dialogView: View, dialogBuilder: AlertDialog.Builder,
                             observer: ProfileUIObserver?): AlertDialog {

        val width = res.getDimension(R.dimen.password_login_dialog_width).toInt()
        val newLogoutDialog = dialogBuilder.create()
        val window = newLogoutDialog.window
        newLogoutDialog.show()
        window?.setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT)
        window?.setGravity(Gravity.CENTER_VERTICAL)
        val drawableBackground = ContextCompat.getDrawable(dialogView.context,
                R.drawable.dialog_label_chooser_shape)
        newLogoutDialog.window?.setBackgroundDrawable(drawableBackground)

        assignButtonEvents(dialogView, newLogoutDialog, observer)


        return newLogoutDialog
    }

    private fun assignButtonEvents(view: View, dialog: AlertDialog,
                                   observer: ProfileUIObserver?) {

        val btn_yes = view.findViewById(R.id.settings_logout_yes) as Button
        val btn_no = view.findViewById(R.id.settings_logout_cancel) as Button

        btn_yes.setOnClickListener {
            dialog.dismiss()
            observer?.onLogoutConfirmedClicked()
        }

        btn_no.setOnClickListener {
            dialog.dismiss()
        }
    }
}
