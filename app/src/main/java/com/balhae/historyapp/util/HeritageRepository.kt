package com.balhae.historyapp.util

import com.balhae.historyapp.network.models.HeritageItem

object HeritageRepository {
    var lastRecognized: List<HeritageItem> = emptyList()
}
