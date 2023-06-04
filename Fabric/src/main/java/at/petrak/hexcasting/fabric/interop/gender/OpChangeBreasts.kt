package at.petrak.hexcasting.fabric.interop.gender

import at.petrak.hexcasting.api.casting.*
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapImmuneEntity
import at.petrak.hexcasting.xplat.IXplatAbstractions
import com.wildfire.api.WildfireAPI
import com.wildfire.main.GenderPlayer
import net.minecraft.world.entity.Entity

object OpChangeBreasts : SpellAction {
    override val argc = 2

    override fun execute(
        args: List<Iota>,
        ctx: CastingEnvironment
    ): SpellAction.Result {
        val target = args.getPlayer(0, argc)
        val scale = args.getDoubleBetween(1, 0.0, 2.5, argc)
        val gp = WildfireAPI.getPlayerById(target.uuid) ?: throw MishapImmuneEntity(target)

        return SpellAction.Result(
            Spell(gp, scale),
            50_000,
            listOf(ParticleSpray.burst(target.position(), scale, 40))
        )
    }

    private data class Spell(val target: GenderPlayer, val scale: Double) : RenderedSpell {
        override fun cast(ctx: CastingEnvironment) {
            target.updateBustSize(scale.toFloat())
        }
    }
}