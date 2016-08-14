package noaa.ioos.comt.jut;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.File;

public class App extends Application {
	
    private TextField searchBar = new TextField();
    private TextField fileTypeField = new TextField();
    private TextField fileSizeField = new TextField();
    private TextField standardsField = new TextField();
    private Rectangle dragAndDropField = new Rectangle();

    private File droppedFile;

    @Override
    public void start(Stage primaryStage) throws Exception{

    	primaryStage.setTitle("CFJut");

        // Run helper method to setup root VBox
        VBox rootNode = configureLayout();

        Scene mainScene = new Scene(rootNode, 440, 350);

        primaryStage.setScene(mainScene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    private VBox configureLayout() {
        // First row search bar (with icon) and upload button
        searchBar.setMinWidth(250);
        searchBar.setPromptText("Search");

        ImageView searchIcon = new ImageView();
        Image searchIconImage = new Image("/search.png");
        searchIcon.setImage(searchIconImage);
        searchIcon.setFitHeight(25);
        searchIcon.setFitWidth(25);

        StackPane searchBarStack = new StackPane();
        searchBarStack.setAlignment(Pos.CENTER_RIGHT);
        searchBarStack.getChildren().addAll(searchBar, searchIcon);

        Button uploadButton = new Button("Upload");
        uploadButton.setMinWidth(110);

        HBox firstRowHBox = new HBox(searchBarStack, uploadButton);
        firstRowHBox.setSpacing(20);

        // Second row drag/drop field
        dragAndDropField.setWidth(360);
        dragAndDropField.setHeight(100);
        dragAndDropField.setFill(Paint.valueOf("#bbb"));

        Text dragAndDropText = new Text("Drag/drop");

        StackPane dragAndDropStack = new StackPane();
        dragAndDropStack.setAlignment(Pos.CENTER);
        dragAndDropStack.getChildren().addAll(dragAndDropField, dragAndDropText);

        // Set the drag/drop handlers on the stack
        setupDragAndDropHandlers(dragAndDropStack);

        // Third-fifth rows file metadata grid
        GridPane fileMetadataGrid = new GridPane();
        fileMetadataGrid.setVgap(10);
        fileMetadataGrid.setHgap(20);

        fileMetadataGrid.add(new Label("File Type"), 0, 2);
        fileTypeField.setMinWidth(300);
        fileMetadataGrid.add(fileTypeField, 1, 2);

        fileMetadataGrid.add(new Label("File Size"), 0, 3);
        fileSizeField.setMinWidth(300);
        fileMetadataGrid.add(fileSizeField, 1, 3);

        fileMetadataGrid.add(new Label("Standards"), 0, 4);
        standardsField.setMinWidth(300);
        fileMetadataGrid.add(standardsField, 1, 4);

        // Add child nodes to parent VBox
        VBox rootNode = new VBox(firstRowHBox, dragAndDropStack, fileMetadataGrid);
        rootNode.setSpacing(20);
        rootNode.setAlignment(Pos.CENTER_LEFT);
        rootNode.setPadding(new Insets(20));
        return rootNode;
    }

    private void setupDragAndDropHandlers(StackPane dragAndDropStack) {
        // Add file(s) to dragboard
        dragAndDropStack.setOnDragDetected(event -> {
            Dragboard db = dragAndDropStack.startDragAndDrop(TransferMode.COPY_OR_MOVE);
            event.consume();
        });

        // Identify target
        dragAndDropStack.setOnDragOver(event -> {
            if (event.getGestureSource() != dragAndDropStack && event.getDragboard().hasFiles()) {
                event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
            }
            event.consume();
        });

        // Highlight drag/drop rectangle when drag enters
        dragAndDropStack.setOnDragEntered(event -> {
            if (event.getGestureSource() != dragAndDropStack && event.getDragboard().hasFiles()) {
                dragAndDropField.setFill(Paint.valueOf("#ddd"));
            }
            event.consume();
        });

        // Undo highlighting when drag exits
        dragAndDropStack.setOnDragExited(event -> {
            if (event.getGestureSource() != dragAndDropStack && event.getDragboard().hasFiles()) {
                dragAndDropField.setFill(Paint.valueOf("#bbb"));
            }
            event.consume();
        });

        // Retrieve dropped file
        dragAndDropStack.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            boolean success = false;
            if (db.hasFiles()) {
                success = true;
                // File is now stored
                droppedFile = db.getFiles().get(0);
                // Display file info to GUI
                fileTypeField.setText(droppedFile.getName());
                fileSizeField.setText(Long.toString(droppedFile.length()));
                try{
                	Dataset ds = new Dataset(droppedFile.getAbsoluteFile().getAbsolutePath());
                	String json = ds.json();
                	// TODO: we want to send this JSON data to an endpoint instead of printing it
                	System.out.println(json);
                }
                catch (Exception e) {
                	// DEV: do nothing
                }
                finally {
                	
                }
            }
            event.setDropCompleted(success);
            event.consume();
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}

