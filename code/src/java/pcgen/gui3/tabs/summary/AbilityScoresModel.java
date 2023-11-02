package pcgen.gui3.tabs.summary;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.Value;
import org.jetbrains.annotations.NotNull;
import pcgen.core.PCStat;
import pcgen.facade.core.CharacterFacade;
import pcgen.facade.util.event.ReferenceEvent;
import pcgen.facade.util.event.ReferenceListener;

import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

@SuppressWarnings("rawtypes")
public class AbilityScoresModel implements ReferenceListener
{
    private final CharacterFacade characterFacade;
    @Getter
    private final ObservableList<StatValues> pcStatList = FXCollections.observableArrayList();

    @Value
    @EqualsAndHashCode(onlyExplicitlyIncluded = true) // Properties don't implement a sane equals method
    public static class StatValues
    {
        @Builder
        private StatValues(@NonNull String label,
                           int mod,
                           int base,
                           int raceBonus,
                           @NonNull String total,
                           int otherBonus,
                           @NonNull ChangeListener<Number> changeListener)
        {
            this.label.set(label);
            this.mod.set(mod);
            this.base.set(base);
            this.raceBonus.set(raceBonus);
            this.total.set(total);
            this.otherBonus.set(otherBonus);

            this.base.addListener(changeListener);
        }

        StringProperty label = new SimpleStringProperty();
        IntegerProperty mod = new SimpleIntegerProperty();
        IntegerProperty base = new SimpleIntegerProperty();
        IntegerProperty raceBonus = new SimpleIntegerProperty();
        StringProperty total = new SimpleStringProperty();
        IntegerProperty otherBonus = new SimpleIntegerProperty();

        @EqualsAndHashCode.Include
        private String getLabelValue()
        {
            return label.get();
        }

        @EqualsAndHashCode.Include
        private int getModValue()
        {
            return mod.getValue();
        }

        @EqualsAndHashCode.Include
        private int getBaseValue()
        {
            return base.getValue();
        }

        @EqualsAndHashCode.Include
        private int getRaceBonusValue()
        {
            return raceBonus.getValue();
        }

        @EqualsAndHashCode.Include
        private String getTotalValue()
        {
            return total.getValue();
        }

        @EqualsAndHashCode.Include
        private int getOtherBonusValue()
        {
            return otherBonus.getValue();
        }
    }

    public AbilityScoresModel(CharacterFacade characterFacade)
    {
        this.characterFacade = characterFacade;
    }

    private List<PCStat> getStats()
    {
        List<PCStat> result = new ArrayList<>();

        characterFacade.getDataSet().getStats().forEach(result::add);

        return result;
    }

    @SuppressWarnings("unchecked")
    public void install()
    {
        getStats().forEach(stat -> characterFacade.getScoreBaseRef(stat).addReferenceListener((ReferenceListener<? super Number>) this));
        characterFacade.addStatsBonusesChangedListener(this);
        rebuildStatList();
        //individual Listeners
    }

    @SuppressWarnings("unchecked")
    public void uninstall()
    {
        getStats().forEach(stat -> characterFacade.getScoreBaseRef(stat).removeReferenceListener((ReferenceListener<? super Number>) this));
        characterFacade.addStatsBonusesChangedListener(this);
    }

    @Override
    @SuppressWarnings("rawtypes")
    public void referenceChanged(ReferenceEvent e)
    {
        // defer rebuilding because the backend is still in the middle of applying the bonuses
        Platform.runLater(this::rebuildStatList);
    }

    private void rebuildStatList()
    {
        List<PCStat> stats = getStats();

        if (pcStatList.size() != stats.size())
        {
            // full rebuild; should only happen when the list is still empty - after that the sizes should always be the same
            pcStatList.setAll(
                    stats.stream()
                            .map(stat -> createObjectProperty(stat, characterFacade))
                            .collect(Collectors.toList()));
        }
        else
        {
            // only change the properties, which will only raise events if the new values are different
            for (int i = 0; i < stats.size(); i++)
            {
                overrideObjectProperty(stats.get(i), characterFacade, pcStatList.get(i));
            }
        }
    }

    @NotNull
    private static StatValues createObjectProperty(PCStat stat, CharacterFacade character)
    {
        return StatValues.builder()
                .label(stat.getDisplayName())
                .mod(character.getModTotal(stat))
                .base(character.getScoreBase(stat))
                .raceBonus(character.getScoreRaceBonus(stat))
                .total(character.getScoreTotalString(stat))
                .otherBonus(character.getScoreOtherBonus(stat))
                .changeListener((observable, oldValue, newValue) ->
                        character.setScoreBase(stat, newValue.intValue()))
                .build();
    }

    @NotNull
    private static void overrideObjectProperty(PCStat stat, CharacterFacade character, StatValues values)
    {
        values.getLabel().setValue(stat.getDisplayName());
        values.getMod().setValue(character.getModTotal(stat));
        values.getBase().setValue(character.getScoreBase(stat));
        values.getRaceBonus().setValue(character.getScoreRaceBonus(stat));
        values.getTotal().setValue(character.getScoreTotalString(stat));
        values.getOtherBonus().setValue(character.getScoreOtherBonus(stat));
    }
}
