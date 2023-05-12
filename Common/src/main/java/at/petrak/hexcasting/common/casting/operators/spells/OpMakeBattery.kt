package at.petrak.hexcasting.common.casting.operators.spells

import at.petrak.hexcasting.api.casting.ParticleSpray
import at.petrak.hexcasting.api.casting.RenderedSpell
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getItemEntity
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapBadItem
import at.petrak.hexcasting.api.casting.mishaps.MishapBadOffhandItem
import at.petrak.hexcasting.api.casting.mishaps.MishapBatteryTooBig
import at.petrak.hexcasting.api.misc.MediaConstants
import at.petrak.hexcasting.api.mod.HexTags
import at.petrak.hexcasting.api.utils.extractMedia
import at.petrak.hexcasting.api.utils.isMediaItem
import at.petrak.hexcasting.common.items.magic.ItemMediaHolder
import at.petrak.hexcasting.common.lib.HexItems
import net.minecraft.world.InteractionHand
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.item.ItemStack

// TODO: how to handle in cirles
object OpMakeBattery : SpellAction {
    override val argc = 1

    override fun execute(
        args: List<Iota>,
        ctx: CastingEnvironment
    ): SpellAction.Result {
        val entity = args.getItemEntity(0, argc)

        val (handStack, hand) = ctx.getHeldItemToOperateOn { it.`is`(HexTags.Items.PHIAL_BASE) }
            ?: throw MishapBadOffhandItem.of(ItemStack.EMPTY.copy(), null, "bottle") // TODO: hack

        if (hand == null)
            throw MishapBadOffhandItem.of(handStack, null, "havent_handled_null_hand_yet") // TODO: hack!

        if (!handStack.`is`(HexTags.Items.PHIAL_BASE)) {
            throw MishapBadOffhandItem.of(
                handStack,
                hand,
                "bottle"
            )
        }
        if (handStack.count != 1) {
            throw MishapBadOffhandItem.of(
                handStack,
                hand,
                "only_one"
            )
        }
        val holder = handStack.getItem()
        if (holder is ItemMediaHolder) {
            if (holder.getMaxMedia(handStack) >= 640000000) {
                throw MishapBatteryTooBig()
            }
        }

        ctx.assertEntityInRange(entity)

        if (!isMediaItem(entity.item) || extractMedia(
                entity.item,
                drainForBatteries = true,
                simulate = true
            ) <= 0
        ) {
            throw MishapBadItem.of(
                entity,
                "media_for_battery"
            )
        }

        return SpellAction.Result(
            Spell(entity, hand, handStack),
            MediaConstants.CRYSTAL_UNIT,
            listOf(ParticleSpray.burst(entity.position(), 0.5))
        )
    }

    private data class Spell(val itemEntity: ItemEntity, val hand: InteractionHand, val handStack: ItemStack) : RenderedSpell {
        override fun cast(ctx: CastingEnvironment) {
            if (itemEntity.isAlive) {
                var baseMedia: Long = 0
                var baseMax: Long = 0
                var holder = handStack.getItem()
                if (holder is ItemMediaHolder) {
                    baseMedia = holder.getMedia(handStack)
                    baseMax = holder.getMaxMedia(handStack)
                }
                val entityStack = itemEntity.item.copy()
                holder = entityStack.getItem()
                if (holder is ItemMediaHolder) {
                    baseMax += holder.getMaxMedia(entityStack) - holder.getMedia(entityStack)
                }
                var mediamount = extractMedia(entityStack, drainForBatteries = true, simulate = true)
                mediamount = extractMedia(entityStack, cost = Math.min(mediamount, 640000000 - baseMax), drainForBatteries = true)
                if (mediamount > 0) {
                    ctx.caster?.setItemInHand(
                        hand,
                        ItemMediaHolder.withMedia(ItemStack(HexItems.BATTERY), mediamount + baseMedia, mediamount + baseMax)
                    ) ?: return
                }

                itemEntity.item = entityStack
                if (entityStack.isEmpty)
                    itemEntity.kill()
            }
        }
    }
}
