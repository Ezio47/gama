/**
 * Created by drogoul, 5 déc. 2014
 * 
 */
package msi.gama.lang.gaml.ui.editor;

import msi.gama.gui.swt.commands.*;
import msi.gama.gui.swt.commands.GamaColorMenu.IColorRunnable;
import msi.gama.util.GamaColor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.widgets.*;

/**
 * The class EditToolbarColorMenu.
 * 
 * @author drogoul
 * @since 5 déc. 2014
 * 
 */
public class EditToolbarColorMenu extends EditToolbarMenu {

	// static {
	// GamaColorMenu.COLOR_MENU_SORT.addChangeListener(new IPreferenceChangeListener<String>() {
	//
	// @Override
	// public boolean beforeValueChange(final String newValue) {
	// return true;
	// }
	//
	// @Override
	// public void afterValueChange(final String newValue) {
	// EditToolbar.visitToolbars(new IToolbarVisitor() {
	//
	// @Override
	// public void visit(final EditToolbar toolbar) {
	// toolbar.resetColorMenu();
	// }
	// });
	// }
	// });
	// }

	IColorRunnable runnable = new IColorRunnable() {

		@Override
		public void run(final int r, final int g, final int b) {
			GamaColor c = new GamaColor(r, g, b, 255);
			applyText(c.serialize(true));
		}
	};

	SelectionListener colorInserter = new SelectionAdapter() {

		@Override
		public void widgetSelected(final SelectionEvent e) {
			MenuItem i = (MenuItem) e.widget;
			applyText(i.getText());
		}
	};

	@Override
	protected void open(final GamlEditor editor, final SelectionEvent trigger) {
		boolean asMenu = trigger.detail == SWT.ARROW;
		setEditor(editor);
		if ( !asMenu ) {
			openView();
		} else {
			final ToolItem target = (ToolItem) trigger.widget;
			final ToolBar toolBar = target.getParent();
			GamaColorMenu.getInstance().open(toolBar, trigger, colorInserter, runnable);
		}
	}

	@Override
	protected void openView() {
		GamaColorMenu.openView(runnable, null);
	}

	@Override
	protected void fillMenu() {
		GamaColorMenu.getInstance().fillMenu();

	}

}
