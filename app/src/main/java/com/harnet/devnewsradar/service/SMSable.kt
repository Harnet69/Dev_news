package com.harnet.devnewsradar.service

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import com.harnet.devnewsradar.R
import com.harnet.devnewsradar.databinding.SendSmsDialogBinding
import com.harnet.devnewsradar.model.Article
import com.harnet.devnewsradar.model.SmsInfo

interface SMSable {

    fun createSmsDialog(context: Context?, currentArticle: Article?, isSendSmsStarted: Boolean, permissionGranted: Boolean) {
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
                        Log.i("SendSms", "createSmsDialog: pushed Send btn ")
                        // check is user put smth to 'recipient' field
                        if (!dialogBinding.smsRecipient.text.isNullOrEmpty()) {
                            if (smsInfo != null) {
                                // get the text of form field 'to' and set it to entity
                                smsInfo.to = dialogBinding.smsRecipient.text.toString()
                                Log.i("SendSms", "createSmsDialog: SEND ")
                                // send SMS
                                sendSms(smsInfo)
                            }
                        }
                    }
                    .setNegativeButton("Cancel") { dialog, which -> }
                    .show()
            }
        }
    }

    // when user sent SMS clicked to 'send SMS' button
    private fun sendSms(smsInfo: SmsInfo) {
        Log.i("SendSms", "sendSms: $smsInfo")
    }
}