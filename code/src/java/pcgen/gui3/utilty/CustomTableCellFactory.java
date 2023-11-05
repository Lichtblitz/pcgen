package pcgen.gui3.utilty;

import java.util.Objects;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NonNull;

import javafx.application.Platform;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

@Builder(access = AccessLevel.PROTECTED)
public class CustomTableCellFactory<S, T> extends TableCell<S, T>
{
    private final Class<? extends BoundController<Property<T>>> fxmlControllerClass;
    private final Property<T> valueProperty = new SimpleObjectProperty<>();
    private Node graphic;
    private boolean isExternalUpdate;

    public static <S, T> Callback<TableColumn<S, T>, TableCell<S, T>>
    forTableColumn(@NonNull Class<? extends BoundController<Property<T>>> fxmlControllerClass)
    {
        return column -> CustomTableCellFactory.<S, T>builder()
                .fxmlControllerClass(fxmlControllerClass)
                .build();
    }

    protected void initialize()
    {
        if (graphic != null)
        {
            return;
        }
        JavaFXLoader.Components<Node, ? extends BoundController<Property<T>>> loaded = JavaFXLoader.load(fxmlControllerClass);
        graphic = loaded.fxml();

            valueProperty.addListener((observable, oldValue, newValue) -> {
                if (isExternalUpdate || Objects.equals(oldValue, newValue))
                {
                    return;
                }
                Platform.runLater(() -> {
                    startEdit();
                    commitEdit(newValue);
                    requestFocus();
                });
            });
        loaded.controller().bind(valueProperty);
        loaded.controller().isEditing().subscribe(isEditing -> {
            if (isEditing)
            {
                getTableView().getSelectionModel().clearAndSelect(getTableRow().getIndex(), getTableColumn());
            }
            else
            {
                getTableView().getSelectionModel().clearSelection();
            }
        });
    }

    @Override
    protected void updateItem(T item, boolean empty)
    {
        isExternalUpdate = true;
        try
        {
            super.updateItem(item, empty);

            if (empty)
            {
                setGraphic(null);
            }
            else
            {
                initialize();

                valueProperty.setValue(item);
                setGraphic(graphic);
            }
        }
        finally
        {
            isExternalUpdate = false;
        }
    }
}
