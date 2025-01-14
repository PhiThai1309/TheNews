package main.SceneController;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;

public class CategoryController extends SceneHandler implements Initializable {
    // UI components in Category scene
    @FXML private AnchorPane anchorPane;
    @FXML private Button mainMenu;
    @FXML private Button button1;
    @FXML private Button button2;
    @FXML private Button button3;
    @FXML private Button button4;
    @FXML private Button button5;
    @FXML private Button button6;
    @FXML private Button button7;
    @FXML private Button button8;
    @FXML private Button button9;
    @FXML private Button button10;

    private final ArrayList<Button> buttons = new ArrayList<>();
    private final int previousCategoryIndex;

    public CategoryController(int categoryIndex) {
        this.previousCategoryIndex = categoryIndex;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Set return button to return to main menu
        mainMenu.setOnAction(e -> menuHome(previousCategoryIndex, false));
        addNodeToList();

        // Assigning category buttons to read articles from specific category
        for (int i = 0; i < buttons.size(); i++){
            int categoryIndex = i;

            buttons.get(i).setOnAction(e -> menuHome(categoryIndex, true));
        }
    }

    // Function to switch to main menu scene
    public void menuHome(int idx, boolean reload){
        super.menuHome(idx, reload);
    }

    // Add buttons to list for easier access
    private void addNodeToList(){
        buttons.addAll(Arrays.asList(button1, button2, button3, button4, button5, button6, button7, button8, button9, button10));
    }
}