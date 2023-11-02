package pcgen.gui3.utilty;

import javafx.beans.property.Property;

public interface BoundController<T extends Property<?>>
{
    void bind(T property);
}
