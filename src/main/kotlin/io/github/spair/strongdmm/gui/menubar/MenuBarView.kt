package io.github.spair.strongdmm.gui.menubar

import io.github.spair.strongdmm.gui.View
import java.awt.Font
import java.awt.event.InputEvent
import javax.swing.*

const val SHORTCUT_CTRL_Q = "ctrl+q"

class MenuBarView : View {

    // File items
    val openEnvItem = createMenuItem("Open Environment...")
    val openMapItem = createMenuItem("Open...", false)
    val availableMapsItem = createMenuItem("Open from available", false)
    val saveItem = createMenuItem("Save", false)
    val exitMenuItem = createMenuItem("Exit").addCtrlShortcut('Q')

    // Edit items
    val undoActionItem = createMenuItem("Undo", false)
    val redoActionItem = createMenuItem("Redo", false)

    override fun init(): JMenuBar {
        return JMenuBar().apply {
            add(createMenu("File", createFileItems()))
            add(createMenu("Edit", createEditItems()))
        }
    }

    private fun createFileItems() = arrayOf<JComponent>(
        openEnvItem,
        JSeparator(),
        openMapItem,
        availableMapsItem,
        JSeparator(),
        saveItem,
        JSeparator(),
        exitMenuItem
    )

    private fun createEditItems() = arrayOf<JComponent>(
        undoActionItem,
        redoActionItem
    )

    private fun createMenuItem(text: String, isEnabled: Boolean = true): JMenuItem {
        return JMenuItem(text).apply {
            font = font.deriveFont(Font.PLAIN)
            this.isEnabled = isEnabled
        }
    }

    private fun createMenu(name: String, items: Array<JComponent>): JComponent = JMenu(name).apply {
        items.forEach { add(it) }
    }

    private fun JMenuItem.addCtrlShortcut(char: Char) = apply {
        accelerator = KeyStroke.getKeyStroke(char.toInt(), InputEvent.CTRL_DOWN_MASK)
    }
}
