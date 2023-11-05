package pcgen.gui3.utilty;

import java.io.IOException;
import java.net.URL;
import lombok.NonNull;
import pcgen.system.LanguageBundle;

import javafx.fxml.FXMLLoader;

public class JavaFXLoader
{

    public record Components<T, U>(T fxml, U controller)
    {
    }

    public static <T, U> Components<T, U> load(@NonNull Class<U> controllerClass)
    {
        URL resource = controllerClass.getResource(controllerClass.getSimpleName() + ".fxml");

        FXMLLoader loader = new FXMLLoader(resource, LanguageBundle.getBundle());

        try
        {
            return new Components<>(loader.load(), loader.getController());
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }
}
