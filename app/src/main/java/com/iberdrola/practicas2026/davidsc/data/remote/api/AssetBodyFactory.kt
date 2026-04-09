package com.iberdrola.practicas2026.davidsc.data.remote.api

import android.content.res.AssetManager
import co.infinum.retromock.BodyFactory
import java.io.InputStream

class AssetBodyFactory(private val assets: AssetManager) : BodyFactory {
    override fun create(input: String): InputStream = assets.open(input)
}