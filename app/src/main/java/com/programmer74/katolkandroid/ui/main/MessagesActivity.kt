package com.programmer74.katolkandroid.ui.main

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.programmer74.katolk.dto.MessageRequestDto
import com.programmer74.katolkandroid.R
import com.programmer74.katolkandroid.ui.KatolkModelSingleton
import java.util.function.BiConsumer
import java.util.function.Consumer

class MessagesActivity() : AppCompatActivity() {
  private var dialogueID: Long = -1

  private lateinit var dialogueName: String

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_messages)

    dialogueID = intent.getLongExtra("dialogueID", -1)
    dialogueName = intent.getStringExtra("dialogueName") ?: "???"

    title = dialogueName

    val lvMessages = findViewById<ListView>(R.id.lvMessages)
    val katolkModel = KatolkModelSingleton.getKatolkModel()

    katolkModel.onNewMessageCallback = Consumer {
      katolkModel.scheduleRetrievingMessages(dialogueID)
    }

    katolkModel.onMessagesRetrievedCallback = BiConsumer { t, u ->
      runOnUiThread {
        lvMessages.adapter = MessagezAdapter(this, u.toTypedArray())

        val unreads = u.filter { !it.wasRead && (it.authorId != KatolkModelSingleton.getMe().id) }
        if (unreads.isNotEmpty()) {
          katolkModel.markMessagesAsRead(dialogueID)
        }
      }
    }

    katolkModel.scheduleRetrievingMessages(dialogueID)

    katolkModel.markMessagesAsRead(dialogueID)
  }

  private fun msgBox(v: View, text: String) {
    Snackbar.make(v, text, Snackbar.LENGTH_LONG).show()
  }

  fun btnSendMsgClicked(v: View) {
    val txtMessage = findViewById<EditText>(R.id.txtMessage)

    val katolkModel = KatolkModelSingleton.getKatolkModel()
    katolkModel.sendMessage(MessageRequestDto(dialogueID, txtMessage.text.toString()))

    katolkModel.scheduleRetrievingMessages(dialogueID)

    txtMessage.setText("")
  }
}