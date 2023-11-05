package pcgen.gui3.tabs.summary;

import lombok.Getter;
import pcgen.gui3.utilty.BoundController;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Spinner;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

@Getter
public class EditableStatCell implements BoundController<Property<Integer>>
{
    private final Property<Integer> externalProperty = new SimpleObjectProperty<>();
    private final BooleanProperty isEditingProperty = new SimpleBooleanProperty(false);

    @FXML
    public Spinner<Integer> spinner;

    public void initialize(){
        spinner.focusWithinProperty().addListener((observable, oldValue, newValue)->{
            if (oldValue && ! newValue){
                externalProperty.setValue(spinner.getValueFactory().getValue());
            }
            isEditingProperty.set(newValue);
        });

        externalProperty.subscribe(value -> spinner.getValueFactory().setValue(value));
    }

    @Override
    public void bind(Property<Integer> property)
    {
        externalProperty.bindBidirectional(property); // don't bind the spinner directly; only update when we lose focus
    }

    @Override
    public ReadOnlyBooleanProperty isEditing()
    {
        return ReadOnlyBooleanWrapper.readOnlyBooleanProperty(isEditingProperty);
    }

    public void onKeyReleased(KeyEvent keyEvent)
    {
        if (KeyCode.ENTER.equals(keyEvent.getCode())){
            externalProperty.setValue(spinner.getValueFactory().getValue());
        }
    }
}
