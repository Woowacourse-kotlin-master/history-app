package com.balhae.historyapp.util

import com.balhae.historyapp.network.models.HeritageDto

object HeritageRepository {
    var lastRecognized: List<HeritageDto> = emptyList()
}
