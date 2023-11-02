package pcgen.gui3.utilty;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NonNull;
import pcgen.system.LanguageBundle;

import javafx.application.Platform;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

@Builder(access = AccessLevel.PROTECTED)
public class CustomTableCellFactory<S, T> extends TableCell<S, T>
{
    private final Class<?> fxmlControllerClass;
    private final Property<T> valueProperty = new SimpleObjectProperty<>();
    private Node graphic;
    private boolean isExternalUpdate;

    public static <S, T> Callback<TableColumn<S, T>, TableCell<S, T>>
    forTableColumn(@NonNull Class<? extends BoundController<? extends Property<T>>> fxmlControllerClass)
    {
        return list -> CustomTableCellFactory.<S, T>builder()
                .fxmlControllerClass(fxmlControllerClass)
                .build();
    }

    protected void initialize()
    {
        if (graphic != null)
        {
            return;
        }
        try
        {
            URL resource = fxmlControllerClass.getResource(fxmlControllerClass.getSimpleName() + ".fxml");
            FXMLLoader loader = new FXMLLoader(resource, LanguageBundle.getBundle());
            graphic = loader.load();
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
            loader.<BoundController<Property<T>>>getController().bind(valueProperty);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
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
