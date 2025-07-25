package localendar.WidgetControllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import localendar.*;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class MainController implements Initializable {
    private Database db = new Database();
    private HashMap<Integer, Category> categories = db.getCategories();

    // Add ChangeListener for search
    @FXML
    private VBox taskArea,

    //  VBox for calendar
    b1,b2,b3,b4,b5,b6,b7,b8,b9,b10,b11,b12,b13,b14,b15,b16,b17,b18,b19,b20,b21,b22,b23,b24,b25,b26,b27,
            b28,b29,b30,b31,b32,b33,b34,b35;

    @FXML
    private AnchorPane root;

    @FXML
    private TextField searchBar;

    // Filters UI
    @FXML
    private ComboBox<String> sort_priority,sort_due,sort_time;
    @FXML
    private CheckBox completed,uncompleted, enableDateRange, enableTimeRange;
    @FXML
    private DatePicker dateFrom, dateTo;
    @FXML
    private Spinner<Integer> fromHour, fromMinute, toHour, toMinute;
    @FXML
    private VBox categoryFilterList, rRuleFilterList;
    @FXML
    private RadioButton dueTimeRadio,dueDateRadio,priorityRadio;

    // Actual filter
    private boolean completedFilter, uncompletedFilter;
    private LocalDate fromDateFilter, toDateFilter;
    private LocalTime fromTimeFilter, toTimeFilter;
    private ArrayList<Category> categoryFilter;
    private ArrayList<String> rRuleFilter;
    private ToggleGroup group;

    // Month day labels
    @FXML
    private Text monthLabel,
            l1,l2,l3,l4,l5,l6,l7,l8,l9,l10,l11,l12,l13,l14,l15,l16,l17,l18,l19,l20,l21,l22,l23
            ,l24,l25,l26,l27,l28,l29,l30,l31,l32,l33,l34,l35;

    private LocalDate curDate;

    private List<Text> monthDayLabels;
    private List<VBox> monthDayBox;

    private Category selectedCategory;
    private StackPane selectedBox;
    private String selectedRruleFilter;


    // TODO Declare your data structure
    /*
        Priority example bruh
        private final PriorityQueue<Task> tasks;
     */

    // TODO Change list to an instance of your data strucutre
    // Priority queue example: private Map<String,Map<LocalDate,PriorityQueue<Node>>> cache;
    private Map<String,Map<LocalDate,List<Node>>> cache;
    private int cacheLimit;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        group = new ToggleGroup();
        dueDateRadio.setToggleGroup(group);
        dueTimeRadio.setToggleGroup(group);
        priorityRadio.setToggleGroup(group);

        group.selectedToggleProperty().addListener((observable, oldToggle, newToggle) -> {
            if (newToggle != null) {
                RadioButton selected = (RadioButton) newToggle;
                if(selected == dueDateRadio){
                    sort_due.setDisable(false);
                    sort_priority.setDisable(true);
                    sort_time.setDisable(true);
                }
                if(selected == dueTimeRadio){
                    sort_due.setDisable(true);
                    sort_priority.setDisable(true);
                    sort_time.setDisable(false);
                }
                if(selected == priorityRadio){
                    sort_due.setDisable(true);
                    sort_priority.setDisable(false);
                    sort_time.setDisable(true);
                }
            }
        });

        categoryFilter = new ArrayList<>();
        rRuleFilter = new ArrayList<>();
        curDate = LocalDate.now();
        cache = new LinkedHashMap<>(); // Sorted hashmap, needed to remove oldest
        cacheLimit = 6;

        fromHour.setDisable(true);
        fromMinute.setDisable(true);
        toHour.setDisable(true);
        toMinute.setDisable(true);
        fromHour.setEditable(false);
        fromMinute.setEditable(false);
        toHour.setEditable(false);
        toMinute.setEditable(false);

        dateFrom.setDisable(true);
        dateTo.setDisable(true);
        dateFrom.setEditable(false);
        dateTo.setEditable(false);

        // TODO Initialize your data strucutre
        /* Priority queue example:
            tasks = new PriorityQueue<>(new TaskComparator());
            For other data structures, no need taskcomparator, instead you have to compare the
            task for its priority order manually

         */
        fromHour.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(
                0, // Min
                23, // Max
                0 // Default
        ));

        toHour.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(
                0, // Min
                23, // Max
                0 // Default
        ));

        fromMinute.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(
                        0, // Min
                        59, // Max
                        0 // Default
                ));

        toMinute.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(
                        0, // Min
                        59, // Max
                        0 // Default
                ));



        monthDayLabels = new ArrayList<>(Arrays.asList(
                l1, l2, l3, l4, l5, l6, l7, l8, l9, l10,
                l11, l12, l13, l14, l15, l16, l17, l18, l19, l20,
                l21, l22, l23, l24, l25, l26, l27, l28, l29, l30,
                l31, l32, l33, l34, l35
        ));
        monthDayBox = new ArrayList<>(Arrays.asList(
                b1,b2,b3,b4,b5,b6,b7,b8,b9,b10,
                b11,b12,b13,b14,b15,b16,b17,b18,
                b19,b20,b21,b22,b23,b24,b25,b26,b27,
                b28,b29,b30,b31,b32,b33,b34,b35
        ));
        monthLabel.setText(curDate.format(DateTimeFormatter.ofPattern("MMMM, yyyy")));

        sort_priority.setItems( FXCollections.observableArrayList("↕    Priority",
                "↓    Highest to Lowest",
                "↑    Lowest to Highest"));

        sort_priority.setValue("↕    Priority");

        sort_due.setItems( FXCollections.observableArrayList("\uD83D\uDCC6   Due date",
                "↓     Latest to Earliest",
                "↑     Earliest to Latest"));

        sort_due.setValue("\uD83D\uDCC6   Due date");

        sort_time.setItems( FXCollections.observableArrayList("\uD83D\uDD53 Due time",
                "↓   Latest to Earliest",
                "↑   Earliest to Latest"));

        sort_time.setValue("\uD83D\uDD53 Due time");
        long startTime = System.nanoTime(); // Get start time
        try{
            ResultSet taskQueryResult = db.getTasks();
            while(taskQueryResult.next()){
                /* TODO
                Do ur data structure thing here, only loading the tasks to ur data structure
                Priority queue example:
                tasks.add(
                        new Task(
                                taskQueryResult.getString("title"),
                                taskQueryResult.getString("body"),
                                taskQueryResult.getBoolean("status"),
                                LocalDate.parse(taskQueryResult.getString("due_date")),
                                LocalTime.parse(taskQueryResult.getString("time")),
                                taskQueryResult.getInt("priority"),
                                taskQueryResult.getString("rrule"),
                                categories.get(taskQueryResult.getInt("category_id"))
                        )
                );
                 */

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        // TODO
        /*
        Then run generateTaskItem(Task task) for each of your task item in your data structure
        Priority queue example:
            tasks.forEach(task->{
                generateTaskItem(task);
            });
         */
        fromHour.valueProperty().addListener((obs, oldVal, newVal) -> filter());
        fromMinute.valueProperty().addListener((obs, oldVal, newVal) -> filter());
        toHour.valueProperty().addListener((obs, oldVal, newVal) -> filter());
        toMinute.valueProperty().addListener((obs, oldVal, newVal) -> filter());

        populateCalendar();
        long endTime = System.nanoTime(); // Get end time
        Benchmark.getInstance().getTime(startTime,endTime,1); // Calculate the time
        Benchmark.getInstance().getSpace(1); // And space used

    }

    public HashMap<Integer, Category> getCategories(){
        return categories;
    }

    private void populateCalendar(){
        YearMonth currentMonth = YearMonth.from(curDate); //  Gets the year and month
        LocalDate firstOfMonth = currentMonth.atDay(1); // Gets the first day of month
        int startDay = firstOfMonth.getDayOfWeek().getValue();

        // Fill leading labels
        YearMonth tempMonth = YearMonth.from(currentMonth.minusMonths(1));
        System.out.println(tempMonth.lengthOfMonth());
        int j = tempMonth.lengthOfMonth();
        for(int i = startDay-2; i>=0;i--){
            monthDayLabels.get(i).setText(String.valueOf(j--));
        }
        j = 1;
        // To prevent stackoverflow, makes sure that its less than 35
        int LengthMonth = Math.min((currentMonth.lengthOfMonth() + startDay - 1), 35);

        for(int i = startDay-1;i<LengthMonth;i++){
            monthDayLabels.get(i).setText(String.valueOf(j++));
        }

        // Fill in trailing label
        j = 1;
        for (int i = startDay - 1 + currentMonth.lengthOfMonth() ; i < 35; i++) {
            monthDayLabels.get(i).setText(String.valueOf(j++));
        }

        // Fill in cache
        if(cache.keySet().size() < cacheLimit) fillMonth();
        else{
            String oldest = cache.keySet().iterator().next(); // Get the first key
            cache.remove(oldest);
            fillMonth();
        }
    }

    private void fillMonth() {
        YearMonth currentMonth = YearMonth.from(curDate);
        LocalDate firstOfMonth = currentMonth.atDay(1);
        int startDayOfWeek = firstOfMonth.getDayOfWeek().getValue();
        int daysInPrevMonth = startDayOfWeek - 1;
        if (daysInPrevMonth < 0) {
            daysInPrevMonth = 6;
        }
        int daysInCurrentMonth = currentMonth.lengthOfMonth();

        int totalDaysInCalendar = daysInPrevMonth + daysInCurrentMonth;
        int daysInNextMonth = (totalDaysInCalendar <= 35) ? (35 - totalDaysInCalendar) : 0;
        if (cache.containsKey(curDate.toString())) {
            // TODO replace list with your data strucutre: exapmple for priority queue:  Map<LocalDate, PriorityQueue<Node>> dayToNodes = cache.get(curDate.toString());
            Map<LocalDate, List<Node>> dayToNodes = cache.get(curDate.toString());
            if (dayToNodes != null) {
                // Load task nodes from cache and place them in the calendar grid
                for (int day = 1; day <= 35; day++) {
                    int actualDay = (day - daysInPrevMonth) > 0
                            ? (day - daysInPrevMonth)
                            : (daysInPrevMonth - day + daysInPrevMonth + currentMonth.lengthOfMonth());

                    // TODO replace List with your data structure
                    // YOUR DATA STRUCTURE<Node> dayTasks = dayToNodes.get(actualDay);
                    int targetBoxIndex = day - 1;

                    if (dayTasks != null) {
                        if (dayTasks.size() > 2) { // TODO dayTasks.size() depends on data strucutre
                            for (int i = 0; i < 2; i++) {
                                // TODO get node from your data structure and put in motnh
                                monthDayBox.get(targetBoxIndex).getChildren().add(dayTasks.get(i));

                                // Example from priority queue:
                                // Node temp = dayTasks.poll();
                                // monthDayBox.get(targetBoxIndex).getChildren().add(temp);
                                // dayTasks.add(temp);
                            }

                            Label viewMoreLabel = new Label("View More");
                            viewMoreLabel.setAlignment(Pos.CENTER);
                            viewMoreLabel.setOnMouseClicked(e -> {
                                Map<String, Task> taskDisplayMap = new LinkedHashMap<>();

                                for (Node node : tasks) {
                                    CalendarTaskItemController controller = (CalendarTaskItemController) node.getUserData();
                                    if (controller != null) {
                                        Task task = controller.getTask();
                                        String displayString = String.format("%s @ %s", task.getTitle(), task.getDueTime());
                                        // Ensure uniqueness by adding a counter if needed
                                        while (taskDisplayMap.containsKey(displayString)) {
                                            displayString += " ";
                                        }
                                        taskDisplayMap.put(displayString, task);
                                    }
                                }

                                if (taskDisplayMap.isEmpty()) return;

                                ChoiceDialog<String> dialog = new ChoiceDialog<>(
                                    taskDisplayMap.keySet().iterator().next(),
                                    taskDisplayMap.keySet()
                                );
                                dialog.setTitle("Choose a task that day");
                                dialog.setHeaderText(null);
                                dialog.setContentText("Available tasks:");
                                root.setDisable(true);

                                Optional<String> result = dialog.showAndWait();
                                root.setDisable(false);

                                result.ifPresent(taskKey -> {
                                    Task selectedTask = taskDisplayMap.get(taskKey);
                                    try {
                                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/OpenTask.fxml"));
                                        Parent openTask = loader.load();

                                        OpenTaskController controller = loader.getController();
                                        controller.setTask(selectedTask);

                                        Stage taskWindow = new Stage();
                                        taskWindow.setTitle(selectedTask.getTitle());
                                        taskWindow.setScene(new Scene(openTask, 692, 411));
                                        taskWindow.setResizable(false);

                                        root.setDisable(true);
                                        taskWindow.setOnHidden(event -> root.setDisable(false));
                                        taskWindow.show();
                                    } catch (Exception er) {
                                        er.printStackTrace();
                                    }
                                });
                            });
                            monthDayBox.get(targetBoxIndex).getChildren().add(viewMoreLabel);
                            } else {
                            for (Node taskNode : dayTasks) {
                                monthDayBox.get(targetBoxIndex).getChildren().add(taskNode);
                            }
                        }
                    }
                    return; // skip building from scratch
                }
            }
        } else {
            LocalDate startWindow = currentMonth.atDay(1).minusDays(daysInPrevMonth);
            LocalDate endWindow = currentMonth.atEndOfMonth().plusDays(daysInNextMonth);

             // TODO Initialize a hashmap for your data structure with localDate as key, and YOURDATASTRUCTURE<Node> as the value
             // Priority queue example: Map<LocalDate, PriorityQueue<Node>> dayToNodes = new HashMap<>();
             for (Task task : tasks) {
                if (task.getRrule().getFrequency() != Frequency.NONE) {
                    Iterator<Task> iterator = task.iterator(endWindow);
                    while (iterator.hasNext()) {
                        Task instance = iterator.next();
                        LocalDate instDate = instance.getDueDate();
                        if (!instDate.isBefore(startWindow) && !instDate.isAfter(endWindow)) {
                        try {
                            FXMLLoader fxmlLoader = new FXMLLoader(MainController.class.getResource("/CalendarTaskItem.fxml"));
                            Node item = fxmlLoader.load();
                            CalendarTaskItemController controller = fxmlLoader.getController();
                            item.setUserData(controller);
                            controller.setRoot(root);
                            controller.setTask(instance);

                            //TODO for below: add item to your data strucutre, if there is no key add a new instance
                            // of your data structre then add the item in taht key
                            int dayOfMonth = task.getDueDate().getDayOfMonth();  // That task
                            /* TODO Uncomment this
                             HASHMAP INITIALIZED EARLIER.putIfAbsent(dayOfMonth, new instance of YOUR_dATASTRUCTURE);
                             HASHMAP INITIALIZED EARLIER.get(dayOfMonth).add(taskNode);
                            */
                            // You might need to compare each node to sort it properly

                            // Priority queue example:
                            // dayToNodes.putIfAbsent(instDate, new PriorityQueue<>(Comparator.comparing(node -> {
                            //    CalendarTaskItemController ctrl = (CalendarTaskItemController) node.getUserData();
                            //    return ctrl.getTask().getDueTime();
                            // })));
                            // dayToNodes.get(instDate).add(item);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        }
                    }
                } else {
                    // Handle one-time tasks
                    LocalDate taskDate = task.getDueDate();
                    if (!taskDate.isBefore(startWindow) && !taskDate.isAfter(endWindow)) {
                        try {
                            // Same logic as above
                            FXMLLoader fxmlLoader = new FXMLLoader(MainController.class.getResource("/CalendarTaskItem.fxml"));
                            Node item = fxmlLoader.load();
                            CalendarTaskItemController controller = fxmlLoader.getController();
                            item.setUserData(controller);
                            controller.setRoot(root);
                            controller.setTask(task);

                            //TODO for below: add item to your data strucutre, if there is no key add a new instance
                            // of your data structre then add the item in taht key
                            int dayOfMonth = task.getDueDate().getDayOfMonth();  // That task
                            /* TODO Uncomment this
                                HASHMAP INITIALIZED EARLIER.putIfAbsent(dayOfMonth, new instance of YOUR_dATASTRUCTURE);
                                HASHMAP INITIALIZED EARLIER.get(dayOfMonth).add(taskNode);
                             */
                            // You might need to compare each node to sort it properly


                            // Priority queue example:
                            // dayToNodes.putIfAbsent(instDate, new PriorityQueue<>(Comparator.comparing(node -> {
                            //    CalendarTaskItemController ctrl = (CalendarTaskItemController) node.getUserData();
                            //    return ctrl.getTask().getDueTime();
                            // })));
                            //    dayToNodes.get(instDate).add(item);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

        for (int day = 0; day < 35; day++) {
            LocalDate actualDate = startWindow.plusDays(day);
            // TODO replace list with your data strucutre
            // Priority queue example: PriorityQueue<Node> dayTasks = dayToNodes.get(actualDate);
           /* Uncomment this, change the (HASHMAP<LocalDate,YOUR_DATASTRUCTURE<Node>>) to the hashmap earlier
                YOURDATASTRUCTURE<Node> dayTasks = (HASHMAP<LocalDate,YOUR_DATASTRUCTURE<Node>>).get(actualDate);
            */
            int targetBoxIndex = day; // Adjust for the box index (0-based)

            if (dayTasks != null) {
                if(dayTasks.size()>2){
                    for(int i = 0; i < 2;i++){
                       // TODO get node from your data structure and put in motnh
                        monthDayBox.get(targetBoxIndex).getChildren().add(dayTasks.get(i));
                        // Priority queue example
                        // Node temp = dayTasks.poll();
                        // monthDayBox.get(targetBoxIndex).getChildren().add(temp);
                        // dayTasks.add(temp);
                    }
                    Label viewMoreLabel = new Label("View More");
                    viewMoreLabel.setAlignment(Pos.CENTER);
                        viewMoreLabel.setOnMouseClicked(e -> {
                            Map<String, Task> taskDisplayMap = new LinkedHashMap<>();
                            for (Node node : dayTasks) {
                                CalendarTaskItemController controller = (CalendarTaskItemController) node.getUserData();
                                if (controller != null) {
                                    Task task = controller.getTask();
                                    String displayString = String.format("%s @ %s", task.getTitle(), task.getDueTime());
                                    while (taskDisplayMap.containsKey(displayString)) {
                                        displayString += " ";
                                    }
                                    taskDisplayMap.put(displayString, task);
                                }
                            }

                        if (taskDisplayMap.isEmpty()) return;

                        ChoiceDialog<String> dialog = new ChoiceDialog<>(
                            taskDisplayMap.keySet().iterator().next(),
                            taskDisplayMap.keySet()
                        );
                        dialog.setTitle("Choose a task that day");
                        dialog.setHeaderText(null);
                        dialog.setContentText("Available tasks:");
                        root.setDisable(true);

                        Optional<String> result = dialog.showAndWait();
                        root.setDisable(false);

                        result.ifPresent(taskKey -> {
                            Task selectedTask = taskDisplayMap.get(taskKey);
                            try {
                                FXMLLoader loader = new FXMLLoader(getClass().getResource("/OpenTask.fxml"));
                                Parent openTask = loader.load();

                                OpenTaskController controller = loader.getController();
                                controller.setTask(selectedTask);

                                Stage taskWindow = new Stage();
                                taskWindow.setTitle(selectedTask.getTitle());
                                taskWindow.setScene(new Scene(openTask, 692, 411));
                                taskWindow.setResizable(false);

                                root.setDisable(true);
                                taskWindow.setOnHidden(event -> root.setDisable(false));
                                taskWindow.show();
                            } catch (Exception er) {
                                er.printStackTrace();
                            }
                        });
                });
                monthDayBox.get(targetBoxIndex).getChildren().add(viewMoreLabel);
                }else{
                    // TODO For each node in your data structure, change if needed
                    for (Node taskNode : dayTasks) {
                        monthDayBox.get(targetBoxIndex).getChildren().add(taskNode);
                    }
                }
            }
        }


        // Cache the tasks for the current month
        /* TODO Uncomment this, the hashmap is the one earlier
          cache.put(curDate.toString(), (HASHMAP<LocalDate,YOUR_DATASTRUCTURE<Node>>));
         */
        }
    }

    public void generateTaskItem(Task task){
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainController.class.getResource("/TaskItems.fxml"));
            Node item = fxmlLoader.load();
            TaskItemController controller = fxmlLoader.getController();
            controller.setCallerRoot(root);
            controller.setTask(task);
            controller.setMain(this);
            taskArea.getChildren().add(item);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void refreshCache(){
        cache = new LinkedHashMap<>();
    }
    // TODO create a getter for your data structure
    // public YOURDATASTRUCTURE getTasks(){ return tasks; }


    private Text createItem(String text, boolean options) {
        Text label = new Text(text);

        if (options) {
            label.setStyle("-fx-font-size: 12px; -fx-font-weight: normal; -fx-fill: #000000;");
        } else {
            label.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-fill: #666666;");
        }
        return label;
    }

    public void refreshTaskList(){
        // TODO add your data strucutre as the argument here
        // public void refreshTaskList(YOUR DATA STRUCTURE tasks)
        taskArea.getChildren().clear();
        // Repopulate task list, from your data structures
        // Priority queue example:
        /*
        for(Task task:tasks){
            generateTaskItem(task);
        }
         */
    }

    @FXML
    private void openCreationScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/TaskCreation.fxml"));
            Parent taskRoot = loader.load();
            TaskCreateController controler = loader.getController();
            controler.setMain(this);

            Stage taskWindow = new Stage();
            taskWindow.setTitle("Create task");
            taskWindow.setScene(new Scene(taskRoot, 600, 400));
            taskWindow.setResizable(false);

            root.setDisable(true); // Disbales the main window when category screen is opened

            taskWindow.setOnHidden(event -> {
                root.setDisable(false);  // Makes it enabled again when category is cllosed
            });

            taskWindow.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void openCategoriesScreen(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/CategoryWindow.fxml"));
            Parent categoriesRoot = loader.load();
            CategoriesController controller = loader.getController();


            controller.setMain(this);

            Stage categoriesWindow = new Stage();
            categoriesWindow.setTitle("Categories");
            categoriesWindow.setScene(new Scene(categoriesRoot, 640, 480));
            categoriesWindow.setResizable(false);

            root.setDisable(true); // Disbales the main window when category screen is opened

            categoriesWindow.setOnHidden(event -> {
                root.setDisable(false);  // Makes it enabled again when category is cllosed
            });

            categoriesWindow.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void left(){
        curDate = curDate.minusMonths(1);
        monthLabel.setText(curDate.format(DateTimeFormatter.ofPattern("MMMM, yyyy")));
        populateCalendar();
    }
    @FXML
    private void right(){
        curDate = curDate.plusMonths(1);
        monthLabel.setText(curDate.format(DateTimeFormatter.ofPattern("MMMM, yyyy")));
        populateCalendar();
    }

    @FXML
    private void today(){
        curDate = LocalDate.now();
        monthLabel.setText(curDate.format(DateTimeFormatter.ofPattern("MMMM, yyyy")));
        populateCalendar();
    }

    @FXML
    private void search(){
        System.out.println(searchBar.getText());
        long startTime = System.nanoTime();
        /*
         TODO filter if any task matches or contains searchBar.getText()
              for every task taht matches, add that task to a new instance of your data structure
              then pass to refreshTaskList()
          Priority queue example:
            PriorityQueue<Task> temp = new PriorityQueue<>(new TaskComparator());
            for(Task task:tasks){
                if(task.getTitle().contains(searchBar.getText())){
                    temp.add(task);
                }
            }
            refreshTaskList(temp);
         */
        long endTime = System.nanoTime();
        Benchmark.getInstance().getTime(startTime,endTime,2);
        Benchmark.getInstance().getSpace(2);
    }

    @FXML
    private void filter(){
        long startTime = System.nanoTime();
        completedFilter = completed.isSelected();
        uncompletedFilter = uncompleted.isSelected();
        System.out.println("---");
        int sortPriorityVal;
        switch (sort_priority.getValue()) {
            case "↕    Priority":
                sortPriorityVal = 0;
                break;
            case "↓    Highest to Lowest":
                sortPriorityVal = 1;
                break;
            case "↑    Lowest to Highest":
                sortPriorityVal = 2;
                break;
            default:
                sortPriorityVal = 0; // fallback
        }


        int sortDateVal;
        switch (sort_due.getValue()){
            case ("\uD83D\uDCC6   Due date"):
                sortDateVal = 0;
                break;
            case ("↓     Latest to Earliest"):
                sortDateVal = 1;
                break;
            case("↑     Earliest to Latest"):
                sortDateVal = 2;
                break;
        }
        int sortTimeVal;
        switch (sort_due.getValue()){
            case ("\uD83D\uDD53 Due time"):
                sortTimeVal = 0;
                break;
            case ("↓     Latest to Earliest"):
                sortTimeVal = 1;
                break;
            case("↑     Earliest to Latest"):
                sortDateVal = 2;
                break;
        }
        if(enableDateRange.isSelected()){
            fromDateFilter = dateFrom.getValue();
            toDateFilter = dateTo.getValue();

            System.out.println(toDateFilter);
            System.out.println(fromDateFilter);
        }
        if(enableTimeRange.isSelected()){
            fromTimeFilter = LocalTime.of(fromHour.getValue(), fromMinute.getValue());
            toTimeFilter = LocalTime.of(toHour.getValue(),toMinute.getValue());

            System.out.println(fromTimeFilter);
            System.out.println(toTimeFilter);
        }

        System.out.println(completedFilter);
        System.out.println(uncompletedFilter);
        System.out.println(rRuleFilter);
        System.out.println(categoryFilter);
        /*  See priority queue branch for example, you have to sort it manually here
            TODO create new instance of your data structure to store filtered task
             filter using the variable above, and also every element of categoryFilter and rRulefilter
             for rRuleFilter, keep on generating the next task item, until it reaches the end date or
             until toDateFilter, if there is no endDate and enableDateRange is not selected, no need to generate
             search through all of the created tasks to add to the filtered task data struct
             then sort the filtered task data struct depending on the selected radio button using
             Toggle selectedToggle = group.getSelectedToggle();
                if (selectedToggle != null) {
                    RadioButton selectedRadio = (RadioButton) selectedToggle;
                    if(selected == dueDateRadio){
                    }
                    if(selected == dueTimeRadio){
                    }
                    if(selected == priorityRadio){
                    }
                }
             Sort using the respective integer value above, sortPriorityVal, sortDateVal, sortTimeVal
             Then pass that to refreshTaskList(), rmb to add the parameter as ur data struct
             Note: For the date and time filters, either the from or to value may be null.
                    If only one is provided, tasks will be filtered to only those that start
                    on/after the "from" value or end on/before the "to" value, respectively.
         */
        long endTime = System.nanoTime();
        Benchmark.getInstance().getTime(startTime,endTime,6);
        Benchmark.getInstance().getSpace(6);
    }

    @FXML
    private void selectCategory() {
        ChoiceDialog<String> d = new ChoiceDialog<>(categories.get(1).getName(),
                categories.values().stream().map(Category::getName).collect(Collectors.toList()));
        d.setTitle("Choose a Category");
        d.setHeaderText(null);
        d.setContentText("Available categories:");
        root.setDisable(true);

        Optional<String> result = d.showAndWait(); // blocks until user chooses or cancels

        root.setDisable(false);
        result.ifPresent(selectedName -> {

            for (Category category : categories.values()) {
                if (category.getName().equals(selectedName)) {
                    if (categoryFilter.contains(category)) {
                        return;
                    }
                    categoryFilter.add(category);
                    addCategoryBox(category); // Add styled clickable box
                }
            }
        });
        filter();
    }

    private void addCategoryBox(Category category) {
        StackPane box = new StackPane();
        box.setPrefWidth(205);
        box.setPrefHeight(28);
        box.setStyle("-fx-border-color: #AAAAAA; -fx-border-radius: 4; -fx-background-radius: 2; -fx-border-insets: 2;");

        String fullName = category.getName();
        String displayName = fullName.length() > 18 ? fullName.substring(0, 15) + "..." : fullName;
        Label label = new Label(displayName);
        label.setAlignment(Pos.CENTER);
        box.getChildren().add(label);

        StackPane.setAlignment(label, Pos.CENTER);

        box.setOnMouseClicked(e -> handleCategoryClick(category));

        categoryFilterList.getChildren().add(box);
    }

    private void handleCategoryClick(Category category) {
        selectedCategory = category;
        // Resets highlight other boxes
        for (Node node : categoryFilterList.getChildren()) {
            node.setStyle("-fx-border-color: #AAAAAA; -fx-border-radius: 4; -fx-background-radius: 2; -fx-border-insets: 2;");
        }

        for (Node node : categoryFilterList.getChildren()) {
            if (node instanceof StackPane box) {
                Label label = (Label) box.getChildren().get(0);
                if (label.getText().equals(category.getName()) || category.getName().startsWith(label.getText())) {
                    selectedBox = box;
                    box.setStyle("-fx-border-color: #AAAAAA; -fx-background-color: #A0A0A0; -fx-border-width: 2;");
                    break;
                }
            }
        }
    }

    @FXML
    private void deleteSelectedCategory(){
        if (selectedCategory != null && selectedBox != null) {
            categoryFilter.remove(selectedCategory);
            categoryFilterList.getChildren().remove(selectedBox);
            selectedCategory = null;
            selectedBox = null;
        }
        filter();
    }

    @FXML
    private void deleteSelectedRrule(){
        System.out.println(selectedRruleFilter);
        System.out.println(rRuleFilter);
        if (!selectedRruleFilter.isBlank()) {
            String[] parts = selectedRruleFilter.split("\\|");
            int count = 1;
            StringBuilder parsed = new StringBuilder();
            for (String part : parts) {
                switch(count){
                    case(1):
                        parsed.append("FREQ=");
                        parsed.append(part.trim());
                        break;
                    case(2):
                        parsed.append(";INTERVAL=");
                        parsed.append(part.trim());
                        break;
                    case(3):
                        parsed.append(";UNTIL=");
                        parsed.append(part.trim());
                        break;
                }
                count++;
            }
            rRuleFilter.remove(parsed.toString());
            rRuleFilterList.getChildren().remove(selectedBox);
            selectedRruleFilter = null;
            selectedBox = null;
        }
        filter();
    }


    private void addRruleBox(String rRule) {
        StackPane box = new StackPane();
        box.setPrefWidth(205);
        box.setPrefHeight(28);
        box.setStyle("-fx-border-color: #AAAAAA; -fx-border-radius: 4; -fx-background-radius: 2; -fx-border-insets: 2;");

        Label label = new Label(rRule);
        label.setAlignment(Pos.CENTER);
        box.getChildren().add(label);

        StackPane.setAlignment(label, Pos.CENTER);

        box.setOnMouseClicked(e -> handleCategoryClick(rRule));

        rRuleFilterList.getChildren().add(box);
    }

    private void handleCategoryClick(String rRule) {
        selectedRruleFilter = rRule;
        // Resets highlight other boxes
        for (Node node : rRuleFilterList.getChildren()) {
            node.setStyle("-fx-border-color: #AAAAAA; -fx-border-radius: 4; -fx-background-radius: 2; -fx-border-insets: 2;");
        }

        for (Node node : rRuleFilterList.getChildren()) {
            if (node instanceof StackPane box) {
                Label label = (Label) box.getChildren().get(0);
                if (label.getText().equals(rRule) || rRule.startsWith(label.getText())) {
                    selectedBox = box;
                    box.setStyle("-fx-border-color: #AAAAAA; -fx-background-color: #A0A0A0; -fx-border-width: 2;");
                    break;
                }
            }
        }
        filter();
    }

    @FXML
    private void selectRrule(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/RecurrenceEditor.fxml"));

            Parent rRuleEditorRoot = loader.load();
            RecurrenceEditorController controller=loader.getController();
            controller.setForFilter(rRuleFilter);
            Stage recurrenceWindow = new Stage();
            recurrenceWindow.setTitle("Recurrence rule");
            recurrenceWindow.setScene(new Scene(rRuleEditorRoot, 341, 160));
            recurrenceWindow.setResizable(false);


            root.setDisable(true); // Disbales the main window when category screen is opened

            recurrenceWindow.setOnHidden(event -> {
                root.setDisable(false);  // Makes it enabled again when category is cllosed
                if(controller.isChanged()){
                    System.out.println(controller.getFilter());
                    rRuleFilter.add(controller.getFilter());
                    addRruleBox(controller.getFilter());
                }
            });

            recurrenceWindow.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void enableDueTimeFilter(){
        if(enableTimeRange.isSelected()){
            fromHour.setDisable(false);
            fromMinute.setDisable(false);
            toHour.setDisable(false);
            toMinute.setDisable(false);
        }else{
            fromHour.setDisable(true);
            fromMinute.setDisable(true);
            toHour.setDisable(true);
            toMinute.setDisable(true);
            toTimeFilter = null;
            fromTimeFilter=null;
        }
        filter();
    }

    @FXML
    private void enableDueDateFilter(){
        if(enableDateRange.isSelected()){
            dateFrom.setDisable(false);
            dateTo.setDisable(false);
        }else{
            dateFrom.setDisable(true);
            dateTo.setDisable(true);
            fromDateFilter = null;
            toDateFilter = null;
        }
        filter();
    }
}
