/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2014 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * An additional term exception under section 7 of the GNU Affero
 * General Public License, version 3, is available at
 * http://developer.catrobat.org/license_additional_term
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.content.bricks;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.MessageContainer;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.drone.DroneConfigManager;
import org.catrobat.catroid.drone.DroneInitializer;

import java.util.List;

public class DroneSetConfig extends BrickBaseType{
	private static final long serialVersionUID = 1L;

	protected String broadcastMessage;
	protected transient AdapterView<?> adapterView;
	private String selectedMessage;

/*	protected Object readResolve() {
		MessageContainer.addMessage(broadcastMessage);
		return this;
	}*/

	public DroneSetConfig() {//String droneConfig) {
//		this.broadcastMessage = droneConfig;
//		MessageContainer.addMessage(droneConfig);
	}

	@Override
	public Brick copyBrickForSprite(Sprite sprite) {
		DroneSetConfig copyBrick = (DroneSetConfig) clone();
		return copyBrick;
	}

	@Override
	public Brick clone() {
		return new DroneSetConfig();//broadcastMessage);
	}

	@Override
	public int getRequiredResources() {
		return NO_RESOURCES;
	}

	@Override
	public View getView(final Context context, int brickId, BaseAdapter baseAdapter) {
		if (animationState) {
			return view;
		}
		if (view == null) {
			alphaValue = 255;
		}
		view = View.inflate(context, R.layout.brick_set_config, null);
		view = getViewWithAlpha(alphaValue);
		setCheckboxView(R.id.brick_set_config_checkbox);

		checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				checked = isChecked;
				adapter.handleCheck(DroneSetConfig.this, isChecked);
			}
		});

		final Spinner setConfigSpinner = (Spinner) view.findViewById(R.id.brick_set_config_spinner);
		setConfigSpinner.setFocusableInTouchMode(false);
		setConfigSpinner.setFocusable(false);


		ArrayAdapter<CharSequence> droneSetConfigAdapter = ArrayAdapter.createFromResource(context,
				R.array.drone_config_spinner, android.R.layout.simple_spinner_item);
		droneSetConfigAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		setConfigSpinner.setAdapter(droneSetConfigAdapter);

		if (!(checkbox.getVisibility() == View.VISIBLE)) {
			setConfigSpinner.setClickable(true);
			setConfigSpinner.setEnabled(true);
		} else {
			setConfigSpinner.setClickable(false);
			setConfigSpinner.setEnabled(false);
		}



		setConfigSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				selectedMessage = setConfigSpinner.getSelectedItem().toString();

				if (selectedMessage.compareTo(context.getString(R.string.drone_config_default)) == 0 && DroneInitializer.droneControlService != null){
					DroneConfigManager.getInstance().setDefaultConfig();
				} else if (selectedMessage.compareTo(context.getString(R.string.drone_config_indoor)) == 0 && DroneInitializer.droneControlService != null){
					DroneConfigManager.getInstance().setIndoorConfig();
				} else if (selectedMessage.compareTo(context.getString(R.string.drone_config_outdoor)) == 0 && DroneInitializer.droneControlService != null){
					DroneConfigManager.getInstance().setOutdoorConfig();
				}

				broadcastMessage = selectedMessage;
				adapterView = parent;
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});

		setSpinnerSelection(setConfigSpinner);
		return view;
	}

	@Override
	public View getPrototypeView(Context context) {
		View prototypeView = View.inflate(context, R.layout.brick_set_config, null);
		Spinner broadcastSpinner = (Spinner) prototypeView.findViewById(R.id.brick_set_config_spinner);
		broadcastSpinner.setFocusableInTouchMode(false);
		broadcastSpinner.setFocusable(false);
		SpinnerAdapter broadcastSpinnerAdapter = MessageContainer.getMessageAdapter(context);
		broadcastSpinner.setAdapter(broadcastSpinnerAdapter);
		setSpinnerSelection(broadcastSpinner);
		return prototypeView;
	}

	@Override
	public View getViewWithAlpha(int alphaValue) {

		if (view != null) {

			View layout = view.findViewById(R.id.brick_set_config_layout);
			Drawable background = layout.getBackground();
			background.setAlpha(alphaValue);

			TextView textBroadcastLabel = (TextView) view.findViewById(R.id.brick_set_config_label);
			textBroadcastLabel.setTextColor(textBroadcastLabel.getTextColors().withAlpha(alphaValue));
			Spinner broadcastSpinner = (Spinner) view.findViewById(R.id.brick_set_config_spinner);
			ColorStateList color = textBroadcastLabel.getTextColors().withAlpha(alphaValue);
			broadcastSpinner.getBackground().setAlpha(alphaValue);
			if (adapterView != null) {
				((TextView) adapterView.getChildAt(0)).setTextColor(color);
			}

			this.alphaValue = (alphaValue);

		}

		return view;
	}

	protected void setSpinnerSelection(Spinner spinner) {
		int position = MessageContainer.getPositionOfMessageInAdapter(spinner.getContext(), broadcastMessage);
		spinner.setSelection(position, true);
	}

/*	// TODO: BroadcastBrick and BroadcastReceiverBrick contain this identical method.
	protected void showNewMessageDialog(final Spinner spinner) {
		final Context context = spinner.getContext();
		BrickTextDialog editDialog = new BrickTextDialog() {

			@Override
			protected void initialize() {
				inputTitle.setText(R.string.dialog_new_broadcast_message_name);
			}

			@Override
			protected boolean handleOkButton() {
				String newMessage = (input.getText().toString()).trim();
				if (newMessage.isEmpty() || newMessage.equals(context.getString(R.string.new_broadcast_message))) {
					dismiss();
					return false;
				}

				broadcastMessage = newMessage;
				MessageContainer.addMessage(broadcastMessage);
				setSpinnerSelection(spinner);
				return true;
			}

			@Override
			public void onDismiss(DialogInterface dialog) {
				setSpinnerSelection(spinner);
				super.onDismiss(dialog);
			}

			@Override
			protected String getTitle() {
				return getString(R.string.dialog_new_broadcast_message_title);
			}

		};

		editDialog.show(((FragmentActivity) context).getSupportFragmentManager(), "dialog_broadcast_brick");
	}*/

	@Override
	public List<SequenceAction> addActionToSequence(Sprite sprite, SequenceAction sequence) {
	/*	if (selectedMessage.compareTo(getClass().getResource().getString(R.string.drone_config_default)) == 0 && DroneInitializer.droneControlService != null){
			//DroneConfigManager.getInstance().setDefaultConfig();
			sequence.addAction(ExtendedActions.droneSetConfig());
		} else if (selectedMessage.compareTo(getClass().getResource().getString(R.string.drone_config_indoor)) == 0 && DroneInitializer.droneControlService != null){
			//DroneConfigManager.getInstance().setIndoorConfig();
		} else if (selectedMessage.compareTo(getClass().getResource().getString(R.string.drone_config_outdoor)) == 0 && DroneInitializer.droneControlService != null){
			//DroneConfigManager.getInstance().setOutdoorConfig();
		}*/

		//sequence.addAction(ExtendedActions.broadcast(sprite, broadcastMessage));
		return null;
	}
}