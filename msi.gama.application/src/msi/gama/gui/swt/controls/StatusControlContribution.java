/*********************************************************************************************
 *
 *
 * 'StatusControlContribution.java', in plugin 'msi.gama.application', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gama.gui.swt.controls;

import java.util.Map;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.menus.WorkbenchWindowControlContribution;
import msi.gama.common.IStatusMessage;
import msi.gama.common.StatusMessage;
import msi.gama.common.SubTaskMessage;
import msi.gama.common.UserStatusMessage;
import msi.gama.common.interfaces.IGui;
import msi.gama.common.interfaces.IUpdaterTarget;
import msi.gama.gui.swt.GamaColors;
import msi.gama.gui.swt.GamaColors.GamaUIColor;
import msi.gama.gui.swt.GamaIcons;
import msi.gama.gui.swt.IGamaColors;
import msi.gama.gui.swt.SwtGui;
import msi.gama.kernel.experiment.ExperimentAgent;
import msi.gama.kernel.simulation.SimulationAgent;
import msi.gama.kernel.simulation.SimulationClock;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.GAMA;
import msi.gama.util.GamaColor;
import msi.gama.util.GamaMapFactory;
import msi.gaml.operators.Dates;

public class StatusControlContribution extends WorkbenchWindowControlContribution implements IPopupProvider, IUpdaterTarget<IStatusMessage> {

	// private Composite compo;
	// private Composite parent;
	volatile boolean isUpdating;
	FlatButton label;
	private Popup popup;
	int state;
	volatile String mainTaskName;
	volatile String subTaskName;
	volatile boolean inSubTask = false;
	volatile boolean InUserStatus = false;
	volatile Double subTaskCompletion;
	private final static int WIDTH = 300;
	private GamaUIColor color;

	public StatusControlContribution() {}

	public StatusControlContribution(final String id) {
		super(id);
	}

	@Override
	protected int computeWidth(final Control control) {
		return WIDTH;
		// return label.computeMinWidth();
		// return compo.getBounds().y;
		// return control.computeSize(SWT.DEFAULT, SWT.DEFAULT, true).x;
	}

	@Override
	public boolean isBusy() {
		return isUpdating;
	}

	@Override
	protected Control createControl(final Composite parent) {
		// parent.setBackground(IGamaColors.VERY_LIGHT_GRAY.color());
		// this.parent = parent;
		final Composite compo = new Composite(parent, SWT.DOUBLE_BUFFERED);
		// compo.setBackground(IGamaColors.VERY_LIGHT_GRAY.color());
		final GridLayout layout = new GridLayout(1, false);
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		compo.setLayout(layout);
		final GridData data = new GridData(SWT.FILL, SWT.CENTER, true, true);
		data.widthHint = WIDTH;
		data.heightHint = 24;
		label = FlatButton.label(compo, IGamaColors.NEUTRAL, "No simulation running");
		label.setLayoutData(data);

		label.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseDown(final MouseEvent e) {
				if ( GAMA.getExperiment() == null ) { return; }
				final ExperimentAgent exp = GAMA.getExperiment().getAgent();
				if ( exp == null ) { return; }
				exp.getClock().toggleDisplay();
				exp.getSimulation().getClock().toggleDisplay();
				exp.informStatus();
			}
		});
		popup = new Popup(this, label);
		SwtGui.setStatusControl(this);
		return compo;
	}

	@Override
	public boolean isDisposed() {
		return label.isDisposed();
	}

	/**
	 * @see msi.gama.gui.swt.controls.IPopupProvider#getPopupText()
	 */
	@Override
	public Map<GamaUIColor, String> getPopupText() {
		if ( !GAMA.getGui().isSimulationPerspective() ) { return null; }
		final Map<GamaUIColor, String> result = GamaMapFactory.create();

		if ( state == IGui.ERROR || state == IGui.WAIT ) {
			final GamaUIColor color = state == IGui.ERROR ? IGamaColors.ERROR : IGamaColors.WARNING;
			result.put(color, label.getText());
			return result;
		}

		final ExperimentAgent agent = GAMA.getExperiment().getAgent();
		if ( agent == null ) {
			result.put(IGamaColors.NEUTRAL, "No experiment opened");
			return result;
		}

		final StringBuilder sb = new StringBuilder(300);
		SimulationClock clock = agent.getClock();
		sb.append(String.format("%-20s %-10d\n", "Experiment cycles elapsed: ", clock.getCycle()));
		sb.append(String.format("%-20s cycle %5d; average %5d; total %10d", "Duration (ms)", clock.getDuration(),
			(int) clock.getAverageDuration(), clock.getTotalDuration()));
		result.put(GamaColors.get(agent.getColor()), sb.toString());
		final IAgent[] simulations = agent.getSimulationPopulation().toArray();

		for ( final IAgent a : simulations ) {
			sb.setLength(0);
			final SimulationAgent sim = (SimulationAgent) a;
			clock = sim.getClock();

			sb.append(String.format("%-20s %-10d\tSimulated time %-30s\n", "Cycles elapsed: ", clock.getCycle(),
				clock.getStartingDate() == null ? Dates.asDate(clock.getTime(), null)
					: Dates.asDate(clock.getStartingDate(), clock.getCurrentDate(), null)));
			sb.append(String.format("%-20s cycle %5d; average %5d; total %10d", "Duration (ms)", clock.getDuration(),
				(int) clock.getAverageDuration(), clock.getTotalDuration()));
			result.put(GamaColors.get(sim.getColor()), sb.toString());

		}

		return result;
	}

	/**
	 * @see msi.gama.gui.swt.controls.IPopupProvider#getPopupBackground()
	 */
	// @Override
	public GamaUIColor getPopupBackground() {
		if ( InUserStatus && color != null ) { return color; }
		return state == IGui.ERROR ? IGamaColors.ERROR
			: state == IGui.WAIT ? IGamaColors.WARNING : state == IGui.NEUTRAL ? IGamaColors.NEUTRAL : IGamaColors.OK;
	}

	@Override
	public Shell getControllingShell() {
		return label.getShell();
	}

	@Override
	public Point getAbsoluteOrigin() {
		return label.toDisplay(new Point(label.getLocation().x, label.getSize().y));
	}

	//
	// Runnable updater = new Runnable() {
	//
	// @Override
	// public void run() {}
	//
	// };

	/**
	 * Method updateWith()
	 * @see msi.gama.gui.swt.controls.ThreadedUpdater.IUpdaterTarget#updateWith(java.lang.Object)
	 */
	@Override
	public void updateWith(final IStatusMessage m) {
		if ( isUpdating ) { return; }
		isUpdating = true;
		if ( m instanceof SubTaskMessage ) {
			if ( InUserStatus ) { return; }
			final SubTaskMessage m2 = (SubTaskMessage) m;
			final Boolean beginOrEnd = m2.getBeginOrEnd();
			if ( beginOrEnd == null ) {
				// completion
				subTaskCompletion = ((SubTaskMessage) m).getCompletion();
			} else if ( beginOrEnd ) {
				// begin task
				subTaskName = m.getText();
				inSubTask = true;
				subTaskCompletion = null;
			} else {
				// end task
				inSubTask = false;
				subTaskCompletion = null;
			}
		} else if ( m instanceof UserStatusMessage ) {
			final String s = m.getText();
			if ( s == null ) {
				resume();
			} else {
				inSubTask = false; // in case
				InUserStatus = true;
				final GamaColor c = m.getColor();
				if ( c == null ) {
					color = null;
					state = IGui.NEUTRAL;
				} else {
					color = GamaColors.get(c);
				}
				mainTaskName = m.getText();
			}
		} else if ( m instanceof StatusMessage ) {
			if ( InUserStatus ) { return; }
			inSubTask = false; // in case
			mainTaskName = m.getText();
			state = m.getCode();
		}

		// updater.run();
		if ( m.getIcon() != null ) {
			label.setImage(GamaIcons.create(m.getIcon()).image());
		} else {
			label.setImage(null);
		}
		label.setColor(getPopupBackground());
		if ( inSubTask ) {
			label.setText(
				subTaskName + (subTaskCompletion != null ? " [" + (int) (subTaskCompletion * 100) + "%]" : ""));
		} else {
			label.setText(mainTaskName == null ? "" : mainTaskName);
		}
		if ( popup.isVisible() ) {
			popup.display();
		}
		isUpdating = false;
		InUserStatus = false;

	}

	@Override
	public int getCurrentState() {
		return state;
	}

	@Override
	public boolean isDynamic() {
		return false;
	}

	/**
	 * Method resume()
	 * @see msi.gama.common.interfaces.IUpdaterTarget#resume()
	 */
	@Override
	public void resume() {
		InUserStatus = false;
		color = null;
		mainTaskName = null;
	}

}
