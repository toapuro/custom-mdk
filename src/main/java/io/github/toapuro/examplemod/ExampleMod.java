package io.github.toapuro.examplemod;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(ExampleMod.MODID)
public class ExampleMod {
    public static final String MODID = "examplemod";

    public ExampleMod(FMLJavaModLoadingContext ctx) {
//        ctx.registerConfig(ModConfig.Type.SERVER, ServerConfig.SPEC);
    }
}
