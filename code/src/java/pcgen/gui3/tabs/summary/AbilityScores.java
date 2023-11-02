package pcgen.gui3.tabs.summary;

import lombok.Getter;
import pcgen.gui3.utilty.CustomTableCellFactory;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableIntegerValue;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.Callback;
import javafx.util.converter.IntegerStringConverter;

@Getter
public class AbilityScores
{
    private final ObjectProperty<AbilityScoresModel> abilityScoresModelProperty = new SimpleObjectProperty<>();

    private final ObservableList<AbilityScoresModel.StatValues> pcStatList =
            FXCollections.observableArrayList();/*statValues-> new Observable[]{
                    statValues.getLabel(),
                    statValues.getTotal(),
                    statValues.getMod(),
                    statValues.getBase(),
                    statValues.getRaceBonus(),
                    statValues.getOtherBonus()
            });*/

    @FXML
    public TableColumn<AbilityScoresModel.StatValues, Integer> editableColumn;

    public AbilityScores()
    {
        abilityScoresModelProperty.subscribe((model -> {
            if (model != null)
            {
                AbilityScoresModel previousModel = abilityScoresModelProperty.get();
                if (previousModel != null)
                {
                    Bindings.unbindContentBidirectional(pcStatList, model.getPcStatList());
                }
                abilityScoresModelProperty.set(model);
                Bindings.bindContentBidirectional(pcStatList, model.getPcStatList());
                assert !pcStatList.isEmpty();
            }
        }));
    }

    public Callback<TableColumn.CellDataFeatures<AbilityScoresModel.StatValues, String>, ObservableValue<String>> getLabelFactory()
    {
        return cellDataFeatures -> cellDataFeatures.getValue().getLabel();
    }

    public Callback<TableColumn.CellDataFeatures<AbilityScoresModel.StatValues, String>, ObservableValue<String>> getTotalFactory()
    {
        return cellDataFeatures -> cellDataFeatures.getValue().getTotal();
    }

    public Callback<TableColumn.CellDataFeatures<AbilityScoresModel.StatValues, Integer>, ObservableIntegerValue> getModFactory()
    {
        return cellDataFeatures -> cellDataFeatures.getValue().getMod();
    }

    public Callback<TableColumn.CellDataFeatures<AbilityScoresModel.StatValues, Integer>, ObservableIntegerValue> getBaseFactory()
    {
        return cellDataFeatures -> cellDataFeatures.getValue().getBase();
    }

    public Callback<TableColumn<AbilityScoresModel.StatValues, Integer>, TableCell<AbilityScoresModel.StatValues, Integer>> getBaseCellFactory()
    {
        return TextFieldTableCell.forTableColumn(new IntegerStringConverter());
    }

    public Callback<TableColumn<AbilityScoresModel.StatValues, Integer>, TableCell<AbilityScoresModel.StatValues, Integer>> getCustomizationCellFactory()
    {
        return CustomTableCellFactory.forTableColumn(EditableStatCell.class);
    }

    public Callback<TableColumn.CellDataFeatures<AbilityScoresModel.StatValues, Integer>, ObservableIntegerValue> getRaceBonusFactory()
    {
        return cellDataFeatures -> cellDataFeatures.getValue().getRaceBonus();
    }

    public Callback<TableColumn.CellDataFeatures<AbilityScoresModel.StatValues, Integer>, ObservableIntegerValue> getOtherBonusFactory()
    {
        return cellDataFeatures -> cellDataFeatures.getValue().getOtherBonus();
    }
}
