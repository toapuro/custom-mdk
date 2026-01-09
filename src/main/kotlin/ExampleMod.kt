package dev.toapuro.examplemod

import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext

@Mod(ExampleMod.MODID)
class ExampleMod(ctx: FMLJavaModLoadingContext) {
    companion object {
        const val MODID: String = "examplemod"
    }
}
