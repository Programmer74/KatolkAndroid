package com.programmer74.katolkandroid.ui

import com.programmer74.katolk.KatolkModel
import com.programmer74.katolk.dto.UserInfoDto

object KatolkModelSingleton {
  private var katolkModel: KatolkModel? = null

  private var me: UserInfoDto? = null

  fun getKatolkModel() = katolkModel ?: error("uninitialized")

  fun setKatolkModel(model: KatolkModel) {
    this.katolkModel = model
  }

  fun getMe() = me ?: error("uninitialized")

  fun setMe(me: UserInfoDto) {
    this.me = me
  }
}