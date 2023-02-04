package qble2.pdf.viewer.gui;

import java.util.LinkedList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.UUID;
import java.util.stream.Collectors;
import javafx.beans.property.BooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.Side;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

// inspired from @author Caleb Brinkman
// https://gist.github.com/floralvikings/10290131
public class AutoCompleteTextField extends TextField {

  private static final int MAX_ENTRIES = 20;

  private final SortedSet<String> suggestions;
  private ContextMenu suggestionsPopup;

  private BooleanProperty isSuggestionsActiveBooleanProperty;

  public AutoCompleteTextField(String searchPromptText,
      BooleanProperty isSuggestionsActiveBooleanPropertyArg) {
    super();

    this.isSuggestionsActiveBooleanProperty = isSuggestionsActiveBooleanPropertyArg;

    setPromptText(searchPromptText);
    setFocusTraversable(false);

    suggestions = new TreeSet<>();
    suggestionsPopup = new ContextMenu();
    textProperty().addListener(new ChangeListener<String>() {
      @Override
      public void changed(ObservableValue<? extends String> observableValue, String s, String s2) {
        if (isSuggestionsActiveBooleanProperty.get()) {
          if (getText().length() == 0) {
            suggestionsPopup.hide();
          } else {
            LinkedList<String> searchResult = new LinkedList<>();

            List<String> filteredEntries =
                suggestions.stream().filter(e -> e.toLowerCase().contains(getText().toLowerCase()))
                    .collect(Collectors.toList());
            searchResult.addAll(filteredEntries);

            if (suggestions.size() > 0) {
              populatePopup(searchResult);
              if (!suggestionsPopup.isShowing()) {
                suggestionsPopup.show(AutoCompleteTextField.this, Side.BOTTOM, 0, 0);
              }
            } else {
              suggestionsPopup.hide();
            }
          }
        }
      }
    });

    focusedProperty().addListener(new ChangeListener<Boolean>() {
      @Override
      public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean,
          Boolean aBoolean2) {
        suggestionsPopup.hide();
      }
    });

  }

  public SortedSet<String> getSuggestions() {
    return suggestions;
  }

  private void populatePopup(List<String> searchResult) {
    List<CustomMenuItem> menuItems = new LinkedList<>();
    int count = Math.min(searchResult.size(), MAX_ENTRIES);
    for (int i = 0; i < count; i++) {
      final String result = searchResult.get(i);
      Label suggestionLabel = new Label(result);

      CustomMenuItem item = new CustomMenuItem(suggestionLabel, true);
      item.setOnAction(new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent actionEvent) {
          setText(result);
          suggestionsPopup.hide();

          fireAutoCompletedEvent(result);
        }
      });
      menuItems.add(item);
    }
    suggestionsPopup.getItems().clear();
    suggestionsPopup.getItems().addAll(menuItems);

    if (searchResult.size() > MAX_ENTRIES) {
      CustomMenuItem moreCustomMenuItem =
          new CustomMenuItem(new Label(String.format("%d more matches...",
              Math.subtractExact(searchResult.size(), MAX_ENTRIES))), true);
      moreCustomMenuItem.setDisable(true);
      suggestionsPopup.getItems().add(moreCustomMenuItem);
    }
  }

  public void updateSuggestions(List<String> entries) {
    this.suggestions.clear();
    if (entries != null) {
      this.suggestions.addAll(entries);
    }
  }

  protected void fireAutoCompletedEvent(String completion) {
    Event.fireEvent(this, new AutoCompletedEvent(completion));
  }

  public static class AutoCompletedEvent extends Event {
    private static final long serialVersionUID = 1L;

    public static final EventType<AutoCompletedEvent> AUTO_COMPLETED =
        new EventType<>("AUTO_COMPLETED" + UUID.randomUUID().toString());

    private final String completion;

    public AutoCompletedEvent(String completion) {
      super(AUTO_COMPLETED);
      this.completion = completion;
    }

    public String getCompletion() {
      return completion;
    }
  }

}
