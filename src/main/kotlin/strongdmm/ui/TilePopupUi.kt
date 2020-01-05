package strongdmm.ui

import imgui.ImGui.*
import imgui.enums.ImGuiWindowFlags
import strongdmm.byond.TYPE_MOB
import strongdmm.byond.VAR_NAME
import strongdmm.byond.dmi.GlobalDmiHolder
import strongdmm.byond.dmm.Tile
import strongdmm.byond.dmm.TileItem
import strongdmm.byond.dmm.TileItemIdx
import strongdmm.controller.action.ReplaceTileAction
import strongdmm.event.Event
import strongdmm.event.EventConsumer
import strongdmm.event.EventSender
import strongdmm.util.imgui.menu
import strongdmm.util.imgui.menuItem
import strongdmm.util.imgui.popup

class TilePopupUi : EventConsumer, EventSender {
    companion object {
        private const val ICON_SIZE: Float = 16f
    }

    private var currentTile: Tile? = null

    init {
        consumeEvent(Event.TilePopupUi.Open::class.java, ::handleOpen)
        consumeEvent(Event.TilePopupUi.Close::class.java, ::handleClose)
        consumeEvent(Event.Global.ResetEnvironment::class.java, ::handleResetEnvironment)
        consumeEvent(Event.Global.CloseMap::class.java, ::handleCloseMap)
    }

    fun process() {
        currentTile?.let { tile ->
            popup("tile_popup", ImGuiWindowFlags.NoMove) {
                showTileItems(tile)
            }
        }
    }

    private fun showTileItems(tile: Tile) {
        tile.area?.let { area -> showTileItemRow(tile, area, TileItemIdx.AREA) }
        tile.mobs.forEach { mob -> showTileItemRow(tile, mob.value, TileItemIdx(mob.index)) }
        tile.objs.forEach { obj -> showTileItemRow(tile, obj.value, TileItemIdx(obj.index)) }
        tile.turf?.let { turf -> showTileItemRow(tile, turf, TileItemIdx.TURF) }
    }

    private fun showTileItemRow(tile: Tile, tileItem: TileItem, index: TileItemIdx) {
        val sprite = GlobalDmiHolder.getSprite(tileItem.icon, tileItem.iconState, tileItem.dir)
        val name = tileItem.getVarText(VAR_NAME)!!

        image(sprite.textureId, ICON_SIZE, ICON_SIZE, sprite.u1, sprite.v1, sprite.u2, sprite.v2)
        sameLine()
        menu("$name##$index") { showTileItemOptions(tile, tileItem, index) }
        sameLine()
        text("[${tileItem.type}]  ") // Two spaces in the end to make text not to overlap over the menu arrow.
    }

    private fun showTileItemOptions(tile: Tile, tileItem: TileItem, index: TileItemIdx) {
        if (index != TileItemIdx.AREA && index != TileItemIdx.TURF) {
            menuItem("Move To Top##$index") {
                sendEvent(Event.ActionController.AddAction(
                    ReplaceTileAction(tile) {
                        tile.moveToTop(tileItem.type.startsWith(TYPE_MOB), index)
                    }
                ))

                sendEvent(Event.Global.RefreshFrame())
            }
            menuItem("Move To Bottom##$index") {
                sendEvent(Event.ActionController.AddAction(
                    ReplaceTileAction(tile) {
                        tile.moveToBottom(tileItem.type.startsWith(TYPE_MOB), index)
                    }
                ))

                sendEvent(Event.Global.RefreshFrame())
            }

            separator()
        }

        menuItem("Edit...##$index") {
            sendEvent(Event.EditVarsDialogUi.Open(Pair(tile, index)))
        }
    }

    private fun handleOpen(event: Event<Tile, Unit>) {
        currentTile = event.body
        openPopup("tile_popup")
    }

    private fun handleClose() {
        currentTile = null
    }

    private fun handleResetEnvironment() {
        currentTile = null
    }

    private fun handleCloseMap() {
        currentTile = null
    }
}
