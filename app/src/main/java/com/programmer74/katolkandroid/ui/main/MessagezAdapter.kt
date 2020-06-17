package com.programmer74.katolkandroid.ui.main

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.programmer74.katolk.dto.DialogueDto
import com.programmer74.katolk.dto.MessageDto
import com.programmer74.katolkandroid.R
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.*

class MessagezAdapter(context: Context, var data: Array<MessageDto>) : BaseAdapter() {

  private val inflater: LayoutInflater = context
      .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

  private val dateFormat =  SimpleDateFormat("dd.MM.yyyy' 'HH:mm:ss");

  override fun getCount(): Int {
    return data.size
  }

  override fun getItem(position: Int): Any {
    return data[position]
  }

  override fun getItemId(position: Int): Long {
    return position.toLong()
  }

  override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
    // TODO Auto-generated method stub
    var vi: View? = convertView
    if (vi == null) vi = inflater.inflate(R.layout.row, parent, false)

    val row = data[position]

    val header = vi!!.findViewById(R.id.header) as TextView
    header.text = row.author + ", " + dateFormat.format(Date(row.date)) + (if (!row.wasRead) " [!]" else "")

    val text = vi.findViewById(R.id.text) as TextView
    text.text = row.body

    return vi
  }
}