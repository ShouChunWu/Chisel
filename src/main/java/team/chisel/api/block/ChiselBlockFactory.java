package team.chisel.api.block;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import com.tterrag.registrate.Registrate;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;
import team.chisel.common.Reference;
import team.chisel.common.block.ItemChiselBlock;

/**
 * Factory to create builders to create blocks
 */
@ParametersAreNonnullByDefault
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ChiselBlockFactory {

    private final Registrate registrate;

    public static ChiselBlockFactory newFactory(Registrate registrate) {
        return new ChiselBlockFactory(registrate);
    }

    public <T extends Block & ICarvable> ChiselBlockBuilder<T> newType(Material material, String blockName, BlockCreator<T> creator, Class<T> blockClass) {
        return newType(material, blockName, creator, blockClass, BlockItem.class);
    }

    public <T extends Block & ICarvable> ChiselBlockBuilder<T> newType(Material material, String blockName, BlockCreator<T> creator, Class<T> blockClass, Class<? extends BlockItem> itemBlockClass) {
        return newType(material, blockName, new BlockProvider<T>() {

            @Override
            public T createBlock(Block.Properties properties, VariationData data) {
                return creator.createBlock(properties, data);
            }

            @Override
            public Class<T> getBlockClass() {
                return blockClass;
            }

            @Override
            public BlockItem createBlockItem(T block, Item.Properties properties) {
                return (BlockItem) new ItemChiselBlock(block, properties);
            }
        });
    }

    public <T extends Block & ICarvable> ChiselBlockBuilder<T> newType(Material material, String blockName, BlockProvider<T> provider) {
        return newType(material, blockName, blockName, provider);
    }
    
    public <T extends Block & ICarvable> ChiselBlockBuilder<T> newType(Material material, String blockName, @Nullable String group, BlockProvider<T> provider) {
        return new ChiselBlockBuilder<T>(this, registrate, material, blockName, group == null ? null : getBlockTag(new ResourceLocation(Reference.MOD_ID, group)), provider);
    }
    
    private final Map<ResourceLocation, Tag<Block>> blockTags = new HashMap<>();
    private final Map<ResourceLocation, Tag<Item>> itemTags = new HashMap<>();

    Tag<Block> getBlockTag(ResourceLocation id) {
        return blockTags.computeIfAbsent(id, BlockTags.Wrapper::new);
    }
    
    Tag<Item> getItemTag(ResourceLocation id) {
        return itemTags.computeIfAbsent(id, ItemTags.Wrapper::new);
    }

    public Registrate getRegistrate() {
        return registrate;
    }
}
