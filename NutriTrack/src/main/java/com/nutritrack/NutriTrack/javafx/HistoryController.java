package com.nutritrack.NutriTrack.javafx;

import com.nutritrack.NutriTrack.config.StageManager;
import com.nutritrack.NutriTrack.entity.Meal;
import com.nutritrack.NutriTrack.entity.User;
import com.nutritrack.NutriTrack.service.MealService;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import org.springframework.stereotype.Controller;

import java.io.File;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
public class HistoryController {

    private final MealService mealService;
    private final StageManager stageManager;

    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private Text totalMealsText;
    @FXML private Text avgCaloriesText;
    @FXML private Text commonMealText;
    @FXML private TextField searchField;
    @FXML private TableView<Meal> mealsTable;
    @FXML private TableColumn<Meal, LocalDate> dateColumn;
    @FXML private TableColumn<Meal, LocalDateTime> timeColumn;
    @FXML private TableColumn<Meal, String> mealTypeColumn;
    @FXML private TableColumn<Meal, String> foodNameColumn;
    @FXML private TableColumn<Meal, Double> portionColumn;
    @FXML private TableColumn<Meal, Double> caloriesColumn;
    @FXML private TableColumn<Meal, Double> proteinColumn;
    @FXML private TableColumn<Meal, Double> carbsColumn;
    @FXML private TableColumn<Meal, Double> fatColumn;
    @FXML private TableColumn<Meal, String> notesColumn;
    @FXML private TableColumn<Meal, Void> actionsColumn;
    @FXML private Pagination pagination;

    private FilteredList<Meal> filteredData;

    private static final int ROWS_PER_PAGE = 15;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public HistoryController(MealService mealService, StageManager stageManager) {
        this.mealService = mealService;
        this.stageManager = stageManager;
    }

    @FXML
    private void initialize() {
        setupTableColumns();
        setupSearchFilter();
        loadAllMeals();
        pagination.currentPageIndexProperty().addListener((obs, oldIndex, newIndex) -> updateTablePage(newIndex.intValue()));
    }

    private void setupTableColumns() {
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("logDate"));
        foodNameColumn.setCellValueFactory(new PropertyValueFactory<>("mealName"));
        mealTypeColumn.setCellValueFactory(new PropertyValueFactory<>("mealType"));
        caloriesColumn.setCellValueFactory(new PropertyValueFactory<>("calories"));
        proteinColumn.setCellValueFactory(new PropertyValueFactory<>("proteinG"));
        carbsColumn.setCellValueFactory(new PropertyValueFactory<>("carbsG"));
        fatColumn.setCellValueFactory(new PropertyValueFactory<>("fatG"));
        portionColumn.setCellValueFactory(new PropertyValueFactory<>("portionSize"));
        notesColumn.setCellValueFactory(new PropertyValueFactory<>("notes"));
        timeColumn.setCellValueFactory(new PropertyValueFactory<>("createdAt"));

        // Custom cell factory to format the time from LocalDateTime
        timeColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.format(DateTimeFormatter.ofPattern("HH:mm")));
                }
            }
        });

        // Add action buttons
        actionsColumn.setCellFactory(param -> new TableCell<>() {
            private final Button editButton = new Button("Edit");
            private final Button deleteButton = new Button("Delete");
            private final HBox pane = new HBox(editButton, deleteButton);

            {
                pane.setSpacing(5);
                editButton.setOnAction(event -> {
                    Meal meal = getTableView().getItems().get(getIndex());
                    // TODO: Implement edit functionality
                });
                deleteButton.setOnAction(event -> {
                    Meal meal = getTableView().getItems().get(getIndex());
                    deleteMeal(meal);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pane);
            }
        });
    }

    private void setupSearchFilter() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(meal -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();
                return meal.getMealName().toLowerCase().contains(lowerCaseFilter) ||
                       meal.getMealType().toString().toLowerCase().contains(lowerCaseFilter);
            });
            updatePagination();
        });
    }

    private void loadAllMeals() {
        User currentUser = stageManager.getLoggedInUser();
        if (currentUser == null) return;

        List<Meal> allMeals = mealService.getAllMealsForUser(currentUser.getId());
        filteredData = new FilteredList<>(FXCollections.observableArrayList(allMeals));
        updateSummary(allMeals);
        updatePagination();
    }

    @FXML
    private void onApplyFilter() {
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();

        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            // Show error
            return;
        }

        filteredData.setPredicate(meal -> {
            LocalDate mealDate = meal.getLogDate();
            boolean after = startDate == null || !mealDate.isBefore(startDate);
            boolean before = endDate == null || !mealDate.isAfter(endDate);
            return after && before;
        });
        updatePagination();
    }

    @FXML
    private void onClearFilter() {
        startDatePicker.setValue(null);
        endDatePicker.setValue(null);
        searchField.clear();
        filteredData.setPredicate(null);
        updatePagination();
    }

    private void updatePagination() {
        int pageCount = (int) Math.ceil((double) filteredData.size() / ROWS_PER_PAGE);
        if (pageCount == 0) pageCount = 1;
        pagination.setPageCount(pageCount);
        pagination.setCurrentPageIndex(0);
        updateTablePage(0);

        updateSummary(filteredData);
    }

    private void updateTablePage(int pageIndex) {
        int fromIndex = pageIndex * ROWS_PER_PAGE;
        int toIndex = Math.min(fromIndex + ROWS_PER_PAGE, filteredData.size());
        mealsTable.setItems(FXCollections.observableArrayList(filteredData.subList(fromIndex, toIndex)));
    }

    private void updateSummary(List<Meal> meals) {
        totalMealsText.setText(String.valueOf(meals.size()));

        double avgCalories = meals.stream()
                .mapToDouble(Meal::getCalories)
                .average()
                .orElse(0.0);
        avgCaloriesText.setText(String.format("%.0f kcal", avgCalories));

        Optional<String> mostCommon = meals.stream()
                .collect(Collectors.groupingBy(Meal::getMealName, Collectors.counting()))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey);

        commonMealText.setText(mostCommon.orElse("-"));
    }

    private void deleteMeal(Meal meal) {
        mealService.deleteMeal(meal.getId());
        loadAllMeals(); // Refresh data
    }

    @FXML
    private void onExport() {
        File file = new File("meal_history.csv");
        try (PrintWriter writer = new PrintWriter(file)) {
            writer.println("Date,MealType,Food,Calories,Protein,Carbs,Fat");
            for (Meal meal : filteredData) {
                writer.printf("%s,%s,%s,%.2f,%.2f,%.2f,%.2f\n",
                        DATE_FORMATTER.format(meal.getLogDate()),
                        meal.getMealType(),
                        meal.getMealName(),
                        meal.getCalories(),
                        meal.getProteinG(),
                        meal.getCarbsG(),
                        meal.getFatG());
            }
        } catch (Exception e) {
            // Handle exception
        }
    }
}
