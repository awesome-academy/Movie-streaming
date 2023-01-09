package vn.ztech.software.movie_streaming.utils.extensions

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.widget.Toast
import androidx.annotation.StringRes
import vn.ztech.software.movie_streaming.R

fun Context.toast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}

fun Context.showAlertDialog(
    @StringRes titleStringId: Int,
    @StringRes messageStringId: Int,
    onClickCancelListener: DialogInterface.OnClickListener = DialogInterface.OnClickListener { _, _ -> },
    onClickOkListener: DialogInterface.OnClickListener = DialogInterface.OnClickListener { _, _ -> }
) {
    AlertDialog.Builder(this)
        .setTitle(this.resources.getString(titleStringId))
        .setMessage(this.resources.getString(messageStringId))
        .setNegativeButton(R.string.dialog_button_cancel, onClickCancelListener)
        .setPositiveButton(R.string.dialog_button_ok, onClickOkListener)
        .show()
}

fun Context.showAlertDialog(
    titleStringId: String,
    messageStringId: String,
    onClickCancelListener: DialogInterface.OnClickListener = DialogInterface.OnClickListener { _, _ -> },
    onClickOkListener: DialogInterface.OnClickListener = DialogInterface.OnClickListener { _, _ -> }
) {
    AlertDialog.Builder(this)
        .setTitle(titleStringId)
        .setMessage(messageStringId)
        .setNegativeButton(R.string.dialog_button_cancel, onClickCancelListener)
        .setPositiveButton(R.string.dialog_button_ok, onClickOkListener)
        .show()
}






