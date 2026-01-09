package dev.toapuro.examplemod.config

import net.minecraftforge.common.ForgeConfigSpec

object ServerConfig {
    private val BUILDER = ForgeConfigSpec.Builder()

    val SPEC: ForgeConfigSpec? = BUILDER.build()
}
