package com.harnet.devnewsradar.service

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.telephony.SmsManager
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import com.harnet.devnewsradar.R
import com.harnet.devnewsradar.databinding.SendSmsDialogBinding
import com.harnet.devnewsradar.model.Article
import com.harnet.devnewsradar.model.SmsInfo
import com.harnet.devnewsradar.view.MainActivity

interface SMSable {

    fun createSmsDialog(
        context: Context?,
        currentArticle: Article?,
        isSendSmsStarted: Boolean,
        permissionGranted: Boolean
    ) {
        // if SMS permission was granted
        if (isSendSmsStarted && permissionGranted) {
            context?.let {
                // create an object of SmsInfo class
                val smsInfo = currentArticle?.title?.let { it1 ->
                    currentArticle?.url?.let { it2 ->
                        currentArticle?.imageUrl?.let { it3 ->
                            SmsInfo(
                                "",
                                it1, it2, it3
                            )
                        }
                    }
                }
                // inflate(bind) xml file
                val dialogBinding = DataBindingUtil.inflate<SendSmsDialogBinding>(
                    LayoutInflater.from(it),
                    R.layout.send_sms_dialog,
                    null,
                    false
                )

                dialogBinding.smsInfo = smsInfo

                // dialog window
                AlertDialog.Builder(it)
                    .setView(dialogBinding.root)
                    .setPositiveButton("Send SMS") { dialog, which ->
                        // check is user put smth to 'recipient' field
                        if (!dialogBinding.smsRecipient.text.isNullOrEmpty()) {
                            if (smsInfo != null) {
                                // get the text of form field 'to' and set it to entity
                                smsInfo.to = dialogBinding.smsRecipient.text.toString()
                                // send SMS
                                sendSms(context, smsInfo)
                            }
                        }
                    }
                    .setNegativeButton("Cancel") { dialog, which -> }
                    .show()
            }
        }
    }

    // when user sent SMS by clicking to 'send SMS' button
    private fun sendSms(context: Context, smsInfo: SmsInfo) {
        val intent = Intent(context, MainActivity::class.java)
        val pi = PendingIntent.getActivity(context, 0, intent, 0)
        val sms = SmsManager.getDefault()
        val smsMessage = "${smsInfo.text}: ${smsInfo.articleUrl}"

        sms.sendTextMessage(smsInfo.to, null, smsMessage, pi, null)
        Toast.makeText(context, "Article was sent to ${smsInfo.to}", Toast.LENGTH_LONG).show()
    }
}