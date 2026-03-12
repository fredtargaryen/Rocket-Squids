package com.fredtargaryen.rocketsquids.client.event;

import com.fredtargaryen.rocketsquids.ModSounds;
import com.fredtargaryen.rocketsquids.client.gui.ConchScreen;
import com.fredtargaryen.rocketsquids.client.model.ModelRocketSquid;
import com.fredtargaryen.rocketsquids.client.model.ModelRocketSquidBaby;
import com.fredtargaryen.rocketsquids.client.model.SquavigatorBakedModelWrapper;
import com.fredtargaryen.rocketsquids.client.render.RenderBabyRS;
import com.fredtargaryen.rocketsquids.client.render.RenderRS;
import com.fredtargaryen.rocketsquids.content.ModBlocks;
import com.fredtargaryen.rocketsquids.content.ModEntities;
import com.fredtargaryen.rocketsquids.content.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.FolderRepositorySource;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.nio.file.Path;
import java.util.Iterator;

import static com.fredtargaryen.rocketsquids.DataReference.MODID;


@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModClientHandler {
    public static void init(FMLJavaModLoadingContext context) {
        IEventBus modEventBus = context.getModEventBus();

        // This adds a new place for Minecraft to look for resourcepacks allowing us to add built-in resourcepacks
        Path resourcePackPath = context.getContainer().getModInfo().getOwningFile().getFile().getFilePath().resolve("resourcepacks");
        Minecraft.getInstance().getResourcePackRepository().addPackFinder(new FolderRepositorySource(resourcePackPath, PackType.CLIENT_RESOURCES, PackSource.BUILT_IN));

        modEventBus.addListener(ModClientHandler::onClientSetup);
        modEventBus.addListener(ModClientHandler::registerRenderers);
        modEventBus.addListener(ModClientHandler::registerLayerDefinitions);
        modEventBus.addListener(ModClientHandler::registerAdditionalBakedModels);
        modEventBus.addListener(ModClientHandler::onModelBake);
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {

    }

    public static ResourceLocation SQUAVIGATOR_IN_HAND;

    @SuppressWarnings("removal")
    @SubscribeEvent // on the mod event bus only on the physical client
    public static void registerAdditionalBakedModels(ModelEvent.RegisterAdditional event) {
        assert ModItems.SQUAVIGATOR.getId() != null;
        SQUAVIGATOR_IN_HAND = new ResourceLocation(MODID,  "item/" + ModItems.SQUAVIGATOR.getId().getPath() + "_in_hand");
        event.register(SQUAVIGATOR_IN_HAND);
    }

    @SubscribeEvent
    public static void onModelBake(ModelEvent.ModifyBakingResult event) {
        assert ModItems.SQUAVIGATOR.getId() != null;
        event.getModels().computeIfPresent(
                // The model resource location of the model to modify.
                new ModelResourceLocation(MODID, ModItems.SQUAVIGATOR.getId().getPath(), "inventory"),
                // A BiFunction with the location and the original models as parameters, returning the new model.
                (location, model) -> new SquavigatorBakedModelWrapper(model)
        );
    }

    @SuppressWarnings("removal")
    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        // register block renderers
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.BLOCK_CONCH.get(), RenderType.cutoutMipped());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.BLOCK_STATUE.get(), RenderType.cutoutMipped());

        // register normal entity renderers
        event.registerEntityRenderer(ModEntities.SAC_TYPE.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(ModEntities.TUBE_TYPE.get(), ThrownItemRenderer::new);

        // register custom entity renderers
        event.registerEntityRenderer(ModEntities.SQUID_TYPE.get(), RenderRS::new);
        event.registerEntityRenderer(ModEntities.BABY_SQUID_TYPE.get(), RenderBabyRS::new);
    }

    public static final ModelLayerLocation SQUID_BODY_LAYER;

    static {
        assert ModEntities.SQUID_TYPE.getId() != null;
        SQUID_BODY_LAYER = new ModelLayerLocation(ModEntities.SQUID_TYPE.getId(), "body");
    }

    public static final ModelLayerLocation BABY_SQUID_BODY_LAYER;

    static {
        assert ModEntities.BABY_SQUID_TYPE.getId() != null;
        BABY_SQUID_BODY_LAYER = new ModelLayerLocation(ModEntities.BABY_SQUID_TYPE.getId(), "body");
    }

    @SubscribeEvent
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(SQUID_BODY_LAYER, ModelRocketSquid::createBodyLayer);
        event.registerLayerDefinition(BABY_SQUID_BODY_LAYER, ModelRocketSquidBaby::createBodyLayer);
    }

    public static void openConchClient(byte conchStage) {
        Minecraft.getInstance().setScreen(new ConchScreen(conchStage));
    }

    public static void playNoteFromMessage(byte note) {
        Player ep = Minecraft.getInstance().player;
        assert ep != null;
        Vec3 pos = ep.position();
        ep.level().playLocalSound(pos.x, pos.y, pos.z, ModSounds.CONCH_NOTES[note], SoundSource.PLAYERS, 1.0F, 1.0F, true);
    }

    public static void playNoteFromMessageConchNeeded(byte note) {
        Player player = Minecraft.getInstance().player;
        // Check if the player is wearing the conch
        assert player != null;
        Iterable<ItemStack> armour = player.getArmorSlots();
        Iterator<ItemStack> iter = armour.iterator();
        iter.next();
        iter.next();
        iter.next();
        ItemStack helmet = iter.next();
        if (helmet.getItem() == ModItems.ITEM_CONCH.get()) {
            Vec3 pos = player.position();
            player.level().playLocalSound(pos.x, pos.y, pos.z, ModSounds.CONCH_NOTES[note], SoundSource.NEUTRAL, 1.0F, 1.0F, true);
        }
    }
}