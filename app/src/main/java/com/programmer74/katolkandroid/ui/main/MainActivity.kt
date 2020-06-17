package com.programmer74.katolkandroid.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.programmer74.katolk.FeignRepository
import com.programmer74.katolk.KatolkModel
import com.programmer74.katolk.dto.DialogueDto
import com.programmer74.katolkandroid.R
import com.programmer74.katolkandroid.ui.KatolkModelSingleton
import java.util.concurrent.atomic.AtomicReference
import java.util.function.BiConsumer
import java.util.function.Consumer

class MainActivity() : AppCompatActivity() {

  private lateinit var feignRepository: FeignRepository

  private lateinit var katolkModel: KatolkModel

  private val dialoguesSnapshot = AtomicReference<List<DialogueDto>>()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    val lvDialogz = findViewById<ListView>(R.id.lvDialogs)
    lvDialogz.setOnItemClickListener { adapterView, view, i, l ->
      val targetDialogue = dialoguesSnapshot.get()[i]
      val activity = Intent(this, MessagesActivity::class.java)
      activity.putExtra("dialogueID", targetDialogue.id)
      activity.putExtra("dialogueName", targetDialogue.name)
      startActivity(activity)
    }

    katolkModel = KatolkModelSingleton.getKatolkModel()

    katolkModel.onDialogListRetrievedCallback = Consumer {
      runOnUiThread {
        dialoguesSnapshot.set(it)
        lvDialogz.adapter = DialogzAdapter(this, it.toTypedArray())
      }
    }

    retriggerCallbacks()
  }

  override fun onActivityReenter(resultCode: Int, data: Intent?) {
    katolkModel.scheduleDialogueListUpdate()
  }

  override fun onRestart() {
    super.onRestart()
    retriggerCallbacks()
  }

  override fun onResume() {
    super.onResume()
    retriggerCallbacks()
  }

  private fun retriggerCallbacks() {
    katolkModel.onNewMessageCallback = Consumer {
      katolkModel.scheduleDialogueListUpdate()
    }

    katolkModel.onMessagesRetrievedCallback = BiConsumer { t, u -> }

    katolkModel.scheduleDialogueListUpdate()
  }
}