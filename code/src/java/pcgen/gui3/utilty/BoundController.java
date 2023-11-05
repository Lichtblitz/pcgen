package pcgen.gui3.utilty;

import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyBooleanProperty;

public interface BoundController<T extends Property<?>>
{
    void bind(T property);

    ReadOnlyBooleanProperty isEditing();
}
