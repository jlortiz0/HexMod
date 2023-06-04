package at.petrak.hexcasting.fabric.interop.gender

import at.petrak.hexcasting.api.casting.asActionResult
import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getPlayer
import at.petrak.hexcasting.api.casting.iota.Iota
import com.wildfire.api.WildfireAPI

object OpGetBreasts : ConstMediaAction {
    override val argc = 1

    override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
        val target = args.getPlayer(0, argc)
        val gp = WildfireAPI.getPlayerById(target.uuid) ?: return (0.0).asActionResult
        return gp.bustSize.asActionResult
    }
}